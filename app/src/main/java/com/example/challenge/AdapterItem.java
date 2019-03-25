package com.example.challenge;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

public class AdapterItem extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<Item> items;

    public AdapterItem (Activity activity, ArrayList<Item> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<Item> category) {
        for (int i = 0; i < category.size(); i++) {
            items.add(category.get(i));
        }
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.listitem, null);
        }

        Item dir = items.get(position);

        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(dir.getTitle());

        TextView description = (TextView) v.findViewById(R.id.price);
        description.setText("ARS $" + dir.getPrice());

        ImageView imagen = (ImageView) v.findViewById(R.id.thumbnail);
        Picasso.with(this.activity).load(dir.getThumbnail()).placeholder(R.mipmap.ic_launcher)
              .into(imagen,new com.squareup.picasso.Callback(){


                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
        return v;
    }
}