package com.example.kunalraheja.yeah;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ProfileSetup extends AppCompatActivity implements View.OnClickListener {

    Button button_sub;

    TextView login;
    ImageView bd;
    EditText et_name, et_email, et_pass, et_phone , bday;
    FirebaseAuth firebaseAuth;

   static FirebaseDatabase firebaseDatabase;
    DatabaseReference users;
    String  uid;

    UserDetail user_detail;
    Calendar calendar ;
    int year , month , day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        button_sub =(Button)findViewById(R.id.re_submit);
        button_sub.setOnClickListener(this);



        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        login = (TextView)findViewById(R.id.re_login);
        et_phone = (EditText) findViewById(R.id.re_phone);
        et_name = (EditText)findViewById(R.id.re_name);
        et_pass = (EditText)findViewById(R.id.re_pass);
        et_email = (EditText)findViewById(R.id.re_email);
        bday= (EditText)findViewById(R.id.re_bday);

        bd=(ImageView)findViewById(R.id.re_im_bd) ;


        bd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
      //  firebaseAuth.addAuthStateListener(objRE);
        users = firebaseDatabase.getReference("Users");

        login.setText("NO,I'M EXISTING USER");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ProfileSetup.this , ExistingUser.class);
                startActivity(in);
                finish();
            }
        });

        user_detail = new UserDetail();

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){

                    uid=user.getUid();
                    Toast.makeText(ProfileSetup.this, " Setup Success auth", Toast.LENGTH_SHORT).show();

                    Intent in = new Intent(ProfileSetup.this , NavigationDrawer.class);
                    startActivity(in);
                    finish();
                }
                else {
                    Toast.makeText(ProfileSetup.this, "Logout", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void setDate() {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };
    private void showDate(int year, int month, int day) {
        bday.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

/*
    FirebaseAuth.AuthStateListener objRE = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if(user==null){

            }
            else{
                uid=user.getUid();
            }
        }
    };

*/
    public boolean checkFields() {
        String e = et_email.getText().toString();
        String p = et_pass.getText().toString();
        String ph = et_phone.getText().toString();
        String n = et_name.getText().toString();
        String bd = bday.getText().toString();
        if (e.isEmpty() || p.isEmpty() || ph.isEmpty() || n.isEmpty() || bd.isEmpty()) {
            return false;
        }
        return true;
    }
    private void registerUser() {

        if (!checkFields()) {
            Toast.makeText(this, "Fill Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String e = et_email.getText().toString();
        if(!e.contains(".com")) {
            Toast.makeText(this, "Email not Valid !!", Toast.LENGTH_SHORT).show();
            return;
        }
        user_detail.setEmail(e);

        String p = et_pass.getText().toString();

        String ph = et_phone.getText().toString();
        if(ph.length()!=10){
            Toast.makeText(this, "Phone number not Valid !!", Toast.LENGTH_SHORT).show();
            return;
        }
        user_detail.setPhone(ph);

        String n = et_name.getText().toString();
        user_detail.setuName(n);
       // user_detail.setStatus(user_detail.getStatus());
        String bd=bday.getText().toString();
        user_detail.setBday(bd);


        firebaseAuth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileSetup.this, "Success", Toast.LENGTH_SHORT).show();
                    if(!uid.isEmpty()){
                        setupProfile(uid);
                    }


                } else {
                    Toast.makeText(ProfileSetup.this, "Fail", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void setupProfile(String uId){
        if (!checkFields()) {
            Toast.makeText(this, "Fill Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String e = et_email.getText().toString();
        user_detail.setEmail(e);
        String ph = et_phone.getText().toString();
        user_detail.setPhone(ph);
        String n = et_name.getText().toString();
        user_detail.setuName(n);



        users.child(uId).setValue(user_detail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileSetup.this, " Setup Success", Toast.LENGTH_SHORT).show();
                  /*  Intent intent = new Intent(ProfileSetup.this, LocService.class);
                    startService(intent);*/
                    Intent in = new Intent(ProfileSetup.this , NavigationDrawer.class);
                    startActivity(in);
                    finish();

                } else {
                    Toast.makeText(ProfileSetup.this, "Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Make sure your email is correct , for future updates and forgot password check !!");
        alertDialog.setTitle("Confirm");
        alertDialog.setPositiveButton("Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        // Write your code here to execute after dialog
                        registerUser();
                        startTask();

                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancle",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                        return;
                    }
                });


        alertDialog.show();


    }




    public void startTask(){
        GcmNetworkManager gcmNetworkManager=GcmNetworkManager.getInstance(this);

        com.google.android.gms.gcm.Task task1 = new PeriodicTask.Builder()
                .setService(CoustomService.class)
                .setPeriod(300)
                .setFlex(10)                                //flexibility time // the task run within every 10 - 30 sec..
                .setTag("Task2")
                .setPersisted(true)       // for service start ia boot
                .build();

        gcmNetworkManager.schedule(task1);



    }



}
