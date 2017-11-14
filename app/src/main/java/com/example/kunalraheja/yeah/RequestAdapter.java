package com.example.kunalraheja.yeah;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunal Raheja on 14-08-2016.
 */
public class RequestAdapter extends ArrayAdapter<RequestAdapterItems> {


    public RequestAdapter(Context context, ArrayList<RequestAdapterItems> reqs) {
        super(context, R.layout.request_layout,reqs);
    }

    private static class ViewHolder {
        TextView uName;
        TextView accept , reject;
        ImageView profilePic;
    }

    public View getView(int position,  View convertView, ViewGroup parent){

        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.request_layout, parent, false);
            viewHolder.uName = (TextView) convertView.findViewById(R.id.request_text);
            viewHolder.accept = (TextView) convertView.findViewById(R.id.request_accept);
            viewHolder.reject = (TextView) convertView.findViewById(R.id.request_reject);
            viewHolder.profilePic = (ImageView) convertView.findViewById(R.id.request_image_url);
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final RequestAdapterItems req = getItem(position);

        //Remains fixed
        viewHolder.uName.setText(req.name+" Wants to be Your Friend !!");

        {//profile image
            if (!req.profile_pic_url.isEmpty()) {
                byte[] imageAsBytes = Base64.decode(req.profile_pic_url.getBytes(), Base64.DEFAULT);
                Bitmap conv_bm = RequireItems.getRoundedBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                viewHolder.profilePic.setImageBitmap(conv_bm);
                //progressDialog.dismiss();
            } else {
                viewHolder.profilePic.setImageResource(R.drawable.addphoto);
            }
        }
        viewHolder.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = new ImageView(getContext());
                if (!req.profile_pic_url.isEmpty()) {
                    byte[] imageAsBytes = Base64.decode(req.profile_pic_url.getBytes(), Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    imageView.setImageBitmap(image);
                }
                else{
                    imageView.setImageResource(R.drawable.addphoto);
                }
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(imageView);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


        viewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference referenceToMe= FirebaseDatabase.getInstance().getReference().child("Users").child(myUid);
                referenceToMe.child("friends").child(req.uid).setValue(req.name);

                DatabaseReference referenceToSender= FirebaseDatabase.getInstance().getReference().child("Users").child(req.uid);
                referenceToSender.child("friends").child(myUid).setValue(RequireItems.getMe());

                DatabaseReference referenceToMeRequest= FirebaseDatabase.getInstance().getReference().child("Users").child(myUid).child("requestReceived");
                referenceToMeRequest.child(req.uid).setValue(null);

                DatabaseReference referenceToSenderRequest= FirebaseDatabase.getInstance().getReference().child("Users").child(req.uid).child("requestSend");
                referenceToSenderRequest.child(myUid).setValue(null);

                Toast.makeText(getContext(),req.name+" is now your Friend !!",Toast.LENGTH_LONG).show();
            }
        });


        viewHolder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference referenceToMe= FirebaseDatabase.getInstance().getReference().child("Users").child(myUid).child("requestReceived");
                DatabaseReference referenceToSender= FirebaseDatabase.getInstance().getReference().child("Users").child(req.uid).child("requestSend");

                referenceToMe.child(req.uid).setValue(null);

                referenceToSender.child(myUid).setValue(null);

                Toast.makeText(getContext(),"Request Rejected",Toast.LENGTH_LONG).show();
            }
        });


        return convertView;
    }


}

