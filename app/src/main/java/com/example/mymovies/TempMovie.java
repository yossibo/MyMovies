package com.example.mymovies;

//*** Temporary movie object, used to pass the movie imdb
 public class TempMovie {
    private String  imdbID;
    private String  title;

    public TempMovie(String imdbID, String title) {
        this.imdbID = imdbID;
        this.title  = title;
    }

    public String getImdbID() { return imdbID; }
    public String getTitle()  { return title;  }

    public void setImdbID(String imdbID) { this.imdbID = imdbID; }
    public void setTitle(String title)   { this.title  = title;  }
}
