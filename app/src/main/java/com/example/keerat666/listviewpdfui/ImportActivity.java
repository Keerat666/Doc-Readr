package com.example.keerat666.listviewpdfui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.victor.loading.rotate.RotateLoading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportActivity extends AppCompatActivity {
int nof=0;
TextView name;
TextView count;
TextView nop;
TextView length;
Spinner spinner;
    int counter=1;
String parsedText="";
String audio;
int now=0;
String xx="";
int yy=0;
int n;
    String [] words;
    ArrayList<String> nm = new ArrayList<String>();
    private RotateLoading rotateLoading;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        SQLiteDatabase db;
        rotateLoading = (RotateLoading) findViewById(R.id.rotateloading);

        db = openOrCreateDatabase( "Documents"        , SQLiteDatabase.CREATE_IF_NECESSARY        , null          );

        Cursor resultSet = db.rawQuery("Select * from tempPath",null);
        nof =        resultSet.getCount();
        List<String> categories = new ArrayList<String>();
        categories.add("English");
        spinner = (Spinner) findViewById(R.id.spinner);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        //Performing action onItemSelected and onNothing selected


    File pdff = null;
        String array[] = new String[resultSet.getCount()];
        int i=0;
        resultSet.moveToFirst();
        while (!resultSet.isAfterLast()) {
            array[i] = resultSet.getString(0);
            nm.add(resultSet.getString(0));
            i++;
            resultSet.moveToNext();
        }

        for(int f=0;f<array.length;f++)
        Log.d("list666",f+""+array[f]);

        if(array.length>=0)
            setData(array[0],0);

    }

    public void nextFile(View view)
    {
        if(counter<=nof-1)
        {
            setData(nm.get(counter),counter);
            counter++;
        }
        else
        {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

    }

    @SuppressLint("WrongConstant")
    public void setData(String d,int x)
    {
        name=(TextView)findViewById(R.id.name);
        File temp=new File(d);
        name.setText(temp.getName());
        //Toast.makeText(this, " No of files left: "+(nof-x-1), Toast.LENGTH_SHORT).show();
        if(x==0)
        {
            xx=d;
            yy=1;
            rotateLoading = (RotateLoading) findViewById(R.id.rotateloading2);
            rotateLoading.start();;

            new countwords().execute();


        }
        else
        {
            xx=d;
            yy=0;
            rotateLoading = (RotateLoading) findViewById(R.id.rotateloading2);
            rotateLoading.start();;
            new countwords().execute();

        }


    }

    @SuppressLint("WrongConstant")
    public void writeData(String path)
    {
        String check = path.substring((path.length() - 3));
        File x=new File(path);
        String fname=x.getName();
        SQLiteDatabase db;
File pdfpath;
        {
            pdfpath = new File(path);
            db = openOrCreateDatabase( "Documents"        , SQLiteDatabase.CREATE_IF_NECESSARY        , null          );
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS filemaster(fpath VARCHAR,fileName VARCHAR ,audioLenth VARCHAR,nop VARCHAR, type VARCHAR);");
                String sql;
               //w Toast.makeText(ImportActivity.this, "table created ", Toast.LENGTH_LONG).show();
                if (check.equals("pdf"))
                {
                             sql =
                            "INSERT or replace INTO filemaster VALUES('"+path+"','"+fname+"','"+audio+"','"+now+"','pdf')" ;
                }

                else
                {
                             sql =
                            "INSERT or replace INTO filemaster VALUES('"+path+"','"+fname+"','"+audio+"','"+now+"','txt')" ;
                }

                Log.d("insert status",""+true);
                db.execSQL(sql);
            }
            catch (Exception e) {
               // Toast.makeText(ImportActivity.this, "ERROR "+e.toString(), Toast.LENGTH_LONG).show();
                Log.d("error db",e.toString());
            }

        }
    }

    void extract(String yourPdfPath, int onc) {
            n=1;
        String check = yourPdfPath.substring((yourPdfPath.length() - 3));

        rotateLoading = (RotateLoading) findViewById(R.id.rotateloading2);

        if(check.equals("pdf"))
        {
            Log.d("onc",""+onc);
            try {
                //rotateLoading.start();
                parsedText="";
                PdfReader reader = new PdfReader(yourPdfPath);
                n = reader.getNumberOfPages();
                for (int ig = 0; ig < n; ig++) {
                    parsedText = parsedText + PdfTextExtractor.getTextFromPage(reader, ig + 1).trim() + "\n"; //Extracting the content from the different pages
                }
                reader.close();
                Log.d("PARSED", "extract: "+parsedText);
            } catch (Exception e) {
                Log.d("Extract failed", "extract: " + e);
            }



        }else {
            Log.d("onc", "" + onc);
            try {
               // rotateLoading.start();
                parsedText = "";
                File file = new File(yourPdfPath);

                StringBuilder text = new StringBuilder();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                    parsedText = text.toString();
                } catch (IOException e) {
                    //You'll need to add proper error handling here
                    Log.d("error", "" + e.toString());
                }





            }catch (Exception ex)
            {
Log.d("Exception",ex.toString());            }
        }


    }

    public class countwords extends AsyncTask<Integer, Void, String>
    {

        @Override

        protected String doInBackground(Integer... params) {

            extract(xx,yy);


            return "OK";

        }


        @SuppressLint("WrongConstant")
        @Override

        protected void onPostExecute(String result) {
            rotateLoading.stop();
            nop=(TextView)findViewById(R.id.pagesvalue);
            nop.setText(n+"");

            words=parsedText.split(" ");
            if(words.length!=0)
            {
                count=(TextView)findViewById(R.id.wordvalue);
                count.setText(words.length+"");
                now=words.length;
                Log.d("data",""+count+" "+nop);
                length=(TextView)findViewById(R.id.lengthvalue) ;
                int ss=words.length/3;
                length.setText(ss/60+"m "+ss%60+"s");
                audio=ss/60+"m "+ss%60+"s";
            }


            writeData(xx);

        }



    }


}
