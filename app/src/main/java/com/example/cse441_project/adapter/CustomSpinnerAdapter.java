package com.example.cse441_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.cse441_project.R;
import com.example.cse441_project.model.Category;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    public CustomSpinnerAdapter(Context context, int resource, List<String> lists) {
        super(context, resource, lists);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_spinner, parent, false);
        String categoryName = this.getItem(position);
        TextView textView = convertView.findViewById(R.id.textViewCategory);
        if (categoryName != null) {
            textView.setText(categoryName);
        }
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_selected, parent, false);
        String categoryName = this.getItem(position);
        TextView textView = convertView.findViewById(R.id.spinner_text);
        if (categoryName != null) {
            textView.setText(categoryName);
        }
        return convertView;
    }


}
