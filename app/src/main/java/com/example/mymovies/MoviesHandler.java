package com.example.mymovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

//*** Movies DB Handler, handles all requested queries from the DB
public class MoviesHandler {

    private MoviesDBHelper dbhelper;

    public MoviesHandler(Context context) {
        dbhelper = new MoviesDBHelper(context, MoviesDBConstants.DATABASE_NAME, null,
                MoviesDBConstants.DATABASE_VERSION);
    }

    // creates and returns a Movies Array List contains the movie lise retrieved from the DB
    public ArrayList<Movie> getAllMovies() {
        ArrayList<Movie> movieList = new ArrayList<>();
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        Cursor cursor = db.query(MoviesDBConstants.MOVIES_TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Movie m = new Movie(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3));
            movieList.add(m);
        }
        db.close();
        return movieList ;
    }

    // create and returns a single movie object retrieved from the DB by a specipic id
    public Movie getOneMovie (int id) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        Cursor cursor = db.query(MoviesDBConstants.MOVIES_TABLE_NAME, null, " _ID = " + id, null, null, null, null);
        cursor.moveToNext() ;
        Movie m = new Movie(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3));
        db.close();
        return m ;
    }

    // Add a movie into the DB
    public void addMovie(Movie movie) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        ContentValues newMovieValues = new ContentValues();
        // newMovieValues.put(MoviesDBConstants.MOVIE_ID, movie.getId());    //no  need because of incremental
        newMovieValues.put(MoviesDBConstants.MOVIE_TITLE, movie.getTitle());
        newMovieValues.put(MoviesDBConstants.MOVIE_DESCRIPTION, movie.getDescription());
        newMovieValues.put(MoviesDBConstants.MOVIE_URL, movie.getURL());

        // Inserting the new row, or throwing an exception if an error occurred
        try {
            db.insertOrThrow(MoviesDBConstants.MOVIES_TABLE_NAME, null, newMovieValues);
            //db.execSQL("insert into books (id, title, price) values(" + book.getId() +", '" + book.getTitle() + "', " + book.getPrice() + ")");
        } catch (SQLiteException ex) {
            Log.e(MoviesDBConstants.LOG_TAG, ex.getMessage());
            throw ex;
        }
        finally {
            db.close();
        }
    }

    // Update a movie in the DB
    public void updateMovie(Movie movie) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        ContentValues newMovieValues = new ContentValues();
        // newMovieValues.put(MoviesDBConstants.MOVIE_ID, movie.getId());    //no  need because of incremental
        newMovieValues.put(MoviesDBConstants.MOVIE_ID, movie.getId());
        newMovieValues.put(MoviesDBConstants.MOVIE_TITLE, movie.getTitle());
        newMovieValues.put(MoviesDBConstants.MOVIE_DESCRIPTION, movie.getDescription());
        newMovieValues.put(MoviesDBConstants.MOVIE_URL, movie.getURL());

        // Updating the new row, or throwing an exception if an error occurred
        try {
            db.update(MoviesDBConstants.MOVIES_TABLE_NAME, newMovieValues, "_ID = " + movie.getId(), null);
            //db.execSQL("insert into books (id, title, price) values(" + book.getId() +", '" + book.getTitle() + "', " + book.getPrice() + ")");
        } catch (SQLiteException ex) {
            Log.e(MoviesDBConstants.LOG_TAG, ex.getMessage());
            throw ex;
        }
        finally {
            db.close();
        }
    }

    // Delete a movie from the DB
    public void deleteMovie (int id) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        // Deleting the specipic row, or throwing an exception if an error occurred
        try {
            db.delete(MoviesDBConstants.MOVIES_TABLE_NAME, "_ID = " + id, null);
        } catch (SQLiteException ex) {
            Log.e(MoviesDBConstants.LOG_TAG, ex.getMessage());
            throw ex;
        }
        finally {
            db.close();
        }
    }

    // Delete all the movies from the DB
    public void deleteAllMovies () {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        // using null instead of a specipic id in the second parameter deletes all rws, or throws an exception if an error occurred
        try {
            db.delete(MoviesDBConstants.MOVIES_TABLE_NAME, null, null);
        } catch (SQLiteException ex) {
            Log.e(MoviesDBConstants.LOG_TAG, ex.getMessage());
            throw ex;
        }
        finally {
            db.close();
        }
    }
}

