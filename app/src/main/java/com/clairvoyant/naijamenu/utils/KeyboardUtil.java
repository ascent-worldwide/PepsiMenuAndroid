package com.clairvoyant.naijamenu.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardUtil {

    private static final String TAG = KeyboardUtil.class.getName();

    /**
     * This implementation hide keyboard from activity
     *
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        try {
            View view = activity.getWindow().getCurrentFocus();
            if (view == null) {
                return;
            }
            InputMethodManager inputManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            // Ignore exceptions if any
            Log.e(TAG, e.toString(), e);
        }

    }


    /**
     * This imolementation close keyboard
     *
     * @param c
     * @param windowToken
     */
    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }


    /**
     * This implementation show keyboard
     *
     * @param context
     * @param editText
     */
    public static void showKeyboard(Context context, EditText editText) {
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

}
