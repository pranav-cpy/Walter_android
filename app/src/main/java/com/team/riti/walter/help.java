package com.team.riti.walter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by pranav on 25/02/2017 AD.
 */

public class help extends Activity {
    int result;
    ImageButton voice;
    TextToSpeech tts;
    String str;
    TextView t;
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        voice=(ImageButton)findViewById(R.id.voicebtn);
        voice.setImageResource(R.drawable.voice);
        t = (TextView) findViewById(R.id.text11);
        t.setMovementMethod(new ScrollingMovementMethod());
        tts = new TextToSpeech(help.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    result = tts.setLanguage(Locale.UK);
                } else {
                    Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void talkfn(View view) {
        if (flag ==true) {
            if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
            } else {
                voice.setImageResource(R.drawable.novoice);
                flag = false;
                str = t.getText().toString();
                tts.setSpeechRate(1f);
                tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
            }

            }else {
        if (tts != null) {
            tts.stop();
            flag=true;
            voice.setImageResource(R.drawable.voice);

        }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();

            tts.shutdown();


        }

    }

}


