package com.clairvoyant.naijamenu;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.clairvoyant.naijamenu.bean.AnswerList;
import com.clairvoyant.naijamenu.bean.CommonRequestBean;
import com.clairvoyant.naijamenu.bean.SurveyParam;
import com.clairvoyant.naijamenu.bean.SurveyQuestionList;
import com.clairvoyant.naijamenu.bean.SurveyQuestionResponse;
import com.clairvoyant.naijamenu.bean.SurveyRequestBean;
import com.clairvoyant.naijamenu.fonts.RobotoRegularButton;
import com.clairvoyant.naijamenu.fonts.RobotoRegularEditText;
import com.clairvoyant.naijamenu.fonts.RobotoRegularTextView;
import com.clairvoyant.naijamenu.utils.AppController;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.gson.Gson;
import com.pepsi.database.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RateRestaurantFragment extends Fragment implements OnClickListener {

    private static String TAG = RateRestaurantFragment.class.getSimpleName();
    private View restaurantView;
    private RobotoRegularEditText name, mobileNumber, email;
    private RobotoRegularTextView birthday, anniversary, questionOne, questionTwo, questionThree, questionFour;
    private RadioButton ansOneRadioOne, ansOneRadioTwo, ansOneRadioThree, ansOneRadioFour;
    private RadioButton ansTwoRadioOne, ansTwoRadioTwo, ansTwoRadioThree, ansTwoRadioFour;
    private RadioButton ansThreeRadioOne, ansThreeRadioTwo, ansThreeRadioThree, ansThreeRadioFour;
    private RadioButton ansFourRadioOne, ansFourRadioTwo, ansFourRadioThree, ansFourRadioFour;
    private RadioGroup[] radios;
    private RobotoRegularButton btnSubmit;
    private RelativeLayout progressView;
    private Context mContext;
    private SurveyQuestionList[] surveyQuestionArray;
    private AnswerList[] answerArray;
    private boolean isDialogShown = false;
    private Dialog noInternetDialog = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView()");
        mContext = getActivity();
        if (!Utils.isOnline(mContext)) {
            String surveyQuestionJSON = DatabaseHelper.getSurveyQuestionsJson(mContext);
            if (!TextUtils.isEmpty(surveyQuestionJSON)) {
                restaurantView = inflater.inflate(R.layout.fragment_rate_restaurant, container, false);
                initializeViews(restaurantView);
                SurveyQuestionResponse surveyModel = new Gson().fromJson(surveyQuestionJSON, SurveyQuestionResponse.class);
                parseSurveyResponse(surveyQuestionJSON, surveyModel, false);
            } else {
                restaurantView = inflater.inflate(R.layout.no_network_activity, container, false);
                RobotoRegularButton tryAgain = restaurantView.findViewById(R.id.try_again);
                tryAgain.setOnClickListener(arg0 -> reload());
            }
        } else {
            restaurantView = inflater.inflate(R.layout.fragment_rate_restaurant, container, false);
            initializeViews(restaurantView);

            if (Utils.isOnline(mContext)) {
                progressView.setVisibility(View.VISIBLE);
                getSurveyQuestions();
            }
        }
        return restaurantView;
    }

    public void reload() {
        Intent intent = getActivity().getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().finish();
        startActivity(intent);
    }

    private void initializeViews(View view) {

        radios = new RadioGroup[4];
        radios[0] = view.findViewById(R.id.rgOne);
        radios[1] = view.findViewById(R.id.rgTwo);
        radios[2] = view.findViewById(R.id.rgThree);
        radios[3] = view.findViewById(R.id.rgFour);

        ansOneRadioOne = view.findViewById(R.id.ans_one_radio_one);
        ansOneRadioTwo = view.findViewById(R.id.ans_one_radio_two);
        ansOneRadioThree = view.findViewById(R.id.ans_one_radio_three);
        ansOneRadioFour = view.findViewById(R.id.ans_one_radio_four);

        ansTwoRadioOne = view.findViewById(R.id.ans_two_radio_one);
        ansTwoRadioTwo = view.findViewById(R.id.ans_two_radio_two);
        ansTwoRadioThree = view.findViewById(R.id.ans_two_radio_three);
        ansTwoRadioFour = view.findViewById(R.id.ans_two_radio_four);

        ansThreeRadioOne = view.findViewById(R.id.ans_three_radio_one);
        ansThreeRadioTwo = view.findViewById(R.id.ans_three_radio_two);
        ansThreeRadioThree = view.findViewById(R.id.ans_three_radio_three);
        ansThreeRadioFour = view.findViewById(R.id.ans_three_radio_four);

        ansFourRadioOne = view.findViewById(R.id.ans_four_radio_one);
        ansFourRadioTwo = view.findViewById(R.id.ans_four_radio_two);
        ansFourRadioThree = view.findViewById(R.id.ans_four_radio_three);
        ansFourRadioFour = view.findViewById(R.id.ans_four_radio_four);

        name = view.findViewById(R.id.rate_name);
        mobileNumber = view.findViewById(R.id.rate_mobile);
        email = view.findViewById(R.id.rate_email);
        birthday = view.findViewById(R.id.birthday);
        anniversary = view.findViewById(R.id.anniversary);
        btnSubmit = view.findViewById(R.id.btn_submit);
        progressView = view.findViewById(R.id.progress_view_restaurant);
        questionOne = view.findViewById(R.id.question_one);
        questionTwo = view.findViewById(R.id.question_two);
        questionThree = view.findViewById(R.id.question_three);
        questionFour = view.findViewById(R.id.question_four);
        btnSubmit.setOnClickListener(this);
        birthday.setOnClickListener(this);
        anniversary.setOnClickListener(this);
    }

    private void getSurveyQuestions() {

        CommonRequestBean bean = new CommonRequestBean();
        bean.setDevice_type(Constants.DEVICE_TYPE);
        bean.setApp_version(Utils.getAppVersion(mContext));
        bean.setRestaurant_id(PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID));

        final String beanStr = new Gson().toJson(bean, CommonRequestBean.class);

        StringRequest request = new StringRequest(Method.POST, Constants.SURVEY_QUESTIONS_API, surveyQuestionSuccess(), restaurantError()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("data", beanStr);
                Log.i("RESTAURANT_PARAM", param.toString());
                return param;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES, Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);
    }

    private void validateFields() {

        if (radios[0].getCheckedRadioButtonId() == -1) {
            Toast.makeText(mContext, R.string.select_answer_one, Toast.LENGTH_SHORT).show();
            return;
        }

        if (radios[1].getCheckedRadioButtonId() == -1) {
            Toast.makeText(mContext, R.string.select_answer_two, Toast.LENGTH_SHORT).show();
            return;
        }

        if (radios[2].getCheckedRadioButtonId() == -1) {
            Toast.makeText(mContext, R.string.select_answer_three, Toast.LENGTH_SHORT).show();
            return;
        }

        if (radios[3].getCheckedRadioButtonId() == -1) {
            Toast.makeText(mContext, R.string.select_answer_four, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name.getText().toString().trim())) {
            Toast.makeText(mContext, R.string.enter_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mobileNumber.getText().toString().trim())) {
            if (TextUtils.isEmpty(email.getText().toString().trim())) {
                Toast.makeText(mContext, R.string.enter_either_mobile_number_or_mail_id, Toast.LENGTH_LONG).show();
                return;
            } else if (!Utils.Validate_EMail(mContext, email.getText().toString().trim(), getString(R.string.enter_valid_email))) {
                return;
            }
        } else if (TextUtils.isEmpty(email.getText().toString().trim())) {
            if (TextUtils.isEmpty(mobileNumber.getText().toString().trim())) {
                Toast.makeText(mContext, R.string.enter_either_mobile_number_or_mail_id, Toast.LENGTH_LONG).show();
                return;
            } else if (mobileNumber.getText().toString().trim().length() < 10) {
                Toast.makeText(mContext, R.string.enter_ten_digit_mobile_number, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!TextUtils.isEmpty(mobileNumber.getText().toString().trim().trim())) {
            if (mobileNumber.getText().toString().trim().length() < 10) {
                Toast.makeText(mContext, R.string.enter_ten_digit_mobile_number, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!TextUtils.isEmpty(email.getText().toString().trim())) {
            if (!Utils.Validate_EMail(mContext, email.getText().toString().trim(), getString(R.string.enter_valid_email))) {
                return;
            }
        }

        if (TextUtils.isEmpty(birthday.getText().toString().trim()) && TextUtils.isEmpty(anniversary.getText().toString().trim()) && !isDialogShown) {
            showConfirmationDialog(getString(R.string.wanna_tell_us_your_birthday));
            return;
        }

        /*
         * if (!Utils.isValidEmail(email.getText().toString().trim())) { Toast.makeText(mContext,
         * R.string.invalid_email_id, Toast.LENGTH_SHORT).show(); return; }
         */

        /*
         * if (TextUtils.isEmpty(mobileNumber.getText().toString())){ Toast.makeText(mContext, R.string.enter_mobile,
         * Toast.LENGTH_SHORT).show(); return; }
         *
         * if (mobileNumber.getText().toString().length()<10){ Toast.makeText(mContext,
         * R.string.enter_ten_digit_mobile_number, Toast.LENGTH_SHORT).show(); return; }
         *
         * if (!Utils.isValidEmail(email.getText().toString())) { Toast.makeText(mContext, R.string.enter_email,
         * Toast.LENGTH_SHORT).show(); return; }
         *
         * if (birthday.getText().toString().equals("Select Birthday")){ Toast.makeText(mContext,
         * R.string.select_birthday, Toast.LENGTH_SHORT).show(); return; }
         *
         * if (anniversary.getText().toString().equals("Select Anniversary")){ Toast.makeText(mContext,
         * R.string.select_anniversary, Toast.LENGTH_SHORT).show(); return; }
         */

        SurveyParam[] surveyList = new SurveyParam[4];

        if (answerArray != null && answerArray.length > 0) {
            for (int i = 0; i < surveyList.length; i++) {
                SurveyParam surveyParam = new SurveyParam();
                //surveyParam.setQuestionId((i + 1));
                surveyParam.setQuestionId(surveyQuestionArray[i].getQuestionId());
                surveyParam.setAnswerId((answerArray[i].getAnswerId()));
                surveyList[i] = surveyParam;
            }
        }

        SurveyRequestBean param = new SurveyRequestBean();
        param.setApp_version(Utils.getAppVersion(mContext));
        param.setSurveyList(surveyList);
        param.setName(name.getText().toString());
        param.setDevice_type(Constants.DEVICE_TYPE);
        String aniversaryStr = anniversary.getText().toString();
        String doa;

        if (!TextUtils.isEmpty(aniversaryStr))
            doa = Utils.ConvertDateFormat(aniversaryStr, "yyyy-MM-dd", "dd MMM, yyyy");
        else
            doa = "";

        param.setDoa(doa);

        String birthdayStr = birthday.getText().toString();
        String dob;

        // param.setDoa(anniversary.getText().toString());
        if (!TextUtils.isEmpty(birthdayStr))
            dob = Utils.ConvertDateFormat(birthdayStr, "yyyy-MM-dd", "dd MMM, yyyy");
        else
            dob = "";

        param.setDob(dob);
        // param.setDob(birthday.getText().toString());
        param.setMobile(mobileNumber.getText().toString());
        param.setEmailId(email.getText().toString());
        param.setRestaurant_id(PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID));

        final String paramStr = new Gson().toJson(param, SurveyRequestBean.class);
        Log.i("RECIPE_PARAM", paramStr.toString());

        if (Utils.isOnline(mContext)) {
            progressView.setVisibility(View.VISIBLE);
            updateRatingToServer(param);
        } else {
            // Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            // showNoConnectionDialog(R.string.please_check_your_internet_connection);
            // noInternetDialog = Utils.showNoConnectionDialog(mContext,R.string.please_check_your_internet_connection);
            // handleNoInternetCondition(noInternetDialog);

            DatabaseHelper.insertRateRestaurantRequestJSOnDate(mContext, paramStr.toString());
            // String rateRestaurantString = DatabaseHelper.getRateRestaurantRequestJSOnDate(mContext);
            // Log.d(TAG,"rateRestaurantString=" + rateRestaurantString);
            Toast.makeText(mContext, getString(R.string.you_rated_the_restaurant_successfully), Toast.LENGTH_SHORT).show();
            goToHomeActivity();
        }
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        getActivity().finish();
    }

    private void handleNoInternetCondition(final Dialog dialog) {
        if (dialog != null) {
            dialog.show();
            RobotoRegularTextView tvTryAgain = dialog.findViewById(R.id.tvTryAgain);
            RobotoRegularTextView ok = dialog.findViewById(R.id.ok);
            tvTryAgain.setOnClickListener(v -> {
                dialog.dismiss();
                validateFields();
            });

            ok.setOnClickListener(v -> dialog.dismiss());
        }
    }

    private com.android.volley.Response.Listener<String> surveyQuestionSuccess() {

        return new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response != null && response.length() > 0) {

                    try {
                        progressView.setVisibility(View.GONE);
                        Utils.longInfo(response);

                        SurveyQuestionResponse surveyModel = new Gson().fromJson(response, SurveyQuestionResponse.class);
                        if (surveyModel.getStatus().equals("true")) {
                            parseSurveyResponse(response, surveyModel, true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    protected void parseSurveyResponse(String response, SurveyQuestionResponse surveyModel, boolean isOnline) {
        if (isOnline) {
            DatabaseHelper.deleteSurveyQuestionsJson(mContext);
            DatabaseHelper.insertSurveyQuestionsJson(mContext, response);
        }

        surveyQuestionArray = surveyModel.getSurveyQuestionList();

        if (surveyQuestionArray != null && surveyQuestionArray.length > 0) {
            int totalQuestion = surveyQuestionArray.length;

            if (totalQuestion >= 4) {
                // if total question is equal to 4
                SurveyQuestionList surveyQuestionList1 = surveyQuestionArray[0];
                questionOne.setText(surveyQuestionList1.getQuestionText());

                SurveyQuestionList surveyQuestionList2 = surveyQuestionArray[1];
                questionTwo.setText(surveyQuestionList2.getQuestionText());

                SurveyQuestionList surveyQuestionList3 = surveyQuestionArray[2];
                questionThree.setText(surveyQuestionList3.getQuestionText());

                SurveyQuestionList surveyQuestionList4 = surveyQuestionArray[3];
                questionFour.setText(surveyQuestionList4.getQuestionText());
            } else if (totalQuestion == 3) {
                SurveyQuestionList surveyQuestionList1 = surveyQuestionArray[0];
                questionOne.setText(surveyQuestionList1.getQuestionText());

                SurveyQuestionList surveyQuestionList2 = surveyQuestionArray[1];
                questionTwo.setText(surveyQuestionList2.getQuestionText());

                SurveyQuestionList surveyQuestionList3 = surveyQuestionArray[2];
                questionThree.setText(surveyQuestionList3.getQuestionText());
            } else if (totalQuestion == 2) {
                SurveyQuestionList surveyQuestionList1 = surveyQuestionArray[0];
                questionOne.setText(surveyQuestionList1.getQuestionText());

                SurveyQuestionList surveyQuestionList2 = surveyQuestionArray[1];
                questionTwo.setText(surveyQuestionList2.getQuestionText());
            } else if (totalQuestion == 1) {
                SurveyQuestionList surveyQuestionList1 = surveyQuestionArray[0];
                questionOne.setText(surveyQuestionList1.getQuestionText());
            }
        }

        answerArray = surveyModel.getAnswerList();

        if (answerArray != null && answerArray.length > 0) {

            AnswerList answer1 = answerArray[0];
            ansOneRadioOne.setText(answer1.getAnswerText());
            ansTwoRadioOne.setText(answer1.getAnswerText());
            ansThreeRadioOne.setText(answer1.getAnswerText());
            ansFourRadioOne.setText(answer1.getAnswerText());

            AnswerList answer2 = answerArray[1];
            ansOneRadioTwo.setText(answer2.getAnswerText());
            ansTwoRadioTwo.setText(answer2.getAnswerText());
            ansThreeRadioTwo.setText(answer2.getAnswerText());
            ansFourRadioTwo.setText(answer2.getAnswerText());

            AnswerList answer3 = answerArray[2];
            ansOneRadioThree.setText(answer3.getAnswerText());
            ansTwoRadioThree.setText(answer3.getAnswerText());
            ansThreeRadioThree.setText(answer3.getAnswerText());
            ansFourRadioThree.setText(answer3.getAnswerText());

            AnswerList answer4 = answerArray[3];
            ansOneRadioFour.setText(answer4.getAnswerText());
            ansTwoRadioFour.setText(answer4.getAnswerText());
            ansThreeRadioFour.setText(answer4.getAnswerText());
            ansFourRadioFour.setText(answer4.getAnswerText());

        }
    }

    private void updateRatingToServer(SurveyRequestBean param) {
        final String paramStr = new Gson().toJson(param, SurveyRequestBean.class);

        StringRequest request = new StringRequest(Method.POST, Constants.RATE_RESTAURANT_API, restaurantSuccess(), restaurantError()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("data", paramStr);
                Log.i("RESTAURANT_PARAM", param.toString());
                return param;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES, Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);
    }

    private com.android.volley.Response.Listener<String> restaurantSuccess() {

        return new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response != null && response.length() > 0) {

                    progressView.setVisibility(View.GONE);
                    Utils.longInfo(response);

                    try {
                        JSONObject obj = new JSONObject(response);
                        String status = obj.getString("status");
                        if (status != null && status.equals("true")) {
                            // Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            Toast.makeText(mContext, getString(R.string.you_rated_the_restaurant_successfully), Toast.LENGTH_SHORT).show();
                            goToHomeActivity();
                        } else {
                            Toast.makeText(mContext, obj.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener restaurantError() {
        return new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressView.setVisibility(View.GONE);
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }

        };
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_submit:
                validateFields();
                break;

            case R.id.birthday:

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        String dob = Utils.ConvertDateFormat(selectedDate, "dd MMM, yyyy", "dd-MM-yyyy");
                        birthday.setText(dob);

                        // birthday.setText(year + "-"
                        // + (monthOfYear + 1) + "-" + dayOfMonth);

                    }
                }, mYear, mMonth, mDay);
                dpd.show();
                dpd.getDatePicker().setMaxDate(new Date().getTime());
                if (dpd != null) {
                    dpd.getDatePicker().setCalendarViewShown(false);
                }
                break;

            case R.id.anniversary:
                final Calendar c1 = Calendar.getInstance();
                int mYear1 = c1.get(Calendar.YEAR);
                int mMonth1 = c1.get(Calendar.MONTH);
                int mDay1 = c1.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd1 = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // anniversary.setText(year + "-"
                        // + (monthOfYear + 1) + "-" + dayOfMonth);
                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        String aniversary = Utils.ConvertDateFormat(selectedDate, "dd MMM, yyyy", "dd-MM-yyyy");
                        anniversary.setText(aniversary);

                    }
                }, mYear1, mMonth1, mDay1);
                dpd1.show();
                dpd1.getDatePicker().setMaxDate(new Date().getTime());
                if (dpd1 != null) {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                }
                break;

        }
    }

    private void showConfirmationDialog(String messsage) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.birthday_or_aniversary_alert_dialog_view);
        RobotoRegularTextView messageView = dialog.findViewById(R.id.message);
        messageView.setText(messsage);
        RobotoRegularTextView noThanks = dialog.findViewById(R.id.tvNoThanks);
        RobotoRegularTextView ok = dialog.findViewById(R.id.ok);
        noThanks.setOnClickListener(v -> {
            dialog.dismiss();
            isDialogShown = true;
        });

        ok.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}