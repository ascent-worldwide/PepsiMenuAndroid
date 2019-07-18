package com.pepsi.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class DatabaseProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    public static Cursor cursor = null;
    public DBHelper mDBHelper = null;
    SQLiteDatabase db = null;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseConstants.AUTHORITY;
        matcher.addURI(authority, DatabaseConstants.TABLENAME_PRODUCTS, DatabaseConstants.URI_TABLENAME_PRODUCTS_VALUE);
        matcher.addURI(authority, DatabaseConstants.TABLENAME_RECIPE_CATEGORY, DatabaseConstants.URI_TABLENAME_RECIPE_CATEGORY_VALUE);
        matcher.addURI(authority, DatabaseConstants.TABLENAME_RECIPE_PRODUCTS, DatabaseConstants.URI_TABLENAME_RECIPE_PRODUCT_VALUE);
        matcher.addURI(authority, DatabaseConstants.TABLENAME_SURVEY_QUESTION, DatabaseConstants.URI_TABLENAME_SURVEY_QUESTION_VALUE);
        matcher.addURI(authority, DatabaseConstants.TABLENAME_RATE_RECIPE_REQUEST, DatabaseConstants.URI_TABLENAME_RATE_RECIPE_REQUEST_VALUE);
        matcher.addURI(authority, DatabaseConstants.TABLENAME_RATE_RESTAURANT_REQUEST, DatabaseConstants.URI_TABLENAME_RATE_RESTAURANT_REQUEST_VALUE);

        return matcher;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = mDBHelper.getReadableDatabase();
        long avilablesize = db.getMaximumSize();
        db.setMaximumSize(avilablesize);
        int deleted = 0;

        if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_PRODUCTS_VALUE) {
            deleted = db.delete(DatabaseConstants.TABLENAME_PRODUCTS, selection, selectionArgs);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RECIPE_CATEGORY_VALUE) {
            deleted = db.delete(DatabaseConstants.TABLENAME_RECIPE_CATEGORY, selection, selectionArgs);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RECIPE_PRODUCT_VALUE) {
            deleted = db.delete(DatabaseConstants.TABLENAME_RECIPE_PRODUCTS, selection, selectionArgs);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_SURVEY_QUESTION_VALUE) {
            deleted = db.delete(DatabaseConstants.TABLENAME_SURVEY_QUESTION, selection, selectionArgs);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RATE_RECIPE_REQUEST_VALUE) {
            deleted = db.delete(DatabaseConstants.TABLENAME_RATE_RECIPE_REQUEST, selection, selectionArgs);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RATE_RESTAURANT_REQUEST_VALUE) {
            deleted = db.delete(DatabaseConstants.TABLENAME_RATE_RESTAURANT_REQUEST, selection, selectionArgs);
        }

        return deleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db = mDBHelper.getWritableDatabase();
        long avilablesize = db.getMaximumSize();
        db.setMaximumSize(avilablesize);

        if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_PRODUCTS_VALUE) {
            db.insert(DatabaseConstants.TABLENAME_PRODUCTS, null, values);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RECIPE_CATEGORY_VALUE) {
            db.insert(DatabaseConstants.TABLENAME_RECIPE_CATEGORY, null, values);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RECIPE_PRODUCT_VALUE) {
            db.insert(DatabaseConstants.TABLENAME_RECIPE_PRODUCTS, null, values);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_SURVEY_QUESTION_VALUE) {
            db.insert(DatabaseConstants.TABLENAME_SURVEY_QUESTION, null, values);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RATE_RECIPE_REQUEST_VALUE) {
            db.insert(DatabaseConstants.TABLENAME_RATE_RECIPE_REQUEST, null, values);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RATE_RESTAURANT_REQUEST_VALUE) {
            db.insert(DatabaseConstants.TABLENAME_RATE_RESTAURANT_REQUEST, null, values);
        }

        return uri;
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        db = mDBHelper.getReadableDatabase();
        long avilablesize = db.getMaximumSize();
        db.setMaximumSize(avilablesize);
        cursor = null;

        if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_PRODUCTS_VALUE) {
            cursor = db.query(DatabaseConstants.TABLENAME_PRODUCTS, projection, selection, selectionArgs, null, null, null);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RECIPE_CATEGORY_VALUE) {
            cursor = db.query(DatabaseConstants.TABLENAME_RECIPE_CATEGORY, projection, selection, selectionArgs, null, null, null);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RECIPE_PRODUCT_VALUE) {
            cursor = db.query(DatabaseConstants.TABLENAME_RECIPE_PRODUCTS, projection, selection, selectionArgs, null, null, null);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_SURVEY_QUESTION_VALUE) {
            cursor = db.query(DatabaseConstants.TABLENAME_SURVEY_QUESTION, projection, selection, selectionArgs, null, null, null);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RATE_RECIPE_REQUEST_VALUE) {
            cursor = db.query(DatabaseConstants.TABLENAME_RATE_RECIPE_REQUEST, projection, selection, selectionArgs, null, null, null);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RATE_RESTAURANT_REQUEST_VALUE) {
            cursor = db.query(DatabaseConstants.TABLENAME_RATE_RESTAURANT_REQUEST, projection, selection, selectionArgs, null, null, null);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int upCount = 0;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long avilablesize = db.getMaximumSize();
        db.setMaximumSize(avilablesize);

        if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_PRODUCTS_VALUE) {
            upCount = db.update(DatabaseConstants.TABLENAME_PRODUCTS, values, selection, selectionArgs);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RECIPE_CATEGORY_VALUE) {
            upCount = db.update(DatabaseConstants.TABLENAME_RECIPE_CATEGORY, values, selection, selectionArgs);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RECIPE_CATEGORY_VALUE) {
            upCount = db.update(DatabaseConstants.TABLENAME_RECIPE_PRODUCTS, values, selection, selectionArgs);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_SURVEY_QUESTION_VALUE) {
            upCount = db.update(DatabaseConstants.TABLENAME_SURVEY_QUESTION, values, selection, selectionArgs);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RATE_RECIPE_REQUEST_VALUE) {
            upCount = db.update(DatabaseConstants.TABLENAME_RATE_RECIPE_REQUEST, values, selection, selectionArgs);
        } else if (sUriMatcher.match(uri) == DatabaseConstants.URI_TABLENAME_RATE_RESTAURANT_REQUEST_VALUE) {
            upCount = db.update(DatabaseConstants.TABLENAME_RATE_RESTAURANT_REQUEST, values, selection, selectionArgs);
        }

        return upCount;
    }

    public class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DatabaseConstants.DB_NAME, null, DatabaseConstants.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTable(db);
            long avilablesize = db.getMaximumSize();
            db.setMaximumSize(avilablesize);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            long available_size = db.getMaximumSize();
            db.setMaximumSize(available_size);

            if (!db.isReadOnly()) {
                // Enable foreign key constraints
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }

        private void createTable(SQLiteDatabase db) {
            long avilablesize = db.getMaximumSize();
            db.setMaximumSize(avilablesize);

            StringBuffer query = new StringBuffer("create table " + DatabaseConstants.TABLENAME_PRODUCTS + "(");
            query.append(DatabaseConstants.COLUMN_CATEGORY_ID + "  VARCHAR,");
            query.append(DatabaseConstants.COLUMN_PRODUCT_DATA + " VARCHAR);");
            db.execSQL(query.toString());

            StringBuffer query1 = new StringBuffer("create table " + DatabaseConstants.TABLENAME_RECIPE_CATEGORY + "(");
            query1.append(DatabaseConstants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,");
            query1.append(DatabaseConstants.COLUMN_RECIPE_CATEGORIES + " VARCHAR);");
            db.execSQL(query1.toString());

            StringBuffer query2 = new StringBuffer("create table " + DatabaseConstants.TABLENAME_RECIPE_PRODUCTS + "(");
            query2.append(DatabaseConstants.COLUMN_CATEGORY_ID + "  VARCHAR,");
            query2.append(DatabaseConstants.COLUMN_PRODUCT_DATA + " VARCHAR);");
            db.execSQL(query2.toString());

            StringBuffer query3 = new StringBuffer("create table " + DatabaseConstants.TABLENAME_SURVEY_QUESTION + "(");
            query3.append(DatabaseConstants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,");
            query3.append(DatabaseConstants.COLUMN_SURVEY_QUESTION_JSON + " VARCHAR);");
            db.execSQL(query3.toString());

            StringBuffer query4 = new StringBuffer("create table " + DatabaseConstants.TABLENAME_RATE_RECIPE_REQUEST + "(");
            query4.append(DatabaseConstants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,");
            query4.append(DatabaseConstants.COLUMN_RATE_RECIPE_REQUEST + " VARCHAR);");
            db.execSQL(query4.toString());

            StringBuffer query5 = new StringBuffer("create table " + DatabaseConstants.TABLENAME_RATE_RESTAURANT_REQUEST + "(");
            query5.append(DatabaseConstants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,");
            query5.append(DatabaseConstants.COLUMN_RATE_RESTAURANT_REQUEST + " VARCHAR);");
            db.execSQL(query5.toString());
        }

    }

}