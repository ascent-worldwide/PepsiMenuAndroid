package com.clairvoyant.naijamenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.Interface1;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;

public class BrandPromotionActivity extends AppCompatActivity implements Interface1 {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_promotion);

        mContext = this;
        String videoUrl = PreferencesUtils.getString(mContext, Constants.VIDEO_URL);
        boolean isVideoDownloaded = PreferencesUtils.getBoolean(mContext, Constants.IS_VIDEO_DOWNLOADED_STR);
        if (TextUtils.isEmpty(videoUrl)) {
            goToRestaurantPromo();
        } else if (!TextUtils.isEmpty(videoUrl) && !isVideoDownloaded) {
            Toast.makeText(mContext, getString(R.string.please_wait_while), Toast.LENGTH_LONG).show();
        } else if (!TextUtils.isEmpty(videoUrl) && isVideoDownloaded) {
            callFragment();
        }

    }

    private void callFragment() {
        Fragment weekInfo = new VideoViewFragment();
        FragmentTransaction generalFragmentTrans = getSupportFragmentManager().beginTransaction();
        generalFragmentTrans.add(R.id.fragment_container, weekInfo).commit();
    }

    @Override
    public void onFragmentInteraction(boolean isVideoFinished) {
        goToRestaurantPromo();
    }

    private void goToRestaurantPromo() {
        String isPromoBannerAvailable = PreferencesUtils.getString(mContext, Constants.IS_PROMO_BANNER_AVAILABLE);
        if (!TextUtils.isEmpty(isPromoBannerAvailable) && isPromoBannerAvailable.equalsIgnoreCase("true")) {
            Fragment weekInfo = new PromotionalFragment();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            FragmentTransaction generalFragmentTrans = getSupportFragmentManager().beginTransaction();
            generalFragmentTrans.add(R.id.fragment_container, weekInfo).commit();
        } else {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("MENU", 1);
            startActivity(intent);
            ((Activity) mContext).finish();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
