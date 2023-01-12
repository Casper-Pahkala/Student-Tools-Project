package com.example.kalenteri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class ForumActivity extends AppCompatActivity {
    EditText messageEditText;
    ImageView sendButton;
    String token;
    RecyclerView recyclerView;
    ArrayList<MessageModel> list;
    MessageAdapter adapter;
    String name;
    int count;
    ArrayList<String> list2;
    Boolean inApp;
    ArrayList<String> usedMessagesList;
    ArrayList<TokenStatusModel> tokenList;
    private static  final int PICK_IMAGE_REQUEST = 1;
    boolean dialogShown=false;
    boolean toChat=false;
    Uri imageUri;
    boolean imageChanged =false;
    String imageID;
    StorageReference storageRef;
    ProgressBar progressBar;
    RelativeLayout mainLayout;
    String color,key;
    boolean colorFetched =false;
    public static StorageReference storageReff;
    public static String pictureRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        sendButton=findViewById(R.id.sendButton);
        progressBar= findViewById(R.id.progressBar);
        MainActivity.toChat=false;
        getSupportActionBar().hide();
        recyclerView=findViewById(R.id.recycler);
        list=new ArrayList<>();

        adapter= new MessageAdapter(this,list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        SharedPreferences pref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        token = pref.getString("token",null);
        messageEditText=findViewById(R.id.messageEditText);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        mainLayout=findViewById(R.id.mainLayout);
        progressBar=findViewById(R.id.progressBar);
        SharedPreferences sharedPreferences=getSharedPreferences("MyPrefsFile",0);
        try{
            if(sharedPreferences.getString("color","")!=null){
                color=sharedPreferences.getString("color","");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        list2=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("users").child(pref.getString("token",null)).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getValue()==null){
                        final Dialog dialog = new Dialog(ForumActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.name_sheet);
                        dialog.show();
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.setCancelable(false);
                        TextView confirmButton = dialog.findViewById(R.id.confirmText);
                        confirmButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                confirmButton.setClickable(false);
                                EditText et= dialog.findViewById(R.id.nameET);
                                if(et.getText().toString().length()>0){
                                    name = et.getText().toString();
                                    String upperString = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

                                    FirebaseDatabase.getInstance().getReference().child("colors").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()) {

                                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                    if (!colorFetched) {
                                                        color = snapshot1.getValue().toString();
                                                        key = snapshot1.getKey();
                                                        colorFetched = true;
                                                    }

                                                }
                                                FirebaseDatabase.getInstance().getReference("colors").child(key).removeValue();
                                                FirebaseDatabase.getInstance().getReference("users").child(token).child("name").setValue(upperString);
                                                FirebaseDatabase.getInstance().getReference("users").child(token).child("color").setValue(color);
                                                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile", 0);
                                                sharedPreferences.edit().putString("color", color).commit();
                                                dialog.hide();
                                            }else{
                                                sharedPreferences.edit().putString("color", "#4A92FF").commit();
                                                FirebaseDatabase.getInstance().getReference("users").child(token).child("color").setValue("#4A92FF");
                                                color="#4A92FF";
                                                FirebaseDatabase.getInstance().getReference("users").child(token).child("name").setValue(upperString);
                                                dialog.hide();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });




                                }

                            }
                        });
                    }else{
                        name = task.getResult().getValue().toString();
                    }

                }else{

                }
            }
        });
        tokenList=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tokenList.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    TokenStatusModel model = snapshot1.getValue(TokenStatusModel.class);
                    tokenList.add(model);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(ForumActivity.this, "Fetching FCM registration token failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        token = task.getResult();

                    }
                });
        usedMessagesList=new ArrayList<>();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(messageEditText.getText().toString().trim().length()>0){
                    String message = messageEditText.getText().toString().trim();
                    Long tsLong = System.currentTimeMillis();
                    MessageModel model = new MessageModel(message, token, tsLong, "",name,color);
                    list.add(model);
                    usedMessagesList.add(String.valueOf(tsLong));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        list.sort(new Comparator<MessageModel>() {
                            @Override
                            public int compare(MessageModel messageModel, MessageModel t1) {
                                return String.valueOf(messageModel.getTimestamp()).compareTo(String.valueOf(t1.getTimestamp()));
                            }
                        });
                    }
                    sendNotifications(message);
                    FirebaseDatabase.getInstance().getReference("forum").child("chats").child(UUID.randomUUID().toString()).setValue(model);
                    messageEditText.setText("");
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(usedMessagesList.size()-1);

                    messageEditText.setText("");
                }else{

                    openFileChooser();
                    toChat=true;
                }


            }
        });

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(messageEditText.getText().toString().length()>0){
                    sendButton.setImageResource(R.drawable.send_message_icon);
                }else{
                    sendButton.setImageResource(R.drawable.paperclip_iconn);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        FirebaseDatabase.getInstance().getReference("forum").child("chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshot1: snapshot.getChildren()){
                            MessageModel model = snapshot1.getValue(MessageModel.class);
                            if(!usedMessagesList.contains(String.valueOf(model.getTimestamp()))) {
                                list.add(model);
                                usedMessagesList.add(String.valueOf(model.getTimestamp()));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    list.sort(new Comparator<MessageModel>() {
                                        @Override
                                        public int compare(MessageModel messageModel, MessageModel t1) {
                                            return String.valueOf(messageModel.getTimestamp()).compareTo(String.valueOf(t1.getTimestamp()));
                                        }
                                    });
                                }

                            }

                        }adapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(list.size());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference("forum").child("chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshot1: snapshot.getChildren()){
                            list2.add(snapshot1.getKey().toString());

                        }

                        if(list2.size()>list.size()){
                            if(isScreenOn&&inApp){
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }, 2000);
        String root = "/data/data/com.example.kalenteri/app_imageDir";
        try{
            String name = sharedPreferences.getString("backgroundName","");
            setBackground(root,name );
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView moreButton = findViewById(R.id.moreButton);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toChat=false;
                openFileChooser();
            }
        });


    }

    private void setBackground(String path,String child){
        try {
            File f=new File(path, child);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.chatBackground);
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
        }
    }
    private void openFileChooser(){
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            if(toChat) {
                imageUri = data.getData();
                imageChanged = true;
                storageRef = FirebaseStorage.getInstance().getReference("chatImages");
                sendImage();
                mainLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }else{
                imageUri = data.getData();
                imageChanged = true;
                uploadToDevice();

            }
        }


    }
    private void sendImage(){
        Long tsLong = System.currentTimeMillis();
        imageID = UUID.randomUUID().toString();
        MessageModel model = new MessageModel("", token , tsLong, imageID+"."+getFileExtension(imageUri),name,color);
        FirebaseDatabase.getInstance().getReference("forum").child("chats").push().setValue(model);
        uploadFile();



    }
    private void uploadFile(){
        if(imageUri != null){
            StorageReference fileReference= storageRef.child(imageID+"."+ getFileExtension(imageUri));

            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    startActivity(new Intent(ForumActivity.this, ForumActivity.class));


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                }
            });




        }else{
        }

    }
    private String getFileExtension(Uri uri){
        ContentResolver cR= getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadToDevice(){
        if(imageUri != null) {
            Picasso.with(this)
                    .load(imageUri)
                    .into(new Target() {
                              @Override
                              public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                  try {
                                      ContextWrapper cw = new ContextWrapper(getApplicationContext());
                                      // path to /data/data/yourapp/app_data/imageDir
                                      File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                                      // Create imageDir
                                      SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile",0);
                                      String name = System.currentTimeMillis() + ".jpg";
                                      sharedPreferences.edit().putString("backgroundName",name).commit();
                                      File mypath=new File(directory,name);
                                      FileOutputStream out = new FileOutputStream(mypath);

                                      try {

                                          bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                      } catch (Exception e) {
                                          e.printStackTrace();
                                      } finally {
                                          try {
                                              out.close();
                                          } catch (IOException e) {
                                              e.printStackTrace();
                                          }
                                      }


                                  } catch (Exception e) {
                                      Toast.makeText(ForumActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                  }

                                  String root = "/data/data/com.example.kalenteri/app_imageDir";
                                  SharedPreferences sharedPreferences=getSharedPreferences("MyPrefsFile",0);
                                  try{
                                      String name = sharedPreferences.getString("backgroundName","");
                                      setBackground(root,name );
                                  } catch (Exception e) {
                                      e.printStackTrace();
                                  }
                              }

                              @Override
                              public void onBitmapFailed(Drawable errorDrawable) {

                              }

                              @Override
                              public void onPrepareLoad(Drawable placeHolderDrawable) {
                              }
                          }
                    );
        }else{
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateParticularField("onlineStatus","onlineChat");
        inApp=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateParticularField("onlineStatus","offline");
        inApp=false;
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
        token = pref.getString("token",null);
        if (!token.equals("null")) {
            FirebaseDatabase.getInstance().getReference("users").child(token).child(fieldName).setValue(fieldValue);


        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForumActivity.this,MainActivity.class));
        finishAffinity();

    }
    private void sendNotifications(String message){
        int count = 0;
        for(int i=0;i<tokenList.size();i++){


            if(token.equals(tokenList.get(i).getToken())){


                if(tokenList.get(i).getOnlineStatus().equals("onlineChat")){
                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,"TEEST","TAST",getApplicationContext(),ForumActivity.this);
                    notificationsSender.SendNotifications(tokenList.get(i).getToken(),name,message);
                }


                if(tokenList.get(i).getOnlineStatus().equals("online")){

                }

                if(tokenList.get(i).getOnlineStatus().equals("offline")){

                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,"TEEST","TAST",getApplicationContext(),ForumActivity.this);
                    notificationsSender.SendNotifications(tokenList.get(i).getToken(),name,message);
                }



            }

        }
    }

    public void showDialog(){
        dialogShown=true;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_show_full_picture);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        ImageView imageView=dialog.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        try{
            ForumActivity.storageReff.child(ForumActivity.pictureRef).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(dialog.getContext()).load(uri).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}