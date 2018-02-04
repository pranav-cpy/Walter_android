package com.team.riti.walter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via barcode.
 */
public class LoginActivity extends AppCompatActivity{
    private Button bt;
    TextView t;
    public static final String prefs = "myprefsfile";
    boolean b;
    JSONObject j=new JSONObject();
    JSONObject json=new JSONObject();
    final String URL="http://192.168.43.29:3010/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);
        t = (TextView) findViewById(R.id.txt);
        bt = (Button) findViewById(R.id.button_login);
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String barcode=t.getText().toString();
                  b=isconnected();
                if(!barcode.matches("")) {
                    if (b) {
                        try {
                            while(json.length()>0)
                                json.remove(json.keys().next());
                            json.put("label", "login");
                            json.put("barcode", barcode);
                            j = HttpClient.SendHttpPost(URL,json);
                          //  Toast.makeText(getApplicationContext(),j.toString(),Toast.LENGTH_SHORT).show();
                         //   Toast.makeText(getApplicationContext(),"name:"+j.getString("status"),Toast.LENGTH_SHORT).show();
                            if (j.getString("status").equals("yes"))
                                {
                                    String name=j.getString("name");
                                    SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putBoolean("loggedin",true);
                                    editor.putString("barcode",barcode);
                                    editor.putString("name",name);
                                    editor.apply();
                                    Intent any = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(any);
                                    finish();

                                }
                                else if(j.getString("status").equals("no"))
                                    Toast.makeText(getApplicationContext(), "Invalid barcode value", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e){

                        }

                    }
                    else
                        Toast.makeText(getApplicationContext(),"No internet connection available",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Enter barcode",Toast.LENGTH_SHORT).show();
                }
            }
        });
            }


    public void barcode(View v) {
        Intent intent = new Intent(LoginActivity.this, scan_barcode.class);
       startActivityForResult(intent, 0);

    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data) {
        if (requestcode == 0) {
            if (resultcode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    t.setText(barcode.displayValue);
                } else {
                    t.setText("No barcode found");
                }
            } else {
                super.onActivityResult(requestcode, resultcode, data);

            }
        }

    }
    public boolean isconnected()
    {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        }catch (Exception e){return false;}
    }
}

















































































































































































































































































