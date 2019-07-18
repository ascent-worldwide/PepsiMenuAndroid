package com.clairvoyant.naijamenu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.clairvoyant.naijamenu.bean.ConfirmPasswordRequestBean;
import com.clairvoyant.naijamenu.bean.ConfirmPasswordResponseBean;
import com.clairvoyant.naijamenu.fonts.RobotoRegularEditText;
import com.clairvoyant.naijamenu.fonts.RobotoRegularTextView;
import com.clairvoyant.naijamenu.interfaces.IPasswordConfirmListener;
import com.clairvoyant.naijamenu.utils.AppController;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used to create a dialog password confirmation
 *
 * @author Home
 */
public class PasswordConfirmationDialog {

    private View view;
    private int position;
    private Context mContext;
    private IPasswordConfirmListener mPasswordConfirmListener;
    private Dialog dialog;
    private ProgressBar progressBar;
    private RelativeLayout layoutDialogContainer;

    public PasswordConfirmationDialog(Context pContext, View view, int position) {
        this.mContext = pContext;
        this.mPasswordConfirmListener = (IPasswordConfirmListener) pContext;
        this.view = view;
        this.position = position;
        showPassConfirmDialogForUpdateMenu(view, position);
    }

    /**
     * Method used to show the password
     *
     * @param view
     * @param position
     */
    private void showPassConfirmDialogForUpdateMenu(final View view, final int position) {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.logout_alert_dialog_view);
        progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
        RobotoRegularTextView messageView = (RobotoRegularTextView) dialog.findViewById(R.id.message);
        messageView.setText(mContext.getResources().getString(R.string.please_confirm_password_to_continue));
        RobotoRegularTextView cancel = (RobotoRegularTextView) dialog.findViewById(R.id.cancel);
        RobotoRegularTextView ok = (RobotoRegularTextView) dialog.findViewById(R.id.ok);
        final RobotoRegularEditText etPassword = (RobotoRegularEditText) dialog.findViewById(R.id.etPassword);
        // layout for dialog container
        layoutDialogContainer = (RelativeLayout) dialog.findViewById(R.id.layoutDialogContainer);

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                mPasswordConfirmListener.onDialogCancel();
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(dialog);
                String password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
                    return;
                }

                layoutDialogContainer.setAlpha(.6f);
                progressBar.setVisibility(View.VISIBLE);

                /**
                 *
                 * perform the password checking task
                 *
                 */

                int restaurantId = PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID);
                pushToServer(Constants.PASSWORD_CONFIRMATION_API, restaurantId, password);

            }
        });
        dialog.show();
        hideSoftKeyboard(dialog);
    }

    /**
     * Method to start the API task
     *
     * @param api
     * @param restaurant_id
     * @param password
     */
    private void pushToServer(final String api, int restaurant_id, String password) {

        String appVersion = Utils.getAppVersion(mContext);
        ConfirmPasswordRequestBean mConfirmPasswordRequestBean = new ConfirmPasswordRequestBean();

        mConfirmPasswordRequestBean.setApp_version(appVersion);
        mConfirmPasswordRequestBean.setDevice_type(Constants.DEVICE_TYPE);
        mConfirmPasswordRequestBean.setRestaurant_id(restaurant_id);
        mConfirmPasswordRequestBean.setPassword(password);

        Gson gson = new Gson();
        final String params = gson.toJson(mConfirmPasswordRequestBean, ConfirmPasswordRequestBean.class);
        StringRequest request = new StringRequest(Method.POST, api, confirmPasswordSuccess(), confirmPasswordError()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("data", params);
                Log.i("CONFIRM_PASS_PARAM", api + "\n" + param.toString());
                return param;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES,
                Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);
    }

    private com.android.volley.Response.Listener<String> confirmPasswordSuccess() {
        return new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    progressBar.setVisibility(View.INVISIBLE);
                    layoutDialogContainer.setAlpha(1f);
                    Utils.longInfo(response);
                    try {
                        Gson gson = new Gson();
                        ConfirmPasswordResponseBean confirmPasswordResponseBean = gson.fromJson(response, ConfirmPasswordResponseBean.class);
                        if (confirmPasswordResponseBean != null && confirmPasswordResponseBean.getStatus().equals("true")) {
                            // SUCCESS
                            int updatedMenuVersion = confirmPasswordResponseBean.getMenu_version();

                            dialog.dismiss();
                            mPasswordConfirmListener.onDialogSuccess(view, position, updatedMenuVersion);
                        } else {
                            // FAILURE
                            Toast.makeText(mContext, confirmPasswordResponseBean.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener confirmPasswordError() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                layoutDialogContainer.setAlpha(1f);
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * Hides the soft keyboard
     */
    private void hideSoftKeyboard(Dialog dialog) {
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void hideSoftKeyboard() {
        if (((Activity) mContext).getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(((Activity) mContext).getCurrentFocus().getWindowToken(), 0);
        }
    }
}
