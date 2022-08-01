package com.example.photosapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.photosapp.R;
import com.example.photosapp.model.Album;
import com.example.photosapp.model.Photo;
import com.example.photosapp.model.Tag;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {

    ListView TagsList;
    ArrayList<String> list;
    Button AddTagButton, DeleteTag, backPicture, nextPicture, backToAlbum;
    EditText editText;
    ArrayAdapter<String> arrayAdapter;
    ArrayList <Album> albumList;
    Album currentAlbum;
    Photo currentPhoto;
    int currentidx;
    int selectedAlbum;
    int albumIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        loadData();

        TagsList = (ListView) findViewById(R.id.TagsList);
        AddTagButton = (Button) findViewById(R.id.AddTagButton);
        editText = (EditText) findViewById(R.id.Tag);
        DeleteTag = (Button) findViewById(R.id.DeleteTag);
        backPicture = (Button) findViewById(R.id.backPicture);
        nextPicture = (Button) findViewById(R.id.nextPicture);
        backToAlbum = (Button) findViewById(R.id.backToAlbum);

//        list = new ArrayList<String>();
//        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
//                android.R.layout.simple_list_item_1, list);

        generateSlideShow();

        displayTags();

        AddTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String names = editText.getText().toString();
//                list.add(names);
//                TagsList.setAdapter(arrayAdapter);
//                arrayAdapter.notifyDataSetChanged();
//                saveData();

                int hyphenIndex = 0;
                for (int i = 0; i < names.length(); i++) {
                    if (names.charAt(i) == '-') {
                        hyphenIndex = i;
                        break;
                    }
                }

                if (hyphenIndex == names.length()) {
                    System.out.println("Invalid input");
                    return;
                }

                String name = names.substring(0,hyphenIndex - 1);
                String value = names.substring(hyphenIndex + 2, names.length());

                currentPhoto.addTag(name, value);

                displayTags();

                editText.setText("");

                saveData();
            }
        });
        DeleteTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                list.remove(selectedAlbum);
//                albumList.remove(selectedAlbum);
//                arrayAdapter.notifyDataSetChanged();

                ArrayList<Tag> currentPhotoTags = currentPhoto.getTags();

                String names = editText.getText().toString();

                for (int i = 0; i < currentPhotoTags.size(); i++) {
                    if (names.equals(currentPhotoTags.get(i).toString())) {
                        currentPhotoTags.remove(i);
                        break;
                    }
                }

                displayTags();

                editText.setText("");

                saveData();
            }
        });

        nextPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
System.out.println("num of photos: " + currentAlbum.getPhotos().size());
System.out.println("albumIndex: " + albumIndex);
System.out.println("next clicked");

                if (albumIndex >= currentAlbum.getPhotos().size() - 1) {
                    albumIndex = 0;
                }
                else {
                    albumIndex++;
                }

                currentPhoto = currentAlbum.getPhotos().get(albumIndex);
                displayPhoto();
                displayTags();

System.out.println("Current photo filepath: " + currentAlbum.getPhotos().get(albumIndex).getFilePath());

            }
        });

        backPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
System.out.println("num of photos: " + currentAlbum.getPhotos().size());
System.out.println("albumIndex: " + albumIndex);
System.out.println("back clicked");

                if (albumIndex <= 0) {
                    albumIndex = currentAlbum.getPhotos().size() - 1;
                }
                else {
                    albumIndex--;
                }

                currentPhoto = currentAlbum.getPhotos().get(albumIndex);
                displayPhoto();
                displayTags();

System.out.println("Current photo filepath: " + currentAlbum.getPhotos().get(albumIndex).getFilePath());
            }
        });


        backToAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveData();
                startActivity(new Intent(PhotoActivity.this, AlbumActivity.class));

            }
        });
    }

    private void saveData() {

        for(int i = 0; i < albumList.size(); i++){
            if(albumList.get(i).getAlbumName().equals(currentAlbum.getAlbumName())){
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

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("album list", null);
        Type type = new TypeToken<ArrayList<Album>>() {}.getType();
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

        currentidx  = sharedPreferences.getInt("selectedIndex", 0);

    }

    public void generateSlideShow() {

/*
        while (currentAlbum.getPhotos().size() > 0) {
            currentAlbum.getPhotos().remove(0);
        }
        System.out.println("Album size: " + currentAlbum.getPhotos().size());
        saveData();
*/

        for (int i = 0; i < currentAlbum.getPhotos().size(); i++) {
            if (currentAlbum.getPhotos().get(i).getFilePath() != null) {
                albumIndex = i;
                break;
            }
        }

        currentPhoto = currentAlbum.getPhotos().get(albumIndex);
        displayPhoto();
    }

    public void displayPhoto () {
        Photo p = currentAlbum.getPhotos().get(albumIndex);

        Uri selectedImage = Uri.parse(p.getFilePath());

        ImageView imageView = findViewById(R.id.ImageDisplay);

        imageView.setImageURI(selectedImage);
    }


    public void displayTags () {
        list = new ArrayList<>();

        if (currentAlbum.getPhotos().get(albumIndex).getTags() == null) {
            return;
        }

        for (int i = 0; i < currentAlbum.getPhotos().get(albumIndex).getTags().size(); i++) {
            list.add(currentAlbum.getPhotos().get(albumIndex).getTags().get(i).toString());
        }

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, list);

        TagsList.setAdapter(arrayAdapter);

        arrayAdapter.notifyDataSetChanged();
    }
}




