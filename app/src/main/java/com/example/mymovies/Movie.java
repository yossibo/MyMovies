package com.example.mymovies;

//*** Movie Class
public class Movie {

    private int     id;
    private String  title;
    private String  description;
    private String  URL;

    public int      getId()         { return id; }
    public String   getTitle()      { return title; }
    public String   getDescription(){ return description; }
    public String   getURL()        { return URL; }

    public void     setId(int id)                       { this.id = id; }
    public void     setTitle(String title)              { this.title = title; }
    public void     setDescription(String description)  { this.description = description; }
    public void     setURL(String URL)                  { this.URL = URL; }

    public Movie(int id, String title, String description, String URL) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.URL = URL;
    }

    public Movie() {
    }

    @Override
    public String toString() {
        return "Movie{" + "id=" + id + ", title='" + title + '\'' + ", description='" + description +
                '\'' + ", URL='" + URL + '\'' + '}';
    }
}
