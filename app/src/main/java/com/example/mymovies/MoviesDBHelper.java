package com.example.mymovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//*** Movies DB Helper
public class MoviesDBHelper extends SQLiteOpenHelper {

    public MoviesDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // creates the DB
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(MoviesDBConstants.LOG_TAG, "Creating all the tables");
        String CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesDBConstants.MOVIES_TABLE_NAME + "(" + MoviesDBConstants.MOVIE_ID +
                " INTEGER PRIMARY KEY autoincrement," + MoviesDBConstants.MOVIE_TITLE + " TEXT," +
                MoviesDBConstants.MOVIE_DESCRIPTION + " TEXT," + MoviesDBConstants.MOVIE_URL + " TEXT)";
        try {
            db.execSQL(CREATE_MOVIES_TABLE);
        } catch (SQLiteException ex) {
            Log.e(MoviesDBConstants.LOG_TAG, "Create table exception: " + ex.getMessage());
        }
    }

    // upgrades thr DB (if needed)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MoviesDBConstants.LOG_TAG, "Upgrading database from version " + oldVersion +
                " to " + newVersion + ", which will destroy all old date");
        db.execSQL("DROP TABLE IF EXISTS " + MoviesDBConstants.MOVIES_TABLE_NAME);
        onCreate(db);
    }
}
