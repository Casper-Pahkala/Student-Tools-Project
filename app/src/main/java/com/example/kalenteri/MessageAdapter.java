package com.example.kalenteri;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {
    private Context context;
    ArrayList<MessageModel> messageModelList;
    MessageAdapter messageAdapter;
    RelativeLayout messageLT;
    int ITEM_SEND = 1;
    String tokenid;
    String name;
    int ITEM_RECEIVE = 2;
    View view;
    int ITEM_SEND_IMAGE = 3;
    int ITEM_RECEIVE_IMAGE = 4;
    public MessageAdapter(Context context, ArrayList<MessageModel> messageModelList) {
        this.context = context;
        this.messageModelList =messageModelList;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sendermessage,parent,false);
            return new SenderViewHolder(view);

        }else if(viewType==ITEM_RECEIVE){
            View view = LayoutInflater.from(context).inflate(R.layout.receivermessage,parent,false);
            return new SenderViewHolder(view);
        }else if(viewType==ITEM_SEND_IMAGE){
            View view = LayoutInflater.from(context).inflate(R.layout.sendermessage_image,parent,false);
            return new ImageViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.receivermessage_image,parent,false);
            return new ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MessageModel model = messageModelList.get(position);


        int minutes = (int) ((model.getTimestamp() / (1000 * 60)) % 60);
        int hours = (int) ((model.getTimestamp() / (1000 * 60 * 60)) % 24);
        hours = hours + 2;
        String minute = String.valueOf(minutes);
        String hour = String.valueOf(hours);
        if (minute.length() == 1) {
            minute = "0" + minutes;
        }
        if (hour.length() == 1) {
            hour = "0" + hours;
        }
        if (hour.equals("24")) {
            hour = "00";
        }
        if (hour.equals("25")) {
            hour = "01";
        }

        if(holder.getClass() == SenderViewHolder.class){
            SenderViewHolder viewHolder = (SenderViewHolder) holder;

            viewHolder.textviewmessage.setText(model.getMessage());
            viewHolder.timeofmessage.setText(hour + ":" + minute);
            viewHolder.textviewmessage.setVisibility(View.VISIBLE);
            viewHolder.nameText.setText(model.getName());
            viewHolder.nameText.setTextColor(Color.parseColor(model.getColor()));

        }
        else {
            ImageViewHolder viewHolder = (ImageViewHolder) holder;
            viewHolder.timeofmessage.setText(hour + ":" + minute);
            viewHolder.messageImageView.setVisibility(View.VISIBLE);
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            viewHolder.nameText.setText(model.getName());
            viewHolder.nameText.setTextColor(Color.parseColor(model.getColor()));

            viewHolder.messageImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ForumActivity.storageReff=FirebaseStorage.getInstance().getReference("chatImages");
                    ForumActivity.pictureRef= model.getImageRef();


                    if (context instanceof ForumActivity) {
                        ((ForumActivity)context).showDialog();
                    }
                }
            });

            StorageReference storageRef= FirebaseStorage.getInstance().getReference("chatImages");

            storageRef.child(model.getImageRef()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(context)
                            .load(uri)
                            .into(viewHolder.messageImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                    if(model.getMessage().equals("")){
                                        viewHolder.textviewmessage.setVisibility(View.GONE);
                                    }else{
                                        viewHolder.textviewmessage.setVisibility(View.VISIBLE);
                                        viewHolder.textviewmessage.setText(model.getMessage());
                                    }
                                }

                                @Override
                                public void onError() {

                                }
                            });
                }
            });
        }


    }


    @Override
    public int getItemViewType(int position) {
        MessageModel messages = messageModelList.get(position);
        SharedPreferences pref = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
        String token = pref.getString("token", null);
        if(token.equals(messages.getSenderToken()))
        {
            if(messages.getImageRef().equals(""))
            {
                return ITEM_SEND;

            }else{
                return ITEM_SEND_IMAGE;
            }


        }else{
            if(messages.getImageRef().equals(""))
            {
                return ITEM_RECEIVE;

            }else{
                return ITEM_RECEIVE_IMAGE;
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView textviewmessage,timeofmessage,nameText;
        RelativeLayout asdLayout;
        ProgressBar progressBar;
        public SenderViewHolder(@NonNull View itemview){
            super(itemview);
            textviewmessage=itemview.findViewById(R.id.senderMessage);
            timeofmessage=itemview.findViewById(R.id.timeText);
            nameText=itemview.findViewById(R.id.nameText);
            asdLayout=itemview.findViewById(R.id.asd);
            progressBar=itemview.findViewById(R.id.progressBar);

        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        TextView textviewmessage,timeofmessage,nameText;
        ShapeableImageView messageImageView;
        ProgressBar progressBar;

        public ImageViewHolder(@NonNull View itemview){
            super(itemview);
            textviewmessage=itemview.findViewById(R.id.senderMessage);
            timeofmessage=itemview.findViewById(R.id.timeText);
            nameText=itemview.findViewById(R.id.nameText);
            messageImageView=itemview.findViewById(R.id.messageImageView);
            progressBar=itemview.findViewById(R.id.progressBar);



        }
    }
}