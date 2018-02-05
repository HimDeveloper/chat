//package com.example.admin.chat;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//
//import java.util.ArrayList;
//
///**
// * Created by admin on 1/11/2018.
// */
//
//public class SendImgAdapter extends ArrayAdapter {
//
//    private Context context;
//    ArrayList<Model> listimg = new ArrayList();
//    private Bitmap bitmap;
//
//    public SendImgAdapter(Context context, int resource, ArrayList<Model> obj) {
//        super(context, resource);
//        this.context=context;
//        listimg=obj;
//    }
//
//    public class Holder{
//        ImageView sendimg;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
//
//        Holder holder = new Holder();
//        if (view == null){
//            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            view =layoutInflater.inflate(R.layout.sendimage, parent, false);
//            holder.sendimg = view.findViewById(R.id.img_send);
//            view.setTag(holder);
//        }else {
//            holder = (Holder)view.getTag();
//        }
//
//        bitmap = BitmapFactory.decodeFile(MainActivity.file.getEncodedPath());
//        holder.sendimg.setImageBitmap(bitmap);
//        return view;
//    }
//}
