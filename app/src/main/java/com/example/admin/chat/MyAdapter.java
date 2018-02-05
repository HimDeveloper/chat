package com.example.admin.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.admin.chat.MainActivity.type;


/**
 * Created by admin on 1/15/2018.
 */

class MyAdapter extends ArrayAdapter {

    private Context context;
    private List<Model> chatdetails = new ArrayList<>();
    private LayoutInflater inflater;


    public MyAdapter(@NonNull Context context, int resource, ArrayList<Model> Object) {
        super(context, resource, Object);
        this.context = context;
        this.chatdetails = Object;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    class Holder {
        TextView username, message, time, img_username, img_time;
        RelativeLayout relativeLayout, rl_img;
        ImageView img_send;
        ImageView sendimg;
        TextView sender_username, sender_message, sender_time;
    }


    @Override
    public View getView(int position, View v, ViewGroup parent) {
        Holder holder = new Holder();
        Model m = (Model) getItem(position);



        if (m.getIsme().equalsIgnoreCase("me")) {
            v = inflater.inflate(R.layout.senderow, parent, false);
            holder.relativeLayout = v.findViewById(R.id.rl_container);
            holder.username = v.findViewById(R.id.txt_username);
            holder.time = v.findViewById(R.id.txt_time);
            holder.message = v.findViewById(R.id.txt_msg);
//            holder.rl_img = v.findViewById(R.id.rl_img);
//            holder.img_username = v.findViewById(R.id.img_username);
//            holder.img_time = v.findViewById(R.id.img_time);
//            holder.img_send = v.findViewById(R.id.img_send);
//
//            if(type == 1) {
//                holder.rl_img.setVisibility(View.GONE);
//                v.setTag(holder);
//            }else {
//                holder.relativeLayout.setVisibility(View.GONE);
//                v.setTag(holder);
//            }

            holder.username.setText(chatdetails.get(position).getUsername());
            holder.message.setText(chatdetails.get(position).getTextmessage());
            holder.time.setText(DateFormat.format("HH:mm", chatdetails.get(position).getTime()));

//            holder.img_username.setText(chatdetails.get(position).getUsername());
//            holder.img_send.setImageURI(chatdetails.get(position).getUrl());
//            holder.img_time.setText(DateFormat.format("HH:mm", chatdetails.get(position).getTime()));

        } else if (m.getIsme().equalsIgnoreCase("you")){
            v = inflater.inflate(R.layout.row, parent, false);
            holder.relativeLayout = v.findViewById(R.id.rl_container);
            holder.username = v.findViewById(R.id.txt_username);
            holder.time = v.findViewById(R.id.txt_time);
            holder.message = v.findViewById(R.id.txt_msg);
//            holder.rl_img = v.findViewById(R.id.rl_img);
//            holder.img_username = v.findViewById(R.id.img_username);
//            holder.img_time = v.findViewById(R.id.img_time);
//            holder.img_send = v.findViewById(R.id.img_send);

            holder.username.setText(chatdetails.get(position).getUsername());
            holder.message.setText(chatdetails.get(position).getTextmessage());
            holder.time.setText(DateFormat.format("HH:mm", chatdetails.get(position).getTime()));

//            holder.img_username.setText(chatdetails.get(position).getUsername());
//            holder.img_send.setImageURI(chatdetails.get(position).getUrl());
//            holder.img_time.setText(DateFormat.format("HH:mm", chatdetails.get(position).getTime()));

//            if(type == 1) {
//                holder.rl_img.setVisibility(View.GONE);
//                v.setTag(holder);
//            }else {
//                holder.relativeLayout.setVisibility(View.GONE);
//                v.setTag(holder);
//            }
        }

        Log.e("Postio", String.valueOf(position));



        return v;
    }
}
