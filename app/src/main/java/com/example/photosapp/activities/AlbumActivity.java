package com.example.photosapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.photosapp.ImageAdapter;
import com.example.photosapp.R;
import com.example.photosapp.RecyclerAdapter;
import com.example.photosapp.model.Album;
import com.example.photosapp.model.Photo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {

    ArrayList<Uri> uri;
    RecyclerAdapter adapter;

    ArrayList<Album> albumList;
    Button add_button, remove_button, DisplayPhotoButton, backToMain;
    Album currentAlbum;
    Photo currentPhoto;
    int currentidx;
    ArrayList <Photo> photoList;
    GridView gridView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

         uri = new ArrayList<>();

        loadData();

        photoList = currentAlbum.getPhotos();

//        updateURI();

        DisplayPhotoButton = (Button) findViewById(R.id.DisplayPhotoButton);
        add_button = (Button) findViewById(R.id.add_button);
        remove_button = (Button) findViewById(R.id.remove_button);
        backToMain = (Button) findViewById(R.id.backToMain);
        gridView = findViewById(R.id.gridView);
        adapter = new RecyclerAdapter(this,uri);
        gridView.setAdapter(adapter);
        imageView = findViewById(R.id.imageView);




        adapter.notifyDataSetChanged();

        TextView albumNameTxt = findViewById(R.id.AlbumNameText);
        albumNameTxt.setText(currentAlbum.getAlbumName());

        DisplayPhotoButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                        startActivity(intent);
                    }
                });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

            }
        });

        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveData();
                startActivity(new Intent(AlbumActivity.this, MainActivity.class));

            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });


//        remove_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                StringLIST.remove(3);
//
//                arrayAdapter.notifyDataSetChanged();
//                saveData();
//            }
//        });

    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null) {
            Uri imgUri = data.getData();
            Log.d("message", "Trying to Add: " + imgUri);
            if(!uri.contains(imgUri)){
                uri.add(imgUri);
                Log.d("message", "Added: " + imgUri);
                currentAlbum.addPhoto(new Photo(imgUri.toString()));
            }
            else{
                Log.d("message", "Already Contains: " + imgUri);

            }
        }


        for (Photo photo : currentAlbum.getPhotos()) {
            if(photo!= null) {

//                Log.d("contains photo: ",  photo.getFilePath());
            }
        }


        adapter.notifyDataSetChanged();
        saveData();
    }


    @Override
    public void onBackPressed(){
        saveData();
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }




    private void updateURI() {
        photoList = currentAlbum.getPhotos();

        Log.d("albumsize", photoList.size() + "");

        uri.clear();
        for (Photo photo : photoList) {
            if(photo!= null) {
                String filePath = photo.getFilePath();
                uri.add(Uri.parse(filePath));
            }
        }

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

    }
