package com.example.gestordegastos;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<Integer> {
    private final List<Integer> imageList;

    public ImageAdapter(Context context, List<Integer> imageList) {
        super(context, 0, imageList);
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getImageItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getImageItemView(position, convertView, parent);
    }

    private View getImageItemView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(getContext());
            imageView.setLayoutParams(new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(imageList.get(position));
        return imageView;
    }
}

