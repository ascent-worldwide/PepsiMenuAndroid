package com.clairvoyant.naijamenu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.clairvoyant.naijamenu.bean.CommonRequestBean;
import com.clairvoyant.naijamenu.bean.QuizPromotionResponse;
import com.clairvoyant.naijamenu.fonts.RobotoRegularButton;
import com.clairvoyant.naijamenu.utils.AppController;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class QuizPromotionActivity extends AppCompatActivity {

    private Context mContext;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        if (!Utils.isOnline(mContext)) {
            setContentView(R.layout.no_network_activity);
            Utils.setOrientation(mContext);
            RobotoRegularButton tryAgain = findViewById(R.id.try_again);
            tryAgain.setOnClickListener(arg0 -> reload());

        } else {
            setContentView(R.layout.activity_quiz_promotion);
            Utils.setOrientation(mContext);
            getQuizPromo();
            Intent intent = getIntent();
            if (intent != null) {
                image = findViewById(R.id.imageView);
                TextView startQuiz = findViewById(R.id.start_quiz);
                final int menuId = getIntent().getIntExtra("MENU", 0);
                startQuiz.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(QuizPromotionActivity.this, MainActivity.class);
                        intent.putExtra("MENU", menuId);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }
    }

    private void getQuizPromo() {

        CommonRequestBean requestBean = new CommonRequestBean();
        requestBean.setApp_version(Utils.getAppVersion(mContext));
        requestBean.setDevice_type(Constants.DEVICE_TYPE);
        requestBean.setRestaurant_id(PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID));

        final String param = new Gson().toJson(requestBean, CommonRequestBean.class);

        StringRequest request = new StringRequest(Method.POST, Constants.QUIZ_PROMOTION_API, promotionSuccess(), promotionError()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("data", param);
                Log.i("PROMOTION_PARAM", param.toString());
                return params;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES, Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);
    }

    private com.android.volley.Response.Listener<String> promotionSuccess() {

        return new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response != null && response.length() > 0) {

//					progressView.setVisibility(View.GONE);
                    Utils.longInfo(response);

                    try {
                        Gson gson = new Gson();
                        QuizPromotionResponse quizResponse = gson.fromJson(response, QuizPromotionResponse.class);
                        if (quizResponse.getStatus().equals("true")) {
                            String url = quizResponse.getContest_banner();
                            // Picasso.with(mContext).load(url).placeholder(R.drawable.promo_placeholder).into(image);
                            Utils.renderImage(mContext, url, image);
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener promotionError() {
        return new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//				progressView.setVisibility(View.GONE);
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void reload() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }
}