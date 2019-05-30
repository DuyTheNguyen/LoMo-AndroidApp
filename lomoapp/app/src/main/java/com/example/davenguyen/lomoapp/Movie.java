package com.example.davenguyen.lomoapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dave Nguyen on 08-Oct-17.
 * This class is used for store information of the movie
 */

public class Movie implements Parcelable {
    private String director,image,name, rating, year, description, genre,id;
    public Movie()
    {
    }

    public Movie(String director, String image, String name, String rating, String year, String description, String genre,String id) {
        this.director = director;
        this.image = image;
        this.name = name;
        this.rating = rating;
        this.year = year;
        this.description= description;
        this.genre = genre;
        this.id=id;
    }

    public String getDirector() {
        return director;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public String getYear() {
        return year;
    }

    public String getDescription() {
        return description;
    }

    public String getGenre() {
        return genre;
    }

    public String getID() {
        return id;
    }

    /**
     * The following block of code parcels/ unparcels data for distribution between activities
     *Describe the contents in the parcel
     *interface forces implementation
     * */

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(name);
        out.writeString(director);
        out.writeString(image);
        out.writeString(rating);
        out.writeString(year);
        out.writeString(description);
        out.writeString(genre);
        out.writeString(id);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>()
    {
        public Movie createFromParcel(Parcel in)
        {
            return new Movie(in);
        }
        public Movie[] newArray(int size)
        {
            return new Movie[size];
        }
    };

    //Private constructor called internally only
    private Movie(Parcel in)
    {
        name = in.readString();
        director=in.readString();
        image=in.readString();
        rating=in.readString();
        year=in.readString();
        description=in.readString();
        genre=in.readString();
        id=in.readString();
    }
}


