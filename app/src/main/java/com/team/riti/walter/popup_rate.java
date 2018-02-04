package com.team.riti.walter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.team.riti.walter.R.drawable.fine;

/**
 * Created by pranav on 25/02/2017 AD.
 */
//listner for submit,send data to server
public class popup_rate extends Activity {
    RatingBar rb; Button bt; TextView txt;
    String name1;//=getIntent().getStringExtra("name");
    String author1;//=getIntent().getStringExtra("author");

    //String name=intent.getStringExtra("name");
   // String author=intent.getStringExtra("author");
    final String URL="http://192.168.43.29:3010/";
    JSONObject j=new JSONObject();
    JSONObject json=new JSONObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_rate);

         name1=getIntent().getStringExtra("name");
        author1=getIntent().getStringExtra("author");

        rb=(RatingBar)findViewById(R.id.ratingBar);
        bt=(Button)findViewById(R.id.submit);
        txt=(TextView)findViewById(R.id.rate_bookname);
        txt.setText(name1);
        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .85), (int) (height * .6));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float i=rb.getRating();

                try {
                    while(json.length()>0)
                        json.remove(json.keys().next());
                    json.put("label","rate");
                    json.put("bookname",name1);
                    json.put("author",author1);
                    json.put("ratevalue",i);

                    j = HttpClient.SendHttpPost(URL,json);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }
                Intent intent =new Intent(popup_rate.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
