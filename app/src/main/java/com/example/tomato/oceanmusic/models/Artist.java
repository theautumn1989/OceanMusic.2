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
    private String art;
    private ArrayList<Song> lstSong;

    public Artist(int id, String name, String art) {
        this.id = id;
        this.name = name;
        this.art = art;
    }

    public Artist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Artist(int id, String name, ArrayList<Song> lstSong) {
        this.id = id;
        this.name = name;
        this.lstSong = lstSong;
    }

    protected Artist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        art = in.readString();
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

    public void setLstSong(ArrayList<Song> lstSong) {
        this.lstSong = lstSong;
    }
    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(art);
    }

    // khi load phần artist sẽ có nhiều ca sĩ trùng nhau 2 hàm này sẽ giải  quyết vấn đề đó
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
