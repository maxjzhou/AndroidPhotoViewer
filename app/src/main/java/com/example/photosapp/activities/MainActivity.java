package com.example.photosapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.photosapp.R;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.example.photosapp.model.Album;
import com.example.photosapp.model.Photo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ListView AlbumList;
    ArrayList<String> list;
    Button AddButton,DeleteAlbumButton,RenameButton;
    EditText editText;
    ArrayAdapter<String> arrayAdapter;
    ArrayList <Album> albumList;
    Album currentAlbum;
    Photo currentPhoto;
    int currentidx;

    int selectedAlbum;

    Button SearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();

        AlbumList = (ListView) findViewById(R.id.AlbumList);
        AddButton = (Button) findViewById(R.id.AddButton);
        DeleteAlbumButton = (Button) findViewById(R.id.DeleteAlbumButton);
        RenameButton = (Button) findViewById(R.id.RenameButton);
        editText = (EditText) findViewById(R.id.edText);
        SearchButton = (Button) findViewById(R.id.SearchButton);
        list = new ArrayList<String>();

        for (Album album: albumList){
            list.add(album.getAlbumName());
        }
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, list);
        AlbumList.setAdapter(arrayAdapter);

        AlbumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAlbum = i;
                editText.setText(list.get(i));
            }
        });


        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String names = editText.getText().toString();
                list.add(names);
                albumList.add(new Album(names));
                saveData();
                arrayAdapter.notifyDataSetChanged();
            }
        });

        Button OpenAlbumButton = (Button) findViewById(R.id.OpenAlbumButton);

        OpenAlbumButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // your handler code here
                        currentAlbum = albumList.get(selectedAlbum);
                        saveData();
                        startActivity(new Intent(MainActivity.this, AlbumActivity.class));

                    }
                });
        DeleteAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(selectedAlbum);
                albumList.remove(selectedAlbum);
                arrayAdapter.notifyDataSetChanged();

                saveData();
            }
        });

        RenameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String names = editText.getText().toString();
                currentAlbum = albumList.get(selectedAlbum);
                albumList.get(selectedAlbum).setAlbumName(names);
                list.set(selectedAlbum, names);

                saveData();
                arrayAdapter.notifyDataSetChanged();
            }
        });


        SearchButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // your handler code here
                        startActivity(new Intent(MainActivity.this, SearchActivity.class));

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



}
