package com.clairvoyant.naijamenu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.clairvoyant.naijamenu.bean.CommonRequestBean;
import com.clairvoyant.naijamenu.bean.PrizeList;
import com.clairvoyant.naijamenu.bean.QuizLevels;
import com.clairvoyant.naijamenu.bean.QuizResponseBean;
import com.clairvoyant.naijamenu.fonts.RobotoRegularButton;
import com.clairvoyant.naijamenu.fonts.RobotoRegularTextView;
import com.clairvoyant.naijamenu.utils.AppController;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GamePlayFragment extends Fragment implements OnClickListener {

    private static String TAG = GamePlayFragment.class.getSimpleName();
    private Activity mContext;
    private View gameView;
    private RelativeLayout progressView, rlChildLayout;
    private RobotoRegularTextView scoretv, time, levelNumber, question, answerOne, answerTwo, answerThree, answerFour;
    private ImageView correctAnswerOne, incorrectAnswerOne, ivOops;
    private ImageView correctAnswerTwo, incorrectAnswerTwo;
    private ImageView correctAnswerThree, incorrectAnswerThree;
    private ImageView correctAnswerFour, incorrectAnswerFour;
    private ArrayList<QuizLevels> levelList;
    private ArrayList<QuizLevels> incorrectLevelList = new ArrayList<>();
    private ArrayList<PrizeList> prizeList;
    private QuizCountDownTimer countDownTimer;
    private long timeElapsed;
    private long startTime = 0;
    private long interval = 1000;
    private int currentLevel = 0;
    private int currentQuestion = 0;
    private int questionInEveryLevel = 0;
    private int correctAnswers = 0;
    private int score = 0;
    private int prizeId = 0;
    private int contestId = 0;
    private boolean byTimer = true;

    private ImageView logout;
    private LinearLayout scoreBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView()");
        mContext = getActivity();
        if (!Utils.isOnline(mContext)) {
            gameView = inflater.inflate(R.layout.no_network_activity, container, false);
            RobotoRegularButton tryAgain = gameView.findViewById(R.id.try_again);
            tryAgain.setOnClickListener(arg0 -> reload());
        } else {
            gameView = inflater.inflate(R.layout.fragment_game_play, container, false);
            initializeViews(gameView);
            if (Utils.isOnline(mContext)) {
                progressView.setVisibility(View.VISIBLE);
                getQuizData();
            } else {
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }

            ImageView logo = gameView.findViewById(R.id.pepsi_logo);
            logo.setOnClickListener(v -> {
            });
        }
        return gameView;
    }

    public void reload() {
        Intent intent = getActivity().getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().finish();
        startActivity(intent);
    }

    private void initializeViews(View gameView) {

        progressView = gameView.findViewById(R.id.progress_view_quiz);
        rlChildLayout = gameView.findViewById(R.id.rlChildLayout);

        scoretv = gameView.findViewById(R.id.score);
        time = gameView.findViewById(R.id.time);
        levelNumber = gameView.findViewById(R.id.level_numbner);
        logout = gameView.findViewById(R.id.logout);

        question = gameView.findViewById(R.id.question);
        answerOne = gameView.findViewById(R.id.answer_one);
        answerTwo = gameView.findViewById(R.id.answer_two);
        answerThree = gameView.findViewById(R.id.answer_three);
        answerFour = gameView.findViewById(R.id.answer_four);

        correctAnswerOne = gameView.findViewById(R.id.correct_answer_one);
        ivOops = gameView.findViewById(R.id.ivOops);

        correctAnswerTwo = gameView.findViewById(R.id.correct_answer_two);
        correctAnswerThree = gameView.findViewById(R.id.correct_answer_three);
        correctAnswerFour = gameView.findViewById(R.id.correct_answer_four);

        incorrectAnswerOne = gameView.findViewById(R.id.incorrect_answer_one);
        incorrectAnswerTwo = gameView.findViewById(R.id.incorrect_answer_two);
        incorrectAnswerThree = gameView.findViewById(R.id.incorrect_answer_three);
        incorrectAnswerFour = gameView.findViewById(R.id.incorrect_answer_four);
        scoreBox = gameView.findViewById(R.id.scorebox);

        answerOne.setOnClickListener(this);
        answerTwo.setOnClickListener(this);
        answerThree.setOnClickListener(this);
        answerFour.setOnClickListener(this);
        logout.setOnClickListener(v -> showCancelDialog(R.string.alert_message));

        ((MainActivity) mContext).setOnBackPressListener(() -> showCancelDialog(R.string.alert_message));
    }

    private void getQuizData() {
        CommonRequestBean requestBean = new CommonRequestBean();
        requestBean.setApp_version(Utils.getAppVersion(mContext));
        requestBean.setDevice_type(Constants.DEVICE_TYPE);
        requestBean.setRestaurant_id(PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID));
        final String paramStr = new Gson().toJson(requestBean, CommonRequestBean.class);

        StringRequest request = new StringRequest(Method.POST, Constants.QUIZ_QUESTIONS_API, quizResponseSuccess(), quizError()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("data", paramStr);
                Log.i("QUIZ_PARAM", param.toString());
                return param;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES, Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);
    }

    private com.android.volley.Response.Listener<String> quizResponseSuccess() {

        return new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    if (response != null && response.length() > 0) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.replace("\n", ""));
                            Log.d("QUIZE_RESPONSE", " " + jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getString(R.string.oops_something_went_wrong), Toast.LENGTH_SHORT).show();

                        }

                        Utils.longInfo(response);
                        QuizResponseBean quizBean = new Gson().fromJson(response, QuizResponseBean.class);
                        if (quizBean.getStatus().equals("true")) {
                            contestId = quizBean.getContest_id();
                            QuizLevels[] levelArray = quizBean.getLevels();
                            if (levelArray != null && levelArray.length > 0) {
                                levelList = new ArrayList<>();
                                for (int i = 0; i < levelArray.length; i++) {
                                    QuizLevels levelBean = new QuizLevels();
                                    levelBean.setLevelId(levelArray[i].getLevelId());
                                    levelBean.setLevelNo(levelArray[i].getLevelNo());
                                    levelBean.setTime(levelArray[i].getTime());
                                    levelBean.setMinQuesToPass(levelArray[i].getMinQuesToPass());
                                    levelBean.setPerQuestionscore(levelArray[i].getPerQuestionscore());
                                    levelBean.setQuizQuestions(levelArray[i].getQuizQuestions());
                                    levelList.add(levelBean);
                                }
                            }

                            PrizeList[] prizeArray = quizBean.getPrizeList();

                            if (prizeArray != null && prizeArray.length > 0) {
                                prizeList = new ArrayList<>();
                                for (int i = 0; i < prizeArray.length; i++) {
                                    PrizeList prizeBean = new PrizeList();
                                    prizeBean.setMinScore(prizeArray[i].getMinScore());
                                    prizeBean.setPrize(prizeArray[i].getPrize());
                                    prizeBean.setPrizeId(prizeArray[i].getPrizeId());
                                    prizeList.add(prizeBean);
                                }
                            }

                            if (levelList != null && levelList.size() > 0) {
                                startTime = levelList.get(currentLevel).getTime() * 1000;
                                countDownTimer = new QuizCountDownTimer(startTime, interval);
                                countDownTimer.start();
                                levelNumber.setText("[" + levelList.size() + "]" + "\n Levels");

                                for (int i = 0; i <= currentLevel; i++) {
                                    LayoutInflater inflater = mContext.getLayoutInflater();
                                    View view = inflater.inflate(R.layout.pepsi_bottle_selected, null);
                                    scoreBox.addView(view);
                                }

                                if (levelList != null && levelList.size() > 0) {
                                    for (int i = 1; i < levelList.size() - currentLevel; i++) {
                                        LayoutInflater inflater = mContext.getLayoutInflater();
                                        View view = inflater.inflate(R.layout.pepsi_bottle, null);
                                        scoreBox.addView(view);
                                    }
                                }

                                if (levelList != null && levelList.size() > 0) {
                                    questionInEveryLevel = levelList.get(0).getQuizQuestions().length;
                                    Log.i("QUESTIONS", String.valueOf(questionInEveryLevel));
                                    question.setText(levelList.get(0).getQuizQuestions()[0].getQuestionText());

                                    answerOne.setText(levelList.get(0).getQuizQuestions()[0].getAnswerList()[0].getName());
                                    answerTwo.setText(levelList.get(0).getQuizQuestions()[0].getAnswerList()[1].getName());
                                    answerThree.setText(levelList.get(0).getQuizQuestions()[0].getAnswerList()[2].getName());
                                    answerFour.setText(levelList.get(0).getQuizQuestions()[0].getAnswerList()[3].getName());
                                }
                            }
                        } else if (quizBean.getStatus().equals("false")) {
                            //false response from server
                            rlChildLayout.setVisibility(View.GONE);
                            ivOops.setVisibility(View.VISIBLE);
                            progressView.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(mContext, getString(R.string.oops_something_went_wrong), Toast.LENGTH_SHORT).show();
                }
                progressView.setVisibility(View.GONE);
            }
        };
    }

    private com.android.volley.Response.ErrorListener quizError() {
        return new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressView.setVisibility(View.GONE);
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onClick(View view) {

        if (levelList == null)
            return;

        switch (view.getId()) {

            case R.id.answer_one:

                if (levelList.get(currentLevel).getQuizQuestions()[currentQuestion].getAnswerList()[0].getIsRight().equals("Y")) {
                    answerOne.setBackgroundResource(R.drawable.border_all_green_rounded);
                    correctAnswerOne.setVisibility(View.VISIBLE);
                    score = score + levelList.get(currentLevel).getPerQuestionscore();
                    scoretv.setText(String.valueOf(score));
                    correctAnswers++;
                } else {
                    incorrectLevelList.add(levelList.get(currentLevel));
                    answerOne.setBackgroundResource(R.drawable.border_all_red_rounded);
                    incorrectAnswerOne.setVisibility(View.VISIBLE);
                }

                pauseAndRefresh();

                break;

            case R.id.answer_two:
                if (levelList.get(currentLevel).getQuizQuestions()[currentQuestion].getAnswerList()[1].getIsRight().equals("Y")) {
                    answerTwo.setBackgroundResource(R.drawable.border_all_green_rounded);
                    correctAnswerTwo.setVisibility(View.VISIBLE);
                    score = score + levelList.get(currentLevel).getPerQuestionscore();
                    scoretv.setText(String.valueOf(score));
                    correctAnswers++;
                } else {
                    incorrectLevelList.add(levelList.get(currentLevel));
                    answerTwo.setBackgroundResource(R.drawable.border_all_red_rounded);
                    incorrectAnswerTwo.setVisibility(View.VISIBLE);
                }

                pauseAndRefresh();

                break;

            case R.id.answer_three:
                if (levelList.get(currentLevel).getQuizQuestions()[currentQuestion].getAnswerList()[2].getIsRight().equals("Y")) {
                    answerThree.setBackgroundResource(R.drawable.border_all_green_rounded);
                    correctAnswerThree.setVisibility(View.VISIBLE);
                    score = score + levelList.get(currentLevel).getPerQuestionscore();
                    scoretv.setText(String.valueOf(score));
                    correctAnswers++;
                } else {
                    incorrectLevelList.add(levelList.get(currentLevel));
                    answerThree.setBackgroundResource(R.drawable.border_all_red_rounded);
                    incorrectAnswerThree.setVisibility(View.VISIBLE);
                }

                pauseAndRefresh();

                break;

            case R.id.answer_four:
                if (levelList.get(currentLevel).getQuizQuestions()[currentQuestion].getAnswerList()[3].getIsRight().equals("Y")) {
                    answerFour.setBackgroundResource(R.drawable.border_all_green_rounded);
                    correctAnswerFour.setVisibility(View.VISIBLE);
                    score = score + levelList.get(currentLevel).getPerQuestionscore();
                    scoretv.setText(String.valueOf(score));
                    correctAnswers++;
                } else {
                    incorrectLevelList.add(levelList.get(currentLevel));
                    answerFour.setBackgroundResource(R.drawable.border_all_red_rounded);
                    incorrectAnswerFour.setVisibility(View.VISIBLE);
                }

                pauseAndRefresh();

                break;
        }
    }

    private void refreshNextQuestion() {

        //CHeck if user has reached last question of the last level
        if (currentLevel == (levelList.size() - 1) && currentQuestion == (levelList.get(currentLevel).getQuizQuestions().length - 1)) {
            currentLevel = 0;
            currentQuestion = 0;
            if (score >= prizeList.get(0).getMinScore()) {
                showCongratsDialog(prizeList.get(0).getPrize());
                prizeId = prizeList.get(0).getPrizeId();
            } else if (prizeList.size() > 1 && score < prizeList.get(0).getMinScore() && score >= prizeList.get(1).getMinScore()) {
                showCongratsDialog(prizeList.get(1).getPrize());
                prizeId = prizeList.get(1).getPrizeId();
            } else if (prizeList.size() > 2 && score < prizeList.get(1).getMinScore() && score >= prizeList.get(2).getMinScore()) {
                showCongratsDialog(prizeList.get(2).getPrize());
                prizeId = prizeList.get(2).getPrizeId();
            } else if (prizeList.size() > 2 && score < prizeList.get(2).getMinScore()) {
                currentQuestion = 0;
                correctAnswers = 0;
                currentLevel = 0;
                //Reseting score as achieved till the last level
                score = 0;
                //Reset Timer & Score
                countDownTimer.cancel();
                startTime = levelList.get(currentLevel).getTime() * 1000;
                countDownTimer = new QuizCountDownTimer(startTime, interval);
                countDownTimer.start();

                showFailedDialog("You ran out of time. Please click below to restart the challange.");
            }
        } else { //Check if the user has reached last question of the current level
            if (currentQuestion == (levelList.get(currentLevel).getQuizQuestions().length - 1)) {
                if (correctAnswers >= levelList.get(currentLevel).getMinQuesToPass()) {
                    showLevelClearedDialog("You have successfully cleared level " + (currentLevel + 1));
                    currentLevel++;
                    countDownTimer.cancel();
                    startTime = levelList.get(currentLevel).getTime() * 1000;
                    countDownTimer = new QuizCountDownTimer(startTime, interval);
                    countDownTimer.start();

                    scoreBox.removeAllViews();
                    for (int i = 0; i <= currentLevel; i++) {
                        LayoutInflater inflater = mContext.getLayoutInflater();
                        View view = inflater.inflate(R.layout.pepsi_bottle_selected, null);
                        scoreBox.addView(view);
                    }

                    for (int i = 1; i < levelList.size() - currentLevel; i++) {
                        LayoutInflater inflater = mContext.getLayoutInflater();
                        View view = inflater.inflate(R.layout.pepsi_bottle, null);
                        scoreBox.addView(view);
                    }

                    currentQuestion = 0;
                    correctAnswers = 0;
                } else {
                    showFailedDialog("You didn't clear the level " + (currentLevel + 1));

                    currentQuestion = 0;
                    correctAnswers = 0;
                    currentLevel = 0;
                    //Reseting score as achieved till the last level
                    score = 0;
                    //Reset Timer & Score
                    countDownTimer.cancel();
                    startTime = levelList.get(currentLevel).getTime() * 1000;
                    countDownTimer = new QuizCountDownTimer(startTime, interval);
                    countDownTimer.start();

                    showRecyleNextQuestion();
                }
            } else {
                if (byTimer)
                    currentQuestion++;
            }
            Log.i("CURRENT_LEVEL", String.valueOf(currentLevel));
            Log.i("CURRENT_QUESTION", String.valueOf(currentQuestion));
        }
    }

    private void showNextQuestion() {
        if (levelList != null && levelList.size() > 0) {
            question.setText(levelList.get(currentLevel).getQuizQuestions()[currentQuestion].getQuestionText());
            answerOne.setText(levelList.get(currentLevel).getQuizQuestions()[currentQuestion].getAnswerList()[0].getName());
            answerTwo.setText(levelList.get(currentLevel).getQuizQuestions()[currentQuestion].getAnswerList()[1].getName());
            answerThree.setText(levelList.get(currentLevel).getQuizQuestions()[currentQuestion].getAnswerList()[2].getName());
            answerFour.setText(levelList.get(currentLevel).getQuizQuestions()[currentQuestion].getAnswerList()[3].getName());

            answerOne.setBackgroundResource(R.drawable.border_all_gray_rounded);
            answerTwo.setBackgroundResource(R.drawable.border_all_gray_rounded);
            answerThree.setBackgroundResource(R.drawable.border_all_gray_rounded);
            answerFour.setBackgroundResource(R.drawable.border_all_gray_rounded);

            correctAnswerOne.setVisibility(View.GONE);
            correctAnswerTwo.setVisibility(View.GONE);
            correctAnswerThree.setVisibility(View.GONE);
            correctAnswerFour.setVisibility(View.GONE);

            incorrectAnswerOne.setVisibility(View.GONE);
            incorrectAnswerTwo.setVisibility(View.GONE);
            incorrectAnswerThree.setVisibility(View.GONE);
            incorrectAnswerFour.setVisibility(View.GONE);
            byTimer = true;
        }
    }

    private void showRecyleNextQuestion() {
        if (incorrectLevelList != null && incorrectLevelList.size() > 0) {
            question.setText(incorrectLevelList.get(currentLevel).getQuizQuestions()[currentQuestion].getQuestionText());
            answerOne.setText(incorrectLevelList.get(currentLevel).getQuizQuestions()[currentQuestion].getAnswerList()[0].getName());
            answerTwo.setText(incorrectLevelList.get(currentLevel).getQuizQuestions()[currentQuestion].getAnswerList()[1].getName());
            answerThree.setText(incorrectLevelList.get(currentLevel).getQuizQuestions()[currentQuestion].getAnswerList()[2].getName());
            answerFour.setText(incorrectLevelList.get(currentLevel).getQuizQuestions()[currentQuestion].getAnswerList()[3].getName());

            answerOne.setBackgroundResource(R.drawable.border_all_gray_rounded);
            answerTwo.setBackgroundResource(R.drawable.border_all_gray_rounded);
            answerThree.setBackgroundResource(R.drawable.border_all_gray_rounded);
            answerFour.setBackgroundResource(R.drawable.border_all_gray_rounded);

            correctAnswerOne.setVisibility(View.GONE);
            correctAnswerTwo.setVisibility(View.GONE);
            correctAnswerThree.setVisibility(View.GONE);
            correctAnswerFour.setVisibility(View.GONE);

            incorrectAnswerOne.setVisibility(View.GONE);
            incorrectAnswerTwo.setVisibility(View.GONE);
            incorrectAnswerThree.setVisibility(View.GONE);
            incorrectAnswerFour.setVisibility(View.GONE);
        }
    }

    private void pauseAndRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            refreshNextQuestion();
            showNextQuestion();
        }, 700);
    }

    private void showFailedDialog(String message) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.level_failed_dialog_view);
        RobotoRegularTextView messageView = dialog.findViewById(R.id.message);
        messageView.setText(message);
        RobotoRegularTextView continueGame = dialog.findViewById(R.id.continue_game);
        continueGame.setOnClickListener(v -> {
            resetViews();
            dialog.dismiss();
        });

        dialog.show();
    }

    protected void resetViews() {
        scoreBox.removeAllViews();

        for (int i = 0; i <= currentLevel; i++) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            View view = inflater.inflate(R.layout.pepsi_bottle_selected, null);
            scoreBox.addView(view);
        }

        for (int i = 1; i < levelList.size() - currentLevel; i++) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            View view = inflater.inflate(R.layout.pepsi_bottle, null);
            scoreBox.addView(view);
        }

        time.setText("00:00");
        scoretv.setText(String.valueOf(score));

        countDownTimer.cancel();
        countDownTimer = new QuizCountDownTimer(startTime, interval);
        countDownTimer.start();
    }

    private void showLevelClearedDialog(String message) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.level_cleared_dialog_view);
        RobotoRegularTextView messageView = dialog.findViewById(R.id.message);
        messageView.setText(message);
        RobotoRegularTextView continueGame = dialog.findViewById(R.id.continue_game);
        continueGame.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showCongratsDialog(String message) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.congratulation_dialog_view);
        RobotoRegularTextView messageView = dialog.findViewById(R.id.message);
        messageView.setText("You Have Won " + message);
        RobotoRegularTextView clickNow = dialog.findViewById(R.id.click_now);
        clickNow.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, GamePlayRegistration.class);
            intent.putExtra("PRIZE_ID", prizeId);
            intent.putExtra("CONTEST_ID", contestId);
            startActivity(intent);
            dialog.dismiss();
            mContext.finish();
        });

        dialog.show();
    }

    private void showCancelDialog(int resource) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.cancel_alert_dialog_view);
        RobotoRegularTextView messageView = dialog.findViewById(R.id.message);
        messageView.setText(resource);
        RobotoRegularTextView cancel = dialog.findViewById(R.id.cancel);
        RobotoRegularTextView ok = dialog.findViewById(R.id.ok);
        cancel.setOnClickListener(v -> dialog.dismiss());

        ok.setOnClickListener(v -> mContext.finish());
        dialog.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    private class QuizCountDownTimer extends CountDownTimer {

        public QuizCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
            long minute = (startTime / 1000) / 60;
            long seconds = (startTime / 1000) % 60;
            time.setText(minute + ":" + seconds);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            String minTxt = "";
            String secTxt = "";
            timeElapsed = startTime - millisUntilFinished;
            long minute = (millisUntilFinished / 1000) / 60;
            long seconds = (millisUntilFinished / 1000) % 60;
            if (minute < 10)
                minTxt = "0" + minute;
            else minTxt = String.valueOf(minute);

            if (seconds < 10)
                secTxt = "0" + seconds;
            else secTxt = String.valueOf(seconds);

            time.setText(minTxt + ":" + secTxt);
        }

        @Override
        public void onFinish() {
            if (currentLevel == levelList.size() - 1) {
                if (score < prizeList.get(0).getMinScore()) {
                    currentQuestion = 0;
                    correctAnswers = 0;
                    currentLevel = 0;
                    // Reseting score as achieved till the last level
                    score = 0;
                    // Reset Timer & Score
                    countDownTimer.cancel();
                    startTime = levelList.get(currentLevel).getTime() * 1000;
                    countDownTimer = new QuizCountDownTimer(startTime, interval);
                    countDownTimer.start();
//					showFailedDialog("You didn't clear the game");
                    showFailedDialog("You ran out of time. Please click below to restart the challange.");
                } else {
                    showCongratsDialog(prizeList.get(0).getPrize());
                    prizeId = prizeList.get(0).getPrizeId();
                }

            } else if (currentLevel < levelList.size() - 1) {
                currentLevel++;
                currentQuestion = 0;
                countDownTimer.cancel();
                startTime = levelList.get(currentLevel).getTime() * 1000;
                countDownTimer = new QuizCountDownTimer(startTime, interval);
                countDownTimer.start();
                byTimer = false;

                scoreBox.removeAllViews();
                for (int i = 0; i <= currentLevel; i++) {
                    LayoutInflater inflater = mContext.getLayoutInflater();
                    View view = inflater.inflate(R.layout.pepsi_bottle_selected, null);
                    scoreBox.addView(view);
                }

                for (int i = 1; i < levelList.size() - currentLevel; i++) {
                    LayoutInflater inflater = mContext.getLayoutInflater();
                    View view = inflater.inflate(R.layout.pepsi_bottle, null);
                    scoreBox.addView(view);
                }


                pauseAndRefresh();
            }
        }
    }
}