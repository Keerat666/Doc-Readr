package com.example.keerat666.listviewpdfui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PDFViewer extends AppCompatActivity implements OnPageChangeListener {

    private static final String TAG = "";
    PDFView pdf;
    Activity activity;
    String x;
    Button chnge;
    RadioGroup rg;
    RadioButton rb1;
    Dialog yourDialog;
    RadioButton rb2;
    String idgen;
    android.support.v7.widget.Toolbar tb;
    ImageButton imgb;
    InputStream input = null;
    ImageButton imgbut;
    int end;
    int i = 0;
    String PATH;
    LinearLayout ll, ll1;
    RelativeLayout rl;
    String parsedText = "";
    TextToSpeech t1;
    Button b;
    int current_page = 0;
    String status = "play";
    int n;
    PdfReader reader = null;
    int previ;
    boolean nmode = false;
    Menu menu;
    SQLiteDatabase db;
    Spinner spin;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);


        db = openOrCreateDatabase("Documents", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS bookmarks(fpath VARCHAR,page_number VARCHAR ,total_pages VARCHAR, type VARCHAR);");

            Toast.makeText(PDFViewer.this, "table created ", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(PDFViewer.this, "ERROR " + e.toString(), Toast.LENGTH_LONG).show();
            Log.d("error db", e.toString());
        }
        // imgb=(ImageButton)findViewById(R.id.imageButton12);
        imgbut = (ImageButton) findViewById(R.id.imageButton2);
        PATH = getIntent().getStringExtra("Pathid");
        int cp = getIntent().getIntExtra("current_page", 0);
        int cpi = cp;

        try {
            reader = new PdfReader(PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        n = reader.getNumberOfPages();
        final SeekBar simpleSeekBar = (SeekBar) findViewById(R.id.seekBar2); // initiate the Seekbar
        simpleSeekBar.setMax(n - 1);


        final File file = new File(PATH);
        pdf = (PDFView) findViewById(R.id.pdfv);

        tb = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar3);
        tb.setTitle(file.getName());
        setSupportActionBar(tb);

        final com.github.barteksc.pdfviewer.listener.OnPageChangeListener onPageChangeListener = new com.github.barteksc.pdfviewer.listener.OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount) {
                simpleSeekBar.setProgress(page);
                current_page = page;
            }
        };
        pdf.fromFile(file)
                .enableAnnotationRendering(true)
                .onPageChange(onPageChangeListener)
                .pageFling(true)
                .nightMode(nmode)
                .defaultPage(cpi)
                .load();


        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    progressChangedValue = progress;
                    pdf.fromFile(file)
                            .defaultPage(progressChangedValue)
                            .onPageChange(onPageChangeListener)
                            .nightMode(nmode)
                            .load();
                    current_page = progress;
                    clear();
                    extract(PATH, progress);
                }

            }


            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("progress", "" + progressChangedValue);
            }
        });
        ll1 = (LinearLayout) findViewById(R.id.ppcont);
        rl = (RelativeLayout) findViewById(R.id.rl);
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, ll1.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ll1.getVisibility() == (ll1.VISIBLE)) {

                    ll1.setVisibility(ll1.GONE);
                } else
                    ll1.setVisibility(ll1.VISIBLE);

            }
        });

        init(0.0f, 0.0f,"");


    }


    public void init(final float p, final float sr,final String voice) {
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int status) {
                t1.setPitch(p);
                t1.setSpeechRate(sr);
                for (Voice tmpVoice : t1.getVoices()) {
                    if (tmpVoice.getName().equals(voice))
                    {
                        t1.setVoice(tmpVoice);

                        break;
                    }
                }
                Log.d("voices", t1.getVoices() + "");
            }
        });
    }

    public void changePitch(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ham, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuNight: {
                Toast.makeText(this, "Night Mode status changed.", Toast.LENGTH_SHORT).show();
                final File file = new File(PATH);
                if (nmode) {
                    nmode = false;
                    menu.findItem(R.id.menuNight).setTitle("Enable Night Mode");
                } else {
                    nmode = true;
                    menu.findItem(R.id.menuNight).setTitle("Disable Night Mode");


                }
                final com.github.barteksc.pdfviewer.listener.OnPageChangeListener onPageChangeListener = new com.github.barteksc.pdfviewer.listener.OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        current_page = page;
                    }
                };
                pdf.fromFile(file)
                        .enableAnnotationRendering(true)
                        .onPageChange(onPageChangeListener)
                        .nightMode(nmode)
                        .defaultPage(current_page)
                        .load();

                break;
            }


            case R.id.bmark: {
                bookmark();
                break;
            }


            case R.id.veset: {
                change();
                break;
            }

            case R.id.tedit: {
                pausevoice();
                Intent i = new Intent(this, edit_mode.class);
                i.putExtra("pdfpath", PATH);
                startActivity(i);
            }


        }
        return true;
    }

    public void change() {
        //Toast.makeText(this, "Voice engine settings changed!", Toast.LENGTH_SHORT).show();
        pausevoice();
        status="pause";
        imgbut.setImageDrawable(getDrawable(R.drawable.mdplay));
        Rect displayRectangle = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        yourDialog = new Dialog(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_content, (ViewGroup) findViewById(R.id.lineardialog));
                          layout.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
                          layout.setMinimumHeight((int) (displayRectangle.height() * 0.3f));
                          yourDialog.setContentView(layout);
                            rg=(RadioGroup)layout.findViewById(R.id.rg101);
                            rb1=(RadioButton)layout.findViewById(R.id.rb1);
                            rb2=(RadioButton)layout.findViewById(R.id.rb2);
                          spin=(Spinner)layout.findViewById(R.id.spinner2);

        try {
            final List<String> country = new ArrayList<String>();
           rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    country.clear();
                    for (Voice tmpVoice : t1.getVoices()) {
                        if(rb1.isChecked()){
                           if(tmpVoice.getName().startsWith("en") && (tmpVoice.getName().contains("male")) &&!(tmpVoice.getName().contains("female"))){

                                country.add(tmpVoice.getName());
                            }
                        }
                        else if(rb2.isChecked()){
                            if(tmpVoice.getName().startsWith("en") && tmpVoice.getName().contains("female")){
                                country.add(tmpVoice.getName());
                            }
                        }

                    }
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(PDFViewer.this, android.R.layout.simple_spinner_item, country);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin.setAdapter(spinnerArrayAdapter);
                }
            });


            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    x=spin.getSelectedItem().toString();
                   // Toast.makeText(PDFViewer.this, ""+x, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }
        catch (Exception ex) {
            Log.e("spin error", ex.toString());
        }


        yourDialog.show();

         final float[] word_rate = new float[1];
        final float[] pitch = new float[1];


        chnge = (Button) layout.findViewById(R.id.apply);
        chnge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  Toast.makeText(PDFViewer.this, ""+x, Toast.LENGTH_SHORT).show();
                init(pitch[0], word_rate[0],x);
                yourDialog.dismiss();
            }
        });
        final SeekBar p = (SeekBar) layout.findViewById(R.id.seekBar78);
        final SeekBar wr = (SeekBar) layout.findViewById(R.id.seekBar89);

        pitch[0] = 0.0f;

        word_rate[0] = 0.0f;
        p.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pitch[0] = progress;
                p.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        wr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                word_rate[0] = progress;
                wr.setProgress(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @SuppressLint("WrongConstant")
    public void bookmark() {
        int error = 0;
        Log.d("Total number of pages", "" + n);
        try {

            String sql =
                    "INSERT or replace INTO bookmarks VALUES('" + PATH + "','" + current_page + "','" + n + "','pdf')";
            Log.d("path", "" + PATH);
            db.execSQL(sql);

        } catch (Exception e) {
            Toast.makeText(PDFViewer.this, "ERROR " + e.toString(), Toast.LENGTH_LONG).show();
            Log.d("error db", e.toString());
            error = 1;
        }
        if (error != 1) {
            Toast.makeText(this, "Bookmark successfully added!", Toast.LENGTH_SHORT).show();

        }
    }

    public void pausevoice() {
        if (t1.isSpeaking()) {
            parsedText = "";
            t1.stop();
            highlight(1, 1);
        }

    }

    public void stop(View view) {
        parsedText = "";
        t1.stop();
        imgbut.setImageDrawable(getDrawable(R.drawable.mdplay));
        final File file = new File(PATH);
        final SeekBar simpleSeekBar = (SeekBar) findViewById(R.id.seekBar2);

        com.github.barteksc.pdfviewer.listener.OnPageChangeListener onPageChangeListener = new com.github.barteksc.pdfviewer.listener.OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount) {
                simpleSeekBar.setProgress(page);
                // initiate the Seekbar
                current_page = page;
                Log.d("current page ", "xxxTenacon" + 0);

            }
        };
        pdf.fromFile(file)
                .defaultPage(0)
                .onPageChange(onPageChangeListener)
                .nightMode(nmode)
                .load();
        simpleSeekBar.setProgress(current_page);
        i = 0;
    }

    public void play(View view) {
        if (status.equals("play")) {

            extract(PATH, current_page);
            status = "pause";
            imgbut.setImageDrawable(getDrawable(R.drawable.mdpause));
        } else if (status.equals("pause")) {
            pausevoice();
            status = "play";
            imgbut.setImageDrawable(getDrawable(R.drawable.mdplay));
        }

    }

    public void nextpage(View view) {
        final File file = new File(PATH);
        final SeekBar simpleSeekBar = (SeekBar) findViewById(R.id.seekBar2);

        com.github.barteksc.pdfviewer.listener.OnPageChangeListener onPageChangeListener = new com.github.barteksc.pdfviewer.listener.OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount) {
                simpleSeekBar.setProgress(page);
                // initiate the Seekbar
                current_page = page;
                Log.d("current page ", "xxxTenacon" + current_page);

            }
        };
        pdf.fromFile(file)
                .defaultPage(current_page + 1)
                .onPageChange(onPageChangeListener)
                .nightMode(nmode)
                .load();
        simpleSeekBar.setProgress(current_page);

    }

    public void prevpage(View view) {
        final File file = new File(PATH);
        final SeekBar simpleSeekBar = (SeekBar) findViewById(R.id.seekBar2);

        com.github.barteksc.pdfviewer.listener.OnPageChangeListener onPageChangeListener = new com.github.barteksc.pdfviewer.listener.OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount) {
                simpleSeekBar.setProgress(page);
                // initiate the Seekbar
                current_page = page;
                Log.d("current page ", "xxxTenacon" + current_page);

            }
        };
        pdf.fromFile(file)
                .defaultPage(current_page - 1)
                .onPageChange(onPageChangeListener)
                .nightMode(nmode)
                .load();
        simpleSeekBar.setProgress(current_page);

    }

    public void nextLine(View view) {
        t1.stop();
        i = end + 1;
    }

    public void prevLine(View view) {
        t1.stop();
        i = previ;
    }

    void extract(String yourPdfPath, int start_page) {
        Log.d("Extract call", "" + start_page);
        Log.d("Extract call", "" + n);

        if (start_page < (n)) {
            try {
                parsedText = "";
                PdfReader reader = new PdfReader(yourPdfPath);
                n = reader.getNumberOfPages();
                // for (int ig = start_page; ig <n; ig++)
                // {
                parsedText = parsedText + PdfTextExtractor.getTextFromPage(reader, start_page + 1); //Extracting the content from the different pages
                //}

                Log.d("Extract text:", parsedText + "sup");
                trigger(0, parsedText+".");
                reader.close();
            } catch (Exception e) {
                Log.d("extract method ", "extract: " + e);
            }
        } else {
          //  Toast.makeText(this, "End of document", Toast.LENGTH_SHORT).show();
        }


    }

    public void trigger(final int l, final String PdfPath) {
        Log.d("speaking-highlight", t1.isSpeaking() + "");
        final Handler handler = new Handler();
        final int delay = 0; //milliseconds


        handler.postDelayed(new Runnable() {
            public void run() {
                //do something
                if (!t1.isSpeaking())
                    highlight(0, 0);
                handler.postDelayed(this, delay);
            }
        }, delay);


    }

    public void clear() {
        parsedText = "";
        i = 0;
        t1.stop();

        // Toast.makeText(this, "Stop!", Toast.LENGTH_SHORT).show();
        // Intent goback=new Intent(this,MainActivity.class);
        // startActivity(goback);
        // TextView f=(TextView)findViewById(R.id.textview);
        // f.setText(parsedText);

    }

    public void highlight(int h, int g) {
        int temp = 0;
        if (i <= parsedText.length()) {
            SpannableString str = new SpannableString(parsedText);

            try {

                end = parsedText.indexOf(".", i);


            } catch (Exception e) {
                Log.d("Ending exception", e + "");

            }

            if (end != -1 && h == 0) {
//                str.setSpan(new BackgroundColorSpan(Color.YELLOW), i, end + 1, 0);
                String toSpeak = parsedText.substring(i, end);
                previ = i;
                i = end + 1;
                Log.d("high check", toSpeak + "testing");
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                Log.d("speaking?", t1.isSpeaking() + "");
            } else if (current_page + 1 < n && t1.isSpeaking() == false) {
                final File file = new File(PATH);
                final SeekBar simpleSeekBar = (SeekBar) findViewById(R.id.seekBar2);

                com.github.barteksc.pdfviewer.listener.OnPageChangeListener onPageChangeListener = new com.github.barteksc.pdfviewer.listener.OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        simpleSeekBar.setProgress(page);
                        // initiate the Seekbar
                        current_page = page;
                        Log.d("current page ", "xxxTenacon666" + current_page);

                    }
                };
                pdf.fromFile(file)
                        .defaultPage(current_page + 1)
                        .onPageChange(onPageChangeListener)
                        .nightMode(nmode)
                        .load();
                i = 0;
                if (current_page < n)
                    current_page++;


                extract(PATH, current_page);

                simpleSeekBar.setProgress(current_page);


            }


        }


    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        // Toast.makeText(this, "Page changed !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // do something on back.
            pausevoice();
            Intent intent = new Intent(PDFViewer.this,MainActivity.class);
            intent.putExtra("PageTrack",current_page+"");
            intent.putExtra("PathTrack",PATH);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}


