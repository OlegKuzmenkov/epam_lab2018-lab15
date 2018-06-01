package com.oleg_kuzmenkov.android.dbwithcontentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

public class MyContentProvider extends ContentProvider {
    private static final String LOG_TAG = "Message";
    // used for the UriMacher
    private static final int NOTES = 10;
    private static final int NOTE_ID = 20;
    private static final String AUTHORITY = "com.oleg_kuzmenkov.android.dbwithcontentprovider";
    private static final String BASE_PATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTE_ID);
    }
    //database
    private DBHelper mDatabase;

    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate ContentProvider");
        mDatabase = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(LOG_TAG, "Query in ContentProvider");
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Set the table
        queryBuilder.setTables(DBHelper.TABLE);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case NOTES:
                break;
            case NOTE_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(DBHelper.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDatabase.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // register to watch a content URI for changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
        long id;
        switch (uriType) {
            case NOTES:
                id = sqlDB.insert(DBHelper.TABLE, null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
        int rowsDeleted;

        switch (uriType) {
            case NOTES:
                rowsDeleted = sqlDB.delete(DBHelper.TABLE, selection, selectionArgs);
                break;
            case NOTE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DBHelper.TABLE, DBHelper.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(DBHelper.TABLE, DBHelper.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
