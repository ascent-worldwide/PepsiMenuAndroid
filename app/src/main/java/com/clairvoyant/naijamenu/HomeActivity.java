package com.clairvoyant.naijamenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.bumptech.glide.Glide;
import com.clairvoyant.naijamenu.bean.AppVersionResponseBean;
import com.clairvoyant.naijamenu.bean.CommonRequestBean;
import com.clairvoyant.naijamenu.bean.PromotionResponseBean;
import com.clairvoyant.naijamenu.fonts.RobotoRegularTextView;
import com.clairvoyant.naijamenu.utils.AppController;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements OnClickListener {

    private Context mContext;
    private ImageView ivRestaurantIcon;
    private RelativeLayout progressView;
    private boolean isPromoBannerAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;

        Utils.setOrientation(mContext);

        LinearLayout menuBox = (LinearLayout) findViewById(R.id.menu_box);
        LinearLayout challengeBox = (LinearLayout) findViewById(R.id.challenge_box);
        RelativeLayout recipeBox = (RelativeLayout) findViewById(R.id.recipe_box);
        RelativeLayout restaurantBox = (RelativeLayout) findViewById(R.id.restaurant_box);
        ivRestaurantIcon = (ImageView) findViewById(R.id.ivRestaurantIcon);

        menuBox.setOnClickListener(this);
        challengeBox.setOnClickListener(this);
        recipeBox.setOnClickListener(this);
        restaurantBox.setOnClickListener(this);

        String restaurantHomeImg = PreferencesUtils.getString(mContext, Constants.RESTAURANT_HOMESCREEN_IMG);
        if (!TextUtils.isEmpty(restaurantHomeImg)) {
            Glide.with(mContext).load(PreferencesUtils.getString(mContext, Constants.RESTAURANT_LOGO)).asBitmap().centerCrop().placeholder(R.drawable.pepsi_logo).fitCenter().into(ivRestaurantIcon);
        }

        if (Utils.isOnline(mContext)) {
            progressView = (RelativeLayout) findViewById(R.id.progress_view_promotion);
            getPromotionalBanners(Constants.PROMOTIONAL_BANNER_API);
        }
    }

    @Override
    public void onClick(View view) {

        Intent intent = null;

        String videoUrl = PreferencesUtils.getString(mContext, Constants.VIDEO_URL);
        boolean isVideoDownloaded = PreferencesUtils.getBoolean(mContext, Constants.IS_VIDEO_DOWNLOADED_STR);
        // if(!TextUtils.isEmpty(videoUrl) && !Constants.IS_VIDEO_DOWNLOADED)
        // {
        // Toast.makeText(mContext, getString(R.string.please_wait_while),
        // Toast.LENGTH_LONG).show();
        // System.exit(0);
        // }

        switch (view.getId()) {

            case R.id.menu_box:
                if (!isPromoBannerAvailable) {
                    // false response from server
                    if (!TextUtils.isEmpty(videoUrl) && !isVideoDownloaded) {
                        Toast.makeText(mContext, getString(R.string.please_wait_while), Toast.LENGTH_LONG).show();
                        break;
                    } else if (!TextUtils.isEmpty(videoUrl) && isVideoDownloaded) {
                        intent = new Intent(mContext, BrandPromotionActivity.class);
                        intent.putExtra("MENU", 1);
                    } else {
                        intent = new Intent(mContext, MainActivity.class);
                        intent.putExtra("MENU", 1);
                    }

                } else {
                    // intent = new Intent(mContext, PromotionActivity.class);
                    if (!TextUtils.isEmpty(videoUrl) && !isVideoDownloaded) {
                        Toast.makeText(mContext, getString(R.string.please_wait_while), Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        intent = new Intent(mContext, BrandPromotionActivity.class);
                        intent.putExtra("MENU", 1);
                    }
                }
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.challenge_box:
                // intent = new Intent (mContext, QuizPromotionActivity.class);
                intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("MENU", 2);
                startActivity(intent);
                break;

            case R.id.recipe_box:
                // intent = new Intent (mContext, PromotionActivity.class);
                intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("MENU", 3);
                startActivity(intent);
                break;

            case R.id.restaurant_box:
                // intent = new Intent (mContext, PromotionActivity.class);
                intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("MENU", 4);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        showConfirmationDialog(R.string.are_you_sure_you_want_to_exit);
    }

    private void getPromotionalBanners(String api) {

        CommonRequestBean requestBean = new CommonRequestBean();
        requestBean.setApp_version(Utils.getAppVersion(mContext));
        requestBean.setDevice_type(Constants.DEVICE_TYPE);
        requestBean.setRestaurant_id(PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID));

        final String param = new Gson().toJson(requestBean, CommonRequestBean.class);

        StringRequest request = new StringRequest(Method.POST, api, promotionSuccess(), promotionError()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("data", param);
                Log.i("PROMOTION_PARAM", param.toString());
                return params;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES,
                Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);
    }

    private com.android.volley.Response.Listener<String> promotionSuccess() {

        return new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response != null && response.length() > 0) {

                    getAppVersionFromAPI(Constants.APP_VERSION_API);

                    progressView.setVisibility(View.GONE);
                    Utils.longInfo(response);

                    try {
                        Gson gson = new Gson();
                        PromotionResponseBean promotionResponse = gson.fromJson(response, PromotionResponseBean.class);

                        PreferencesUtils.putString(mContext, Constants.IS_PROMO_BANNER_AVAILABLE,
                                promotionResponse.getStatus());
                        if (promotionResponse.getStatus().equals("false")) {
                            // false response from server
                            isPromoBannerAvailable = false;
                            /*
                             * Intent intent = new Intent(mContext,
                             * MainActivity.class); intent.putExtra("MENU", 1);
                             * startActivity(intent); finish();
                             */
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
                progressView.setVisibility(View.GONE);
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }

        };
    }

    /**
     * Method to get the app version from the api
     *
     * @param api
     */
    private void getAppVersionFromAPI(final String api) {

        CommonRequestBean requestBean = new CommonRequestBean();
        requestBean.setApp_version(Utils.getAppVersion(mContext));
        requestBean.setDevice_type(Constants.DEVICE_TYPE);
        requestBean.setRestaurant_id(PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID));

        final String param = new Gson().toJson(requestBean, CommonRequestBean.class);

        StringRequest request = new StringRequest(Method.POST, api, appVersionSuccess(), appVersionError()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("data", param);
                Log.i("APP_VERSION_PARAM", api + "\n" + param.toString());
                return params;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES,
                Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);
    }

    private com.android.volley.Response.Listener<String> appVersionSuccess() {
        return new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    Utils.longInfo(response);
                    try {
                        Gson gson = new Gson();
                        AppVersionResponseBean appVersionResponse = gson.fromJson(response, AppVersionResponseBean.class);
                        if (appVersionResponse.getStatus().equals("true")) {
                            String strCurrentVersion = Utils.getAppVersion(mContext);
                            String strUpdatedVersion = appVersionResponse.getApp_version();
                            if (strCurrentVersion != null && strUpdatedVersion != null) {
                                double currentVersion = Double.parseDouble(strCurrentVersion);
                                double updatedVersion = Double.parseDouble(strUpdatedVersion);
                                Log.i("APP_VERSIONS", "current : " + currentVersion + " : " + "updated : " + updatedVersion);
                                if (updatedVersion > currentVersion) {
                                    showUpdateAppDialog();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        };
    }

    private com.android.volley.Response.ErrorListener appVersionError() {
        return new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressView.setVisibility(View.GONE);
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }

        };
    }

    private void showConfirmationDialog(int resource) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.app_exit_dialog_view);
        RobotoRegularTextView messageView = (RobotoRegularTextView) dialog.findViewById(R.id.message);
        messageView.setText(resource);
        RobotoRegularTextView cancel = (RobotoRegularTextView) dialog.findViewById(R.id.cancel);
        RobotoRegularTextView ok = (RobotoRegularTextView) dialog.findViewById(R.id.ok);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    /**
     * Method to show the popup for upgrade application to a higher version.
     */
    private void showUpdateAppDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.app_exit_dialog_view);
        RobotoRegularTextView messageView = (RobotoRegularTextView) dialog.findViewById(R.id.message);
        messageView.setText(R.string.updateAppPopupTxt);
        RobotoRegularTextView cancel = (RobotoRegularTextView) dialog.findViewById(R.id.cancel);
        RobotoRegularTextView ok = (RobotoRegularTextView) dialog.findViewById(R.id.ok);
        ok.setText("Update");
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String appPackageName = mContext.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        dialog.show();
    }

}