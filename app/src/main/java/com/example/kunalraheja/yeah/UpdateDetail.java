package com.example.kunalraheja.yeah;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class UpdateDetail extends AppCompatActivity {

    ImageButton bt_back , bt_ed_status ,bt_ed_uname , bt_profile_image,update_pic;
    Button bt_save;
    EditText bday , ph;
    TextView email;
    TextView name , status;

    Bitmap file;
    String choosepic[]={"Open Gallery","Remove"};
    ProgressDialog progressDialog;

    String FB_TABLE_USERS="Users",FB_URL="photo_url",FB_MYLOC="myLoc",FB_LOCATIONS="locations",FB_PHONE="phone",FB_NAME="uName",FB_BDAY="bday",FB_STATUS="status",FB_EMAIL="email";


    DatabaseReference user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_detail);


       progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        bt_back=(ImageButton)findViewById(R.id.update_detail_bt_back);
        bt_ed_status=(ImageButton)findViewById(R.id.update_detail_bt_status);
        bt_ed_uname=(ImageButton)findViewById(R.id.update_detail_bt_uname);
        bt_save=(Button)findViewById(R.id.update_detail_bt_save);
        bt_profile_image=(ImageButton)findViewById(R.id.update_detail_bt_profile_image);
        update_pic=(ImageButton)findViewById(R.id.update_pic);

        status = (TextView)findViewById(R.id.update_detail_status) ;
        name = (TextView)findViewById(R.id.update_detail_name) ;

        email=(TextView) findViewById(R.id.update_detail_email);
        bday=(EditText)findViewById(R.id.update_detail_bday);
        ph=(EditText)findViewById(R.id.update_detail_ph);

        bt_save.setOnClickListener(save);
        update_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();
                changedp();
            }
        });



        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    UserDetail u =  dataSnapshot.getValue(UserDetail.class);
                    name.setText(u.getuName());
                    status.setText(u.getStatus());
                    email.setText(u.getEmail());
                    ph.setText(u.getPhone());
                    bday.setText(u.getBday());

                    //profile Image
                    String base64Image = u.getPhoto_url();
                    if(!base64Image.isEmpty()) {
                        byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
                        Bitmap conv_bm = RequireItems.getRoundedBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                        bt_profile_image.setImageBitmap(conv_bm);
                        progressDialog.dismiss();
                    }
                    else{
                        bt_profile_image.setImageResource(R.drawable.addphoto);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        bt_ed_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateDetail.this);


                // Setting Dialog Title
                alertDialog.setTitle("Status:");

                final EditText input = new EditText(UpdateDetail.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                input.setText(status.getText().toString());
                input.selectAll();
                input.setPadding(5,25,5,5);
                alertDialog.setView(input);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                // Write your code here to execute after dialog
                                status.setText(input.getText().toString());
                                Toast.makeText(getApplicationContext(),"Done", Toast.LENGTH_SHORT).show();

                            }
                        });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancle",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                dialog.cancel();
                            }
                        });


                alertDialog.show();



            }
        });


        bt_ed_uname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateDetail.this);


                // Setting Dialog Title
                alertDialog.setTitle("Enter your name");

                final EditText input = new EditText(UpdateDetail.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                input.setText(name.getText().toString());
                input.selectAll();
                input.setPadding(5,25,5,5);
                alertDialog.setView(input);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                // Write your code here to execute after dialog
                                name.setText(input.getText().toString());
                                Toast.makeText(getApplicationContext(),"Done", Toast.LENGTH_SHORT).show();

                            }
                        });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancle",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                dialog.cancel();
                            }
                        });


                alertDialog.show();



        }
        });

        user= FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    }

    View.OnClickListener save = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


          String uname,st , pho , bd ;
            bd=bday.getText().toString();
            pho=ph.getText().toString();
            uname=name.getText().toString();
            st=status.getText().toString();

           final ProgressDialog progressDialog = ProgressDialog.show(UpdateDetail.this, "","Please Wait...", true);

            user.child("phone").setValue(pho);
            user.child("bday").setValue(bd);
            user.child("uName").setValue(uname);
            user.child("status").setValue(st);
            final String key = user.getKey();
            user=FirebaseDatabase.getInstance().getReference().child("Users").child(key);
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    progressDialog.dismiss();
                 Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Profile Successfully Updated", Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Error", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).setActionTextColor(Color.RED).show();
                }
            });

        }
    };









    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == 1||requestCode == 2) && resultCode == RESULT_OK) {
           file = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            file.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byte [] byte_arr = stream.toByteArray();
            String url = Base64.encodeToString(byte_arr,Base64.DEFAULT);
            DatabaseReference pic=FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("photo_url");
            pic.setValue(url);

//            Bitmap conv_bm = getRoundedBitmap(file);
//              bt_profile_image.setImageBitmap(conv_bm);
        }


    }






    public void changedp()
    {
        AlertDialog.Builder adb=new AlertDialog.Builder(this);
        adb.setTitle("Update Profile Picture");
        adb.setItems(choosepic, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                 if(item==0)
                {
                    Intent gi = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    gi.putExtra("crop", "true");
                    gi.putExtra("aspectX", 1);
                    gi.putExtra("aspectY", 1);
                    gi.putExtra("outputX", 640);
                    gi.putExtra("outputY",640);
                    gi.putExtra("scale",true);
                    gi.putExtra("return-data", true);
                    startActivityForResult(gi,2);
                }
                else if (item==1)
                {
                    dialog.dismiss();
                    bt_profile_image.setImageResource(R.drawable.addphoto);
                    String url="";
                    progressDialog.show();
                    DatabaseReference pic=FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("photo_url");

                    pic.setValue(url).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(UpdateDetail.this,"Update Succesfull !!",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UpdateDetail.this,"Something went wrong !!",Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });

        adb.show();



    }

}
