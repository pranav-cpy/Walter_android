package com.team.riti.walter;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // add data from server to these strings. rate will be integer convert it to string
    boolean first=true;
    String[] bookname;
    String[] author;
    String[] rate ;
    String des="";
    boolean[] availablility ;
    boolean[] reminder_state ;
    JSONObject json=new JSONObject();
    JSONObject j=new JSONObject();
    public Toolbar mtoolbar;
    int result;
    TextView user_name;
    String barcode="";String nameval="";
    TextView txt;
    EditText ed;
    TextToSpeech tts;
    String str="";
    final String URL="http://192.168.43.29:3010/";
    int pos;
    int count=0;
    ///starting voice to text
    public void record(View view) {
        if (view.getId() == R.id.imageButton) {
            speechinput();
        }
    }

    public void speechinput() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening....");
        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "sorry your device doesnt support speech recognition", Toast.LENGTH_LONG).show();
        }

    }

    public void onActivityResult(int request_code, int result_code, Intent i) {//part of voice to text
        super.onActivityResult(request_code, result_code, i);
        switch (request_code) {
            case 100:
                if (result_code == RESULT_OK && i != null) {
                    ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    ed.setText(result.get(0));
                }
                break;
        }
    }
// end of voice to text

    public void okclick(View view)// request submit portion
    {
        str = ed.getText().toString();
        if (str.matches(""))
            Toast.makeText(getApplicationContext(), "Enter a valid request in the textbox or use mic button", Toast.LENGTH_SHORT).show();
        else if (isconnected()) {
            try {
                while(json.length()>0)
                     json.remove(json.keys().next());
                json.put("label", "text");
                json.put("barcode", barcode);
                json.put("request", str);

            } catch (JSONException e) {
            }
            try{
            j = HttpClient.SendHttpPost(URL,json);
            } catch (NullPointerException e){
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
            }
            try {
                if (j.getString("label").equals( "help"))
                    startActivity(new Intent(MainActivity.this, help.class));
                else if (j.getString("label").equals("credit"))
                    startActivity(new Intent(MainActivity.this, credit.class));
                else if (j.getString("label").equals("reminder"))
                    startActivity(new Intent(MainActivity.this, reminder.class));
                else if (j.getString("label").equals("returndate"))
                    startActivity(new Intent(MainActivity.this, returnd.class));
                else if (j.getString("label").equals("fine"))
                    startActivity(new Intent(MainActivity.this, pop.class));
                else if (j.getString("label").equals("logout")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setMessage("Are you sure you want to logout?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (isconnected()) {
                                JSONObject j = new JSONObject();
                                try {
                                    json.put("label", "logout");
                                    json.put("barcode", barcode);
                                    j = HttpClient.SendHttpPost(URL,json);
                                } catch (JSONException e) {
                                    //Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                                SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.clear();
                                editor.apply();
                                Intent any = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(any);
                                Toast.makeText(getApplicationContext(), "logged out successfully",
                                        Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getApplicationContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
                        }

                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();

                }
                else if (j.getString("label").equals("text")) {
                    first = false;
                    count=j.getInt("ct");

                    String response;
                    response=j.getString("response");
                    txt.setText(response);
                    responsevoice();
                    bookname=new String[count];
                    author=new String[count];
                    availablility=new boolean[count];
                    reminder_state=new boolean[count];
                    rate=new String[count];
                    for(int i=0;i<count;i++)
                    {
                        bookname[i]=j.getString("findbook"+i);

                        author[i]=j.getString("author"+i);

                        availablility[i]=j.getBoolean("available"+i);

                        reminder_state[i]=j.getBoolean("reminderset"+i);

                        rate[i]=j.getString("rating"+i);

                    }

                }
                else if(j.getString("label").equals("greetings"))
                {
                    count=0;
                    String response = j.getString("response");
                    txt.setText(response);
                    responsevoice();
                }

            }catch (JSONException e) {

            }


        }
        else
            Toast.makeText(getApplicationContext(),"No internet available",Toast.LENGTH_SHORT).show();
    }



    public void responsevoice() {
        if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
            Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
        } else {
            str = txt.getText().toString();
            tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean b = check_login();
        if (b == false) {
            Intent any = new Intent(MainActivity.this, LoginActivity.class);
            finish();
            startActivity(any);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, MyService.class));
        //getting barcode from shared preference
        SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
        barcode=settings.getString("barcode","");
        nameval=settings.getString("name","Welcome");



        if(isconnected())
        {
            try {



               while(json.length()>0)
                   json.remove(json.keys().next());
               json.put("label","rating");
               json.put("barcode",barcode);
               j = HttpClient.SendHttpPost(URL,json);
               String book=j.getString("bookname");
               String author=j.getString("author");

                   Intent intent =new Intent(MainActivity.this,popup_rate.class);
                  intent.putExtra("name",book);
                   intent.putExtra("author",author);
                   startActivity(intent);
            }catch (JSONException e){
              //  Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
            }catch (NullPointerException e){}


        }
        //list
        ListView listview = (ListView) findViewById(R.id.listview2);
        CustomAdapter customadapter = new CustomAdapter(this);
        listview.setAdapter(customadapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    while(json.length()>0)
                        json.remove(json.keys().next());
                    json.put("label","description");
                    json.put("bookname",bookname[position]);
                    json.put("author",author[position]);
                    pos=position;
                    j = HttpClient.SendHttpPost(URL,json);
                    des=j.getString("description");
                    AlertDialog.Builder alert1 = new AlertDialog.Builder(MainActivity.this);
                    if (availablility[pos] == false) {
                        alert1.setMessage("About the book");
                        alert1.setMessage(bookname[pos]);
                        alert1.setMessage("About the book " + bookname[pos] + "\n\n" + des + "\n\nBook not available !");
                      //  AlertDialog alertDialog = alert1.create();
                      //  alertDialog.show();
                    } else if (availablility[pos] == true) {
                        alert1.setMessage("About the book");
                        alert1.setMessage(bookname[pos]);
                        alert1.setMessage("About the book " + bookname[pos] + "\n\n" + des + "\n\nBook available !");
                     //   AlertDialog alertDialog = alert1.create();
                     //   alertDialog.show();

                    }


                    alert1.setPositiveButton("Read it to me", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                                Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                            } else {
                                str = txt.getText().toString();
                                tts.speak(des, TextToSpeech.QUEUE_FLUSH, null);

                            }


                        }
                    });
                    AlertDialog alertDialog = alert1.create();
                    alertDialog.show();










                } catch (JSONException e) {
                  //  Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }

            }
        });
        //tts object is for voice response
        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    result = tts.setLanguage(Locale.UK);
                } else {
                    Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ed = (EditText) findViewById(R.id.req_box);
        txt = (TextView) findViewById(R.id.txtresponse);
        mtoolbar = (Toolbar) findViewById(R.id.nav);
        setSupportActionBar(mtoolbar);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mtoolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        user_name=(TextView) header.findViewById(R.id.stud_name);
        user_name.setText(nameval);
    }

    @Override
    protected void onDestroy() {
        if(tts!=null){
            tts.stop();
            tts.shutdown();
            super.onDestroy();

        }}

    private boolean check_login() {
        SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
        return (settings.getBoolean("loggedin",false));
        //return settings.getBoolean(LoginActivity.prefs,false);
    }

    class CustomAdapter extends BaseAdapter {
        Context context;
        CustomAdapter(Context context){this.context=context;}

        @Override
        public int getCount() {
            if (first)
                return 0;
           return  count;
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
        public View getView(final int i, View view, ViewGroup parent) {
                view = getLayoutInflater().inflate(R.layout.custom_layout_main, null);
                TextView textView_name = (TextView) view.findViewById(R.id.textView_bookm);
                TextView textView_description = (TextView) view.findViewById(R.id.textView_authorm);
                TextView textview_rate = (TextView) view.findViewById(R.id.textView_rate);
                final ImageView image_bell = (ImageView) view.findViewById(R.id.imageview_rem);
            textView_name.setText(bookname[i]);
            textView_description.setText(author[i]);
            textview_rate.setText(rate[i]);
                if (availablility[i] == false && reminder_state[i] == false)
                    image_bell.setImageResource(R.drawable.bell_no_rem);
                else if (availablility[i] == false && reminder_state[i] == true)
                    image_bell.setImageResource(R.drawable.bell_rem);

                image_bell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean b = isconnected();
                        if (b) {
                            while(json.length()>0)
                                json.remove(json.keys().next());
                            if ((availablility[i] == false) && (reminder_state[i] == false)) {
                                try {
                                    json.put("label","cancelreminder");
                                    json.put("barcode",barcode);
                                    json.put("bookname",bookname[i]);
                                    json.put("author",author[i]);
                                    j = HttpClient.SendHttpPost(URL,json);
                                    image_bell.setImageResource(R.drawable.bell_rem);
                                    reminder_state[i]=true;
                                } catch (JSONException e) {
                                  //  Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            } else if (availablility[i] == false && reminder_state[i] == true) {
                                try {
                                    json.put("label","cancelreminder");
                                    json.put("barcode",barcode);
                                    json.put("bookname",bookname[i]);
                                    json.put("author",author[i]);
                                    j = HttpClient.SendHttpPost(URL,json);
                                    image_bell.setImageResource(R.drawable.bell_no_rem);
                                    reminder_state[i]=false;

                                } catch (JSONException e) {
                                   // Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                            }


                        } else
                            Toast.makeText(getApplicationContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
                    }
                });

                return view;

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_reminder) {
            if(isconnected())
                startActivity(new Intent(MainActivity.this, reminder.class));
            else
                Toast.makeText(getApplicationContext(),"No internet connection available",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_returnd) {
            startActivity(new Intent(MainActivity.this, returnd.class));
        } else if (id == R.id.nav_fine) {
            if(isconnected())
                startActivity(new Intent(MainActivity.this, pop.class));
            else
                Toast.makeText(getApplicationContext(),"No internet connection available",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(MainActivity.this, help.class));
        } else if (id == R.id.nav_credit) {
            startActivity(new Intent(MainActivity.this, credit.class));
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Are you sure you want to logout?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (isconnected()) {

                        try {

                            while(json.length()>0)
                               json.remove(json.keys().next());
                            json.put("label","logout");
                            json.put("barcode",barcode);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                            j = HttpClient.SendHttpPost(URL,json);
                            SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.clear();
                        editor.apply();
                        Intent any = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(any);
                        Toast.makeText(getApplicationContext(), "logged out successfully",
                                Toast.LENGTH_SHORT).show();
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
            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public boolean isconnected()
    {
       try {
           String command = "ping -c 1 google.com";
           return (Runtime.getRuntime().exec(command).waitFor() == 0);
       }catch (Exception e){return false;}
    }




   /*public void senddata(JSONObject json)
    {
       final String send=json.toString();
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... params) {

                return getserverresponse(send);
            }

            @Override
            protected void onPostExecute(String s) {

            }
        }.execute();
    }
 public String getserverresponse(String send)
 {
     HttpPost post=new HttpPost("");
     try {
         StringEntity entity=new StringEntity(send);
         post.setEntity(entity);
         DefaultHttpClient client=new DefaultHttpClient();
         BasicResponseHandler handler=new BasicResponseHandler();
         String response=client.execute(post,handler);
         return response;
     } catch (UnsupportedEncodingException e) {
     }catch (IOException e) {
     }
     return "unable to contact server";
 }*/



}
