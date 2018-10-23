package com.example.keerat666.listviewpdfui;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class bookmark_layout extends ArrayAdapter<String> {

    private final Activity context;
    ArrayList<String> maintitle = new ArrayList<>();
    ArrayList<String> subtitle = new ArrayList<>();
    ArrayList<Integer> imgid = new ArrayList<>();
    ArrayList<Integer> imgid2 = new ArrayList<>();
    ArrayList<String> path = new ArrayList<>();
    ArrayList<Integer> cp = new ArrayList<>();
    ArrayList<Integer> nop = new ArrayList<>();




    public bookmark_layout(Activity context,   ArrayList<String>maintitle, ArrayList<String> subtitle, ArrayList<Integer> imgid,ArrayList<String> path, ArrayList<Integer> cp , ArrayList<Integer> nop) {
        super(context, R.layout.activity_bookmark_layout, maintitle);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.maintitle=maintitle;
        this.subtitle=subtitle;
        this.imgid=imgid;
        this.imgid2=imgid2;
        this.path=path;
        this.cp=cp;
        this.nop=nop;
    }


    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.activity_bookmark_layout, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);
        titleText.setText(maintitle.get(position));
        imageView.setImageResource(imgid.get(position));
        subtitleText.setText(""+cp.get(position)+" pages read out of "+nop.get(position) +" pages !");
        ProgressBar x= rowView.findViewById(R.id.xxx);
        x.setMax(nop.get(position));

        x.setProgress(cp.get(position));
        return rowView;

    };
}