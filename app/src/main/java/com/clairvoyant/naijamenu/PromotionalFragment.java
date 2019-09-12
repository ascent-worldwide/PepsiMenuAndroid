package com.clairvoyant.naijamenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.clairvoyant.naijamenu.bean.BannerListBean;
import com.clairvoyant.naijamenu.bean.CommonRequestBean;
import com.clairvoyant.naijamenu.bean.PromotionResponseBean;
import com.clairvoyant.naijamenu.fonts.RobotoRegularButton;
import com.clairvoyant.naijamenu.fonts.RobotoRegularTextView;
import com.clairvoyant.naijamenu.utils.AppController;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class PromotionalFragment extends Fragment {
    private Context mContext;
    private RelativeLayout progressView;
    private RobotoRegularTextView skip;
    // private LinearLayout dotBox;
    // private LinearLayout dotOne, dotTwo, dotThree, dotFour, dotFive;
    private boolean restaurantPromotion = false;
    private String restaurantPromotionUrl = "";
    private int menuId = 1;
    private View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // View rootView = inflater.inflate(R.layout.activity_video_view, container, false);

        mContext = getActivity();
        initialiseViews(inflater, container);
        return rootView;
    }

    private void initialiseViews(LayoutInflater inflater, ViewGroup container) {
        if (!Utils.isOnline(mContext)) {
            // no internet
            String promoBannerData = PreferencesUtils.getString(mContext, Constants.PROMO_BANNER);
            if (!TextUtils.isEmpty(promoBannerData)) {
                // setContentView(R.layout.activity_promotion);
                rootView = inflater.inflate(R.layout.activity_video_view, container, false);
                Utils.setOrientation(mContext);
                // promotionViewPager = (DynamicHeightViewPager) rootView.findViewById(R.id.promotion_viewpager);
                progressView = rootView.findViewById(R.id.progress_view_promotion);
                skip = rootView.findViewById(R.id.skip);
                // dotBox = (LinearLayout) rootView.findViewById(R.id.dot_box);

                PromotionResponseBean promotionResponse = new Gson().fromJson(promoBannerData, PromotionResponseBean.class);
                parseJsonLogic(promotionResponse, promoBannerData, false);
            } else {
                // setContentView(R.layout.no_network_activity);
                rootView = inflater.inflate(R.layout.no_network_activity, container, false);
                Utils.setOrientation(mContext);
                RobotoRegularButton tryAgain = rootView.findViewById(R.id.try_again);
                tryAgain.setOnClickListener(arg0 -> {
                    // reload();
                });
            }
        } else {
            // setContentView(R.layout.activity_promotion);
            rootView = inflater.inflate(R.layout.activity_promotion, container, false);
            Utils.setOrientation(mContext);
            // promotionViewPager = (DynamicHeightViewPager) rootView.findViewById(R.id.promotion_viewpager);
            progressView = rootView.findViewById(R.id.progress_view_promotion);
            skip = rootView.findViewById(R.id.skip);
            // dotBox = (LinearLayout) rootView.findViewById(R.id.dot_box);

            // if (getIntent() != null) {
            // menuId = getIntent().getIntExtra("MENU", 0);
            // }
            if (Utils.isOnline(mContext)) {
                getPromotionalBanners(Constants.PROMOTIONAL_BANNER_API);
            } else {
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * @Override protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); mContext =
     * this; if (!Utils.isOnline(mContext)) { // no internet String promoBannerData =
     * PreferencesUtils.getString(mContext, Constants.PROMO_BANNER); if (!TextUtils.isEmpty(promoBannerData)) {
     * setContentView(R.layout.activity_promotion); Utils.setOrientation(mContext); promotionViewPager =
     * (DynamicHeightViewPager) findViewById(R.id.promotion_viewpager); progressView = (RelativeLayout)
     * findViewById(R.id.progress_view_promotion); skip = findViewById(R.id.skip); dotBox =
     * (LinearLayout) findViewById(R.id.dot_box);
     *
     * PromotionResponseBean promotionResponse = new Gson().fromJson(promoBannerData, PromotionResponseBean.class);
     * parseJsonLogic(promotionResponse, promoBannerData, false); } else { setContentView(R.layout.no_network_activity);
     * Utils.setOrientation(mContext); RobotoRegularButton tryAgain =
     * findViewById(R.id.try_again); tryAgain.setOnClickListener(new OnClickListener() {
     *
     * @Override public void onClick(View arg0) { reload(); } }); } } else {
     * setContentView(R.layout.activity_promotion); Utils.setOrientation(mContext); promotionViewPager =
     * (DynamicHeightViewPager) findViewById(R.id.promotion_viewpager); progressView = (RelativeLayout)
     * findViewById(R.id.progress_view_promotion); skip = findViewById(R.id.skip); dotBox =
     * (LinearLayout) findViewById(R.id.dot_box); if (getIntent() != null) { menuId = getIntent().getIntExtra("MENU",
     * 0); } if (Utils.isOnline(mContext)) { getPromotionalBanners(Constants.PROMOTIONAL_BANNER_API); } else {
     * Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show(); } } }
     */

    /*
     * public void reload() { Intent intent = getIntent(); intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); finish();
     * startActivity(intent); }
     */

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

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES, Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);
    }

    private com.android.volley.Response.Listener<String> promotionSuccess() {

        return new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response != null && response.length() > 0) {

                    progressView.setVisibility(View.GONE);
                    Utils.longInfo(response);

                    try {
                        Gson gson = new Gson();
                        PromotionResponseBean promotionResponse = gson.fromJson(response, PromotionResponseBean.class);
                        if (promotionResponse.getStatus().equals("true")) {
                            PreferencesUtils.putString(mContext, Constants.PROMO_BANNER, response);
                            parseJsonLogic(promotionResponse, response, true);
                        } else {
                            // false response from server
                            Intent intent = new Intent(mContext, MainActivity.class);
                            intent.putExtra("MENU", menuId);
                            startActivity(intent);
                            ((Activity) mContext).finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        };
    }

    protected void parseJsonLogic(PromotionResponseBean promotionResponse, String response, boolean isInternetAvailable) {
        if (isInternetAvailable)
            PreferencesUtils.putString(mContext, Constants.PROMO_BANNER, response);

        // if (promotionResponse.getBanner_type() == 1)
        // {
        BannerListBean[] bannerList = promotionResponse.getBannerList();
        String[] urls = new String[bannerList.length];
        for (int i = 0; i < bannerList.length; i++) {
            urls[i] = bannerList[i].getBanner_url();
        }

        /*
         * if(urls != null && urls.length>0) { CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(mContext, urls);
         * promotionViewPager.setAdapter(pagerAdapter); promotionViewPager.setVisibility(View.VISIBLE); }
         */

        if (!TextUtils.isEmpty(promotionResponse.getSplash_img_url())) {
            restaurantPromotion = true;
            restaurantPromotionUrl = promotionResponse.getSplash_img_url();
        }

        // if (urls.length >= 1) {
        // dotBox.setVisibility(View.VISIBLE);
        //
        // dotOne = (LinearLayout) rootView.findViewById(R.id.dot_one);
        // dotTwo = (LinearLayout) rootView.findViewById(R.id.dot_two);
        // dotThree = (LinearLayout) rootView.findViewById(R.id.dot_three);
        // dotFour = (LinearLayout) rootView.findViewById(R.id.dot_four);
        // dotFive = (LinearLayout) rootView.findViewById(R.id.dot_five);

        /*
         * switch (urls.length) {
         *
         * case 1: dotOne.setVisibility(View.VISIBLE); break; case 2: dotOne.setVisibility(View.VISIBLE);
         * dotTwo.setVisibility(View.VISIBLE); break; case 3: dotOne.setVisibility(View.VISIBLE);
         * dotTwo.setVisibility(View.VISIBLE); dotThree.setVisibility(View.VISIBLE); break; case 4:
         * dotOne.setVisibility(View.VISIBLE); dotTwo.setVisibility(View.VISIBLE); dotThree.setVisibility(View.VISIBLE);
         * dotFour.setVisibility(View.VISIBLE); break; case 5: dotOne.setVisibility(View.VISIBLE);
         * dotTwo.setVisibility(View.VISIBLE); dotThree.setVisibility(View.VISIBLE);
         * dotFour.setVisibility(View.VISIBLE); dotFive.setVisibility(View.VISIBLE); break; }
         *
         * dotOne.setBackgroundResource(R.drawable.gray_circle); dotTwo.setBackgroundResource(R.drawable.red_circle);
         * dotThree.setBackgroundResource(R.drawable.red_circle); dotFour.setBackgroundResource(R.drawable.red_circle);
         * dotFive.setBackgroundResource(R.drawable.red_circle);
         *
         *
         *
         * promotionViewPager.addOnPageChangeListener(new OnPageChangeListener() {
         *
         * public void onPageSelected(int position) {
         *
         *
         * switch (position) {
         *
         * case 0: dotOne.setBackgroundResource(R.drawable.gray_circle);
         * dotTwo.setBackgroundResource(R.drawable.red_circle); dotThree.setBackgroundResource(R.drawable.red_circle);
         * dotFour.setBackgroundResource(R.drawable.red_circle); dotFive.setBackgroundResource(R.drawable.red_circle);
         * break;
         *
         * case 1: dotOne.setBackgroundResource(R.drawable.red_circle);
         * dotTwo.setBackgroundResource(R.drawable.gray_circle); dotThree.setBackgroundResource(R.drawable.red_circle);
         * dotFour.setBackgroundResource(R.drawable.red_circle); dotFive.setBackgroundResource(R.drawable.red_circle);
         * break;
         *
         * case 2: dotOne.setBackgroundResource(R.drawable.red_circle);
         * dotTwo.setBackgroundResource(R.drawable.red_circle); dotThree.setBackgroundResource(R.drawable.gray_circle);
         * dotFour.setBackgroundResource(R.drawable.red_circle); dotFive.setBackgroundResource(R.drawable.red_circle);
         * break;
         *
         * case 3: dotOne.setBackgroundResource(R.drawable.red_circle);
         * dotTwo.setBackgroundResource(R.drawable.red_circle); dotThree.setBackgroundResource(R.drawable.red_circle);
         * dotFour.setBackgroundResource(R.drawable.gray_circle); dotFive.setBackgroundResource(R.drawable.red_circle);
         * break;
         *
         * case 4: dotOne.setBackgroundResource(R.drawable.red_circle);
         * dotTwo.setBackgroundResource(R.drawable.red_circle); dotThree.setBackgroundResource(R.drawable.red_circle);
         * dotFour.setBackgroundResource(R.drawable.red_circle); dotFive.setBackgroundResource(R.drawable.gray_circle);
         * break;
         *
         * } }
         *
         * @Override public void onPageScrolled(int arg0, float arg1, int arg2) {
         *
         * }
         *
         * @Override public void onPageScrollStateChanged(int arg0) {
         *
         * } });
         *
         * runThread(); }
         */
        // runThread();

        if (restaurantPromotion) {
            Intent intent = new Intent(mContext, RestaurantPromotionActivity.class);
            intent.putExtra("RESTAURANT_PROMOTION", restaurantPromotionUrl);
            intent.putExtra("MENU", menuId);
            startActivity(intent);
            ((Activity) mContext).finish();
        } else {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("MENU", menuId);
            startActivity(intent);
            ((Activity) mContext).finish();
        }

        /*
         * } else if (promotionResponse.getBanner_type() == 2) { // BannerListBean[] bannerList =
         * promotionResponse.getBannerList(); // youTubeVideoURL = bannerList[0].getBanner_url(); // youTubeView =
         * (YouTubePlayerView) rootView.findViewById(R.id.promotion_youtube_view); //
         * youTubeView.initialize(Constants.DEVELOPER_KEY, (OnInitializedListener)mContext); //
         * youTubeView.setVisibility(View.VISIBLE); // runThread(); }
         */
    }

	/*private void runThread()
	{

		new Thread()
		{
			public void run()
			{
				try
				{
					Thread.sleep(3000);
					((Activity) mContext).runOnUiThread(new Runnable()
					{

						@Override
						public void run()
						{
							skip.setVisibility(View.VISIBLE);
							skip.setOnClickListener(new OnClickListener()
							{

								@Override
								public void onClick(View v)
								{
									if (restaurantPromotion)
									{
										Intent intent = new Intent(mContext, RestaurantPromotionActivity.class);
										intent.putExtra("RESTAURANT_PROMOTION", restaurantPromotionUrl);
										intent.putExtra("MENU", menuId);
										startActivity(intent);
										((Activity) mContext).finish();
									}
									else
									{
										Intent intent = new Intent(mContext, MainActivity.class);
										intent.putExtra("MENU", menuId);
										startActivity(intent);
										((Activity) mContext).finish();
									}
								}
							});
						}
					});
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}*/

    private com.android.volley.Response.ErrorListener promotionError() {
        return new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressView.setVisibility(View.GONE);
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }

        };
    }

    /*
     * @Override public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean
     * wasRestored) {
     *
     * if (!wasRestored) { String youTubeID = Utils.getYouTubeId(youTubeVideoURL); if(!TextUtils.isEmpty(youTubeID))
     * player.cueVideo(youTubeID); } }
     *
     * @Override public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult
     * errorReason) {
     *
     * if (errorReason.isUserRecoverableError()) { errorReason.getErrorDialog((Activity)mContext,
     * RECOVERY_DIALOG_REQUEST).show(); } else { String errorMessage = String.format(getString(R.string.error_player),
     * errorReason.toString()); Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show(); } }
     */

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        private String[] mResources;

        public CustomPagerAdapter(Context context, String[] mResources) {
            mContext = context;
            this.mResources = mResources;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((LinearLayout) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            Utils.renderImage(mContext, mResources[position], imageView);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView((LinearLayout) object);
        }
    }

}
