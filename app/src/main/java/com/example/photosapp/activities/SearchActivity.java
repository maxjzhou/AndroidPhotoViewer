package com.example.photosapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.example.photosapp.R;
import com.example.photosapp.RecyclerAdapter;
import com.example.photosapp.model.Album;
import com.example.photosapp.model.Photo;
import com.example.photosapp.model.Tag;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ArrayList<Album> albumList;
    Album currentAlbum;
    Photo currentPhoto;
    int currentidx;
    Button searchToMain, searchButton;
    EditText searchBar;
    GridView gridView;
    ArrayList<Uri> uri;
    RecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        loadData();

        searchToMain = (Button) findViewById(R.id.searchToMain);
        searchBar = (EditText) findViewById(R.id.SearchBar);
        searchButton = (Button) findViewById(R.id.SearchButton);
        gridView = (GridView) findViewById(R.id.gridView);
        uri = new ArrayList<>();
        adapter = new RecyclerAdapter(this,uri);
        gridView.setAdapter(adapter);

        searchToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveData();
                startActivity(new Intent(SearchActivity.this, MainActivity.class));

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String n = searchBar.getText().toString();

                if (n.equals("") || searchBar.getText() == null) {
                    return;
                }

                while (uri.size() > 0) {
                    uri.remove(0);
                }

                showResults();

                searchBar.setText("");
            }
        });

    }



    private void saveData () {

        for (int i = 0; i < albumList.size(); i++) {
            if (albumList.get(i).getAlbumName().equals(currentAlbum.getAlbumName())) {
                albumList.set(i, currentAlbum);
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(albumList);
        editor.putString("album list", json);

        String json2 = gson.toJson(currentAlbum);
        editor.putString("selectedAlbum", json2);

        String json3 = gson.toJson(currentPhoto);
        editor.putString("selectedPhoto", json3);

        editor.putInt("selectedIndex", currentidx);


        editor.apply();
    }

    private void loadData () {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("album list", null);
        Type type = new TypeToken<ArrayList<Album>>() {
        }.getType();
        albumList = gson.fromJson(json, type);

        if (albumList == null) {
            albumList = new ArrayList<>();
        }

        String json2 = sharedPreferences.getString("selectedAlbum", "");
        currentAlbum = gson.fromJson(json2, Album.class);

        if (currentAlbum == null) {
            currentAlbum = new Album("");
        }


        String json3 = sharedPreferences.getString("selectedPhoto", "");
        currentPhoto = gson.fromJson(json3, Photo.class);

        if (currentPhoto == null) {
            currentPhoto = new Photo("");
        }

        currentidx = sharedPreferences.getInt("selectedIndex", 0);

    }

    public void showResults () {

        ArrayList<Photo> displayedPhotos = new ArrayList<>();

        for (int i = 0; i < albumList.size(); i++) {

            Album alb = albumList.get(i);
            traversePhotos(alb, displayedPhotos);

        }

        // Display photos on gridview from displayedPhotos

        displayPhotosOnGridView(displayedPhotos);

    }

    public void traversePhotos (Album alb, ArrayList<Photo> displayedPhotos) {

        for (int i = 0; i < alb.getPhotos().size(); i++) {

            Photo p = alb.getPhotos().get(i);
            traverseTags(p, displayedPhotos);
        }

    }

    public void traverseTags (Photo p, ArrayList<Photo> displayedPhotos) {

        ArrayList<Tag> tagList = p.getTags();

        for (int i = 0; i < tagList.size(); i++) {

            Tag t = tagList.get(i);

            if (t.toString().equals(searchBar.getText().toString())) {
                displayedPhotos.add(p);
                return;
            }

            if (matchTag(t,searchBar.getText().toString())) {
                displayedPhotos.add(p);
                return;
            }

        }

    }

    public boolean matchTag (Tag t, String s) {

        String name = t.getName();
        String value = t.getValue();

        if (s.length() <= name.length() && name.substring(0, s.length()).equals(s)) {
            return true;
        }

        if (s.length() <= value.length() && value.substring(0, s.length()).equals(s)) {
            return true;
        }

        return false;
    }

    public void displayPhotosOnGridView (ArrayList<Photo> displayedPhotos) {

        for (int i = 0; i < displayedPhotos.size(); i++) {

            Photo p = displayedPhotos.get(i);
            Uri imgUri = Uri.parse(p.getFilePath());

            if (!uri.contains(imgUri)) {
                uri.add(imgUri);
            }

        }

        adapter.notifyDataSetChanged();

    }

}