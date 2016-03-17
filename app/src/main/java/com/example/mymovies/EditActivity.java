package com.example.mymovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class EditActivity extends AppCompatActivity {

    public static final String API_URL = "https://www.omdbapi.com/?i=";

    private EditText editMovieName ;
    private String textMovieName ;
    private EditText editMovieDesc ;
    private String textMovieDesc ;
    private EditText editMovieURL ;
    private String textMovieURL ;
    private int textMovieId ;
    private String textMovieImdb ;
    private ProgressDialog progress ;
    private ProgressDialog posterProgress ;

    String url;
    String userRequest ;

    Intent intent ;
    Movie newMovie ;
    MoviesHandler movieHandler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movie);

        intent = getIntent();
        userRequest = intent.getStringExtra("UserRequest");

        editMovieName = (EditText) findViewById(R.id.textMovieName);
        editMovieDesc = (EditText) findViewById(R.id.textMovieDesc);
        editMovieURL = (EditText) findViewById(R.id.textMovieURL);

//--    Toast.makeText(this, "user request is:"+userRequest, Toast.LENGTH_LONG).show();
        // if ADD_MOVIE : do nothing
        if (userRequest.equals(MainActivity.EDIT_MOVIE)) {
            // if EDIT_MOVIE : get movie id, retrieve it from DB and present the data
            textMovieId = intent.getIntExtra("MovieID",-1);
            int id = (textMovieId) ;

            movieHandler = new MoviesHandler(this) ;
            newMovie = movieHandler.getOneMovie(id);

            editMovieName.setText(newMovie.getTitle());
            editMovieDesc.setText(newMovie.getDescription());
            editMovieURL.setText(newMovie.getURL());
        }
        else if (userRequest.equals(MainActivity.INTERNET_MOVIE)) {
            // if INTERNET_MOVIE : get movie imdb, retrieve it from the internet and present it
            textMovieImdb = intent.getStringExtra("MovieImdb");
            url = API_URL + textMovieImdb;

//--        Toast.makeText(EditActivity.this,"url recieved:"+ url , Toast.LENGTH_LONG).show();
            SpecificMovieSearch myTask = new SpecificMovieSearch();
            myTask.execute(url);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        textMovieURL = editMovieURL.getText().toString() ;
        if (!textMovieURL.equals("")) {
            ShowURL myURL = new ShowURL();
            myURL.execute(textMovieURL);
        }
    }

    // do when clicking "OK" : updating the DB by the according to the initial request
    public void executeMethod(View v) {
        textMovieName = editMovieName.getText().toString() ;
        textMovieDesc = editMovieDesc.getText().toString() ;
        textMovieURL = editMovieURL.getText().toString() ;

        if (!textMovieName.equals("")) {
            if (userRequest.equals(MainActivity.ADD_MOVIE)) {
                // if arrieved with "ADD_MOVIE" request : add it to the DB
                newMovie = new Movie ( 0 , textMovieName , textMovieDesc , textMovieURL) ;
                movieHandler = new MoviesHandler(this) ;
                movieHandler.addMovie(newMovie);
            } else if (userRequest.equals(MainActivity.EDIT_MOVIE)) {
                // if arrieved with "EDIT_MOVIE" request : update it in the DB
                newMovie = new Movie ( textMovieId , textMovieName , textMovieDesc , textMovieURL) ;
                movieHandler = new MoviesHandler(this) ;
                movieHandler.updateMovie(newMovie);
            } else if (userRequest.equals(MainActivity.INTERNET_MOVIE)) {
                // if arrieved with "INTERNET_MOVIE" request : add it to the DB
                newMovie = new Movie ( 0 , textMovieName , textMovieDesc , textMovieURL) ;
                movieHandler = new MoviesHandler(this) ;
                movieHandler.addMovie(newMovie);
            }
            setResult(RESULT_OK);
            finish();
        } else
            Toast.makeText(this, getResources().getString(R.string.editErrMsgNoMovieName), Toast.LENGTH_LONG).show();
    }

    // do when clicking "CANCEL" : go back to tne former layer
    public void cancelMethod(View v) {
        finish();
    }

    // do when clicking "SHOW" : perform an async task that retrieve the url and present it
    public void showPicture (View v){
        textMovieURL = editMovieURL.getText().toString() ;
        if (!textMovieURL.equals("")) {
            ShowURL myURL = new ShowURL();
            myURL.execute(textMovieURL);
        }
    }

    // perform the picture internet search by url using async task
    class ShowURL extends AsyncTask<String, Void, String> {
        Bitmap bitmap;
        @Override
        protected void onPreExecute() {
            // do this first - show a progress bar
            posterProgress = new ProgressDialog(EditActivity.this);
            posterProgress.setTitle(getResources().getString(R.string.editPictureLoadingDialogTitle));
            posterProgress.setMessage(getResources().getString(R.string.editPictureLoadingDialogMessage));
            posterProgress.show();
        }
        @Override
        protected String doInBackground(String... params) {
            BufferedReader input = null;
            HttpURLConnection connection = null;
            StringBuilder response = new StringBuilder();
//--+1  take this sleep off
            SystemClock.sleep(500);
            try {
                String queryString = "";
                // prepare a URL object :
                URL url = new URL(params[0] + queryString);
                // open a connection
                connection = (HttpURLConnection) url.openConnection();
                // check the result status of the connection: if not good - return
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) { return null; }
                InputStream in = (InputStream) url.getContent();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // close the stream if it exists:
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // close the connection if it exists:
                if (connection != null) {
                    connection.disconnect();
                }
            }
            // get the string from the string builder:
            return response.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            // do this last (with the result from the doInBackground() ) show the picture
            posterProgress.dismiss();
            ImageView myimg= (ImageView) findViewById(R.id.imageView);
            myimg.setImageBitmap(bitmap);
        }
    }

    // perform the movie details internet search by imdb using async task
    class SpecificMovieSearch extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // do this first (UI Thread)
            progress = new ProgressDialog(EditActivity.this);
            progress.setTitle(getResources().getString(R.string.editDetailsLoadingDialogTitle));
            progress.setMessage(getResources().getString(R.string.editDetailsLoadingDialogMessage));
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            BufferedReader input = null;
            HttpURLConnection connection = null;
            StringBuilder response = new StringBuilder();
//--+1  take this sleep off
            SystemClock.sleep(500);
            try {
                //create a url:
                URL url = new URL(params[0]);
                //create a connection and open it:
                connection = (HttpURLConnection) url.openConnection();
                //        Toast.makeText(this, url , Toast.LENGTH_LONG).show();
                //status check: if not good - return.
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){ return null; }
                //get a buffer reader to read the data stream as characters(letters) in a buffered way.
                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                //go over the input, line by line
                String line="";
                while ((line=input.readLine())!=null){
                    //append it to a StringBuilder to hold the resulting string
                    response.append(line+"\n");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                //must close the reader
                if (input!=null){
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //must disconnect the connection
                if(connection!=null){ connection.disconnect();
                }
            }
            //return the collected string: this will be returned to : onPostExecute(String result)
            return response.toString();
        }
        @Override
        protected void onPostExecute(String s) {
            JSONObject responseObject = null;
            progress.dismiss();
            try {
                responseObject = new JSONObject(s);
                JSONObject movieObject = responseObject;
                editMovieName.setText(movieObject.getString("Title"));
                editMovieDesc.setText(movieObject.getString("Plot"));
                editMovieURL.setText(movieObject.getString("Poster"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
