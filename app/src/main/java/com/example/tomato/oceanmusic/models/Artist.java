package com.example.tomato.oceanmusic.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by IceMan on 11/12/2016.
 */

public class Artist implements Parcelable {
    private int id;
    private String name;
    private ArrayList<Song> lstSong;
    private String numberAlbum;
    private String numberSong;

    public Artist(int id, String name, String numberAlbum, String numberSong) {
        this.id = id;
        this.name = name;
        this.numberAlbum = numberAlbum;
        this.numberSong = numberSong;
    }


    protected Artist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        numberAlbum = in.readString();
        numberSong = in.readString();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Song> getLstSong() {
        return lstSong;
    }


    public String getNumberAlbum() {
        return numberAlbum;
    }

    public void setNumberAlbum(String numberAlbum) {
        this.numberAlbum = numberAlbum;
    }

    public String getNumberSong() {
        return numberSong;
    }

    public void setNumberSong(String numberSong) {
        this.numberSong = numberSong;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(numberAlbum);
        parcel.writeString(numberSong);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        if (id != artist.id) return false;
        return name != null ? name.equals(artist.name) : artist.name == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
