package com.enesceylan.LocationOrPictureRecorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.enesceylan.LocationOrPictureRecorder.R;
import com.enesceylan.LocationOrPictureRecorder.model.Place;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Place> {
    ArrayList<Place> placeArrayList;
    Context context;

    public CustomAdapter(@NonNull Context context, ArrayList<Place> placeArrayList) {
        super(context, R.layout.custom_list_row,placeArrayList);
        this.placeArrayList=placeArrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View customView=layoutInflater.inflate(R.layout.custom_list_row,parent,false);
        TextView nameTextView=customView.findViewById(R.id.nameTextView);
        nameTextView.setText(placeArrayList.get(position).name);

        return customView;
    }
}
