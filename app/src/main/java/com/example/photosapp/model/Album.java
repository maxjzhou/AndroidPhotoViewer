package com.example.photosapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Album  {

    private ArrayList<Photo> photos;
    String albumName;

    public Album(String name) {
        this.albumName = name;
        photos = new ArrayList<Photo>();
    }

    public String getAlbumName(){
        return albumName;
    }


    public void setAlbumName(String albumName){
        this.albumName = albumName;
    }


    public ArrayList<Photo> getPhotos()
    {
        return this.photos;
    }


    public void addPhoto(Photo photo) {
        photos.add(photo);
    }
}

