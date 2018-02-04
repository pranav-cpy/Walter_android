package com.team.riti.walter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.View;
import android.app.Activity;
import android.os.Bundle;
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

/**
 * Created by pranav on 25/02/2017 AD.
 */

public class pop extends Activity {
    String barcode;
    TextView fine_txt;
    final String URL="http://192.168.43.29:3010/";;
    double fine=0.0;
    JSONObject j=new JSONObject();
    JSONObject json=new JSONObject();
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        fine_txt=(TextView) findViewById(R.id.amount);
        SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
        barcode=settings.getString("barcode","");
        try {
            while(json.length()>0)
                json.remove(json.keys().next());
            json.put("label","fine");json.put("barcode",barcode);
            j = HttpClient.SendHttpPost(URL,json);
            fine=j.getInt("fine");
            fine_txt.setText( String.valueOf(fine));

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
        }catch (NullPointerException e){}
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width= dm.widthPixels;
        int height= dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.4));
    }


}