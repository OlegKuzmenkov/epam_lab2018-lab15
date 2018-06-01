package com.oleg_kuzmenkov.android.dbwithcontentprovider;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = "Message";

    private Button mSubmitButton;
    private Button mDeleteButton;
    private EditText mTitleNoteEditText;
    private EditText mDateOfCreationNoteEditText;
    private EditText mDetailsNoteEditText;
    private SimpleCursorAdapter mSimpleCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Prepare the loader.  Either re-connect with an existing one or start a new one.
        getLoaderManager().initLoader(0, null, this);

        // Fields from the database (projection) Must include the _id column for the adapter to work
        String[] from = new String[] { DBHelper.COLUMN_ID,DBHelper.COLUMN_TITLE,DBHelper.COLUMN_DATE,DBHelper.COLUMN_DETAILS };
        int[] to = new int[] { R.id.note_id,R.id.note_title,R.id.note_date_of_creation,R.id.note_details };

        mSimpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.note_view, null, from, to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        setListAdapter(mSimpleCursorAdapter);

        mTitleNoteEditText = findViewById(R.id.note_title);
        mDateOfCreationNoteEditText = findViewById(R.id.note_date_of_creation);
        mDetailsNoteEditText = findViewById(R.id.note_details);

        mSubmitButton = findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG,"onSubmitButtonClick");
                ContentValues values = new ContentValues();

                //get information from the fields and clear them
                values.put(DBHelper.COLUMN_TITLE, mTitleNoteEditText.getText().toString());
                mTitleNoteEditText.setText("");
                values.put(DBHelper.COLUMN_DATE, mDateOfCreationNoteEditText.getText().toString());
                mDateOfCreationNoteEditText.setText("");
                values.put(DBHelper.COLUMN_DETAILS, mDetailsNoteEditText.getText().toString());
                mDetailsNoteEditText.setText("");

                //insert values into Database
                getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
            }
        });

        mDeleteButton = findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContentResolver().delete(MyContentProvider.CONTENT_URI, null, null);
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(LOG_TAG,"onCreateLoader");
        //String[] projection = { "_id","title", "date" };
        return new CursorLoader(this, MyContentProvider.CONTENT_URI, null, null, null, null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG,"onLoadFinished");
        mSimpleCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(LOG_TAG,"onLoaderReset");
        // data is not available anymore, delete reference
        mSimpleCursorAdapter.swapCursor(null);
    }
}
