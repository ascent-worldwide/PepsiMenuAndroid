package com.clairvoyant.naijamenu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.clairvoyant.naijamenu.bean.QuizWinnerResponse;
import com.clairvoyant.naijamenu.bean.WinnerDetailRequest;
import com.clairvoyant.naijamenu.fonts.RobotoRegularButton;
import com.clairvoyant.naijamenu.fonts.RobotoRegularEditText;
import com.clairvoyant.naijamenu.utils.AppController;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class GamePlayRegistration extends AppCompatActivity {

    private static String TAG = GamePlayRegistration.class.getSimpleName();

    private RobotoRegularEditText name, email, phoneNumber;
    private RobotoRegularButton btnSubmit;
    private Context mContext;
    private int contest_id = 0;
    private int prizeId = 0;
    private RelativeLayout progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, " onCreate()");
        setContentView(R.layout.activity_game_registration);
        mContext = this;
        Utils.setOrientation(mContext);
        name = findViewById(R.id.et_name);
        email = findViewById(R.id.et_email);
        phoneNumber = findViewById(R.id.et_phone_number);
        btnSubmit = findViewById(R.id.btn_submit);
        progressView = findViewById(R.id.progress_view_quiz_winner);
        Intent intent = getIntent();
        if (intent != null) {
            contest_id = intent.getIntExtra("CONTEST_ID", 0);
            prizeId = intent.getIntExtra("PRIZE_ID", 0);
        }

        btnSubmit.setOnClickListener(v -> validateFields());
    }

    private void validateFields() {

        if (TextUtils.isEmpty(name.getText().toString().trim())) {
            Toast.makeText(mContext, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email.getText().toString().trim())) {
            Toast.makeText(mContext, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.Validate_EMail(mContext, email.getText().toString().trim(), "Enter Valid Email")) {
            return;
        }
		
		/*if (!Utils.isValidEmail(email.getText().toString().trim())) {
			
		}*/

        if (TextUtils.isEmpty(phoneNumber.getText().toString().trim())) {
            Toast.makeText(mContext, "Enter Phone Number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.getText().toString().trim().length() < 10) {
            Toast.makeText(mContext, "Enter 10 digit Phone Number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Utils.isOnline(mContext)) {
            progressView.setVisibility(View.VISIBLE);
            pushToServer(Constants.WINNER_DETAIL_API);
        }
    }

    private void pushToServer(String api) {

        String appVersion = Utils.getAppVersion(mContext);
        WinnerDetailRequest quizParam = new WinnerDetailRequest();

        quizParam.setApp_version(appVersion);
        quizParam.setDevice_type(Constants.DEVICE_TYPE);
        quizParam.setName(name.getText().toString());
        quizParam.setContest_id(contest_id);
        quizParam.setRestaurant_id(PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID));
        quizParam.setEmail(email.getText().toString());
        quizParam.setMobile(phoneNumber.getText().toString());
        quizParam.setPrizeId(prizeId);

        Gson gson = new Gson();
        final String params = gson.toJson(quizParam, WinnerDetailRequest.class);

        StringRequest request = new StringRequest(Method.POST, api, winnerSuccess(), winnerError()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("data", params);
                Log.i("LOGIN_PARAM", param.toString());
                return param;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES, Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);
    }

    private com.android.volley.Response.Listener<String> winnerSuccess() {

        return new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    if (response != null && response.length() > 0) {
                        progressView.setVisibility(View.GONE);
                        Utils.longInfo(response);
                        QuizWinnerResponse responseBean = new Gson().fromJson(response, QuizWinnerResponse.class);
                        if (responseBean.getStatus().equals("true")) {
                            Toast.makeText(mContext, getString(R.string.your_detail_has_been_uploaded), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(mContext, HomeActivity.class));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener winnerError() {
        return new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressView.setVisibility(View.GONE);
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }

        };
    }
}