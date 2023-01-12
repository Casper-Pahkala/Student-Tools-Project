package com.example.kalenteri;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.InputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    RelativeLayout addButton,mainLayout;
    String dailyQuote;
    TextView dailyQuoteText;
    FirebaseDatabase database;
    DatabaseReference reference;
    ProgressBar progressBar;
    String token;
    int year,month,day,hour,minute;
    TextInputEditText editText;
    String date;
    String textt;
    ArrayList<Model> list;
    Boolean fetched=false;
    Adapter adapter;
    RecyclerView recyclerView;
    TextView restaurantsButton,versionText,noButton,yesButton;
    RelativeLayout updateButton, listContent, userButton;
    String updateURL;
    String v,c;
    TimePicker timePicker;
    String fix1,fix2,fix3,fix4;
    ImageView thumbsup;
    long newcount;
    long noCount,yesCount;
    public static boolean toChat=false;

    final Calendar myCalendar = Calendar. getInstance () ;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        addButton=findViewById(R.id.addButton);
        dailyQuoteText=findViewById(R.id.dailyQuoteText);
        mainLayout=findViewById(R.id.mainLayout);
        progressBar=findViewById(R.id.progressBar);
        list = new ArrayList<>();
        versionText=findViewById(R.id.versionText);
        restaurantsButton=findViewById(R.id.restaurantsButton);
        updateButton=findViewById(R.id.updateButton);
        listContent=findViewById(R.id.listContent);
        noButton=findViewById(R.id.noButton);
        yesButton=findViewById(R.id.yesButton);
        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().getBoolean("toChat")){
                startActivity(new Intent(MainActivity.this, ForumActivity.class));

            }

        }
        userButton=findViewById(R.id.userButton);
        RelativeLayout forumButton = findViewById(R.id.forumButtom);
        forumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ForumActivity.class));
            }
        });

        FirebaseDatabase.getInstance().getReference("update").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    if(snapshot1.getKey().equals("fix1")){
                        fix1=snapshot1.getValue().toString();
                    }
                    if(snapshot1.getKey().equals("fix2")){
                        fix2=snapshot1.getValue().toString();
                    }
                    if(snapshot1.getKey().equals("fix3")){
                        fix3=snapshot1.getValue().toString();
                    }
                    if(snapshot1.getKey().equals("fix4")){
                        fix4=snapshot1.getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.user_sheet);

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                dialog.getWindow().setGravity(Gravity.CENTER);

                SharedPreferences pref = getSharedPreferences("User", Context.MODE_PRIVATE);
                EditText et= dialog.findViewById(R.id.nameET);
                EditText password = dialog.findViewById(R.id.passwordET);
                if(pref!=null) {
                    String name = pref.getString("name", null);
                    String passwor = pref.getString("password", null);
                    et.setText(name);
                    password.setText(passwor);
                }

                TextView confirmButton = dialog.findViewById(R.id.confirmText);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText et= dialog.findViewById(R.id.nameET);
                        EditText password = dialog.findViewById(R.id.passwordET);
                        if(et.getText().toString().length()>0 && password.getText().toString().length()>0){
                            String user = et.getText().toString();
                            String pass = password.getText().toString();

                            SharedPreferences pref = getSharedPreferences("User", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("name",user);
                            editor.putString("password",pass);
                            editor.commit();
                            dialog.hide();
                        }

                    }
                });

            }
        });

        FirebaseDatabase.getInstance().getReference("update").child("forcedupdate").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    String versionName2 = task.getResult().getValue().toString();
                    String versionName = BuildConfig.VERSION_NAME;
                    PackageInfo pInfo = null;
                    try {
                        pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

                        String verCode = pInfo.versionName;
                        v=verCode;
                        if(verCode.equals(versionName2)){

                        }else{
                            final Dialog dialog = new Dialog(MainActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.update_sheet);
                            TextView text1 = dialog.findViewById(R.id.text1);
                            text1.setText(fix1);

                            TextView text2 = dialog.findViewById(R.id.text2);
                            text2.setText(fix2);
                            TextView text3 = dialog.findViewById(R.id.text3);
                            text3.setText(fix3);
                            TextView text4 = dialog.findViewById(R.id.text4);
                            text4.setText(fix4);
                            dialog.show();
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                            dialog.getWindow().setGravity(Gravity.CENTER);
                            dialog.setCancelable(false);
                            RelativeLayout button = dialog.findViewById(R.id.updateButton);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(!updateURL.isEmpty()) {
                                        Uri uri = Uri.parse(updateURL); // missing 'http://' will cause crashed
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);

                                    }
                                }
                            });
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        FirebaseDatabase.getInstance().getReference("update").child("optionalupdate").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    String versionName2 = task.getResult().getValue().toString();
                    try {
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        int verCode = pInfo.versionCode;
                        c=String.valueOf(verCode);
                        if (String.valueOf(verCode).equals(versionName2)) {
                            updateButton.setVisibility(View.GONE);
                        }
                        TextView text1=findViewById(R.id.text1);
                        text1.setText(fix1);
                        TextView text2 = findViewById(R.id.text2);
                        text2.setText(fix2);
                        TextView text3 = findViewById(R.id.text3);
                        text3.setText(fix3);
                        TextView text4 = findViewById(R.id.text4);
                        text4.setText(fix4);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        updateButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!updateURL.isEmpty()) {
                    Uri uri = Uri.parse(updateURL); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                }else{
                    Toast.makeText(MainActivity.this, "No updates", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        restaurantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, restActivity
                        .class));
            }
        });

        FirebaseDatabase.getInstance().getReference("update").child("url").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    updateURL=task.getResult().getValue().toString();
                }
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Fetching FCM registration token failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        token = task.getResult();

                        SharedPreferences pref = getSharedPreferences("Token", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("token",token);
                        editor.commit();
                        FirebaseDatabase.getInstance().getReference("users").child(token).child("token").setValue(token);
                        adapter= new Adapter(list, MainActivity.this,token);
                        recyclerView=findViewById(R.id.recycler);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        };
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setHasFixedSize(true);
                        listenForChanges();
                        fetched=true;
                    }
                });

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_sheet);
        editText = dialog.findViewById(R.id.editText);
        CalendarView calendar = dialog.findViewById(R.id.calendar);
        TextView readyButton = dialog.findViewById(R.id.readyButton);
        TextView cancelButton = dialog.findViewById(R.id.cancelButton);
        timePicker=dialog.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        calendar.setMinDate((new Date().getTime()));

        SimpleDateFormat sdff = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat sdfff = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat sdffff = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat sdfffff = new SimpleDateFormat("HH", Locale.getDefault());
        SimpleDateFormat sdfffffff = new SimpleDateFormat("mm", Locale.getDefault());
        year= Integer.parseInt(sdff.format(new Date()));
        month= Integer.parseInt(sdfff.format(new Date()));
        day= Integer.parseInt(sdffff.format(new Date()));
        hour= Integer.parseInt(sdfffff.format(new Date()));
        minute=Integer. parseInt(sdfffffff.format(new Date()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        String currentdate = sdf.format(new Date());
        date=currentdate;

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                i1=i1+1;
                date=i2+"/"+i1;
                year=i;
                month=i1;
                day=i2;
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                hour=i;
                minute=i1;

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                textt=text;
                String id = UUID.randomUUID().toString();
                Model model = new Model(text,date,id);

                FirebaseDatabase.getInstance().getReference("Ilmoitukset").child(token).child(id)
                        .setValue(model);
                listenForChanges();
                dialog.hide();

                myCalendar .set(Calendar. YEAR , year) ;
                myCalendar .set(Calendar. MONTH , month) ;
                myCalendar .set(Calendar. DAY_OF_MONTH , day) ;
                myCalendar.set(Calendar.HOUR_OF_DAY,hour);
                myCalendar.set(Calendar.MINUTE,minute);
                updateLabel() ;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });

        reference= FirebaseDatabase.getInstance().getReference("dailyquote").child("quote");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dailyQuoteText.setText(snapshot.getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("dailyquote").child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.getValue().equals("")){

                    new DownloadImageTask((ImageView) findViewById(R.id.dailyQuoteImage))
                            .execute(snapshot.getValue().toString());

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PackageInfo pInfo = null;
                            try {
                                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                int verCode = pInfo.versionCode;
                                c=String.valueOf(verCode);
                                String verCode2 = pInfo.versionName;
                                v=verCode2;
                                versionText.setText("v."+v+"."+c);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            progressBar.setVisibility(View.GONE);
                                               mainLayout.setVisibility(View.VISIBLE);
                        }
                    }, 00);

                }else{
                    ImageView imageView=findViewById(R.id.dailyQuoteImage);
                    imageView.setImageDrawable(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        VideoView videoView = findViewById(R.id.dailyQuoteVideo);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        TextView lukkariButton = findViewById(R.id.lukkariButton);
        lukkariButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LukkarikoneActivity.class));
            }
        });

        FirebaseDatabase.getInstance().getReference("dailyquote").child("video").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.getValue().equals("")){

                    VideoView videoView = findViewById(R.id.dailyQuoteVideo);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    videoView.setLayoutParams(layoutParams);
                    videoView.setVideoPath(snapshot.getValue().toString());


                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            mainLayout.setVisibility(View.VISIBLE);
                            videoView.setVisibility(View.VISIBLE);
                            videoView.start();
                        }
                    }, 1200);
                }else{
                    VideoView videoView = findViewById(R.id.dailyQuoteVideo);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 150);
                    videoView.setLayoutParams(layoutParams);
                    videoView.setVisibility(View.GONE);
                    videoView.stopPlayback();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        results();

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void scheduleNotification (Notification notification , long delay) {
        Intent notificationIntent = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION , notification) ;
        int random= new Random().nextInt(99999);
        PendingIntent pendingIntent = PendingIntent.getBroadcast ( this, random , notificationIntent , PendingIntent.FLAG_IMMUTABLE ) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;


        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.DAY_OF_MONTH,day);

        cal.set(Calendar.HOUR_OF_DAY,hour);
        cal.set(Calendar.MINUTE,minute);
        cal.set(Calendar.SECOND,0);
        int asd= (int) (cal.getTimeInMillis());

        SimpleDateFormat sdfff = new SimpleDateFormat("MM", Locale.getDefault());
        int month1= Integer.parseInt(sdfff.format(new Date()));
        SimpleDateFormat sdffff = new SimpleDateFormat("dd", Locale.getDefault());
        int day1= Integer.parseInt(sdffff.format(new Date()));
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
        int hour1= Integer.parseInt(sdf.format(new Date()));
        SimpleDateFormat sdff = new SimpleDateFormat("mm", Locale.getDefault());
        int minute1= Integer.parseInt(sdff.format(new Date()));

        Calendar cal2=Calendar.getInstance();
        cal2.set(Calendar.MONTH,month1);
        cal2.set(Calendar.YEAR,year);
        cal2.set(Calendar.DAY_OF_MONTH,day1);

        cal2.set(Calendar.HOUR_OF_DAY,hour1);
        cal2.set(Calendar.MINUTE,minute1);
        cal2.set(Calendar.SECOND,0);

        String date = month+" "+day+" "+hour+":"+minute+" "+year;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd HH:mm yyyy", Locale.getDefault());
        LocalDateTime localDate = LocalDateTime.parse(date, formatter);
        long timeInMilliseconds = localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
        Toast.makeText(this, String.valueOf(timeInMilliseconds), Toast.LENGTH_SHORT).show();
        int delayy = (day-day1)*24*60*60*1000 + (hour-hour1)*60*60*1000+ (minute-minute1)*60*1000;
        int delay2 = (month-month1)*30*24*60*60*1000+(day-day1)*24*60*60*1000 + (hour-hour1)*60*60*1000+ (minute-minute1)*60*1000 - 24*60*60*1000;
        long diff = cal.getTimeInMillis() - cal2.getTimeInMillis();
        Toast.makeText(this, "Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS), Toast.LENGTH_SHORT).show();


       // alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+delayy, pendingIntent);

        Intent notificationIntent2 = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent2.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent2.putExtra(MyNotificationPublisher. NOTIFICATION , getNotification("Muistus!","Huomenna "+textt));
        int random2= new Random().nextInt(99999);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast ( this, random2 , notificationIntent2 , PendingIntent.FLAG_IMMUTABLE ) ;
       // alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+delayy-24*60*, pendingIntent);


    }
    private Notification getNotification (String title,String content) {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.DAY_OF_MONTH,day);

        cal.set(Calendar.HOUR_OF_DAY,hour);
        cal.set(Calendar.MINUTE,minute);
        cal.set(Calendar.SECOND,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( title ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }

    private void updateLabel () {



        scheduleNotification(getNotification("Muistutus!",textt) , 1234) ;

    }


    private void results(){
        FirebaseDatabase.getInstance().getReference("dailyquote").child("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noCount =(long) snapshot.child("no").getValue();
                yesCount =(long) snapshot.child("yes").getValue();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noCount=noCount+1;
                FirebaseDatabase.getInstance().getReference("dailyquote").child("rating").child("no").setValue(noCount).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        noButton.setVisibility(View.GONE);
                        yesButton.setVisibility(View.GONE);
                        showResults();
                    }
                });
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("dailyquote").child("rating").child("yes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        long count = (long) task.getResult().getValue();
                        newcount = count+1;
                        FirebaseDatabase.getInstance().getReference("dailyquote").child("rating").child("yes").setValue(newcount).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                noButton.setVisibility(View.GONE);
                                yesButton.setVisibility(View.GONE);
                                showResults();

                            }
                        });
                    }
                });
            }
        });
    }

    private void showResults() {
        FirebaseDatabase.getInstance().getReference("dailyquote").child("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long nocount =(long) snapshot.child("no").getValue();
                long yescount =(long) snapshot.child("yes").getValue();
                TextView notext= findViewById(R.id.noText);
                notext.setText(String.valueOf(nocount));
                TextView yestext= findViewById(R.id.yesText);
                yestext.setText(String.valueOf(yescount));
                RelativeLayout results = findViewById(R.id.results);
                results.setVisibility(View.VISIBLE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void listenForChanges() {

        FirebaseDatabase.getInstance().getReference("Ilmoitukset").child(token).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Model model = snapshot1.getValue(Model.class);
                    list.add(model);
                    listContent.setVisibility(View.VISIBLE);
                    Collections.sort(list, new Comparator<Model>() {

                        @Override
                        public int compare(Model itemModel, Model t1) {
                            String first;
                            String second;
                            if(itemModel.getDate().length()==5) {
                                first = itemModel.getDate().charAt(3) + "" + itemModel.getDate().charAt(4)
                                        + itemModel.getDate().charAt(0) + "" + itemModel.getDate().charAt(1);
                            }else {
                                first = itemModel.getDate().charAt(2) + "" + itemModel.getDate().charAt(3)
                                        + 0 + "" + itemModel.getDate().charAt(0);
                            }

                            if(t1.getDate().length()==5) {
                                second = t1.getDate().charAt(3) + "" + t1.getDate().charAt(4)
                                        + t1.getDate().charAt(0) + "" + t1.getDate().charAt(1);
                            }else {
                                second = t1.getDate().charAt(2) + "" + t1.getDate().charAt(3)
                                        + 0 + "" + t1.getDate().charAt(0);
                            }
                            return first.compareTo(second);
                        }
                    });
                    adapter.notifyDataSetChanged();





                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
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
            token = pref.getString("token", null);
            Long tsLong = System.currentTimeMillis();
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            FirebaseDatabase.getInstance().getReference("users").child(token).child("lastOnline").setValue(date+"/"+tsLong);
        }


    }

    public void updateParticularField(String fieldName,String fieldValue){

        SharedPreferences pref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        if(pref!=null) {


            token = pref.getString("token", null);
            if (token != null) {
                if (!token.equals("null")) {
                    FirebaseDatabase.getInstance().getReference("users").child(token).child(fieldName).setValue(fieldValue);


                }
            }
        }


    }
    public String getElapsedDaysText(Long c1, Long c2)
    {
        String elapsedDaysText = null;
        try
        {
            long milliSeconds1 = c1;
            long milliSeconds2 = c2;
            long periodSeconds = (milliSeconds2 - milliSeconds1) / 1000;
            long elapsedDays = periodSeconds / 60 / 60 / 24 -29;
            elapsedDaysText = String.valueOf(elapsedDays);
        }
        catch (Exception e)
        {

        }
        return elapsedDaysText;
    }
}