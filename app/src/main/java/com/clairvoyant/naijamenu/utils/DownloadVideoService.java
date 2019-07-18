package com.clairvoyant.naijamenu.utils;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.clairvoyant.naijamenu.R;
import com.clairvoyant.naijamenu.bean.BrandPromotionRequestBean;
import com.clairvoyant.naijamenu.bean.BrandPromotionResponseBean;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class DownloadVideoService extends IntentService {
    public DownloadVideoService() {
        super("DownloadVideoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        callApi(Constants.GET_BRAND_PROMOTION);
    }

    private void callApi(String apiUrl) {
        if (Utils.isOnline(this)) {
            try {
                BrandPromotionRequestBean bean = new BrandPromotionRequestBean();
                String appVersion = Utils.getAppVersion(this);
                bean.setApp_version(appVersion);
                bean.setDevice_type(Constants.DEVICE_TYPE);
                bean.setRestaurant_id(PreferencesUtils.getInt(this, Constants.RESTAURANT_ID));
//		    	bean.setBrandVideoUrlVersion(PreferencesUtils.getInt(this, Constants.BRAND_VIDEOS_URL_VERSION));
                bean.setBrandVideoUrlVersion(1);

                final String params = new Gson().toJson(bean, BrandPromotionRequestBean.class);

                StringRequest request = new StringRequest(Method.POST, apiUrl, success(), error()) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> param = new HashMap<>();
                        param.put("data", params);
                        Log.i("GET BRAND PROMOTION Request", param.toString());
                        return param;
                    }
                };

                RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES, Constants.VOLLEY_BACKUP_MULT);
                request.setRetryPolicy(retryPolicy);
                AppController.getInstance().addToRequestQueue(request);

            } catch (Exception ex) {
                Log.e("Exception e : ", ex.getStackTrace().toString());
            }
        }
    }

    private com.android.volley.Response.Listener<String> success() {

        return new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response != null && response.length() > 0) {
                    Utils.longInfo(response);

                    try {
                        BrandPromotionResponseBean brandResponse = new Gson().fromJson(response, BrandPromotionResponseBean.class);
                        if (brandResponse != null && brandResponse.isStatus()) {
                            String videoUrl = brandResponse.getBrandVideoUrl();
                            if (!TextUtils.isEmpty(videoUrl)) {
                                int localVideoVersion = PreferencesUtils.getInt(DownloadVideoService.this, Constants.BRAND_VIDEOS_URL_VERSION);

                                if (localVideoVersion < brandResponse.getBrandVideoUrlVersion()) {
                                    int index = videoUrl.lastIndexOf("/");
                                    index++;
                                    String fileName = videoUrl.substring(index, videoUrl.length());
                                    new DownloadVideoTask(DownloadVideoService.this, videoUrl, fileName, brandResponse.getBrandVideoUrlVersion()).execute("");
                                }

                            }
                        } else {
                            //false response from server
                            System.out.println("false response from server when consuming GetBrandPromotion API");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener error() {
        return new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(R.string.network_failure);
            }
        };
    }
}