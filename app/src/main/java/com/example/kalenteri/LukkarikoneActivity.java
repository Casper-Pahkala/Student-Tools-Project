package com.example.kalenteri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LukkarikoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lukkarikone);
        getSupportActionBar().setTitle("Lukkarikone");

        WebView wb = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = wb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String url = "https://lukkarit.uwasa.fi/#/";
        wb.getSettings().setJavaScriptEnabled(true);
        wb.loadUrl(url);

        wb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //link not opened in chrome
                if (url.startsWith("https://login.uwasa")) {
                    final WebView webView = (WebView) findViewById(R.id.webview);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setWebChromeClient(new WebChromeClient());
                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            SharedPreferences pref = getSharedPreferences("User", Context.MODE_PRIVATE);

                            if(pref!=null){
                                String name = pref.getString("name",null);
                                String password=pref.getString("password",null);

                                String jsContent = "function() { document.getElementById('username').value = '"+name+"'; " +
                                        "document.getElementById('password').value = '"+password+"';" +
                                        "document.getElementsByName('_eventId_proceed')[0].click(); }";

                                view.loadUrl("javascript:(" + jsContent + ")()");
                            }

                        }
                    });

                }

                view.loadUrl(url);
                return true;
            }
        });
        wb.loadUrl(url);





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