package com.example.kalenteri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class restActivity extends AppCompatActivity implements View.OnClickListener {
    TextView text;
    ArrayList<CottonClubModel> list;
    RelativeLayout backButton;
    boolean ma,ti,ke,to,pe;
    TextView Ma,Ti,Ke,To,Pe;
    Document doc;
    Elements element;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
        text=findViewById(R.id.text);
        backButton=findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        list=new ArrayList<>();
        getSupportActionBar().hide();
        ma=false;
        ti=false;
        ke=false;
        to=false;
        pe=false;

        Ma=findViewById(R.id.Ma);
        Ti=findViewById(R.id.Ti);
        Ke=findViewById(R.id.Ke);
        To=findViewById(R.id.To);
        Pe=findViewById(R.id.Pe);
        new doit().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){


            case R.id.Ma:
                if(ma==false) {
                    ma = true;
                    ti = false;
                    ke = false;
                    to = false;
                    pe = false;
                    element = doc.select("div#monday");
                    Ma.setTextColor(Color.parseColor("#03DCC6"));
                    Ti.setTextColor(Color.parseColor("#FFFFFF"));
                    Ke.setTextColor(Color.parseColor("#FFFFFF"));
                    To.setTextColor(Color.parseColor("#FFFFFF"));
                    Pe.setTextColor(Color.parseColor("#FFFFFF"));
                    refresh();
                }
                break;
            case R.id.Ti:
                if(ti==false) {
                    ma = false;
                    ti = true;
                    ke = false;
                    to = false;
                    pe = false;
                    element = doc.select("div#tuesday");
                    Ti.setTextColor(Color.parseColor("#03DCC6"));
                    Ma.setTextColor(Color.parseColor("#FFFFFF"));
                    Ke.setTextColor(Color.parseColor("#FFFFFF"));
                    To.setTextColor(Color.parseColor("#FFFFFF"));
                    Pe.setTextColor(Color.parseColor("#FFFFFF"));
                    refresh();
                }
                break;
            case R.id.Ke:
                if(!ke) {
                    ma = false;
                    ti = false;
                    ke = true;
                    to = false;
                    pe = false;
                    element = doc.select("div#wednesday");
                    Ke.setTextColor(Color.parseColor("#03DCC6"));
                    Ma.setTextColor(Color.parseColor("#FFFFFF"));
                    Ti.setTextColor(Color.parseColor("#FFFFFF"));
                    To.setTextColor(Color.parseColor("#FFFFFF"));
                    Pe.setTextColor(Color.parseColor("#FFFFFF"));
                    refresh();
                }
                break;
            case R.id.To:
                if(!to) {
                    ma = false;
                    ti = false;
                    ke = false;
                    to = true;
                    pe = false;
                    element = doc.select("div#thursday");
                    To.setTextColor(Color.parseColor("#03DCC6"));
                    Ma.setTextColor(Color.parseColor("#FFFFFF"));
                    Ti.setTextColor(Color.parseColor("#FFFFFF"));
                    Ke.setTextColor(Color.parseColor("#FFFFFF"));
                    Pe.setTextColor(Color.parseColor("#FFFFFF"));
                    refresh();
                }
                break;
            case R.id.Pe:
                if(!pe) {
                    ma = false;
                    ti = false;
                    ke = false;
                    to = false;
                    pe = true;
                    element = doc.select("div#friday");
                    Pe.setTextColor(Color.parseColor("#03DCC6"));
                    Ma.setTextColor(Color.parseColor("#FFFFFF"));
                    Ti.setTextColor(Color.parseColor("#FFFFFF"));
                    Ke.setTextColor(Color.parseColor("#FFFFFF"));
                    To.setTextColor(Color.parseColor("#FFFFFF"));
                    refresh();
                }
                break;
        }
    }

    public class doit extends AsyncTask<Void,Void,Void> {
        String mainText,mondayLunch1,mondayLunch2,mondaylunch3,weekSalad;
        String salaatti;
        String[] mainseparated,separated,separated1, mondayFood, separated2;
        String[] salad;
        Element elements;


        @Override
        protected Void doInBackground(Void... voids) {

            try {
                doc = Jsoup.connect("https://kurnii.fi/").get();
                mainText= element.text();


            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            switch (day) {

                case Calendar.MONDAY:
                    ma=true;
                    Ma.setTextColor(Color.parseColor("#03DCC6"));

                    element = doc.select("div#monday");
                    break;
                case Calendar.TUESDAY:
                    ti=true;
                    Ti.setTextColor(Color.parseColor("#03DCC6"));

                    element = doc.select("div#tuesday");
                    break;
                case Calendar.WEDNESDAY:
                    ke=true;
                    Ke.setTextColor(Color.parseColor("#03DCC6"));

                    element = doc.select("div#wednesday");
                    break;
                case Calendar.THURSDAY:
                    To.setTextColor(Color.parseColor("#03DCC6"));

                    to=true;
                    element = doc.select("div#thursday");
                    break;
                case Calendar.FRIDAY:
                    Pe.setTextColor(Color.parseColor("#03DCC6"));

                    pe=true;
                    element = doc.select("div#friday");
                    break;

            }

            refresh();


        }







        }
        public void refresh(){
            ProgressBar progressBar=findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            LinearLayout content=findViewById(R.id.content);
            content.setVisibility(View.GONE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        Element child = element.get(0);
                        Element child2 = child.child(0);
                        TextView lunch1 = findViewById(R.id.saladtext);
                        lunch1.setText("- " + child2.child(child2.children().size()-1).text());
                        TextView lunch2 = findViewById(R.id.lunch1text);
                        lunch2.setText("- " + child2.child(1).text());
                        TextView lunch3 = findViewById(R.id.lunch2text);
                        lunch3.setText("- " + child2.child(2).text());
                        if(child2.children().size()>4) {
                            TextView lunch4 = findViewById(R.id.lunch3text);
                            lunch4.setText("- " + child2.child(3).text());
                        }
                        if(child2.children().size()>5) {
                            TextView lunch5 = findViewById(R.id.lunch4text);
                            lunch5.setVisibility(View.VISIBLE);
                            lunch5.setText("- " + child2.child(4).text());
                        }else{
                            TextView lunch5 = findViewById(R.id.lunch4text);
                            lunch5.setVisibility(View.GONE);

                        }


                        Element child3 = child.child(2);
                        TextView lunch12 = findViewById(R.id.lunch12text);
                        lunch12.setText("- " + child3.child(1).text());
                        if(child3.children().size()>2){
                            TextView lunch22 = findViewById(R.id.lunch22text);
                            lunch22.setText("- " + child3.child(2).text());
                        }

                        if(child3.children().size()>3){
                            TextView lunch32 = findViewById(R.id.lunch32text);
                            lunch32.setText("- " + child3.child(3).text());
                        }


                        Element child4 = child.child(4);
                        TextView lunch13 = findViewById(R.id.lunch13text);
                        lunch13.setText("- " + child4.child(1).text());
                        TextView lunch23 = findViewById(R.id.lunch23text);
                        lunch23.setText("- " + child4.child(2).text());

                        Element child5 = child.child(6);
                        TextView lunch14 = findViewById(R.id.lunch14text);
                        lunch14.setText("- " + child5.child(1).text());
                        TextView lunch24 = findViewById(R.id.lunch24text);
                        lunch24.setText("- " + child5.child(2).text());
                        TextView lunch34 = findViewById(R.id.lunch34text);
                        lunch34.setText("- " + child5.child(3).text());


                        Element child6 = child.child(7);
                        TextView lunch15 = findViewById(R.id.lunch15text);
                        lunch15.setText("- " + child6.child(1).text());
                        TextView lunch25 = findViewById(R.id.lunch25text);
                        lunch25.setText("- " + child6.child(2).text());

                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                        LinearLayout content = findViewById(R.id.content);
                        content.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        e.printStackTrace();
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                        LinearLayout content = findViewById(R.id.content);
                        content.setVisibility(View.VISIBLE);
                    }



                }
            }, 300);

        }

    @Override
    protected void onResume() {
        super.onResume();
        updateParticularField("onlineStatus","online");

    }

    @Override
    protected void onPause() {
        super.onPause();
        updateParticularField("onlineStatus","offline");
        SharedPreferences pref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        if(pref!=null) {
            String token = pref.getString("token", null);
            Long tsLong = System.currentTimeMillis();
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            FirebaseDatabase.getInstance().getReference("users").child(token).child("lastOnline").setValue(date+"/"+tsLong);
        }


    }

    public void updateParticularField(String fieldName,String fieldValue){

        SharedPreferences pref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        String token = pref.getString("token",null);
        if (!token.equals("null")) {
            FirebaseDatabase.getInstance().getReference("users").child(token).child(fieldName).setValue(fieldValue);


        }
    }
    }
