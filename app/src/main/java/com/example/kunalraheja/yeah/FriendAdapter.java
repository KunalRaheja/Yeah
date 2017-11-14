package com.example.kunalraheja.yeah;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Kunal Raheja on 28-08-2016.
 */
public class FriendAdapter extends ArrayAdapter<UserAdapterItems> {

    Context context;

    public FriendAdapter(Context context, ArrayList<UserAdapterItems> users) {
        super(context, R.layout.user_display, users);
        this.context=context;
    }


    private static class ViewHolder {
        TextView uName;
        TextView uEmail;
        ImageView profilePic;
        Button button;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        UserAdapterItems user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view v

        final ViewHolder viewHolder;
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_display, parent, false);
            viewHolder.uName = (TextView) convertView.findViewById(R.id.ud_name);
            viewHolder.uEmail = (TextView) convertView.findViewById(R.id.ud_email);
            viewHolder.profilePic = (ImageView) convertView.findViewById(R.id.ud_image_url);
            viewHolder.button = (Button) convertView.findViewById(R.id.ud_button);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }


        // Populate the data into the template view using the data object
        viewHolder.uName.setText(user.uname);
        viewHolder.uEmail.setText(user.email);

        {//profile image
            if (!user.url.isEmpty()) {
                byte[] imageAsBytes = Base64.decode(user.url.getBytes(), Base64.DEFAULT);
                Bitmap conv_bm = RequireItems.getRoundedBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                viewHolder.profilePic.setImageBitmap(conv_bm);
                //progressDialog.dismiss();
            } else {
                viewHolder.profilePic.setImageResource(R.drawable.addphoto);
            }
        }

        viewHolder.profilePic.setTag(user);
        viewHolder.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserAdapterItems userAdapterItems=(UserAdapterItems) v.getTag();



                ImageView imageView = new ImageView(getContext());
                if (!userAdapterItems.url.isEmpty()) {
                    byte[] imageAsBytes = Base64.decode(userAdapterItems.url.getBytes(), Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    imageView.setImageBitmap(image);
                    //progressDialog.dismiss();
                } else {
                    imageView.setImageResource(R.drawable.addphoto);
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(imageView);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

            // Return the completed view to render on screen
        });


        viewHolder.button.setTag(user);

        viewHolder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                final UserAdapterItems userAdapterItems=(UserAdapterItems) v.getTag();
                final String myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DatabaseReference referenceToMe = FirebaseDatabase.getInstance().getReference().child("Users").child(myUid).child("requestSend").child(userAdapterItems.uid);
                final DatabaseReference referenceToRequested=FirebaseDatabase.getInstance().getReference().child("Users").child(userAdapterItems.uid).child("requestRecieved").child(userAdapterItems.uid);
                String items[]={"View Profile","Location","Unfriend"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){


                                    case 0:
                                        Intent in = new Intent(getContext(), ProfileDesign.class);
                                        in.putExtra("UID", userAdapterItems.uid);
                                        context.startActivity(in);
                                        break;

                                    case 1:

                                        DatabaseReference Ref =FirebaseDatabase.getInstance().getReference().child("Users").child(userAdapterItems.uid).child("myLoc");
                                        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                               String myLoc=dataSnapshot.getValue().toString();
                                                 AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setMessage(myLoc).setTitle("Location");

                                                AlertDialog alert = builder.create();
                                                alert.show();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;

                                    case 2:
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setMessage("Are you sure you want to unfriend "+userAdapterItems.uname+"!")
                                                .setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //do things
                                                        final DatabaseReference referenceToMe = FirebaseDatabase.getInstance().getReference().child("Users").child(myUid).child("friends").child(userAdapterItems.uid);
                                                        final DatabaseReference referenceToRequested=FirebaseDatabase.getInstance().getReference().child("Users").child(userAdapterItems.uid).child("friends").child(myUid);
                                                            referenceToMe.setValue(null);
                                                            referenceToRequested.setValue(null);

                                                    }
                                                })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                            }
                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();

                                        break;
                                }

                            }
                        })
                        .show();
            }
        });


        return convertView;
    }
}
