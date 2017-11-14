package com.example.kunalraheja.yeah;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Kunal Raheja on 07-08-2016.
 */
public class ProfileDesign extends AppCompatActivity {

    TextView email_id , phone , bday , uname , status;
    ImageButton profile_pic,back;
    String uid ="",pic_url="";
    ProgressDialog progressDialog;


//    ProgressDialog pd1 = new ProgressDialog(ProfileDesign.this);

    DatabaseReference user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_design_edit);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        email_id=(TextView)findViewById(R.id.activity_pd_email);
        uname=(TextView)findViewById(R.id.activity_pd_name);
        status=(TextView)findViewById(R.id.activity_pd_status);
        bday=(TextView)findViewById(R.id.activity_pd_bday);
        phone=(TextView)findViewById(R.id.activity_pd_ph);
        profile_pic=(ImageButton)findViewById(R.id.activity_pd_photo);
        back=(ImageButton)findViewById(R.id.activity_pd_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

/*            @Override
            public void onClick(View v) {

                ImageView imageView = new ImageView(getApplicationContext());
                if (!pic_url.isEmpty()) {
                    byte[] imageAsBytes = Base64.decode(pic_url.getBytes(), Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    imageView.setImageBitmap(image);
                    //progressDialog.dismiss();
                } else {
                    imageView.setImageResource(R.drawable.addphoto);
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                alertDialogBuilder.setView(imageView);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });*/



        String uid=getIntent().getStringExtra("UID");
        if(uid==null||uid.isEmpty()){
            finish();

        }
        else{
            Toast.makeText(this,uid,Toast.LENGTH_SHORT).show();
            DatabaseReference user= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

            user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    UserDetail u =  dataSnapshot.getValue(UserDetail.class);
                    uname.setText(u.getuName());
                    status.setText(u.getStatus());
                    email_id.setText(u.getEmail());
                    phone.setText(u.getPhone());
                    bday.setText(u.getBday());

                    //profile Image
                    String base64Image = u.getPhoto_url();
                    pic_url=u.getPhoto_url();
                    if(!base64Image.isEmpty()) {
                        byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
                        Bitmap conv_bm = RequireItems.getRoundedBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                        profile_pic.setImageBitmap(conv_bm);
                        progressDialog.dismiss();
                    }
                    else{
                        profile_pic.setImageResource(R.drawable.addphoto);
                    }
                }



                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

}
