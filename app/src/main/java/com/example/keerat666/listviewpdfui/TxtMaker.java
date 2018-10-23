package com.example.keerat666.listviewpdfui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TxtMaker extends AppCompatActivity {
    TextView t;
    Button chnge2;
    String parsedText;
    int statusNight;
    int i = 0;
    int end = 0;
    String p;
    String x;
Spinner spin;
    ScrollView sv;
    TextToSpeech t1;
    String status = "play";
    ImageButton imgbut;
    RelativeLayout rl;
    int tempi;
    RadioGroup rg;
    RadioButton rb1;
    RadioButton rb2;
    LinearLayout ll1;
    int plength = 0;
    ArrayList<Integer> itracker = new ArrayList<>();
    int counter = 0;
    ArrayList<String> lines = new ArrayList<>();
    SeekBar s;
    Menu menu;
    android.support.v7.widget.Toolbar tb;
    Dialog yourDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txt_maker);
        ll1 = (LinearLayout) findViewById(R.id.ppcont1);
        sv = (ScrollView) findViewById(R.id.sviewtxt);
        i = 0;
        tb = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar30);
        setSupportActionBar(tb);
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
        p = getIntent().getStringExtra("txtpath");
        File file = new File(p);

        p = getIntent().getStringExtra("txtpath");
        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll1.getVisibility() == ll1.VISIBLE) {
                    ll1.setVisibility(ll1.GONE);
                } else
                    ll1.setVisibility(ll1.VISIBLE);
            }
        });
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
                lines.add(line);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
            Log.d("error", "" + e.toString());
        }
        t = (TextView) findViewById(R.id.tview);
        t.setText(text.toString());
        parsedText = text.toString()+".";
        s = (SeekBar) findViewById(R.id.seekBar3);
        s.setMax(parsedText.length());
        imgbut = (ImageButton) findViewById(R.id.imageButton2);

        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    i = progress;
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

    public void txtextract() {
        p = getIntent().getStringExtra("txtpath");
        File file = new File(p);

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
                lines.add(line);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
            Log.d("error", "" + e.toString());
        }
        t = (TextView) findViewById(R.id.tview);
        t.setText(text.toString());
        parsedText = text.toString() + ".";
        s.setMax(parsedText.length());
        trigger(0);
    }


    // Log.d("Inner Text",st);

    public void clear() {
        parsedText = "";
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

    public void pausevoice() {

        parsedText = "";
        i = tempi;
        t1.stop();


    }

    public void nextLine(View view) {
        t1.stop();

    }

    public void prevLine(View view) {
        if (itracker.size() >= 2) {
            t1.stop();
            Log.d("i before", "" + i);
            i = i - itracker.get(itracker.size() - 1);
            i = i - itracker.get(itracker.size() - 2);
            Log.d("itracker last", "" + itracker.get(itracker.size() - 1));
            Log.d("I value", i + "");
        } else
            Toast.makeText(this, "No previous line found!", Toast.LENGTH_SHORT).show();


    }


    public void play(View view) {
        if (status.equals("play")) {

            status = "pause";
            imgbut.setImageDrawable(getDrawable(R.drawable.mdpause));
            txtextract();
        } else if (status.equals("pause")) {
            pausevoice();
            status = "play";
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
                if(statusNight==1){
                    str.setSpan(new BackgroundColorSpan(Color.GRAY), i, end + 1, 0);
                }
                else
                str.setSpan(new BackgroundColorSpan(Color.YELLOW), i, end + 1, 0);
                Log.d("string length", "SL " + str.length() + " I " + i + " END " + end);
                String toSpeak = parsedText.substring(i, end);

                t.setText(str);
                tempi = i;
                s.setProgress(end);
                i = end + 1;

                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                plength = end - tempi + 1;
                itracker.add(plength);
                Log.d("plength", itracker + "");
                Log.d("speaking?", t1.isSpeaking() + "");
            } else {
                i = 0;
            }


        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // do something on back.
           // pausevoice();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("PathTrack", p);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hamt, menu);
        this.menu = menu;
        return true;
    }
public void dark()
{
    if(statusNight==0){
    sv.setBackgroundColor(getResources().getColor(R.color.nmode));
    t.setTextColor(getResources().getColor(R.color.background_material_dark));
    statusNight=1;
    }
    else{
        sv.setBackgroundColor(getResources().getColor(R.color.background_material_dark));
        t.setTextColor(getResources().getColor(R.color.nmode));
        statusNight=0;
    }


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
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(TxtMaker.this, android.R.layout.simple_spinner_item, country);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin.setAdapter(spinnerArrayAdapter);
                }
            });


            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    x=spin.getSelectedItem().toString();
                   // Toast.makeText(TxtMaker.this, ""+x, Toast.LENGTH_SHORT).show();
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

        Button chnge;

        chnge = (Button) layout.findViewById(R.id.apply);
        chnge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Toast.makeText(TxtMaker.this, ""+x, Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menuNightt:
                dark();
                break;


            case R.id.vesett: {
                change();
                break;
            }



        }
        return true;

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

}



