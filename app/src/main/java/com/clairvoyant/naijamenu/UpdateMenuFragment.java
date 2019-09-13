package com.clairvoyant.naijamenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.clairvoyant.naijamenu.bean.AllMenuCategoryBean;
import com.clairvoyant.naijamenu.bean.AllMenuCategoryOuterBean;
import com.clairvoyant.naijamenu.bean.CommonRequestBean;
import com.clairvoyant.naijamenu.bean.MenuCategoryBean;
import com.clairvoyant.naijamenu.bean.MenuCategoryOuterBean;
import com.clairvoyant.naijamenu.bean.ProductBean;
import com.clairvoyant.naijamenu.bean.ProductBeanOuter;
import com.clairvoyant.naijamenu.fonts.RobotoRegularButton;
import com.clairvoyant.naijamenu.utils.AppController;
import com.clairvoyant.naijamenu.utils.BasicImageDownloader;
import com.clairvoyant.naijamenu.utils.BasicImageDownloader.ImageError;
import com.clairvoyant.naijamenu.utils.BasicImageDownloader.OnImageLoaderListener;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.gson.Gson;
import com.pepsi.database.DatabaseHelper;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UpdateMenuFragment extends Fragment {

    private static String TAG = UpdateMenuFragment.class.getSimpleName();
    private View mUpdateManuView;
    private Context mContext;
    private ProgressBar mProgressBar;
    private TextView mTxtCurrentversion;
    private TextView mTxtUpdateMenuInfo;
    private Button btnUpdateMenu;
    private int updatedMenuVersion;
    private List<String> listUrls;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView()");
        mContext = getActivity();
        updatedMenuVersion = getArguments().getInt(Constants.UPDATED_MENU_VERSION);
        mUpdateManuView = inflater.inflate(R.layout.fragment_update_menu, container, false);
        initializeViews(mUpdateManuView, inflater, container);
        return mUpdateManuView;
    }

    /**
     * Method is used to initiate the items from the layout
     *
     * @param view
     * @param inflater
     * @param container
     */
    private void initializeViews(View view, LayoutInflater inflater, ViewGroup container) {
        mTxtCurrentversion = view.findViewById(R.id.txtCurrentversion);
        mTxtUpdateMenuInfo = view.findViewById(R.id.txtUpdateMenuInfo);
        btnUpdateMenu = view.findViewById(R.id.btnUpdateMenu);
        mProgressBar = view.findViewById(R.id.progressBar);
        btnUpdateMenu.setOnClickListener(v -> {

        /*	new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                }
            }, 2000);*/

            if (Utils.isOnline(mContext)) {
                btnUpdateMenu.setAlpha(.5f);
                btnUpdateMenu.setEnabled(false);
                mTxtCurrentversion.setText(String.format(getResources().getString(R.string.versionTxt), updatedMenuVersion));
                mTxtUpdateMenuInfo.setText(getResources().getString(R.string.menuUpdatingTxt));
                mProgressBar.setVisibility(View.VISIBLE);
                getMenuItem(Constants.CATEGORY_API);
            } else {
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }
        });


        /**
         *  arrange the views according to the menu version
         */
        // get the menu_version and save it into pref if not present
        int menuVersionFromPref = PreferencesUtils.getInt(mContext, Constants.MENU_VERSION, 0);
        if (menuVersionFromPref != updatedMenuVersion) {
            mTxtCurrentversion.setText(String.format(getResources().getString(R.string.currentVersionTxt), menuVersionFromPref));
            btnUpdateMenu.setAlpha(1);
            btnUpdateMenu.setEnabled(true);
            mTxtUpdateMenuInfo.setText(getResources().getString(R.string.menuUpdateAvailableTxt));
        } else {
            // no update available
            btnUpdateMenu.setAlpha(.5f);
            btnUpdateMenu.setEnabled(false);
            mTxtCurrentversion.setText(String.format(getResources().getString(R.string.currentVersionTxt), menuVersionFromPref));
            mTxtUpdateMenuInfo.setText(getResources().getString(R.string.noUpdateAvailableTxt));
        }

        // check if the internet is available
        if (Utils.isOnline(mContext)) {
            // screen is visible

        } else {
            mUpdateManuView = inflater.inflate(R.layout.no_network_activity, container, false);
            RobotoRegularButton tryAgain = mUpdateManuView.findViewById(R.id.try_again);
            tryAgain.setOnClickListener(arg0 -> reload());
        }
    }

    /**
     * Method is used to reload the current activity
     */
    protected void reload() {
        Intent intent = getActivity().getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().finish();
        startActivity(intent);
    }

    // get categories
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

    // method called when there is success after api hit
    private com.android.volley.Response.Listener<String> menuSuccess() {
        return new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    Utils.longInfo(response);
                    try {
                        Gson gson = new Gson();
                        MenuCategoryOuterBean menuCategoryOuterBean = gson.fromJson(response.trim(),
                                MenuCategoryOuterBean.class);
                        if (menuCategoryOuterBean.getStatus().equals("true")) {
                            // save the response in Preference so that we can
                            // able to see the data offline
                            PreferencesUtils.putString(mContext, Constants.HOME_CATEGORIES, response);

                            final Set<String> setUrlImages = new HashSet<>();
                            // save the images into storage
                            if (menuCategoryOuterBean.getCategories() != null)
                                for (MenuCategoryBean menuCategoryBean : menuCategoryOuterBean.getCategories()) {
                                    if (menuCategoryBean != null) {
                                        setUrlImages.add(menuCategoryBean.getCategoryURL());
                                        for (MenuCategoryBean menuSubCategoryBean : menuCategoryBean.getSubCategories()) {
                                            if (menuSubCategoryBean != null) {
                                                setUrlImages.add(menuSubCategoryBean.getCategoryURL());
                                            }
                                        }
                                    }
                                }

                            if (listUrls == null) {
                                listUrls = new ArrayList<>();
                            }
                            listUrls.addAll(setUrlImages);
                            Log.i("list_size", "list of urls size after category : " + listUrls.size());
                        }

                        // start task to fetch all products
                        getAllProducts(Constants.GET_ALL_MENU);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    // method called when there is any error
    private com.android.volley.Response.ErrorListener menuError() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * Method is used to get all products from the api and save it to the database
     *
     * @param api
     */
    private void getAllProducts(final String api) {
        CommonRequestBean requestBean = new CommonRequestBean();
        requestBean.setApp_version(Utils.getAppVersion(mContext));
        requestBean.setDevice_type(Constants.DEVICE_TYPE);
        requestBean.setRestaurant_id(PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID));

        final String param = new Gson().toJson(requestBean, CommonRequestBean.class);
        StringRequest request = new StringRequest(Method.POST, api, getAllMenuSuccess(), menuError()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("data", param);
                Log.i("ALL_PRODUCTS_PARAMS", api + "\n" + param.toString());
                return params;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT, Constants.VOLLEY_MAX_RETRIES, Constants.VOLLEY_BACKUP_MULT);
        request.setRetryPolicy(retryPolicy);
        AppController.getInstance().addToRequestQueue(request);
    }

    // method called when there is success after api hit
    private com.android.volley.Response.Listener<String> getAllMenuSuccess() {
        return new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    Utils.longInfo(response);
                    try {
                        final Gson gson = new Gson();
                        final AllMenuCategoryOuterBean allMenuCategoryOuterBean = gson.fromJson(response.trim(),
                                AllMenuCategoryOuterBean.class);
                        if (allMenuCategoryOuterBean.getStatus().equals("true")) {
                            // call method to save all menu task
                            saveAllMenuIntoDatabase(allMenuCategoryOuterBean, gson);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    /**
     * Method to save the data into database with images
     */
    @SuppressLint("StaticFieldLeak")
    private void saveAllMenuIntoDatabase(final AllMenuCategoryOuterBean pAllMenuCategoryOuterBean, final Gson gson) {
        // start the asyntask to insert the data into database table
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                // save the response in Preference so that we can
                // able to see the data offline
                final Set<String> setUrlImages = new HashSet<>();
                for (AllMenuCategoryBean allMenuCategoryBean : pAllMenuCategoryOuterBean.getCategories()) {
                    int categoryId = allMenuCategoryBean.getCategoryId();
                    final ProductBeanOuter productBeanOuter = allMenuCategoryBean.getProducts();
                    final String strProductsDataByCategory = gson.toJson(productBeanOuter, ProductBeanOuter.class);
                    // delete the older data if already present
                    DatabaseHelper.deleteProductDataByCategoryId(mContext, String.valueOf(categoryId));
                    // insert the category id and related product into database
                    DatabaseHelper.insertProductData(mContext, strProductsDataByCategory, categoryId);

                    // save the images into storage
                    if (productBeanOuter.getProductlist() != null)
                        for (ProductBean productBean : productBeanOuter.getProductlist()) {
                            if (productBean != null) {
                                setUrlImages.add(productBean.getProductUrl());
                                Collections.addAll(setUrlImages, productBean.getProductDetailUrl());
                            }
                            //break;
                        }
                }

                // start to store images in local storage
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listUrls == null) {
                            listUrls = new ArrayList<>();
                        }
                        listUrls.addAll(setUrlImages);
                        Log.i("list_size", "list of urls size : " + listUrls.size());
                        // call method to download all images
                        saveImagesIntoStorage(listUrls.get(0), 0);
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(final Void result) {
                /**
                 * put the updated menu version in the shared preference
                 */
//				mProgressBar.setVisibility(View.GONE);
//				PreferencesUtils.putInt(mContext, Constants.MENU_VERSION, updatedMenuVersion);
//				btnUpdateMenu.setVisibility(View.GONE);
//				mTxtCurrentversion.setText(String.format(getResources().getString(R.string.currentVersionTxt), updatedMenuVersion));
//				mTxtUpdateMenuInfo.setText(getResources().getString(R.string.menuUpdatedTxt));
            }
        }.execute();
    }

    /**
     * Method is used to save the images into local storage
     *
     * @param url
     */
    private void saveImagesIntoStorage(String url, final int position) {

        if (url == null || url.isEmpty()) {
            // start method again
            if (listUrls != null && listUrls.size() - 1 > position) {
                startDownloadAgain(position);
            } else {
                return;
            }
        }

        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final String fileName = url.substring(url.lastIndexOf('/') + 1);
        // start the task to download images
        final BasicImageDownloader downloader = new BasicImageDownloader(new OnImageLoaderListener() {
            @Override
            public void onError(ImageError error) {
                Log.i("error", "error in downloading image : " + error.getErrorCode() + ": " + error.getMessage());
                error.printStackTrace();

                // start method again
                startDownloadAgain(position);
            }

            @Override
            public void onProgressChange(int percent) {
            }

            @Override
            public void onComplete(Bitmap result) {
                /* save the image - I'm gonna use JPEG */
                final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                /* don't forget to include the extension into the file name */
                final File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "Pepsi_Menu" + File.separator + fileName + "." + mFormat.name().toLowerCase());

                // write the image to disk
                BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                    @Override
                    public void onBitmapSaved() {
                        Log.i("success", "Image saved as: " + myImageFile.getAbsolutePath());
                    }

                    @Override
                    public void onBitmapSaveError(ImageError error) {
                        Log.i("error", "error in saving image : " + error.getErrorCode() + ": " + error.getMessage());
                        error.printStackTrace();
                    }
                }, mFormat, false);

                // start method again
                startDownloadAgain(position);
            }
        });
        downloader.download(url, true);
    }

    // method to start download again
    private void startDownloadAgain(final int position) {
        if (listUrls != null && listUrls.size() - 1 > position) {
            int newPos = position + 1;
            saveImagesIntoStorage(listUrls.get(newPos), newPos);
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                    PreferencesUtils.putInt(mContext, Constants.MENU_VERSION, updatedMenuVersion);
                    btnUpdateMenu.setVisibility(View.GONE);
                    mTxtCurrentversion.setText(String.format(getResources().getString(R.string.currentVersionTxt), updatedMenuVersion));
                    mTxtUpdateMenuInfo.setText(getResources().getString(R.string.menuUpdatedTxt));
                }
            });
        }
    }
}
