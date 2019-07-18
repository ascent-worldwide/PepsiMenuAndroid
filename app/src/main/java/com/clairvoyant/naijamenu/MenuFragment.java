package com.clairvoyant.naijamenu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.clairvoyant.naijamenu.adapter.MenuRecyclerAdapter;
import com.clairvoyant.naijamenu.bean.CommonRequestBean;
import com.clairvoyant.naijamenu.bean.MenuCategoryBean;
import com.clairvoyant.naijamenu.bean.MenuCategoryOuterBean;
import com.clairvoyant.naijamenu.utils.AppController;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.gson.Gson;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuFragment extends Fragment {

    ProgressDialog mProgressDialog;
    private RecyclerView menuRecycler;
    private int columns = 4;
    private View menuView;
    private Activity mContext;
    private RelativeLayout progressView, rlRootLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();

        if (Utils.isOnline(mContext)) {
            inflatelayout(inflater, container);
            if (Utils.isOnline(mContext)) {
                progressView.setVisibility(View.VISIBLE);
                getMenuItem(Constants.CATEGORY_API);
            } else if (!TextUtils.isEmpty(PreferencesUtils.getString(mContext, Constants.HOME_CATEGORIES))) {
                inflatelayout(inflater, container);
                // if internet is not available we get the data from preference so that the app can work offline
                try {
                    String response = PreferencesUtils.getString(mContext, Constants.HOME_CATEGORIES);
                    MenuCategoryOuterBean menuCategoryOuterBean = new Gson().fromJson(response, MenuCategoryOuterBean.class);
                    parseData(response, menuCategoryOuterBean, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(mContext, Constants.HOME_CATEGORIES))) {
            inflatelayout(inflater, container);
            // if internet is not available we get the data from preference so that the app can work offline
            try {

                String response = PreferencesUtils.getString(mContext, Constants.HOME_CATEGORIES);
                MenuCategoryOuterBean menuCategoryOuterBean = new Gson().fromJson(response, MenuCategoryOuterBean.class);
                parseData(response, menuCategoryOuterBean, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            menuView = inflater.inflate(R.layout.no_network_activity, container, false);
        }

        return menuView;
    }

    private void inflatelayout(LayoutInflater inflater, ViewGroup container) {
        menuView = inflater.inflate(R.layout.menu_category_fragment, container, false);
        progressView = (RelativeLayout) menuView.findViewById(R.id.progress_view_menu);
        rlRootLayout = (RelativeLayout) menuView.findViewById(R.id.rlRootLayout);
        menuRecycler = (RecyclerView) menuView.findViewById(R.id.menu_recycler);
        menuRecycler.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            columns = 4;
        else
            columns = 2;

        menuRecycler.setLayoutManager(new GridLayoutManager(mContext, columns));

    }

    private void getMenuItem(final String api) {

        CommonRequestBean requestBean = new CommonRequestBean();
        requestBean.setApp_version(Utils.getAppVersion(mContext));
        requestBean.setDevice_type(Constants.DEVICE_TYPE);
        requestBean.setRestaurant_id(PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID));

        final String param = new Gson().toJson(requestBean, CommonRequestBean.class);

        StringRequest request = new StringRequest(Method.POST, api, menuSuccess(), menuError()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("data", param);
                Log.i("MENU_PARAM", api + "\n" + param.toString());
                return params;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES, Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);
    }

    private com.android.volley.Response.Listener<String> menuSuccess() {

        return new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response != null && response.length() > 0) {

                    Utils.longInfo(response);
                    try {
                        Gson gson = new Gson();
                        MenuCategoryOuterBean menuCategoryOuterBean = gson.fromJson(response.trim(), MenuCategoryOuterBean.class);

                        if (menuCategoryOuterBean.getStatus().equals("true")) {
                            parseData(response, menuCategoryOuterBean, true);
                        }

                        progressView.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressView.setVisibility(View.GONE);
                    }
                }
                progressView.setVisibility(View.GONE);
            }
        };
    }

    protected void parseData(String response, MenuCategoryOuterBean menuCategoryOuterBean, boolean isInternetAvailable) {
        if (isInternetAvailable) {
            // save the response in Preference so that we can able to see the data offline
            PreferencesUtils.putString(mContext, Constants.HOME_CATEGORIES, response);

            String savedData = PreferencesUtils.getString(mContext, Constants.HOME_CATEGORIES);
            System.out.println("savedData=" + savedData);

        }

        MenuCategoryBean[] menuCategories = menuCategoryOuterBean.getCategories();
        ArrayList<MenuCategoryBean> menuCategoryList = new ArrayList<>();

        for (MenuCategoryBean menuCategoryBean1 : menuCategories) {
            MenuCategoryBean menuCategoryBean = new MenuCategoryBean();
            menuCategoryBean.setCategoryId(menuCategoryBean1.getCategoryId());
            menuCategoryBean.setCategoryName(menuCategoryBean1.getCategoryName());
            menuCategoryBean.setCategoryURL(menuCategoryBean1.getCategoryURL());
            menuCategoryBean.setDoescontainCat(menuCategoryBean1.getDoescontainCat());
            menuCategoryBean.setSubCategories(menuCategoryBean1.getSubCategories());
            menuCategoryList.add(menuCategoryBean);
        }
		
		/*for (int i = 0; i <  menuCategories.length; i++) {
			MenuCategoryBean menuCategoryBean = new MenuCategoryBean();
			menuCategoryBean.setCategoryId(menuCategories[i].getCategoryId());
			menuCategoryBean.setCategoryName(menuCategories[i].getCategoryName());
			menuCategoryBean.setCategoryURL(menuCategories[i].getCategoryURL());
			menuCategoryBean.setDoescontainCat(menuCategories[i].getDoescontainCat());
			menuCategoryBean.setSubCategories(menuCategories[i].getSubCategories());
			menuCategoryList.add(menuCategoryBean);
		}*/

        MenuRecyclerAdapter mAdapter = new MenuRecyclerAdapter(mContext, menuCategoryList);
        menuRecycler.setAdapter(mAdapter);
    }

    private com.android.volley.Response.ErrorListener menuError() {
        return new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressView.setVisibility(View.GONE);
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }

        };
    }

    @Override
    public void onResume() {
        super.onResume();
        String imgUrl;
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imgUrl = PreferencesUtils.getString(mContext, Constants.RESTAURANT_BACKGROUND_IMG_LANDSCAPE);
            if (!TextUtils.isEmpty(imgUrl)) {
                new DownloadImage().execute(imgUrl);
            }
        } else {
            imgUrl = PreferencesUtils.getString(mContext, Constants.RESTAURANT_BACKGROUND_IMG_PORTRAIT);
            if (!TextUtils.isEmpty(imgUrl)) {
                new DownloadImage().execute(imgUrl);
            }
        }
    }

    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
				/*mProgressDialog = new ProgressDialog(mContext);
				// Set progressdialog title
				mProgressDialog.setTitle("Download Image Tutorial");
				// Set progressdialog message
				mProgressDialog.setMessage("Loading...");
				mProgressDialog.setIndeterminate(false);*/
            progressView.setVisibility(View.VISIBLE);
            // Show progressdialog
//				mProgressDialog.show();
        }


        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(isAdded()) {
                if(result != null) {
                    Drawable dr = new BitmapDrawable(getResources(), result);
                    rlRootLayout.setBackground(dr);
                } else {
                    rlRootLayout.setBackgroundResource(R.drawable.offline_background);
                }
                progressView.setVisibility(View.GONE);

            }
        }
    }

}