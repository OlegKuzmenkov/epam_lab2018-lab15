package com.oleg_kuzmenkov.android.dbwithcontentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "Message";
    private static final String DB_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    // Database table
    public static final String TABLE = "note";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DETAILS = "details";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text, "
            + COLUMN_DATE + " text,"
            + COLUMN_DETAILS + " text"
            + ");";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(LOG_TAG, "onCreate database");
        sqLiteDatabase.execSQL(DATABASE_CREATE);
        addSomeDataToDatabase(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void addSomeDataToDatabase(SQLiteDatabase sqLiteDatabase) {
        ContentValues cv = new ContentValues();
        for (int i=0;i<2;i++){
            cv.put(COLUMN_TITLE, "Go to shop");
            cv.put(COLUMN_DATE, "12.04.2018");
            cv.put(COLUMN_DETAILS,"Buy tomatoes and apples");
            sqLiteDatabase.insert(TABLE, null, cv);
        }
    }
}
