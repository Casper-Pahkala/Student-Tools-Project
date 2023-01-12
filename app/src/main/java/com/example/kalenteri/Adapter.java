package com.example.kalenteri;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {
    ArrayList<Model> list;
    String token;
    Context context;
    String date2;

    public Adapter(ArrayList<Model> list, Context context, String token) {
        this.list = list;
        this.context = context;
        this.token=token;

    }

    @NonNull
    @Override
    public Adapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.ilmoitus_layout, parent, false);
        return new MyHolder(v);



    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.MyHolder holder, int position) {
        final Model model = list.get(position);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_sheet);
        TextView confirm = dialog.findViewById(R.id.readyButton);
        TextView cancelButton = dialog.findViewById(R.id.cancelButton);


        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        try {
            String date = model.getDate();
            if(date.length()==5) {
                date2 = "2022-" + date.charAt(3) + "" + date.charAt(4) + "-" + date.charAt(0) + date.charAt(1);
            }else{
                date2 = "2022-" + date.charAt(2) + "" + date.charAt(3) + "-"  +0+ date.charAt(1);

            }
            Date futureDate = dateFormat.parse(date2);
            Date currentDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
                String currentdate = sdf.format(new Date());
                if (currentdate.equals(model.getDate())) {
                    holder.timeLeftText.setText("Today");

                } else {


                    long diff = futureDate.getTime()
                            - currentDate.getTime();
                    long days = diff / (24 * 60 * 60 * 1000);
                    days = days + 1;
                    String day = String.valueOf(days);



                    if (days == 1) {
                        holder.timeLeftText.setText("Tomorrow");

                    } else {
                        if (day.charAt(0) == 0) {
                            holder.timeLeftText.setText("In " + day.charAt(1) + " Days");
                        } else {
                            holder.timeLeftText.setText("In " + day + " Days");
                        }
                    }

                }




        } catch (ParseException e) {
            e.printStackTrace();
        }


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        String currentdate = sdf.format(new Date());


        final Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.exit_left_to_right);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                list.remove(model);
                notifyDataSetChanged();


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("Ilmoitukset").child(token).child(model.getId()).removeValue();
                dialog.hide();
                holder.layout.startAnimation(animation);
                Toast.makeText(context, "Ilmoitus poistettu", Toast.LENGTH_SHORT).show();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        holder.mainText.setText(model.getText());
        holder.dateText.setText(model.getDate());
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                dialog.getWindow().setGravity(Gravity.CENTER);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dateText, mainText, timeLeftText;
        RelativeLayout layout;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            dateText=itemView.findViewById(R.id.dateText);
            mainText=itemView.findViewById(R.id.text);
            timeLeftText=itemView.findViewById(R.id.timeLeftText);
            layout=itemView.findViewById(R.id.layout);

        }

        @Override
        public void onClick(View view) {


        }
    }
}
