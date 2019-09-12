package com.clairvoyant.naijamenu.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.clairvoyant.naijamenu.bean.RateRecipeBean;
import com.clairvoyant.naijamenu.bean.RateRestaurantBean;
import com.pepsi.database.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetWorkChangeReceiver extends BroadcastReceiver {

    private static String TAG = NetWorkChangeReceiver.class.getSimpleName();
    private String id = null;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        boolean isWifiConnected = false;
        boolean isMobileConnected = false;
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (networkInfo != null)
                isWifiConnected = networkInfo.isConnected();

            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (networkInfo != null)
                isMobileConnected = networkInfo.isConnected();

            if (isMobileConnected || isWifiConnected) {
//				Intent documentUplaodService =  new Intent(context,RateRecipeOrRestaurant.class);
//				context.startService(documentUplaodService);
                if (Utils.isOnline(context)) {
                    syncRateRecipeData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void syncRateRestaurantData() {
        ArrayList<RateRestaurantBean> rateRestaurantRequestJSONList = DatabaseHelper.getRateRestaurantRequestJSOnDate(mContext);
        if (rateRestaurantRequestJSONList != null && rateRestaurantRequestJSONList.size() > 0) {
            for (RateRestaurantBean rateRestaurantBean : rateRestaurantRequestJSONList) {
                id = rateRestaurantBean.getId();
                String requestData = rateRestaurantBean.getRateRestaurantJSONRequest();
                Log.d(TAG, "id=" + id);
                Log.d(TAG, "data=" + requestData);
                updateRestaurantRatingToServer(requestData);
            }
        } else {
            Log.d(TAG, "no data to sync in rate Restaurant feedback");
        }
    }

    private void updateRestaurantRatingToServer(final String requestData) {
        StringRequest request = new StringRequest(Method.POST, Constants.RATE_RESTAURANT_API, restaurantSuccess(), restaurantError()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("data", requestData);
                Log.i("RESTAURANT_PARAM", param.toString());
                return param;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES, Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);

    }

    private void syncRateRecipeData() {
        ArrayList<RateRecipeBean> rateRecipeRequestJSONList = DatabaseHelper.getRateRecipeRequestJSOnDate(mContext);
        if (rateRecipeRequestJSONList != null && rateRecipeRequestJSONList.size() > 0) {
            for (RateRecipeBean rateRecipeBean : rateRecipeRequestJSONList) {
                id = rateRecipeBean.getId();
                String requestData = rateRecipeBean.getRateRecipeJSONRequest();
                Log.d(TAG, "id=" + id);
                Log.d(TAG, "data=" + rateRecipeBean.getRateRecipeJSONRequest());
                updateRatingToServer(requestData);
            }
        } else {
            Log.d(TAG, "no data to sync in recipe feedback");
        }

        syncRateRestaurantData();
    }

    private void updateRatingToServer(final String requestData) {
        StringRequest request = new StringRequest(Method.POST, Constants.RATE_RECIPE_API, recipeSuccess(), recipeError()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("data", requestData);
                Log.i(TAG, param.toString());
                return param;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES, Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);

    }

    private com.android.volley.Response.Listener<String> recipeSuccess() {

        return new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response != null && response.length() > 0) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String status = obj.getString("status");
                        if (status != null && status.equals("true")) {
                            Log.d(TAG, "id on success=" + id);
                            DatabaseHelper.deleteRateRecipeRequestJSOnDateById(mContext, id);
                        } else {
                            //Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "false response from server while submitting recipe rating=" + obj.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener recipeError() {
        return new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "volley error while submitting recipe rating=" + error);
            }

        };
    }

    private com.android.volley.Response.Listener<String> restaurantSuccess() {

        return new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response != null && response.length() > 0) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String status = obj.getString("status");
                        if (status != null && status.equals("true")) {
                            Log.d(TAG, "id on success=" + id);
                            DatabaseHelper.deleteRateRestaurantRequestJSOnDateById(mContext, id);
                        } else {
                            Log.d(TAG, "false response from server while submitting reataurant rating=" + obj.getString("error"));
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
                Log.d(TAG, "volley error in submitting restaurant rating=" + error);
            }

        };
    }
}