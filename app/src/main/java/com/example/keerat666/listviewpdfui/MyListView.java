package com.example.keerat666.listviewpdfui;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyListView extends ArrayAdapter<String> {

    private final Activity context;
    ArrayList<String> maintitle = new ArrayList<>();
    ArrayList<String> subtitle = new ArrayList<>();
    ArrayList<Integer> imgid = new ArrayList<>();
    ArrayList<Integer> imgid2 = new ArrayList<>();
    ArrayList<String> path = new ArrayList<>();
    ArrayList<String> subtitle2 = new ArrayList<>();




    public MyListView(Activity context,   ArrayList<String>maintitle, ArrayList<String> subtitle, ArrayList<Integer> imgid,ArrayList<Integer> imgid2,ArrayList<String> path ,ArrayList<String> subtitle2 ) {
        super(context, R.layout.mylist, maintitle);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.maintitle=maintitle;
        this.subtitle=subtitle;
        this.imgid=imgid;
        this.imgid2=imgid2;
        this.path=path;
        this.subtitle2=subtitle2;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);
        TextView subtitleText2 = (TextView) rowView.findViewById(R.id.subtitle4);

        ImageView imageView2 = (ImageView) rowView.findViewById(R.id.icon2);
        titleText.setText(maintitle.get(position));
        imageView.setImageResource(imgid.get(position));
        imageView2.setImageResource(imgid2.get(position));
        subtitleText.setText(subtitle.get(position));
        subtitleText2.setText(subtitle2.get(position));


        return rowView;

    };
}