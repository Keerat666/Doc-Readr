package com.example.keerat666.listviewpdfui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.victor.loading.rotate.RotateLoading;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    ListView list;
    View v;
    Switch c;
    Dialog yourDialog;
    ImageButton ibut;
    TextView t;
    SearchView sview;
    int sl=0;
    Toolbar tb;
    ArrayList<String> maintitle = new ArrayList<String>();
    ArrayList<String> subtitle = new ArrayList<String>();
    ArrayList<String> subtitle2 = new ArrayList<String>();

    ArrayList<Integer> imgid = new ArrayList<Integer>();
    ArrayList<Integer> playid = new ArrayList<Integer>();
    ArrayList<String> path = new ArrayList<>();

    ArrayList<String> maintitle_f = new ArrayList<String>();
    ArrayList<String> subtitle_f = new ArrayList<String>();
    ArrayList<Integer> imgid_f = new ArrayList<Integer>();
    ArrayList<String> path_f = new ArrayList<>();
    ArrayList<String> idd = new ArrayList<>();
    ArrayList<String> path_folder = new ArrayList<>();
    ArrayList<Date> datemodified=new ArrayList<>();
    ArrayList<File>filelist=new ArrayList<>();


    ArrayList<String> maintitle_b = new ArrayList<String>();
    ArrayList<String> subtitle_b= new ArrayList<String>();
    ArrayList<Integer> imgid_b = new ArrayList<Integer>();
    ArrayList<String> path_b = new ArrayList<>();
    ArrayList<Integer> cp= new ArrayList<>();
    ArrayList<Integer> nop = new ArrayList<>();




    String pdfpath;
    SQLiteDatabase db;
    Button butdel;
    private File pdfFile;
    private String pth;
    private String txtpath;
    private String tpath;
    private File file;
    MyListView adapter;
    traverseList adapt;
    bookmark_layout ad;
    String lastPath="";
    int lastPage=0;
    String audiol;
    String wordsl;
    int count=0;
    static final Integer READ_EXST = 0x4;
    static ArrayList<String> pdf=new ArrayList<String>();
    static ArrayList<String> txt=new ArrayList<String>();

    private RotateLoading rotateLoading;
    SpeedDialView speedDialView,speedDialView1;

    int set=0;
    Button buttonnew,btnnew;
    String pdfPattern = "";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try
        {
            Intent intent = getIntent();
            lastPath = intent.getStringExtra("PathTrack");
            lastPage = Integer.parseInt(intent.getStringExtra("PageTrack"));
           // Toast.makeText(this, "page: "+lastPage, Toast.LENGTH_SHORT).show();
        } catch(Exception e)
        {
            Log.d("intent error",""+e);
        }

       // btnnew=(Button)findViewById(R.id.buttonnew2);
        String pathx= Environment.getExternalStorageDirectory().getAbsolutePath();
        File f = new File(pathx);//converted string object to file
        String[] values = f.list();//getting the list of files in string array
        //now presenting the data into screen
        rotateLoading = (RotateLoading) findViewById(R.id.rotateloading);
        list = (ListView) findViewById(R.id.list);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String contactId = ((TextView) view.findViewById(R.id.title)).getText().toString();
                final String ffpath=path.get(position);
               // Toast.makeText(MainActivity.this, ""+contactId, Toast.LENGTH_SHORT).show();
                Rect displayRectangle = new Rect();
                Window window = MainActivity.this.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                yourDialog = new Dialog(MainActivity.this);
                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.delete_dialog, (ViewGroup) findViewById(R.id.ldelete));
                layout.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
                layout.setMinimumHeight((int) (displayRectangle.height() * 0.1f));
                yourDialog.setContentView(layout);
                yourDialog.show();
                butdel=(Button)layout.findViewById(R.id.buttondel);
                butdel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            deleteentry(ffpath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                ibut=(ImageButton)layout.findViewById(R.id.imageButton7);
                ibut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                           deleteentry(ffpath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return true;
            }
        });
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
        adapter = new MyListView(this, maintitle, subtitle, imgid, playid,path , subtitle2);
        db = openOrCreateDatabase( "Documents"        , SQLiteDatabase.CREATE_IF_NECESSARY        , null          );
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS filemaster(fpath VARCHAR,fileName VARCHAR ,audioLenth VARCHAR,nop VARCHAR, type VARCHAR);");

           // Toast.makeText(MainActivity.this, "table created ", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            //Toast.makeText(MainActivity.this, "ERROR "+e.toString(), Toast.LENGTH_LONG).show();
            Log.d("error db",e.toString());
        }
        RelativeLayout buttonContainer = (RelativeLayout) findViewById(R.id.buttoncont);
        buttonnew = (Button)findViewById(R.id.buttonnew);
        sview = (SearchView) findViewById(R.id.searchView1);
        speedDialView = findViewById(R.id.speedDial);
        speedDialView1= findViewById(R.id.speedDialfolder);
        tb=(Toolbar)findViewById(R.id.toolbar2);
        sview.setQueryHint("Search Here");

        BottomNavigationView bottomNavigationView1 = (BottomNavigationView)
                findViewById(R.id.navigation);

        bottomNavigationView1.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home1:
                            {
                                try {
                                    index();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }

                            case R.id.folder:
                            {


                                folder();
                                break;
                            }

                            case R.id.bookmark1:
                            {
                                pdf.clear();


                                bookmarks();
                                break;
                            }

                        }
                        return true;
                    }
                });
        speedDialView1.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                folderimport();
                return false;
            }

            @Override
            public void onToggleChanged(boolean isOpen) {
            }
        });

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_link, R.drawable.pdf_icon)
                        .setFabBackgroundColor(getResources().getColor(R.color.NAV))
                        .setLabel("PDF")
                        .setLabelBackgroundColor(getResources().getColor(R.color.NAV))
                        .create()
        );
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_link1, R.drawable.pdf)
                        .setFabBackgroundColor(getResources().getColor(R.color.NAV))
                        .setLabel("TXT")
                        .setLabelBackgroundColor(getResources().getColor(R.color.NAV))
                        .create()
        );
        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                switch (speedDialActionItem.getId()) {
                    case R.id.fab_link:
                        itext();
                        return false; //
                    case R.id.fab_link1:
                        txtcall();
                        return false;
                    default:
                        return false;
                }
            }
        });
        try {
            index();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
    public class MyAsyncTask extends AsyncTask<Integer, Void, String>
    {

        @Override

        protected String doInBackground(Integer... params) {

            pdf.clear();
            path_folder.clear();
            Search_Dir(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));


            return "OK";

        }


        @SuppressLint("WrongConstant")
        @Override

        protected void onPostExecute(String result) {
            tb.setVisibility(tb.VISIBLE);
            speedDialView.setVisibility(speedDialView.GONE);
            speedDialView1.setVisibility(speedDialView1.VISIBLE);
            int i=0;
            sl=filelist.size();
            ListView l=(ListView) findViewById(R.id.list);
            l.setAdapter(adapt);
            Log.d("pdf list",""+pdf);
            File []flarray = new File[filelist.size()];
            filelist.toArray(flarray);
            Arrays.sort(flarray, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
            filelist=new ArrayList<File>(Arrays.asList(flarray));
            Collections.reverse(filelist);
            path_folder.clear();
            for(int j=0;j<sl;j++)
                path_folder.add(filelist.get(j).getAbsolutePath());
            sl=filelist.size();
            for(i=0;i<sl;i++)
            {
                String p=filelist.get(i).getAbsolutePath();
                String sb=p.substring(p.length()-3);
                Log.d("pdfpattern",""+sb);
                String fnamese=filelist.get(i).getName();
                if(fnamese.endsWith("pdf"))
                    generatef(filelist.get(i));
                else if(fnamese.endsWith("txt"))

                    generatetxt_f(filelist.get(i));


            }


            tb.setTitle(sl+" Files Found");
         //   Toast.makeText(MainActivity.this, "Total "+sl+" files found !", Toast.LENGTH_SHORT).show();

            rotateLoading.stop();
            list.setAdapter(adapt);
            db = openOrCreateDatabase( "Documents"        , SQLiteDatabase.CREATE_IF_NECESSARY        , null          );
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS filemaster(fpath VARCHAR,fileName VARCHAR ,audioLenth VARCHAR,nop VARCHAR, type VARCHAR);");

            }
            catch (Exception e) {
                Toast.makeText(MainActivity.this, "ERROR "+e.toString(), Toast.LENGTH_LONG).show();
                Log.d("error db",e.toString());
            }
            set=1;
        }



        }

    public void folderimport(){
        Intent importing=new Intent(this,ImportActivity.class);
        startActivity(importing);
    }
    public void Access()
    {

        int yes=0;
        int no=0;
        for(int i=0;i<=list.getCount()-1;i++)
        {
            v=list.getAdapter().getView(i,null,null);
            c=(Switch)v.findViewById(R.id.switch1);
            t=(TextView)v.findViewById(R.id.title);
            Boolean switchState = c.isChecked();
            Log.d("Status",""+switchState+"");
            if(switchState)
            {
                yes++;
            }
            else
                no++;



        }
        Log.d("Yes",""+yes);
        Log.d("No",""+no);

    }
    public void Search_Dir(File dir) {

        File FileList[] = dir.listFiles();

        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {

                if (FileList[i].isDirectory()) {
                    Search_Dir(FileList[i]);
                } else {
                    if (FileList[i].getName().endsWith(pdfPattern) || FileList[i].getName().endsWith("txt") )
                    {
                        //here you have that file.
                        String name=FileList[i]+"";
                        Log.d("file found",""+name);
                        if(FileList[i].getName().endsWith(pdfPattern) || FileList[i].getName().endsWith("txt"))

                            pdf.add(FileList[i].getName());
                            path_folder.add(FileList[i].toString());
                        Date lastModDate = new Date(FileList[i].lastModified());
                        datemodified.add(lastModDate);
                        filelist.add(FileList[i]);
                            count++;


                    }
                }
            }
        }
    }

    public void itext() {
        try
        {
            db.delete("tempPath", null, null);

        } catch(Exception e)
        {
            Log.d("error temp txt",""+e);
        }
        new ChooserDialog().with(this)
                .withStartFile(pth)
                .withChosenListener(new ChooserDialog.Result() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        String check = path.substring((path.length() - 3));
                        String fname=path.substring(0,(path.length() - 3));
                        if (check.equals("pdf")) {
                            pdfpath = path;
                            db = openOrCreateDatabase("Documents", 0, null);
                            try {
                                db.execSQL("CREATE TABLE IF NOT EXISTS tempPath(fpath VARCHAR);");
                                try {

                                    String sql =
                                            "INSERT or replace INTO tempPath VALUES('" + pdfpath + "')";
                                    Log.d("path", "" + pdfpath);
                                    db.execSQL(sql);
                                    Log.d("Written to db", "Written to db");
                                    goimport();
                                } catch (Exception e) {
                                    Log.d("error db", e.toString());
                                }
                            } catch (Exception e) {
                                Log.d("database open error", e + "");
                            }
                        } else
                            Toast.makeText(MainActivity.this, "Please choose a pdf file", Toast.LENGTH_SHORT).show();


                    }
                })
                .build()
                .show();
    }


    @SuppressLint("WrongConstant")
    public void search(String ss)
    {
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(null);
try{
    SQLiteDatabase db;
    db = openOrCreateDatabase( "Documents"        , SQLiteDatabase.CREATE_IF_NECESSARY        , null          );

    Cursor resultSet = db.rawQuery("Select * from filemaster",null);
    String array[] = new String[resultSet.getCount()];
    String fileNames[]=new String[resultSet.getCount()];
    int i = 0;
    adapter = new MyListView(this, maintitle, subtitle, imgid, playid,path,subtitle2);
    adapter.clear();
    resultSet.moveToFirst();
    while (!resultSet.isAfterLast()) {
        array[i] = resultSet.getString(0);
        fileNames[i]=resultSet.getString(4);
        resultSet.moveToNext();
        pdfFile=new File(array[i]);

        if( Pattern.compile(Pattern.quote(ss), Pattern.CASE_INSENSITIVE).matcher(pdfFile.getName()).find())
        {
            if(pdfFile.getName().length()>10)
            {
                String d=pdfFile.getName();
                maintitle.add(d);

            }
            else
                maintitle.add(pdfFile.getName());

            subtitle.add(array[i]);

            if(fileNames[i].equals("pdf"))
                imgid.add(R.drawable.pdf_icon);
            else
                imgid.add(R.drawable.txt);



            playid.add(R.drawable.play);
            path.add(array[i]);
            adapter.notifyDataSetChanged();
        }

        i++;

    }
    list.setAdapter(adapter);

    Log.d("array",""+array[0]+" "+array[1]);
    Log.d("filename",""+fileNames[0]+" "+fileNames[1]);

} catch(Exception ex)
{
    Toast.makeText(this, ""+ex.toString(), Toast.LENGTH_SHORT).show();
}


    }

    public void txtcall() {
        try
        {
            db.delete("tempPath", null, null);

        } catch(Exception e)
        {
            Log.d("error temp txt",""+e);
        }

        final ChooserDialog show = new ChooserDialog().with(this)
                .withStartFile(tpath)
                .withChosenListener(new ChooserDialog.Result() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        String check = path.substring((path.length() - 3));
                        if (check.equals("txt")) {
                            txtpath = path;
                            db = openOrCreateDatabase("Documents", 0, null);
                            try {
                                db.execSQL("CREATE TABLE IF NOT EXISTS tempPath(fpath VARCHAR);");
                                try {

                                    String sql =
                                            "INSERT or replace INTO tempPath VALUES('" + txtpath + "')";
                                    Log.d("path", "" + pdfpath);
                                    db.execSQL(sql);
                                    Log.d("Written to db", "Written to db");
                                    goimport();
                                } catch (Exception e) {
                                    Log.d("error db", e.toString());
                                }
                            } catch (Exception e) {
                                Log.d("database open error", e + "");
                            }
                        } else
                            Toast.makeText(MainActivity.this, "Please choose a txt file", Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .show();


    }
public  void goimport()
{
    Intent intent = new Intent(this, ImportActivity.class);
    startActivity(intent);
}
    public void generate(String p) throws IOException {
        pdfFile=new File(p);
        String parsedText="";
        PdfReader reader = new PdfReader(p);
        int n = reader.getNumberOfPages();


            maintitle.add(pdfFile.getName());
        subtitle.add("Length :"+audiol);
        subtitle2.add("Words : "+wordsl+"");
        imgid.add(R.drawable.pdf_icon);
        if(p.equals(lastPath)){
            playid.add(R.drawable.pausemain);
        }
        else
        playid.add(R.drawable.play);
        path.add(p);
        adapter.notifyDataSetChanged();

        if (maintitle.size() > 7) {
            maintitle.remove((maintitle.size() - 2));
            subtitle.remove((subtitle.size() - 2));
            imgid.remove((imgid.size() - 2));
            playid.remove(playid.size() - 2);
            subtitle2.remove(subtitle2.size()-2);
            maintitle.add("");
            subtitle.add("");
            subtitle2.add("");
            imgid.add(0);
            playid.add(0);
            adapter.notifyDataSetChanged();

        } else if (maintitle.size() == 7) {
            maintitle.add("");
            subtitle.add("");
            imgid.add(0);
            playid.add(0);
            subtitle2.add("");
            adapter.notifyDataSetChanged();
        }

    }

    public void generatetxt(String p) {
        file=new File(p);
        if(file.getName().length()>10)
        {
            String d=pdfFile.getName();
            maintitle.add(d);

        }
        else
            maintitle.add(file.getName());
        File file = new File(p);

        subtitle.add("Length :"+audiol);
        subtitle2.add("Words : "+wordsl+"");
        imgid.add(R.drawable.pdf);
        if(p.equals(lastPath)){
            playid.add(R.drawable.pausemain);
        }
        else
        playid.add(R.drawable.play);
        path.add(p);
        adapter.notifyDataSetChanged();

        if (maintitle.size() > 7) {
            maintitle.remove((maintitle.size() - 2));
            subtitle.remove((subtitle.size() - 2));
            imgid.remove((imgid.size() - 2));
            playid.remove(playid.size() - 2);
            subtitle2.remove(subtitle2.size()-2);
            maintitle.add("");
            subtitle.add("");
            subtitle2.add("");
            imgid.add(0);
            playid.add(0);

            adapter.notifyDataSetChanged();

        } else if (maintitle.size() == 7) {
            maintitle.add("");
            subtitle.add("");
            subtitle2.add("");
            imgid.add(0);
            playid.add(0);

            adapter.notifyDataSetChanged();
        }
    }
    public void ViewPDF(String path , int cp){
       // Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
        Intent i=new Intent(this,PDFViewer.class);
        i.putExtra("Pathid",path);
        i.putExtra("current_page",cp);
        startActivity(i);
    }
    public void TakeText(String p) {

        Intent i=new Intent(this,TxtMaker.class);
        i.putExtra("txtpath",p);
        startActivity(i);

    }
    @SuppressLint("WrongConstant")
    void index() throws IOException {
  //      btnnew.setVisibility(btnnew.GONE);
        tb.setVisibility(tb.GONE);
        sview.setVisibility(sview.VISIBLE);
        speedDialView.setVisibility(speedDialView.VISIBLE);
        speedDialView1.setVisibility(speedDialView1.GONE);
        adapter.clear();
        buttonnew.setVisibility(buttonnew.GONE);
            db = openOrCreateDatabase( "Documents"        , SQLiteDatabase.CREATE_IF_NECESSARY        , null          );

            Cursor resultSet = db.rawQuery("Select * from filemaster",null);
            String array[] = new String[resultSet.getCount()];
            String fileNames[]=new String[resultSet.getCount()];
            int i = 0;


         resultSet.moveToFirst();
         while (!resultSet.isAfterLast())
         {
             Log.d("errorqwerty", resultSet.getString(0));
        array[i] = resultSet.getString(0);
        fileNames[i]=resultSet.getString(4);
             Log.d("now",""+resultSet.getString(3));
            wordsl=""+resultSet.getString(3);
            audiol=""+resultSet.getString(2);
             resultSet.moveToNext();
        pdfFile=new File(array[i]);
        if(fileNames[i].equals("pdf"))
            generate(array[i]);
        else if(fileNames[i].equals("txt"))
            generatetxt(array[i]);

        }
   list = (ListView) findViewById(R.id.list);
    list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String p=path.get(position);
                String sb=p.substring(p.length()-3);
                if(sb.equals("pdf"))
                {
                    if(p.equals(lastPath) && lastPage>=0)
                    ViewPDF(p,lastPage);
                    else
                        ViewPDF(p,0);



                }
                if(sb.equals("txt"))
                    TakeText(p);
                // Toast.makeText(MainActivity.this, ""+p, Toast.LENGTH_SHORT).show();

            }
        });


        SearchView searchView = (SearchView) findViewById(R.id.searchView1);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//              if (searchView.isExpanded() && TextUtils.isEmpty(newText)) {

                search(newText);
                return true;
            }

            public void callSearch(String query) {
                //Do searching
                search(query);
            }

        });
        final int sl = adapter.getCount();


    }
    public void generatef(File dd) {
        Log.d("gen f","pdf");

        pdfFile=dd;

        int file_size = Integer.parseInt(String.valueOf(pdfFile.length()));
        if(pdfFile.getName().length()>10)
        {
            String d=pdfFile.getName();
            maintitle_f.add(d);

        }
        else
            maintitle_f.add(pdfFile.getName());
        Date dt = new Date(pdfFile.lastModified());
        DateFormat dateFormat =  new SimpleDateFormat("dd/MM/yy hh:mm:ss");
        String strDate = dateFormat.format(dt);
        subtitle_f.add(strDate+"");
        imgid_f.add(R.drawable.pdf_icon);


        path_f.add(pdfFile.getAbsolutePath());
       // path_folder.add(pf);
        adapt.notifyDataSetChanged();

        if (maintitle_f.size() > 7) {
            maintitle_f.remove((maintitle_f.size() - 2));
            subtitle_f.remove((subtitle_f.size() - 2));
            imgid_f.remove((imgid_f.size() - 2));
            maintitle_f.add("");
            subtitle_f.add("");
            imgid_f.add(0);
            adapt.notifyDataSetChanged();

        } else if (maintitle_f.size() == 7) {
            maintitle_f.add("");
            subtitle_f.add("");
            imgid_f.add(0);
            adapt.notifyDataSetChanged();
        }

    }
    public void generatetxt_f(File dd) {
        Log.d("gen f","txt");
        File f=dd;
        int size = 0;
    try (Cursor returnCursor = getContentResolver().query(Uri.fromFile(f), null, null, null, null)) {
           int sizeIndex = 0;

        }


        if(f.getName().length()>10)
        {
            String d=f.getName();
            maintitle_f.add(d);

        }
        else
            maintitle_f.add(f.getName());
        Date dt = new Date(f.lastModified());
        DateFormat dateFormat =  new SimpleDateFormat("dd/MM/yy hh:mm:ss");
        String strDate = dateFormat.format(dt);
            subtitle_f.add(strDate+"");
            imgid_f.add(R.drawable.pdf);


        path_f.add(f.getAbsolutePath());
        adapt.notifyDataSetChanged();

        if (maintitle_f.size() > 7) {
            maintitle_f.remove((maintitle_f.size() - 2));
            subtitle_f.remove((subtitle_f.size() - 2));
            imgid_f.remove((imgid_f.size() - 2));

            maintitle_f.add("");
            subtitle_f.add("");
            imgid_f.add(0);

            adapt.notifyDataSetChanged();

        } else if (maintitle_f.size() == 7) {
            maintitle_f.add("");
            subtitle_f.add("");
            imgid_f.add(0);

            adapt.notifyDataSetChanged();
        }
    }
    @SuppressLint("WrongConstant")
    public void dropdb()
    {

        db = openOrCreateDatabase( "Documents"        , SQLiteDatabase.CREATE_IF_NECESSARY        , null          );
        try {
            String DATABASE_TABLE="tempPath";
            db.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE + "'");
            Log.d("del stat","Deleted");
        }
        catch (Exception e) {
            Log.d("error db",e.toString());
        }

    }


    public void traverse(View view)
    {
        pdf.clear();
        maintitle_f.clear();
        subtitle_f.clear();
        imgid_f.clear();
        path_f.clear();
        path_folder.clear();
        filelist.clear();

        switch(view.getId())
        {
            case R.id.buttonnew:
            {  pdfPattern=".pdf";

                adapt = new traverseList(this, maintitle_f, subtitle_f, imgid_f,path_f,path_folder,idd);

                Log.d("Searching pdf","TRUE");
                sl=0;
               // btnnew.setVisibility(btnnew.GONE);
                buttonnew.setVisibility(buttonnew.GONE);


                // generatef("/storage/emulated/0/Download/1536252254408_SRM -Shortlisting-Technical Interview.pdf");
                rotateLoading.start();
                dropdb();
                //pdf.clear();
                new MyAsyncTask().execute();
                break;
            }
// handle button A click;

           /* case R.id.buttonnew2:
            {
                pdfPattern=".txt";
                pdf.clear();
                path_folder.clear();
                adapt = new traverseList(this, maintitle_f, subtitle_f, imgid_f,path_f,path_folder,idd);

                ListView l=(ListView)findViewById(R.id.list);
                list.setAdapter(adapt);
                Log.d("Searching txt","TRUE");
                sl=0;
                btnnew.setVisibility(btnnew.GONE);
                buttonnew.setVisibility(buttonnew.GONE);


                // generatef("/storage/emulated/0/Download/1536252254408_SRM -Shortlisting-Technical Interview.pdf");
                rotateLoading.start();
                dropdb();
                //pdf.clear();
                new MyAsyncTask().execute();
                break;


            }*/

// handle button B click;
            default:
                throw new RuntimeException("Unknown button ID");
        }


    }
    
    void folder(){
      //  btnnew.setVisibility(btnnew.VISIBLE);
        sview.setVisibility(sview.GONE);
        ListView list1 = (ListView) findViewById(R.id.list);
        list1.setAdapter(null);
        buttonnew.setVisibility(buttonnew.VISIBLE);
        speedDialView.setVisibility(speedDialView.VISIBLE);

    }
    @SuppressLint("WrongConstant")
    void  bookmarks(){
       // btnnew.setVisibility(btnnew.GONE);
        sview.setVisibility(sview.GONE);
        speedDialView1.setVisibility(speedDialView1.GONE);
        speedDialView.setVisibility(speedDialView.VISIBLE);
        ListView list2 = (ListView) findViewById(R.id.list);
        list2.setAdapter(null);
        ad=new bookmark_layout(this,maintitle_b,subtitle_b,imgid_b,path_b,cp,nop);
        ad.clear();
        buttonnew.setVisibility(buttonnew.GONE);


try{
    Cursor resultSet = db.rawQuery("Select * from bookmarks",null);
    String array[] = new String[resultSet.getCount()];
    String fileNames[]=new String[resultSet.getCount()];
    int i = 0;


    resultSet.moveToFirst();
    while (!resultSet.isAfterLast())
    {
        Log.d("errorqwerty", resultSet.getString(0));
        array[i] = resultSet.getString(0);
        String nop=resultSet.getString(1);
        int pageno=Integer.parseInt(nop);
        String top=resultSet.getString(2);
        int pagenototal=Integer.parseInt(top);
        Log.d("Bookmarks entry","path : "+array[i]+"Page Number : "+pageno+"Total pages : "+pagenototal);
        pdfFile=new File(array[i]);

        fileNames[i]=resultSet.getString(3);
        resultSet.moveToNext();
        pdfFile=new File(array[i]);
        Log.d("filename",""+fileNames[i]);
        if(fileNames[i].equals("pdf"))
            generatebm(array[i],pageno,pagenototal);
        else if(fileNames[i].equals("txt"))
            generatetxtbm(array[i]);

    }
    list = (ListView) findViewById(R.id.list);
    list.setAdapter(ad);


    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String p=path_b.get(position);
            String sb=p.substring(p.length()-3);
            if(sb.equals("pdf"))
                ViewPDF(p,cp.get(position));
            if(sb.equals("txt"))
                TakeText(p);
            // Toast.makeText(MainActivity.this, ""+p, Toast.LENGTH_SHORT).show();

        }
    });

}catch (Exception e)
{
    Log.d("Exception",""+e);
}

    }

    public void generatebm(String p , int c , int t) {
        pdfFile=new File(p);
        Log.d("Bookmark generate test","I came here");
        if(pdfFile.getName().length()>10)
        {
            String d=pdfFile.getName();
            maintitle_b.add(d);

        }
        else
            maintitle_b.add(pdfFile.getName());
        subtitle_b.add(p);
        imgid_b.add(R.drawable.pdf_icon);
        path_b.add(p);
        cp.add(c);
        nop.add(t);
        ad.notifyDataSetChanged();

        if (maintitle_b.size() > 7) {
            maintitle_b.remove((maintitle_b.size() - 2));
            subtitle_b.remove((subtitle_b.size() - 2));
            imgid_b.remove((imgid_b.size() - 2));
            cp.remove(cp.size()-2);
            nop.remove(nop.size()-2);

            maintitle_b.add("");
            subtitle_b.add("");
            imgid_b.add(0);
            cp.add(0);
            nop.add(0);
            ad.notifyDataSetChanged();

        } else if (maintitle_b.size() == 7) {
            maintitle_b.add("");
            subtitle_b.add("");
            imgid_b.add(0);
            nop.add(0);
            cp.add(0);

            ad.notifyDataSetChanged();
        }

    }

    public void generatetxtbm(String p) {
        file=new File(p);

            maintitle.add(file.getName());

        subtitle.add(p);
        imgid.add(R.drawable.pdf);
        playid.add(R.drawable.play);
        path.add(p);
        adapter.notifyDataSetChanged();

        if (maintitle.size() > 7) {
            maintitle.remove((maintitle.size() - 2));
            subtitle.remove((subtitle.size() - 2));
            imgid.remove((imgid.size() - 2));
            playid.remove(playid.size() - 2);

            maintitle.add("");
            subtitle.add("");
            imgid.add(0);
            playid.add(0);

            adapter.notifyDataSetChanged();

        } else if (maintitle.size() == 7) {
            maintitle.add("");
            subtitle.add("");
            imgid.add(0);
            playid.add(0);

            adapter.notifyDataSetChanged();
        }
    }

   public void deleteentry(String s) throws IOException {
        db.execSQL("delete from "+"filemaster"+" where fpath='"+s+"'");
        try {
            maintitle.clear();
            subtitle.clear();
            subtitle2.clear();
            imgid.clear();
            playid.clear();
            path.clear();
            index();
            yourDialog.dismiss();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
