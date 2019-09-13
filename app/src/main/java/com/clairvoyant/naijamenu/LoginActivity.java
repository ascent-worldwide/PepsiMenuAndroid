package com.clairvoyant.naijamenu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.clairvoyant.naijamenu.bean.LoginParamBean;
import com.clairvoyant.naijamenu.bean.LoginResponseBean;
import com.clairvoyant.naijamenu.fonts.RobotoLightEditText;
import com.clairvoyant.naijamenu.fonts.RobotoRegularButton;
import com.clairvoyant.naijamenu.utils.AppController;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.DownloadVideoTask;
import com.clairvoyant.naijamenu.utils.HttpsTrustManager;
import com.clairvoyant.naijamenu.utils.KeyboardUtil;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static String TAG = LoginActivity.class.getSimpleName();
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2000;

    private RobotoLightEditText etUsername, etPassword;
    private RobotoRegularButton btnSignIn;
    private Context mContext;
    private RelativeLayout progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        Utils.setOrientation(mContext);

//        int orientation = PreferencesUtils.getInt(mContext, Constants.ORIENTATION);
//        if(orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
//        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//        else
//        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        //check if GCM token is empty
//        if (TextUtils.isEmpty(Utils.getGCMId(mContext)))
//        {
        // GCM Registration
//         startService(new Intent(this, DownloadVideoService.class));
//        }

        boolean loggineIn = PreferencesUtils.getBoolean(mContext, Constants.LOGGED);

        if (loggineIn) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        wakeupViews();
        checkForStoragePermission();
    }

    private void wakeupViews() {

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        progressView = findViewById(R.id.progress_view_login);
        btnSignIn.setOnClickListener(v -> {
            if (Utils.isOnline(mContext)) {
                initializeLogin();
            } else {
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeLogin() {

        if (!etUsername.getText().toString().equalsIgnoreCase("") && !etPassword.getText().toString().equalsIgnoreCase("")) {

            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (Utils.isOnline(mContext)) {
                KeyboardUtil.hideKeyboard(LoginActivity.this);
                progressView.setVisibility(View.VISIBLE);
                pushToServer(Constants.LOGIN_API, username, password);
            }
        } else {
            Toast.makeText(this, R.string.login_password_mandatory, Toast.LENGTH_LONG).show();
        }
    }

    private void pushToServer(final String api, final String username, final String password) {

        String appVersion = Utils.getAppVersion(mContext);
        LoginParamBean loginParam = new LoginParamBean();

        loginParam.setApp_version(appVersion);
        loginParam.setDevice_type(Constants.DEVICE_TYPE);
        loginParam.setUsername(username);
        loginParam.setPassword(password);
//    	loginParam.setDevice_id("1234567891234567");
//    	loginParam.setMake(Utils.getMake());
//    	loginParam.setModel(Utils.getModel());
//    	loginParam.setDevice_gcm_token(Utils.getGCMId(mContext));

        HttpsTrustManager.allowAllSSL();


        Gson gson = new Gson();
        final String params = gson.toJson(loginParam, LoginParamBean.class);

        StringRequest request = new StringRequest(Method.POST, api, loginSuccess(), loginError()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("data", params);
                Log.i("LOGIN_PARAM", param.toString());
                return param;
            }

		/*	@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String,String> headers = new HashMap<String, String>();
				headers.put("Content-Type", "application/json");
				return headers;
			}*/
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES, Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);
    }

    private com.android.volley.Response.Listener<String> loginSuccess() {

        return new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response != null && response.length() > 0) {

                    progressView.setVisibility(View.GONE);


                    //String resp = response.substring(1,response.length()-1);

                    try {
                        JSONObject jsonObject = new JSONObject(response.replace("\n", ""));
                        Log.d("LOGIN_RESPONSE", " " + jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Utils.longInfo(response);

                    try {
                        Gson gson = new Gson();
                        LoginResponseBean loginResponse = gson.fromJson(response, LoginResponseBean.class);
                        if (loginResponse != null && loginResponse.getStatus().equals("true")) {
                            if (!TextUtils.isEmpty(loginResponse.getThemeColor()))
                                PreferencesUtils.putString(mContext, Constants.RESTAURANT_THEME, loginResponse.getThemeColor());
                            else
                                PreferencesUtils.putString(mContext, Constants.RESTAURANT_THEME, "#f25a43");

//                            PreferencesUtils.putInt(mContext, Constants.ORIENTATION, loginResponse.getOrientation());
                            PreferencesUtils.putString(mContext, Constants.RESTAURANT_HOMESCREEN_IMG, loginResponse.getRestaurant_home_screen_img());
                            PreferencesUtils.putString(mContext, Constants.RESTAURANT_BACKGROUND_IMG_LANDSCAPE, loginResponse.getRestaurant_menu_landscape_img());
                            PreferencesUtils.putString(mContext, Constants.RESTAURANT_BACKGROUND_IMG_PORTRAIT, loginResponse.getRestaurant_menu_portrait_img());

                            PreferencesUtils.putString(mContext, Constants.PASSWORD, etPassword.getText().toString().trim());
                            Log.d(TAG, "current password=" + etPassword.getText().toString().trim());
                            PreferencesUtils.putBoolean(mContext, Constants.LOGGED, true);
                            int restaurantId = loginResponse.getRestaurant_id();
                            PreferencesUtils.putInt(mContext, Constants.RESTAURANT_ID, restaurantId);
                            PreferencesUtils.putString(mContext, Constants.RESTAURANT_NAME, loginResponse.getRestaurant_name());
                            PreferencesUtils.putString(mContext, Constants.RESTAURANT_LOGO, loginResponse.getRestaurant_logo());

                            // below variable will be used to know whether to display restaurant name along with restaurant logo in toolbar of the app.
                            //0 mean not to display text, and 1 mean to display reataurant name along with reataurant logo
                            int restaurantLogoWithText = loginResponse.getRestaurant_logo_wid_text();
                            PreferencesUtils.putInt(mContext, Constants.RESTAURANT_LOGO_WITH_TEXT, restaurantLogoWithText);

                            //get video url and video version
                            String videoUrl = loginResponse.getBrandVideoUrl();

                            if (!TextUtils.isEmpty(videoUrl)) {
                                int brandVideoUrlVersion = loginResponse.getBrandVideoUrlVersion();

                                PreferencesUtils.putString(mContext, Constants.VIDEO_URL, videoUrl);


                                int index = videoUrl.lastIndexOf("/");
                                index++;

                                String fileName = videoUrl.substring(index, videoUrl.length());
                                Log.d(TAG, "newStr=" + fileName);

                                new DownloadVideoTask(mContext, videoUrl, fileName, brandVideoUrlVersion).execute("");
                            }

                            Toast.makeText(mContext, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(mContext, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(mContext, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener loginError() {
        return new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressView.setVisibility(View.GONE);
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public  void checkForStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                return;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        } else {
            Toast.makeText(mContext, R.string.write_external_storage_permission, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}