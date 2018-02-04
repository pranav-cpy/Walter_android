package com.team.riti.walter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 25/02/2017 AD.
 */

public class reminder extends Activity {
    int count=0;
    String[] bookname;// = {"pranav", "gopi", "pranav", "gopi", "pranav", "gopi", "pranav", "gopi", "pranav", "gopi","walter"};
    String[] author;// = {"actor", "writer", "singer", "actor", "writer", "singer", "actor", "writer", "singer", "teacher","assistent"};
    JSONObject j=new JSONObject();
    JSONObject json=new JSONObject();
    String barcode;
    final String URL="http://192.168.43.29:3010/";
    boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder);
        SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
        barcode=settings.getString("barcode","");
        try {
            while(json.length()>0)
                json.remove(json.keys().next());
            json.put("label", "reminder");
            json.put("barcode", barcode);
            j = HttpClient.SendHttpPost(URL,json);
           /*     JSONArray jsonArray = j.getJSONArray("bookname");
                JSONArray jsonArray1 = j.getJSONArray("author");
                flag = true;
                bookname = new String[jsonArray.length()];
                author = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    bookname[i] = jsonArray.getString(i);
                    author[i] = jsonArray1.getString(i);*/
            count=j.getInt("ct");
            bookname=new String[count];
            author=new String[count];

            for(int i=0;i<count;i++)
             {
                 bookname[i]=j.getString("bookname"+i);
                 author[i]=j.getString("author"+i);
             }
        }catch (JSONException e){

        }catch (NullPointerException e){}
       // super.onCreate(savedInstanceState);
     //   setContentView(R.layout.reminder);
       // Toast.makeText(getApplicationContext(),"ggg"+5,Toast.LENGTH_SHORT).show();
        ListView listview = (ListView) findViewById(R.id.listview);
        final CustomAdapter customadapter = new CustomAdapter();
        listview.setAdapter(customadapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item=bookname[position];
                AlertDialog.Builder alert=new AlertDialog.Builder(reminder.this);
                alert.setMessage("Are you sure you want to remove reminder for "+item);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isconnected()) {
                            try {
                                if(i==-1)
                                i=i+1;
                                else
                                i=i+1;

                                while(json.length()>0)
                                    json.remove(json.keys().next());
                                json.put("label", "cancelreminder");
                                json.put("barcode", barcode);
                                json.put("bookname", bookname[i]);
                                json.put("author", author[i]);
                                j = HttpClient.SendHttpPost(URL, json);
                                Toast.makeText(getApplicationContext(), "removed successfully",
                                        Toast.LENGTH_SHORT).show();
                                while(json.length()>0)
                                    json.remove(json.keys().next());
                                json.put("label", "reminder");
                                json.put("barcode", barcode);
                                j = HttpClient.SendHttpPost(URL, json);
                                count=j.getInt("ct");
                                bookname=new String[count];
                                author=new String[count];
                                for(i=0;i<count;i++)
                                {
                                    bookname[i]=j.getString("bookname"+i);
                                    author[i]=j.getString("author"+i);
                                }
                                customadapter.notifyDataSetChanged();

                            }catch (JSONException e){
                                }catch (NullPointerException e){}
                            }
                        else
                            Toast.makeText(getApplicationContext(),"No internet connection available",Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog=alert.create();
                alertDialog.show();
            }
        });

    }



    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            //if(j!=null)
            return count;
         //   else
          //  return  0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.custom_layout_reminder,null);
            //ImageView imageview = (ImageView) view.findViewById(R.id.imageView);
            TextView textView_name = (TextView) view.findViewById(R.id.textView_book);
            TextView textView_description = (TextView) view.findViewById(R.id.textView_author);
            //imageview.setImageResource(images[i]);
            textView_name.setText(bookname[i]);
            textView_description.setText(author[i]);

            return view;
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
