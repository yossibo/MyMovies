package com.example.mymovies;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final int    ADD_REQUEST = 1 ;
    public static final String ADD_MOVIE   = "ADD_MOVIE" ;
    public static final int    EDIT_REQUEST = 2 ;
    public static final String EDIT_MOVIE   = "EDIT_MOVIE" ;
    public static final int    INTERNET_REQUEST = 3 ;
    public static final String INTERNET_MOVIE   = "INTERNET_MOVIE" ;

    MoviesHandler movieHandler;
    ArrayList<Movie> movieList = new ArrayList<>() ;
    ListView moviesListView ;
    MovieAdapter MyAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialMovieDB();
        showMovies();
        moviesListView.setOnItemClickListener(this);
        registerForContextMenu(moviesListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMovies();
    }

    // insert a few movies to the DB in case it is empty
    // in case you dont want them, use the delete all movies option and then continue...
    public void initialMovieDB() {
        movieHandler = new MoviesHandler(this);
        movieList = movieHandler.getAllMovies() ;
        if ( movieList.size() == 0 ) {
            String initNames[] = {"Avatar","Star War","The Matrix","Toy story","Vertigo","Kazablan","Taxi driver"} ;
            String initDescription = new String("bla bla ") ;
            Movie m ;
            for (int i=0 ; i<initNames.length ; i++) {
                initDescription = initDescription + "bla ";
                m = new Movie ( i , initNames[i] , initDescription , "" ) ;
                movieHandler.addMovie(m);
            }
        }
    }

    // shows the movies from the DB on the main page listview
    public void showMovies() {
        movieHandler = new MoviesHandler(this);
        movieList = movieHandler.getAllMovies() ;

        moviesListView = (ListView) findViewById(R.id.listView);
        MyAdapter = new MovieAdapter(this,R.layout.activity_edit_movie, movieList) ;
        moviesListView.setAdapter(MyAdapter);
    }

    // on short click, open intent and start EDIT_MOVIE procedure
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent editIntent = new Intent(this,EditActivity.class) ;
        int selectedMovieId = movieList.get(position).getId();

        editIntent.putExtra("UserRequest", EDIT_MOVIE) ;
        editIntent.putExtra("MovieID", selectedMovieId) ;
        startActivityForResult(editIntent, EDIT_REQUEST);
    }

    // when coming back, check if lase intent ended OK and refresh the movieList view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) { showMovies(); }
    }

//-----> Movie Adapter class ------------------------------------------------------------------
    // presents the movies from the DB
    class MovieAdapter extends ArrayAdapter<Movie> {
        public MovieAdapter (Context content , int resource, List<Movie> objects) {
            super(content, resource, objects);
        }
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView ;
            if(v == null) {
                LayoutInflater v1 = LayoutInflater.from(getContext());
                v = v1.inflate(R.layout.single_item, null) ;
            }
            Movie singleMovie = getItem(position);

            TextView movieName = (TextView) v.findViewById(R.id.singleItemTextView) ;
            movieName.setText(singleMovie.getTitle());

            return v ;
        }
    }

//-----> onCreate popupMenu addButton -------------------------------------------------------------
    // treats the add movie button with a popup menu by opening the requested intent
    public void addMovie(View v) {
        ImageButton addMovie ;
        addMovie = (ImageButton) findViewById(R.id.plusButton);

        PopupMenu popup = new PopupMenu(this,addMovie);
        popup.getMenuInflater().inflate(R.menu.popup_add, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.manual_add): // Manual add
                        Intent addIntent = new Intent(MainActivity.this, EditActivity.class);
                        addIntent.putExtra("UserRequest", ADD_MOVIE);
                        startActivityForResult(addIntent, ADD_REQUEST);
                        return true;
                    case (R.id.internet_add): // internet add
                        Intent internetIntent = new Intent(MainActivity.this, Internet_search.class);
                        internetIntent.putExtra("UserRequest", INTERNET_MOVIE);
                        startActivityForResult(internetIntent, INTERNET_REQUEST);
                        return true;
                }
                return true;
            }
        });
        popup.show() ;
        showMovies();
    }

//-----> onCreate contextMenu main_listview -------------------------------------------------------
    // inflate the context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_main_listview, menu);
    }
    // treats the context menu, enables to edit or delete a movie
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int selectedMovieId = movieList.get(info.position).getId();
        switch (item.getItemId()) {
            case R.id.edit_movie_menu:
                Intent editIntent = new Intent(this, EditActivity.class);
                editIntent.putExtra("UserRequest", EDIT_MOVIE) ;
                editIntent.putExtra("MovieID", selectedMovieId) ;
                startActivityForResult(editIntent, EDIT_REQUEST);
                return true;
            case R.id.delete_movie_menu:
                movieHandler.deleteMovie(selectedMovieId);
                showMovies();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

//-----> onCreate optionMenu main_menu ------------------------------------------------------------
    // inflate the option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater optInflater = getMenuInflater();
        optInflater.inflate(R.menu.option_menu, menu);
        return true;
    }
    // treat the option menu, enables to delete all movies or Exit
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_movies:
                // ***** open an alert dialog to reconfirm the delete
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.optionDeleteAllMoviesMsgDialogTitle))
                        .setMessage(getResources().getString(R.string.optionDeleteAllMoviesMsgDialogMessage))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                movieHandler.deleteAllMovies();
                                showMovies();                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            case R.id.exit:
                // ***** open an alert dialog to reconfirm the exit request
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.optionExitMsgDialogTitle))
                        .setMessage(getResources().getString(R.string.optionExitMsgDialogMessage))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}



