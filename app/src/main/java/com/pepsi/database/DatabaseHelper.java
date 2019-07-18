package com.pepsi.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.clairvoyant.naijamenu.bean.RateRecipeBean;
import com.clairvoyant.naijamenu.bean.RateRestaurantBean;

import java.util.ArrayList;

public class DatabaseHelper {

    public static Cursor cursor = null;
    public static ContentResolver resolver = null;
    Context mContext = null;

    /**
     * insert product data
     *
     * @param context
     * @param productData
     * @param categoryId
     */
    public static void insertProductData(Context context, String productData, int categoryId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_CATEGORY_ID, categoryId);
        values.put(DatabaseConstants.COLUMN_PRODUCT_DATA, productData);

        context.getContentResolver()
                .insert(DatabaseConstants.URI_TABLENAME_PRODUCTS, values);
    }

    /**
     * get product by category id
     *
     * @param context
     * @param categoryId
     * @return
     */
    public static String getProductDataByCategoryId(Context context, String categoryId) {
        String productData = null;
        final Uri _addressUri = DatabaseConstants.URI_TABLENAME_PRODUCTS;

        String whereClause = DatabaseConstants.COLUMN_CATEGORY_ID + " = ?";
        String[] selectionArgs = new String[]{categoryId};

        Cursor _cursor = context.getContentResolver().query(_addressUri, null, whereClause, selectionArgs, null);

        _cursor.moveToFirst();

        while (_cursor.isAfterLast() == false) {
            productData = _cursor.getString(_cursor.getColumnIndexOrThrow(DatabaseConstants.COLUMN_PRODUCT_DATA));
            _cursor.moveToNext();
        }

        if (_cursor != null)
            _cursor.close();

        return productData;
    }

    /**
     * delete product by category
     *
     * @param context
     * @param categoryId
     */
    public static void deleteProductDataByCategoryId(Context context, String categoryId) {
        resolver = context.getContentResolver();
        int count = resolver.delete(DatabaseConstants.URI_TABLENAME_PRODUCTS,
                DatabaseConstants.COLUMN_CATEGORY_ID + "= ?",
                new String[]{categoryId});

        System.out.println(" Deleted prod from Cart " + count);
    }

    /**
     * insert recipe categories
     *
     * @param context
     * @param recipeCategories
     */
    public static void insertRecipeCategories(Context context, String recipeCategories) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_RECIPE_CATEGORIES, recipeCategories);

        context.getContentResolver()
                .insert(DatabaseConstants.URI_TABLENAME_RECIPE_CATEGORY, values);
    }

    /**
     * get recipe categories
     *
     * @param context
     * @return
     */
    public static String getRecipeCategories(Context context) {
        String recipeCategories = null;
        final Uri _addressUri = DatabaseConstants.URI_TABLENAME_RECIPE_CATEGORY;

        Cursor _cursor = context.getContentResolver().query(_addressUri, null, null, null, null);


        if (_cursor == null)
            return null;

        _cursor.moveToFirst();

        while (_cursor.isAfterLast() == false) {
            recipeCategories = _cursor.getString(_cursor.getColumnIndexOrThrow(DatabaseConstants.COLUMN_RECIPE_CATEGORIES));
            _cursor.moveToNext();
        }

        if (_cursor != null)
            _cursor.close();

        return recipeCategories;
    }

    /**
     * delete recipe categories
     *
     * @param context
     */
    public static void deleteRecipeCategories(Context context) {
        resolver = context.getContentResolver();
        resolver.delete(DatabaseConstants.URI_TABLENAME_RECIPE_CATEGORY, null, null);
    }

    /**
     * insert recipe product based on category id
     *
     * @param context
     * @param categoryId
     * @param recipeProduct
     */
    public static void insertRecipeProduct(Context context, String categoryId, String recipeProduct) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_CATEGORY_ID, categoryId);
        values.put(DatabaseConstants.COLUMN_PRODUCT_DATA, recipeProduct);

        context.getContentResolver()
                .insert(DatabaseConstants.URI_TABLENAME_RECIPE_PRODUCT, values);
    }

    /**
     * recipe products by category id
     *
     * @param context
     * @param categoryId
     * @return
     */
    public static String getRecipeProductByCategoryId(Context context, String categoryId) {
        String productData = null;
        final Uri _addressUri = DatabaseConstants.URI_TABLENAME_RECIPE_PRODUCT;

        String whereClause = DatabaseConstants.COLUMN_CATEGORY_ID + " = ?";
        String[] selectionArgs = new String[]{categoryId};

        Cursor _cursor = context.getContentResolver().query(_addressUri, null, whereClause, selectionArgs, null);

        _cursor.moveToFirst();

        while (_cursor.isAfterLast() == false) {
            productData = _cursor.getString(_cursor.getColumnIndexOrThrow(DatabaseConstants.COLUMN_PRODUCT_DATA));
            _cursor.moveToNext();
        }

        if (_cursor != null)
            _cursor.close();

        return productData;
    }

    /**
     * delete recipe product by category id
     *
     * @param context
     * @param categoryId
     */
    public static void deleteRecipeProductByCategoryId(Context context, String categoryId) {
        resolver = context.getContentResolver();
        resolver.delete(DatabaseConstants.URI_TABLENAME_RECIPE_PRODUCT,
                DatabaseConstants.COLUMN_CATEGORY_ID + "= ?",
                new String[]{categoryId});
    }

    public static void insertSurveyQuestionsJson(Context context, String surveyQuestionJson) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_SURVEY_QUESTION_JSON, surveyQuestionJson);

        context.getContentResolver()
                .insert(DatabaseConstants.URI_TABLENAME_SURVEY_QUESTION, values);
    }

    public static String getSurveyQuestionsJson(Context context) {
        String surveyQuestions = null;
        final Uri _addressUri = DatabaseConstants.URI_TABLENAME_SURVEY_QUESTION;

        Cursor _cursor = context.getContentResolver().query(_addressUri, null, null, null, null);

        _cursor.moveToFirst();

        while (_cursor.isAfterLast() == false) {
            surveyQuestions = _cursor.getString(_cursor.getColumnIndexOrThrow(DatabaseConstants.COLUMN_SURVEY_QUESTION_JSON));
            _cursor.moveToNext();
        }

        if (_cursor != null)
            _cursor.close();

        return surveyQuestions;
    }

    public static void deleteSurveyQuestionsJson(Context context) {
        resolver = context.getContentResolver();
        resolver.delete(DatabaseConstants.URI_TABLENAME_RATE_RESTAURANT_REQUEST, null, null);
    }

    public static void insertRateRecipeRequestJSOnDate(Context context, String rateRecipeRequestData) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_RATE_RECIPE_REQUEST, rateRecipeRequestData);

        context.getContentResolver()
                .insert(DatabaseConstants.URI_TABLENAME_RATE_RECIPE_REQUEST, values);
    }

    public static ArrayList<RateRecipeBean> getRateRecipeRequestJSOnDate(Context context) {
        ArrayList<RateRecipeBean> rateRecipeJSONRequestList = new ArrayList<>();
        final Uri _addressUri = DatabaseConstants.URI_TABLENAME_RATE_RECIPE_REQUEST;

        Cursor _cursor = context.getContentResolver().query(_addressUri, null, null, null, null);

        _cursor.moveToFirst();

        while (_cursor.isAfterLast() == false) {
            RateRecipeBean data = new RateRecipeBean();
            String id = _cursor.getString(_cursor.getColumnIndexOrThrow(DatabaseConstants.COLUMN_ID));
            String rateRecipeRequest = _cursor.getString(_cursor.getColumnIndexOrThrow(DatabaseConstants.COLUMN_RATE_RECIPE_REQUEST));

            data.setId(id);
            data.setRateRecipeJSONRequest(rateRecipeRequest);

            rateRecipeJSONRequestList.add(data);
            _cursor.moveToNext();
        }

        if (_cursor != null)
            _cursor.close();

        return rateRecipeJSONRequestList;
    }

    public static void deleteRateRecipeRequestJSOnDateById(Context context, String id) {
        resolver = context.getContentResolver();
        resolver.delete(DatabaseConstants.URI_TABLENAME_RATE_RECIPE_REQUEST,
                DatabaseConstants.COLUMN_ID + "= ?",
                new String[]{id});
    }

    public static void insertRateRestaurantRequestJSOnDate(Context context, String rateRestaurantRequestData) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_RATE_RESTAURANT_REQUEST, rateRestaurantRequestData);

        context.getContentResolver()
                .insert(DatabaseConstants.URI_TABLENAME_RATE_RESTAURANT_REQUEST, values);
    }

    public static ArrayList<RateRestaurantBean> getRateRestaurantRequestJSOnDate(Context context) {
        ArrayList<RateRestaurantBean> rateRestaurantJSONRequestList = new ArrayList<>();
        final Uri _addressUri = DatabaseConstants.URI_TABLENAME_RATE_RESTAURANT_REQUEST;

        Cursor _cursor = context.getContentResolver().query(_addressUri, null, null, null, null);

        _cursor.moveToFirst();

        while (_cursor.isAfterLast() == false) {
            RateRestaurantBean data = new RateRestaurantBean();

            data.setId(_cursor.getString(_cursor.getColumnIndexOrThrow(DatabaseConstants.COLUMN_ID)));
            data.setRateRestaurantJSONRequest(_cursor.getString(_cursor.getColumnIndexOrThrow(DatabaseConstants.COLUMN_RATE_RESTAURANT_REQUEST)));

            rateRestaurantJSONRequestList.add(data);
            _cursor.moveToNext();
        }

        if (_cursor != null)
            _cursor.close();

        return rateRestaurantJSONRequestList;
    }

    public static void deleteRateRestaurantRequestJSOnDateById(Context context, String id) {
        resolver = context.getContentResolver();
        resolver.delete(DatabaseConstants.URI_TABLENAME_RATE_RESTAURANT_REQUEST,
                DatabaseConstants.COLUMN_ID + "= ?",
                new String[]{id});
    }

}
