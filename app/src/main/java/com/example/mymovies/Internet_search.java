package com.example.mymovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Internet_search extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String API_URL = "https://www.omdbapi.com/?s=";

    private ProgressDialog progress ;
    private EditText editLookFor;
    private String textLookFor;

    String url;
    Intent intent;
    ListView tempMoviesListView ;
    TempMovieAdapter MyTempAdapter ;
    ArrayList<TempMovie> tempMovieList = new ArrayList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intenet_search);

        intent = getIntent();

        editLookFor = (EditText) findViewById(R.id.lookFor);
        textLookFor = editLookFor.getText().toString();


    }

    @Override
    protected void onResume() {
        super.onResume();
        textLookFor = editLookFor.getText().toString();
        if (!textLookFor.equals("")) showResults();
    }

    // do when clicking on the "CANCEL" button - finish the intent
    public void cancelInternet(View v) {
        finish();
    }

    // do when clicking on the "GO" button - execute the internet search
    public void executeSearch(View v) {
        textLookFor = editLookFor.getText().toString();
        if (!textLookFor.equals("")) {
            showResults();
        } else
            Toast.makeText(this, getResources().getString(R.string.internetErrMsgNoValueEntered), Toast.LENGTH_LONG).show();
    }

    // perform the internet search and show the results
    private void showResults() {
//--        Toast.makeText(this,"text:"+ textLookFor , Toast.LENGTH_LONG).show();
        url = API_URL + textLookFor;
        url = url.replaceAll(" ","%20");
//--        Toast.makeText(this, "Search for:" + url, Toast.LENGTH_LONG).show();
        MyMovieSearch myTask = new MyMovieSearch();
        myTask.execute(url);
    }

    // perform the list of movies internet search by name using async task
    class MyMovieSearch extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // do this first - show a progress bar
            progress = new ProgressDialog(Internet_search.this);
            progress.setTitle(getResources().getString(R.string.internetListLoadingDialogTitle));
            progress.setMessage(getResources().getString(R.string.internetListLoadingDialogMessage));
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
                //status check: if connection not good - return.
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
            if(s == null || s.equals("")) {
                Toast.makeText(Internet_search.this, getResources().getString(R.string.internetErrMsgWebAccessFailed), Toast.LENGTH_LONG).show();
                progress.dismiss();
                return;
            }
            // if there's a valid response from the API, handle it with JSON
            JSONObject responseObject = null;
            try {
                responseObject = new JSONObject(s);
                JSONArray resultsArray = responseObject.getJSONArray("Search");
                tempMovieList.clear();
                // Iterate over the JSON array:
                for (int i = 0; i < resultsArray.length(); i++) {
                    // the JSON object in position i
                    JSONObject movieObject = resultsArray.getJSONObject(i);
                    // get the primitive values in the object
                    String title = movieObject.getString("Title");
                    String imdbID = movieObject.getString("imdbID");
                    TempMovie tm = new TempMovie(imdbID,title);
                    tempMovieList.add(tm);
                }
                progress.dismiss();
                // present the response using a list adapter
                tempMoviesListView = (ListView) findViewById(R.id.intenetListView);
                MyTempAdapter = new TempMovieAdapter(Internet_search.this,R.layout.activity_intenet_search, tempMovieList) ;
                tempMoviesListView.setAdapter(MyTempAdapter);
                // set a listener to catch a click on one of the movies in the list view
                tempMoviesListView.setOnItemClickListener(Internet_search.this);
                registerForContextMenu(tempMoviesListView);
            }
            catch (JSONException e) {
                Toast.makeText(Internet_search.this, getResources().getString(R.string.internetErrMsgNoMovieThisName), Toast.LENGTH_LONG).show();
                progress.dismiss();
                e.printStackTrace();
            }
        }
    }

    // a click listener on the list view
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent editIntent = new Intent(this,EditActivity.class) ;
        String selectedMovieId = tempMovieList.get(position).getImdbID();
        editIntent.putExtra("UserRequest", MainActivity.INTERNET_MOVIE) ;
        editIntent.putExtra("MovieImdb", selectedMovieId) ;
        startActivityForResult(editIntent, MainActivity.INTERNET_REQUEST);
//***** Remark : I prefer to finish the intent altougth the designer asked to leave it alive *****
        finish();
    }

//-----> Temp Movie Adapter class -----------------------------------------------------------------
    class TempMovieAdapter extends ArrayAdapter<TempMovie> {
        public TempMovieAdapter (Context content , int resource, List<TempMovie> objects) {
            super(content, resource, objects);
        }
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView ;
            if(v == null) {
                LayoutInflater v1 = LayoutInflater.from(getContext());
                v = v1.inflate(R.layout.single_item, null) ;
            }
            TempMovie singleMovie = getItem(position);

            TextView movieName = (TextView) v.findViewById(R.id.singleItemTextView) ;
            movieName.setText(singleMovie.getTitle());

            return v ;
        }
    }
}
