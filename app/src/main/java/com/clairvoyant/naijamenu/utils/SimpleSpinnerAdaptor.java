package com.clairvoyant.naijamenu.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.clairvoyant.naijamenu.R;
import com.clairvoyant.naijamenu.fonts.RobotoLightTextView;

import java.util.ArrayList;

public class SimpleSpinnerAdaptor extends ArrayAdapter<String> {

    private ArrayList<String> categoryList;
    private Context mContext;
    private int textViewResourceId;
    private LayoutInflater inflater;

    public SimpleSpinnerAdaptor(Context mContext, int textViewResourceId, ArrayList<String> categoryList) {
        super(mContext, textViewResourceId, categoryList);
        this.mContext = mContext;
        this.categoryList = categoryList;
        inflater = LayoutInflater.from(mContext);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        return convertView;
    }


    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        RobotoLightTextView category = (RobotoLightTextView) convertView.findViewById(R.id.spinner_textview);
        category.setText(categoryList.get(position));
        return convertView;

    }
}