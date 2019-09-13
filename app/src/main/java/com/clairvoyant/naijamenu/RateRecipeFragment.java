package com.clairvoyant.naijamenu;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.clairvoyant.naijamenu.bean.CommonRequestBean;
import com.clairvoyant.naijamenu.bean.MenuCategoryBean;
import com.clairvoyant.naijamenu.bean.MenuCategoryOuterBean;
import com.clairvoyant.naijamenu.bean.ProductBean;
import com.clairvoyant.naijamenu.bean.ProductBeanOuter;
import com.clairvoyant.naijamenu.bean.ProductRequestBean;
import com.clairvoyant.naijamenu.bean.RateRecipeParams;
import com.clairvoyant.naijamenu.fonts.RobotoRegularButton;
import com.clairvoyant.naijamenu.fonts.RobotoRegularEditText;
import com.clairvoyant.naijamenu.fonts.RobotoRegularTextView;
import com.clairvoyant.naijamenu.utils.AppController;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RateRecipeFragment extends Fragment implements OnClickListener {

    private static String TAG = RateRecipeFragment.class.getSimpleName();

    private View recipeView;
    private Spinner categorySpinner, subCategorySpinner, productSpinner;
    private RatingBar rating;
    private int categoryId, productId;
    private RobotoRegularEditText review, name, mobileNumber, email;
    private RobotoRegularTextView birthday, anniversary;
    private RobotoRegularButton btnSubmit;
    private RelativeLayout progressView;
    private Context mContext;
    private int subcat = 0;
    private ArrayList<ProductBean> productList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView()");
        mContext = getActivity();
        recipeView = inflater.inflate(R.layout.fragment_rate_recipe, container, false);
        initializeViews(recipeView);
        return recipeView;
    }

    private void initializeViews(View view) {

        categorySpinner = view.findViewById(R.id.category_spinner);
        subCategorySpinner = view.findViewById(R.id.subcategory_spinner);
        productSpinner = view.findViewById(R.id.product_spinner);
        rating = view.findViewById(R.id.rating);
        review = view.findViewById(R.id.review);
        name = view.findViewById(R.id.rate_name);
        mobileNumber = view.findViewById(R.id.rate_mobile);
        email = view.findViewById(R.id.rate_email);
        birthday = view.findViewById(R.id.birthday);
        anniversary = view.findViewById(R.id.anniversary);
        btnSubmit = view.findViewById(R.id.btn_submit);
        progressView = view.findViewById(R.id.progress_view_recipe);
        btnSubmit.setOnClickListener(this);
        birthday.setOnClickListener(this);
        anniversary.setOnClickListener(this);

        if (Utils.isOnline(mContext)) {
            progressView.setVisibility(View.VISIBLE);
            getMenuItem(Constants.CATEGORY_API);
        } else {
            Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
        }

        productSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    position--;

                if (productList != null && productList.size() > 0) {
                    ProductBean productData = productList.get(position);
                    productId = productData.getProductId();
                    Log.d(TAG, "productId=" + productId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
		
		/*subCategorySpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				if(position >0)
					position--;
				
				if(productList != null && productList.size()>0)
				{
					ProductBean productData = productList.get(position);
					productId = productData.getProductId();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				
			}
			
		});*/
    }

    private void getMenuItem(String api) {

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
                Log.i("PROMOTION_PARAM", param.toString());
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
                    progressView.setVisibility(View.GONE);
                    Utils.longInfo(response);
                    try {
                        Gson gson = new Gson();
                        MenuCategoryOuterBean menuCategoryOuterBean = gson.fromJson(response, MenuCategoryOuterBean.class);

                        if (menuCategoryOuterBean.getStatus().equals("true")) {

                            MenuCategoryBean[] menuCategories = menuCategoryOuterBean.getCategories();
                            final ArrayList<MenuCategoryBean> menuCategoryList = new ArrayList<>();
                            for (int i = 0; i < menuCategories.length; i++) {
                                MenuCategoryBean menuCategoryBean = new MenuCategoryBean();
                                menuCategoryBean.setCategoryId(menuCategories[i].getCategoryId());
                                menuCategoryBean.setCategoryName(menuCategories[i].getCategoryName());
                                menuCategoryBean.setCategoryURL(menuCategories[i].getCategoryURL());
                                menuCategoryBean.setDoescontainCat(menuCategories[i].getDoescontainCat());
                                menuCategoryBean.setSubCategories(menuCategories[i].getSubCategories());
                                menuCategoryList.add(menuCategoryBean);
                            }

                            ArrayList<String> mainCategory = new ArrayList<>();

//							String[] categories = new String[menuCategoryList.size() + 1];
//							categories[0] = "Select";
                            mainCategory.add(0, "Select");
                            for (int i = 0; i < menuCategoryList.size(); i++) {
//								categories[i] = menuCategoryList.get(i-1).getCategoryName();
                                String name = menuCategoryList.get(i).getCategoryName();
                                mainCategory.add(name);
                            }

                            ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mainCategory);
                            categorySpinner.setAdapter(adapter_state);

							/*if(menuCategoryList != null && menuCategoryList.size()>0)
							{
								for (int j = 0; j < menuCategoryList.size(); j++)
								{
									if (menuCategoryList.get(j).getDoescontainCat().equalsIgnoreCase("Y")) {
										MenuCategoryBean []subCatList = menuCategoryList.get(j).getSubCategories();
										final String[] subCategories = new String[menuCategoryList.get(j).getSubCategories().length];
										for (int i = 0; i < subCatList.length; i++) {
											subCategories[i] = subCatList[i].getCategoryName();
										}
										ArrayAdapter<String> adapter_subcategory = new ArrayAdapter<String>(mContext,  android.R.layout.simple_spinner_item, subCategories);
										subCategorySpinner.setAdapter(adapter_subcategory);
									} else {
										if (Utils.isOnline(mContext)) {
											subcat = 1;
											productSpinner.setVisibility(View.GONE);
											progressView.setVisibility(View.VISIBLE);
											getProducts(menuCategoryList.get(j).getCategoryId());
										} else {
											Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
										}
									}
								}
							}*/
							/*if(menuCategories != null && menuCategories.length>0)
							{
								catSubCatValues = new HashMap<>(menuCategories.length);
								for (int j = 0; j < menuCategories.length; j++)
								{
									if (menuCategoryList.get(j).getDoescontainCat().equalsIgnoreCase("Y")) 
									{
										MenuCategoryBean []subCatList = menuCategoryList.get(j).getSubCategories();
										final String[] subCategories = new String[menuCategoryList.get(j).getSubCategories().length];
										for (int i = 0; i < subCatList.length; i++) 
										{
//											subCategories[i] = subCatList[i].getCategoryName();
											catSubCatValues.put(j, subCatList[i].getCategoryName());
										}
//										ArrayAdapter<String> adapter_subcategory = new ArrayAdapter<String>(mContext,  android.R.layout.simple_spinner_item, subCategories);
//										subCategorySpinner.setAdapter(adapter_subcategory);
									} else {
										if (Utils.isOnline(mContext)) {
											subcat = 1;
											productSpinner.setVisibility(View.GONE);
											progressView.setVisibility(View.VISIBLE);
											getProducts(menuCategoryList.get(j).getCategoryId());
										} else {
											Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
										}
									}
								}
							}*/
                            categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    position--;
                                    if (position < 0) {
                                        subCategorySpinner.setSelection(0);
                                        productSpinner.setVisibility(View.GONE);
                                    } else {
                                        if (menuCategoryList.get(position).getDoescontainCat().equalsIgnoreCase("Y")) {
                                            productSpinner.setVisibility(View.VISIBLE);
                                            subcat = 2;
											/*final String[] subCategories = new String[menuCategoryList.get(position).getSubCategories().length + 1];
											subCategories[0] = "Select";
											for (int i = 1; i < subCategories.length; i++) {
												subCategories[i] = menuCategoryList.get(i-1).getCategoryName();
											}
											ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(mContext,  android.R.layout.simple_spinner_item, subCategories);
											subCategorySpinner.setAdapter(adapter_state);*/

                                            ArrayList<String> subCategories = new ArrayList<>(menuCategoryList.get(position).getSubCategories().length);

                                            MenuCategoryBean[] bean = menuCategoryList.get(position).getSubCategories();
//											final String[] subCategories = new String[menuCategoryList.get(position).getSubCategories().length + 1];
                                            subCategories.add(0, "Select");
                                            for (int i = 0; i < bean.length; i++) {
                                                String name = bean[i].getCategoryName();
                                                subCategories.add(name);
                                            }
                                            ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, subCategories);
                                            subCategorySpinner.setAdapter(adapter_state);

                                            final MenuCategoryBean[] subCategoriesArr = menuCategoryList.get(position).getSubCategories();

                                            subCategorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                    position--;
                                                    if (position > 0) {
                                                        productSpinner.setVisibility(View.VISIBLE);
                                                        int categoryId = subCategoriesArr[position].getCategoryId();
                                                        subcat = 3;
                                                        getProducts(categoryId);
                                                    } else {
                                                        productSpinner.setVisibility(View.GONE);
                                                        productSpinner.setAdapter(null);

                                                    }
                                                }

                                                @Override
                                                public void onNothingSelected(
                                                        AdapterView<?> parent) {
                                                }
                                            });
                                        } else {
                                            if (Utils.isOnline(mContext)) {
                                                productSpinner.setVisibility(View.GONE);
                                                progressView.setVisibility(View.VISIBLE);
                                                subcat = 1;
                                                getProducts(menuCategoryList.get(position).getCategoryId());
                                            } else {
                                                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
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

    private void getProducts(int categoryId) {

        this.categoryId = categoryId;
        ProductRequestBean requestBean = new ProductRequestBean();
        requestBean.setApp_version(Utils.getAppVersion(mContext));
        requestBean.setDevice_type(Constants.DEVICE_TYPE);
        //		requestBean.setRestaurant_id(PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID));
//		requestBean.setRestaurant_id(2);
        requestBean.setRestaurant_id(PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID));
        requestBean.setCategoryId(categoryId);

        if (subcat == 3) {
            requestBean.setSubCategory("true");
        }

        final String param = new Gson().toJson(requestBean, ProductRequestBean.class);

        StringRequest request = new StringRequest(Method.POST, Constants.PRODUCT_BY_CATEGORY_ID_API, productsSuccess(), menuError()) {

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

    private com.android.volley.Response.Listener<String> productsSuccess() {

        return new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response != null && response.length() > 0) {
                    progressView.setVisibility(View.GONE);
                    Utils.longInfo(response);
                    try {
                        Gson gson = new Gson();
                        ProductBeanOuter productBeanOuter = gson.fromJson(response, ProductBeanOuter.class);

                        if (productBeanOuter.getStatus().equals("true")) {

                            ProductBean[] productArray = productBeanOuter.getProductlist();
                            productList = new ArrayList<>();

                            for (ProductBean bean : productArray) {
                                ProductBean productBean = new ProductBean();
                                productBean.setProductId(bean.getProductId());
                                productBean.setProductName(bean.getProductName());
                                productBean.setProductDesc(bean.getProductDesc());
                                productBean.setPrice(bean.getPrice());
                                productBean.setProductType(bean.getProductType());
                                productBean.setSpiceLevel(bean.getSpiceLevel());
                                productBean.setPreparationTime(bean.getPreparationTime());
                                productBean.setDetail_url_type(bean.getDetail_url_type());
                                productBean.setProductUrl(bean.getProductUrl());
                                productBean.setProductDetailUrl(bean.getProductDetailUrl());
                                productList.add(productBean);
                            }
                            String[] products = new String[productList.size() + 1];
                            products[0] = "Select";
                            for (int i = 1; i < products.length; i++) {
                                products[i] = productList.get(i - 1).getProductName();
                            }
                            ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, products);
                            if (subcat == 1) {
//								subCategorySpinner.setAdapter(null);
//								subCategorySpinner.setAdapter(adapter_state);
                                subCategorySpinner.setAdapter(null);
                                subCategorySpinner.setVisibility(View.GONE);
//								productSpinner.setVisibility(View.GONE);
//								productSpinner.setAdapter(null);
                                productSpinner.setVisibility(View.VISIBLE);
                                productSpinner.setAdapter(adapter_state);
                            } else if (subcat == 2) {
                                productSpinner.setAdapter(adapter_state);
                                productSpinner.setVisibility(View.VISIBLE);
                            } else if (subcat == 3) {
                                productSpinner.setAdapter(adapter_state);
                                productSpinner.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void validateFields() {

        if (categorySpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(mContext, R.string.select_cousine, Toast.LENGTH_SHORT).show();
            return;
        }

        if (subCategorySpinner.getSelectedItemPosition() == 0 && subCategorySpinner.getVisibility() == View.VISIBLE) {
            Toast.makeText(mContext, R.string.select_a_sub_category, Toast.LENGTH_SHORT).show();
            return;
        }

		/*if (productSpinner.getSelectedItemPosition() == 0 && productSpinner.getVisibility() == View.VISIBLE) {
			Toast.makeText(mContext, R.string.select_menu_item, Toast.LENGTH_SHORT).show();
			return;
		}*/

        if (productSpinner.getSelectedItemPosition() == 0 && productSpinner.getVisibility() == View.VISIBLE) {
            Toast.makeText(mContext, R.string.select_a_product, Toast.LENGTH_SHORT).show();
            return;
        }

        if (rating.getRating() == 0.0) {
            Toast.makeText(mContext, R.string.rate_food, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(review.getText().toString())) {
            Toast.makeText(mContext, R.string.write_review, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name.getText().toString())) {
            Toast.makeText(mContext, R.string.enter_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mobileNumber.getText().toString())) {
            Toast.makeText(mContext, R.string.enter_mobile, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mobileNumber.getText().toString().length() < 10) {
            Toast.makeText(mContext, R.string.enter_ten_digit_mobile_number, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.Validate_EMail(mContext, email.getText().toString().trim(), getString(R.string.enter_valid_email))) {
            return;
        }

        if (birthday.getText().toString().equals("Select Birthday")) {
            Toast.makeText(mContext, R.string.select_birthday, Toast.LENGTH_SHORT).show();
            return;
        }

        if (anniversary.getText().toString().equals("Select Anniversary")) {
            Toast.makeText(mContext, R.string.select_anniversary, Toast.LENGTH_SHORT).show();
            return;
        }


        RateRecipeParams param = new RateRecipeParams();
        param.setApp_version(Utils.getAppVersion(mContext));
        param.setCategoryId(categoryId);
        param.setProductId(productId);
        param.setDescription(review.getText().toString());
        param.setName(name.getText().toString());
        param.setDevice_type(Constants.DEVICE_TYPE);
        String doa = Utils.ConvertDateFormat(anniversary.getText().toString(), "yyyy-MM-dd", "dd MMM, yyyy");
        param.setDoa(doa);
        String dob = Utils.ConvertDateFormat(birthday.getText().toString(), "yyyy-MM-dd", "dd MMM, yyyy");
        param.setDob(dob);
        param.setMobile(mobileNumber.getText().toString());
        param.setEmailId(email.getText().toString());
        param.setRestaurant_id(PreferencesUtils.getInt(mContext, Constants.RESTAURANT_ID));
//		param.setRestaurant_id(2);
        param.setRating(rating.getRating());

        final String paramStr = new Gson().toJson(param, RateRecipeParams.class);
        Log.i("RECIPE_PARAM", paramStr.toString());

        if (Utils.isOnline(mContext)) {
//			progressView.setVisibility(View.VISIBLE);
//			updateRatingToServer(param);
        } else {
            Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRatingToServer(RateRecipeParams param) {
        final String paramStr = new Gson().toJson(param, RateRecipeParams.class);

        StringRequest request = new StringRequest(Method.POST, Constants.RATE_RECIPE_API, recipeSuccess(), recipeError()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("data", paramStr);
                Log.i("RECIPE_PARAM", param.toString());
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

                    progressView.setVisibility(View.GONE);
                    Utils.longInfo(response);

                    try {
                        JSONObject obj = new JSONObject(response);
                        String status = obj.getString("status");
                        if (status != null && status.equals("true")) {
                            Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            mContext.startActivity(new Intent(mContext, HomeActivity.class));
                            getActivity().finish();
                        } else {
                            Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                progressView.setVisibility(View.GONE);
                Toast.makeText(mContext, R.string.network_failure, Toast.LENGTH_SHORT).show();
            }

        };
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_submit:
                validateFields();
                break;

            case R.id.birthday:

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(mContext,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            String dob = Utils.ConvertDateFormat(selectedDate, "dd MMM, yyyy", "dd-MM-yyyy");
                            birthday.setText(dob);
                        }, mYear, mMonth, mDay);
                dpd.show();
                dpd.getDatePicker().setMaxDate(new Date().getTime());
                break;

            case R.id.anniversary:
                final Calendar c1 = Calendar.getInstance();
                int mYear1 = c1.get(Calendar.YEAR);
                int mMonth1 = c1.get(Calendar.MONTH);
                int mDay1 = c1.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd1 = new DatePickerDialog(mContext,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            String aniversary = Utils.ConvertDateFormat(selectedDate, "dd MMM, yyyy", "dd-MM-yyyy");
                            anniversary.setText(aniversary);
//					anniversary.setText(year + "-"
//							+ (monthOfYear + 1) + "-" + dayOfMonth);

                        }, mYear1, mMonth1, mDay1);
                dpd1.show();
                dpd1.getDatePicker().setMaxDate(new Date().getTime());

                break;

        }

    }
}