package com.pepsi.database;

import android.net.Uri;
import android.provider.BaseColumns;

public interface DatabaseConstants {
    String DB_NAME = "laVeranda.db";
    int DB_VERSION = 2;

    String COLUMN_ID = BaseColumns._ID;
    //String AUTHORITY = "com.pepsi.menu";
    String AUTHORITY = "com.clairvoyant.naijamenu";

    String TABLENAME_PRODUCTS = "product";
    String TABLENAME_RECIPE_CATEGORY = "recipe_categories";
    String TABLENAME_RECIPE_PRODUCTS = "recipe_products";
    String TABLENAME_SURVEY_QUESTION = "survey_question";
    String TABLENAME_RATE_RECIPE_REQUEST = "rate_recipe_request";
    String TABLENAME_RATE_RESTAURANT_REQUEST = "rate_restaurant_request";

    /**
     * product Columns
     */
    String COLUMN_CATEGORY_ID = "category_id";
    String COLUMN_PRODUCT_DATA = "product_data";

    /**
     * recipe_categories Columns
     */
    String COLUMN_RECIPE_CATEGORIES = "recipe_categories";

    /**
     * recipe_categories Columns
     */
    String COLUMN_SURVEY_QUESTION_JSON = "surveyquestions";

    /**
     * rate_recipe_request Columns
     */
    String COLUMN_RATE_RECIPE_REQUEST = "rate_recipe_request_data";

    /**
     * rate_restaurant_request Columns
     */
    String COLUMN_RATE_RESTAURANT_REQUEST = "rate_restaurant_request_data";

    /**
     * All URIs for Tables
     */
    Uri URI_TABLENAME_PRODUCTS = Uri.parse("content://" + AUTHORITY + "/" + TABLENAME_PRODUCTS);
    Uri URI_TABLENAME_RECIPE_CATEGORY = Uri.parse("content://" + AUTHORITY + "/" + TABLENAME_RECIPE_CATEGORY);
    Uri URI_TABLENAME_RECIPE_PRODUCT = Uri.parse("content://" + AUTHORITY + "/" + TABLENAME_RECIPE_PRODUCTS);
    Uri URI_TABLENAME_SURVEY_QUESTION = Uri.parse("content://" + AUTHORITY + "/" + TABLENAME_SURVEY_QUESTION);
    Uri URI_TABLENAME_RATE_RECIPE_REQUEST = Uri.parse("content://" + AUTHORITY + "/" + TABLENAME_RATE_RECIPE_REQUEST);
    Uri URI_TABLENAME_RATE_RESTAURANT_REQUEST = Uri.parse("content://" + AUTHORITY + "/" + TABLENAME_RATE_RESTAURANT_REQUEST);

    int URI_TABLENAME_PRODUCTS_VALUE = 100;
    int URI_TABLENAME_RECIPE_CATEGORY_VALUE = 101;
    int URI_TABLENAME_RECIPE_PRODUCT_VALUE = 102;
    int URI_TABLENAME_SURVEY_QUESTION_VALUE = 103;
    int URI_TABLENAME_RATE_RECIPE_REQUEST_VALUE = 104;
    int URI_TABLENAME_RATE_RESTAURANT_REQUEST_VALUE = 105;

}