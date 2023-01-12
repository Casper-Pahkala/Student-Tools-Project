package com.example.kalenteri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RestaurantsActivity extends AppCompatActivity {
    TextView text,saladText,lunch1Text,lunch2text,mtext,lunch3text,lunch4text;
    ArrayList<String> cottonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        text=findViewById(R.id.text);
        saladText=findViewById(R.id.saladtext);
        lunch1Text=findViewById(R.id.lunch1text);
        lunch2text=findViewById(R.id.lunch2text);
        mtext=findViewById(R.id.mtext);

        lunch3text=findViewById(R.id.lunch3text);
        cottonList=new ArrayList<>();
        new doit().execute();


    }

    public class doit extends AsyncTask<Void,Void,Void> {
        String mainText,mondayLunch1,mondayLunch2,mondaylunch3,weekSalad;
        String salaatti;
        String[] mainseparated,separated,separated1, mondayFood, separated2;
        String[] salad;
        Elements elements;
        Elements element;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Document doc = Jsoup.connect("https://www.cotton-club.fi/ruokalista").get();
                element = doc.select("p.smallsubtitle.item");
                mainText= element.text();
                elements = doc.body().select("*");
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            for(Element child: element){
                cottonList.add(child.text());
            }
            Boolean tuesday=true;
            if(tuesday){
                separated=cottonList.get(0).split(" /");
                String salad = separated[0];

                separated=cottonList.get(4).split(" /");
                String lunch1=separated[0];

                separated=cottonList.get(5).split(" /");
                String lunch2 = separated[0];

                separated=cottonList.get(6).split(" /");
                String lunch3 = separated[0];

                separated=cottonList.get(7).split(" /");
                String lunch4 = separated[0];

                saladText.setText(salad);
                lunch1Text.setText(lunch1);
                lunch2text.setText(lunch2);
                lunch3text.setText(lunch3);
                //lunchText.setText(lunch1+" / "+cottonList.get(5)+" / "+cottonList.get(6)+" / "+cottonList.get(7));
            }




        }
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