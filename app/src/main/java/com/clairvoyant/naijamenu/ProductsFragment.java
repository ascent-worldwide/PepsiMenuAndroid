package com.clairvoyant.naijamenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.clairvoyant.naijamenu.adapter.MenuItemRecyclerAdapter;
import com.clairvoyant.naijamenu.bean.ProductBean;
import com.clairvoyant.naijamenu.bean.ProductBeanOuter;
import com.clairvoyant.naijamenu.bean.ProductRequestBean;
import com.clairvoyant.naijamenu.fonts.RobotoRegularButton;
import com.clairvoyant.naijamenu.utils.AppController;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.gson.Gson;
import com.pepsi.database.DatabaseHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductsFragment extends Fragment {

    private RecyclerView menuRecycler;
    private int columns = 4;
    private View menuView;
    private Activity mContext;
    private RelativeLayout progressView, rlRootLayout;
    private int categoryId;
    private boolean subCategory;
    private boolean isInternetConnectionLayoutVisible = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            categoryId = getArguments().getInt("CATEGORY_ID");
            subCategory = getArguments().getBoolean("subCategory", false);
        }

        if (Utils.isOnline(mContext)) {
            initialiseViews(inflater, container);

            if (Utils.isOnline(mContext)) {
                progressView.setVisibility(View.VISIBLE);
                getMenuItem(Constants.PRODUCT_BY_CATEGORY_ID_API, categoryId);
            } else if (!TextUtils.isEmpty(DatabaseHelper.getProductDataByCategoryId(mContext, String.valueOf(categoryId)))) {
                initialiseViews(inflater, container);
                String resString = DatabaseHelper.getProductDataByCategoryId(mContext, String.valueOf(categoryId));
                ProductBeanOuter productBeanOuter = new Gson().fromJson(resString, ProductBeanOuter.class);
                parsingLogic(productBeanOuter);
            } else {
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }
        } else if (!TextUtils.isEmpty(DatabaseHelper.getProductDataByCategoryId(mContext, String.valueOf(categoryId)))) {
            initialiseViews(inflater, container);
            String resString = DatabaseHelper.getProductDataByCategoryId(mContext, String.valueOf(categoryId));
            ProductBeanOuter productBeanOuter = new Gson().fromJson(resString, ProductBeanOuter.class);
            parsingLogic(productBeanOuter);
        } else {
            isInternetConnectionLayoutVisible = true;
            menuView = inflater.inflate(R.layout.no_network_activity, container, false);
            RobotoRegularButton tryAgain = (RobotoRegularButton) menuView.findViewById(R.id.try_again);
            tryAgain.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    reload();
                }
            });

        }

        return menuView;
    }

    private void initialiseViews(LayoutInflater inflater, ViewGroup container) {
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

        if (getArguments() != null) {
            categoryId = getArguments().getInt("CATEGORY_ID");
            subCategory = getArguments().getBoolean("subCategory", false);
        }
    }

    public void reload() {
        Intent intent = getActivity().getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().finish();
        startActivity(intent);
    }

    private void getMenuItem(final String api, int categoryId) {

        ProductRequestBean requestBean = new ProductRequestBean();
        requestBean.setApp_version(Utils.getAppVersion(mContext));
        requestBean.setDevice_type(Constants.DEVICE_TYPE);
        requestBean.setRestaurant_id(PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID));
        requestBean.setCategoryId(categoryId);
        if (subCategory)
            requestBean.setSubCategory("true");

        final String param = new Gson().toJson(requestBean, ProductRequestBean.class);

        StringRequest request = new StringRequest(Method.POST, api, menuSuccess(), menuError()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("data", param);
                Log.i("PRODUCT_PARAM", api + "\n" + param.toString());
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
                        ProductBeanOuter productBeanOuter = gson.fromJson(response, ProductBeanOuter.class);

                        if (productBeanOuter.getStatus().equals("true")) {
                            DatabaseHelper.deleteProductDataByCategoryId(mContext, String.valueOf(categoryId));
                            DatabaseHelper.insertProductData(mContext, response, categoryId);
                            String key = String.valueOf(categoryId);
                            // PreferencesUtils.putString(mContext, key, response);
                            // System.out.println("key="+key+" "+"value="+response);
                            Log.i(String.valueOf(categoryId), DatabaseHelper.getProductDataByCategoryId(mContext, String.valueOf(categoryId)));
                            parsingLogic(productBeanOuter);
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

    protected void parsingLogic(ProductBeanOuter productBeanOuter) {
        ProductBean[] productArray = productBeanOuter.getProductlist();
        ArrayList<ProductBean> productList = new ArrayList<>();
        for (int i = 0; i < productArray.length; i++) {
            ProductBean productBean = new ProductBean();
            productBean.setProductId(productArray[i].getProductId());
            productBean.setProductName(productArray[i].getProductName());
            productBean.setProductDesc(productArray[i].getProductDesc());
            productBean.setPrice(productArray[i].getPrice());
            productBean.setProductType(productArray[i].getProductType());
            productBean.setSpiceLevel(productArray[i].getSpiceLevel());
            productBean.setPreparationTime(productArray[i].getPreparationTime());
            productBean.setDetail_url_type(productArray[i].getDetail_url_type());
            productBean.setProductUrl(productArray[i].getProductUrl());
            productBean.setProductDetailUrl(productArray[i].getProductDetailUrl());
            productList.add(productBean);
        }

        MenuItemRecyclerAdapter mAdapter = new MenuItemRecyclerAdapter(mContext, productList);
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
        if(isInternetConnectionLayoutVisible){
            return;
        }
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
            /*
             * mProgressDialog = new ProgressDialog(mContext); // Set progressdialog title
             * mProgressDialog.setTitle("Download Image Tutorial"); // Set progressdialog message
             * mProgressDialog.setMessage("Loading..."); mProgressDialog.setIndeterminate(false);
             */
            if(progressView != null)
                progressView.setVisibility(View.VISIBLE);
            // Show progressdialog
            // mProgressDialog.show();
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

            if(isAdded() && getActivity() !=null) {
                Drawable dr = new BitmapDrawable(getResources(), result);
                rlRootLayout.setBackground(dr);
                progressView.setVisibility(View.GONE);
            }
        }
    }
}