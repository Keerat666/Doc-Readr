package com.example.keerat666.listviewpdfui;

import android.app.Activity;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class traverseList extends ArrayAdapter<String> {

    private final Activity context;
    ArrayList<String> maintitle = new ArrayList<>();
    ArrayList<String> subtitle = new ArrayList<>();
    ArrayList<Integer> imgid = new ArrayList<>();
    ArrayList<String> path = new ArrayList<>();
    ArrayList<String> path_folder = new ArrayList<>();
    ArrayList<String> idd = new ArrayList<>();

    SQLiteDatabase db;




    public traverseList(Activity context,   ArrayList<String>maintitle, ArrayList<String> subtitle, ArrayList<Integer> imgid,ArrayList<String> path,ArrayList<String> pathfolder , ArrayList<String> idd) {
        super(context, R.layout.toggle, maintitle);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.maintitle=maintitle;
        this.subtitle=subtitle;
        this.imgid=imgid;
        this.path=path;
        this.path_folder=pathfolder;

    }

    public void writetodb(String pdfpath)
    {

        db = context.openOrCreateDatabase("Documents",0,null);        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS tempPath(fpath VARCHAR);");
        try {

            String sql =
                    "INSERT or replace INTO tempPath VALUES('"+pdfpath+"')" ;
            Log.d("path",""+pdfpath);
            db.execSQL(sql);
            Log.d("Written to db","Written to db");

        }
        catch (Exception e) {
            Log.d("error db",e.toString());
        }

        }
        catch (Exception e) {
            Log.d("error db",e.toString());
        }
    }

    public void delfromdb(String pdfpath)
    {

        db = context.openOrCreateDatabase("Documents",0,null);        try {
        db.execSQL("CREATE TABLE IF NOT EXISTS tempPath(fpath VARCHAR);");
        try {

            db.delete("tempPath","fpath=?",new String[]{pdfpath});
            Log.d("del stat","Deleted");
        }
        catch (Exception e) {
            Log.d("error db",e.toString());
        }

    }
    catch (Exception e) {
        Log.d("error db",e.toString());
    }
    }


    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.toggle, null,true);

        final TextView titleText = (TextView) rowView.findViewById(R.id.title);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle4);
        ImageView imageView2 = (ImageView) rowView.findViewById(R.id.icon2);
        titleText.setText(maintitle.get(position));
        imageView.setImageResource(imgid.get(position));
        subtitleText.setText(subtitle.get(position));
        final Switch c=(Switch)rowView.findViewById(R.id.switch1);
        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position

                if(isChecked)
                {
                    Log.d("sstaus",""+isChecked+""+path_folder.get(position));
                    c.setChecked(true);
                    writetodb(path_folder.get(position));

                }
                else
                {
                    Log.d("sstaus",""+isChecked+""+path_folder.get(position));
                    c.setChecked(false);
                    delfromdb(path_folder.get(position));
                }
            }
        });
        return rowView;

    };

}