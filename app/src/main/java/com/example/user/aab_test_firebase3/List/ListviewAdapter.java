package com.example.user.aab_test_firebase3.List;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.user.aab_test_firebase3.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ListviewAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private ArrayList<Listviewitem> data;
    private int layout;
    public ListviewAdapter(Context context, int layout, ArrayList<Listviewitem> data){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data=data;
        this.layout=layout;
    }
    @Override
    public int getCount(){return data.size();}
    @Override
    public String getItem(int position){return data.get(position).getName();}
    @Override
    public long getItemId(int position){return position;}
    @Override
    public View getView(int position, View convertView, final ViewGroup parent){
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
        }
        Listviewitem listviewitem=data.get(position);
        final ImageView icon=(ImageView)convertView.findViewById(R.id.imageview);

        //[ Firebase Storage IMAGE 적용 START ]
        String foodId=listviewitem.getName();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("foods");
        storageRef.child(foodId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(parent).load(uri).into(icon);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        //[ Firebase Storage IMAGE 적용 END ]

        TextView name=(TextView)convertView.findViewById(R.id.textview);
        name.setText(listviewitem.getName());
        return convertView;
    }
}