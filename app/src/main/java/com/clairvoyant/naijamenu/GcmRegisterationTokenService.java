package com.clairvoyant.naijamenu;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class GcmRegisterationTokenService extends IntentService {

    private static final String TAG = GcmRegisterationTokenService.class.getSimpleName();

    public GcmRegisterationTokenService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getGCMToken();
    }

    private void getGCMToken() {
        GoogleCloudMessaging gcm;

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);

            if (Utils.isOnline(getBaseContext())) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
                    }

                    String regid = gcm.register(Constants.SENDER_ID);

                    if (!regid.isEmpty()) {
                        Utils.setGCMId(getBaseContext(), regid);
                        Log.i("GCM_ID", Utils.getGCMId(getBaseContext()));
                    }
                } catch (IOException ex) {
                    Log.e("Exception e : ", ex.getStackTrace().toString());
                }
            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }
}