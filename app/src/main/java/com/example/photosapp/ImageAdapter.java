package com.example.photosapp;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Uri> uriArrayList;
    private int currentPosition = -1;

    public ImageAdapter(Context c, ArrayList<Uri> uriArrayList) {
        mContext = c;
        this.uriArrayList = uriArrayList;
    }

    public int getCount() {
        return uriArrayList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;

        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.getLayoutParams().height = 250;
            imageView.getLayoutParams().width = 200;
            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
        }

        return imageView;

    }

    public void setCurrentPosition(int position) {
        currentPosition = position;
    }
}

