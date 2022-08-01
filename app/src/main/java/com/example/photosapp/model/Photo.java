package com.example.photosapp.model;

import java.util.ArrayList;

public class Photo{

    public ArrayList<Tag> tags;
    public String filePath;



    public Photo(String filePath) {
        tags = new ArrayList<>();
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void addTag(String name, String value){
        Tag tag = new Tag(name, value);
        tags.add(tag);
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }


}
