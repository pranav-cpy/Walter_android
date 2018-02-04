package com.team.riti.walter;
/* Created by pranav on 25/02/2017 AD.
 */
    import java.text.DateFormat;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.Calendar;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.Map;

    import android.app.Activity;
    import android.app.Dialog;
    import android.app.TimePickerDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.support.v7.app.AlertDialog;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.AdapterView;
    import android.widget.BaseAdapter;
    import android.widget.ImageButton;
    import android.widget.ListView;
    import android.widget.TextView;
    import android.widget.TimePicker;
    import android.widget.Toast;
    import com.android.volley.Request;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.JsonObjectRequest;
    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

public class returnd extends Activity {
        long []id,id1;
        int count=0;
        Date date[] ;//=new Date[20];
        String[] dd;
        String[] bookname ;
//fill the above strings with data  coming over network
    String barcode;
        private TextView displayTime;
    final  String URL="http://192.168.43.29:3010/";
    JSONObject j=new JSONObject();
    JSONObject json=new JSONObject();
        String str;
        int flag=0;
        private ImageButton pickTime;
        private TextView session;
        int  hr,pHour=07;
        int pMinute=30;
        /** This integer will uniquely define the dialog to be used for displaying time picker.*/
        static final int TIME_DIALOG_ID = 0;


        /** Callback received when the user "picks" a time in the dialog */
        private TimePickerDialog.OnTimeSetListener mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        pHour = hourOfDay;
                        pMinute = minute;
                        hr=pHour;
                        if(hr>=12)
                        {
                            flag=1;
                        }
                        if(hr>13)
                            hr=hr%12;

                        updateDisplay();
                        displayToast();
                        SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("hour",pHour);
                        editor.putInt("minute",pMinute);

                        editor.apply();
                    }
                };

        /** Updates the time in the TextView */
        private void updateDisplay() {
            displayTime.setText(
                    new StringBuilder()
                            .append(pad(hr)).append(":")
                            .append(pad(pMinute)));
            if (flag==0){
                str="am";
                session.setText("am");}
            else{
                 str="pm";
                session.setText("pm");}
        }

        /** Displays a notification when the time is updated */
        private void displayToast() {
            Toast.makeText(this, new StringBuilder().append("alert time is set for ").append(displayTime.getText()).append(str),Toast.LENGTH_SHORT).show();

        }

        /** Add padding to numbers less than ten */
        private static String pad(int c) {
            if (c >= 10)
                return String.valueOf(c);
            else
                return "0" + String.valueOf(c);
        }

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.returndates);
            SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
            barcode=settings.getString("barcode","");
            pHour=settings.getInt("hour",07);
            pMinute=settings.getInt("minute",30);


            SharedPreferences.Editor editor = settings.edit();
            try {
                while(json.length()>0)
                    json.remove(json.keys().next());
                json.put("label","returndate");
                json.put("barcode",barcode);
                j = HttpClient.SendHttpPost(URL,json);

                         count=j.getInt("ct");

                        bookname = new String[count];
                        date = new Date[count];
                        dd= new String[count];
                        id = new long[count];
                        editor.putInt("noofbookstoreturn",count);
                        for(int i=0;i<count;i++) {
                            bookname[i]=j.getString("bookname"+i);
                            String str = j.getString("returndate"+i);

                           // SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
                          //  Date d = sdf.parse(str);
                          //  date[i] = d;
                            dd[i]= str;//j.getString("date"+i);
                            id[i]=j.getLong("bookid"+i);
                            editor.putString("booktoreturn"+i,bookname[i]);
                            editor.putString("returndate"+i,str);
                            editor.putLong("idreturnbook"+i,id[i]);
                            editor.apply();
                        }
                }catch (JSONException e){
              //  Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
           // } catch (ParseException e) {
            }
            ListView listview = (ListView) findViewById(R.id.listview1);
            final CustomAdapter customadapter = new CustomAdapter();
            listview.setAdapter(customadapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    String item= bookname[position];


                    AlertDialog.Builder alert=new AlertDialog.Builder(returnd.this);
                    alert.setMessage("Are you sure you want to renew the book  "+item);
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
                            long bookid=settings.getLong("idreturnbook"+position,0);
                            if (isconnected()) {
                                try {
                                    while(json.length()>0)
                                        json.remove(json.keys().next());
                                    json.put("label", "renew");
                                    json.put("barcode", barcode);
                                    json.put("bookid", String.valueOf(bookid));
                                    j = HttpClient.SendHttpPost(URL,json);
                                    if (j.get("status").equals( "no"))
                                        Toast.makeText(getApplicationContext(), "Book can't be renewed", Toast.LENGTH_SHORT).show();
                                    else if (j.get("status").equals("yes"))
                                        Toast.makeText(getApplicationContext(), "renewed successfully", Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                }
                                try {
                                    SharedPreferences.Editor editor = settings.edit();
                                    json.put("label", "returndate");
                                    json.put("barcode", barcode);
                                 j=HttpClient.SendHttpPost(URL,json);

                                    count=j.getInt("ct");
                                    bookname = new String[count];
                                    date = new Date[count];
                                    id1 = new long[count];

                                    for(int k=0;k<count;k++) {
                                        bookname[k]=j.getString("bookname"+k);
                                        String str = j.getString("date"+k);
                                       // SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
                                       // Date d = sdf.parse(str);
                                      //  date[k] = d;
                                        dd[k]=str;
                                        id1[k]=j.getLong("bookid"+k);

                                        editor.putInt("noofbookstoreturn",count);
                                        editor.putString("booktoreturn"+k,bookname[k]);
                                        editor.putString("returndate"+k,str);
                                        editor.putLong("idreturnbook"+k,id1[k]);
                                        editor.apply();
                                    }
                                    customadapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                   // Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                               // } catch (ParseException e) {

                                }
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

            /** Capture our View elements */
            displayTime = (TextView) findViewById(R.id.timedisplay);
            pickTime = (ImageButton) findViewById(R.id.picktime);
            session= (TextView)findViewById(R.id.session);
            //@Override
            //public boolean onCreateOptionsMenu(Menu menu) {
            //getMenuInflater().inflate(R.menu.main, menu);
            //return true;
            //}

            // @Override
            //public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            //int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            //if (id == R.id.action_settings) {
            //  return true;
            //}

            //return super.onOptionsItemSelected(item);
            //}


            /** Listener for click event of the button */
            pickTime.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDialog(TIME_DIALOG_ID);
                }
            });


            /** Display the default time in the TextView */
            displayTime.setText(
                    new StringBuilder()
                            .append(pad(pHour)).append(":")
                            .append(pad(pMinute)));
            session.setText(" ");
        }

        /** Create a new dialog for time picker */

        @Override
        protected Dialog onCreateDialog(int id) {
            switch (id) {
                case TIME_DIALOG_ID:
                    return new TimePickerDialog(this,
                            mTimeSetListener, pHour, pMinute, false);
            }
            return null;
        }

        class CustomAdapter extends BaseAdapter {

            @Override
            public int getCount() {
                if (isconnected())
                    return count;
                SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
                return settings.getInt("noofbookstoreturn",0);
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
                view = getLayoutInflater().inflate(R.layout.custom_layout_returnd,null);
                //ImageView imageview = (ImageView) view.findViewById(R.id.imageView);
                TextView textView_name = (TextView) view.findViewById(R.id.textView_book1);
                TextView textView_description = (TextView) view.findViewById(R.id.textView_date);
                SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
                textView_name.setText(settings.getString("booktoreturn"+i,""));
                textView_description.setText(settings.getString("returndate"+i,""));
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

