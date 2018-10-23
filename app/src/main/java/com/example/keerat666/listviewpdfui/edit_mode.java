package com.example.keerat666.listviewpdfui;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class edit_mode extends AppCompatActivity {
    TextView t;
    String parsedText;
    int i = 0;
    int end = 0;
    String p;
    ScrollView sv;
    TextToSpeech t1;
    String status = "play";
    ImageButton imgbut;
    RelativeLayout rl;
    int tempi;
    LinearLayout ll1;
    int plength = 0;
    ArrayList<Integer> itracker = new ArrayList<>();
    int counter = 0;
    ArrayList<String> lines = new ArrayList<>();
    SeekBar s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mode);
        ll1 = (LinearLayout) findViewById(R.id.ppcont1);
        rl = (RelativeLayout) findViewById(R.id.rlcont);
        sv = (ScrollView) findViewById(R.id.sviewtxt);
        i = 0;
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    t1.setPitch(1.0f);
                    t1.setSpeechRate(1.0f);
                    Log.d("voices", t1.getVoices() + "");
                    // Toast.makeText(TxtMaker.this, ""+t1.getVoices(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        p = getIntent().getStringExtra("pdfpath");
        File file = new File(p);

        StringBuilder text = new StringBuilder();
        try {
            parsedText = "";
            PdfReader reader = new PdfReader(p);
            int n = reader.getNumberOfPages();
            for (int ig = 1; ig < n; ig++) {
                parsedText = parsedText + PdfTextExtractor.getTextFromPage(reader, ig); //Extracting the content from the different pages
            }

            Log.d("Extract text:", parsedText + "sup");
            reader.close();
            t = (TextView) findViewById(R.id.tview);
            t.setText(parsedText);
        } catch (Exception e) {
            Log.d("extract method ", "extract: " + e);
        }

        s = (SeekBar) findViewById(R.id.seekBar3);
        s.setMax(parsedText.length());
        imgbut = (ImageButton) findViewById(R.id.imageButton2);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(edit_mode.this, "table created ", Toast.LENGTH_LONG).show();

                if (ll1.getVisibility() == (ll1.VISIBLE)) {

                    ll1.setVisibility(ll1.GONE);


                } else {


                    ll1.setVisibility(ll1.VISIBLE);

                }


            }
        });
        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    i = progressChangedValue;
                    t1.stop();
                    Log.d("Seek", "" + progressChangedValue);
                }

            }


            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("progress", "" + progressChangedValue);
            }
        });


    }

    void extract(String yourPdfPath) {

        try {
            parsedText = "";
            PdfReader reader = new PdfReader(yourPdfPath);
            int n = reader.getNumberOfPages();
            for (int ig = 1; ig <= n; ig++) {
                parsedText = parsedText + PdfTextExtractor.getTextFromPage(reader, ig); //Extracting the content from the different pages
            }
parsedText+=".";
            Log.d("Extract text:", parsedText + "sup");
            trigger(0);
            reader.close();
        } catch (Exception e) {
            Log.d("extract method ", "extract: " + e);
        }
    }









    // Log.d("Inner Text",st);

    public void clear()
    {
        parsedText="";
        i = 0;
        t1.stop();
    }
    public void trigger(final int i) {
        Log.d("speaking-highlight", t1.isSpeaking() + "");
        final Handler handler = new Handler();
        final int delay = 0; //milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {
                //do something
                if (!t1.isSpeaking()) {
                    highlight(i);
                }


                handler.postDelayed(this, delay);
            }
        }, delay);


    }

    public void pausevoice()
    {

        parsedText="";
        i=tempi;
        t1.stop();

    }

    public void nextLine(View view)
    {
        t1.stop();

    }

    public void prevLine(View view)
    {
        if(itracker.size()>=2)
        {
            t1.stop();
            Log.d("i before",""+i);
            i=i-itracker.get(itracker.size()-1);
            i=i-itracker.get(itracker.size()-2);
            Log.d("itracker last",""+itracker.get(itracker.size()-1));
            Log.d("I value",i+"");
        }
        else
            Toast.makeText(this, "No previous line found!", Toast.LENGTH_SHORT).show();



    }



    public void play(View view)
    {
        if(status.equals("play"))
        {

            status="pause";
            imgbut.setImageDrawable(getDrawable(R.drawable.mdpause));
            extract(p);
        }
        else if(status.equals("pause"))
        {
            pausevoice();
            status="play";
            imgbut.setImageDrawable(getDrawable(R.drawable.mdplay));
        }

    }
    public void highlight(int h) {

        String span;
        if (i <= parsedText.length()) {
            SpannableString str = new SpannableString(parsedText);

            try {

                end = parsedText.indexOf(".", i);
                Log.d("end index", end + "");

            } catch (Exception e) {
                Log.d("Ending exception", e + "");
                // Toast.makeText(this, "End of i!", Toast.LENGTH_SHORT).show();

            }

            if (end != -1) {

                str.setSpan(new BackgroundColorSpan(Color.YELLOW), i, end + 1, 0);
                Log.d("string length","SL "+str.length()+" I "+i+" END "+end);
                String toSpeak = parsedText.substring(i, end);

                t.setText(str);
                tempi=i;
                s.setProgress(end);
                i = end + 1;

                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                plength=end-tempi+1;
                itracker.add(plength);
                Log.d("plength",itracker+"");
                Log.d("speaking?", t1.isSpeaking() + "");
            }
            else
            {
                i=0;
            }

        }

    }
}
