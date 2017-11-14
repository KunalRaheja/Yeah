package com.example.kunalraheja.yeah;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.provider.FirebaseInitProvider;

public class ExistingUser extends AppCompatActivity implements View.OnClickListener {

    EditText email , pass;
    Button login;
    TextView txt ;

     FirebaseAuth firebaseAuth;
    ProgressDialog pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_user);


        email=(EditText)findViewById(R.id.eu_email);
        pass=(EditText)findViewById(R.id.eu_pass);
        login=(Button)findViewById(R.id.eu_login);
        txt=(TextView)findViewById(R.id.eu_reg);
        pd=new ProgressDialog(this);
        pd.setTitle("Logging in");


        login.setOnClickListener(this);

        txt.setText("Register ! !");
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ExistingUser.this , ProfileSetup.class);
                startActivity(in);
                finish();
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        login();
    }

    private void login(){

        pd.show();

        String e = email.getText().toString();
        String p = pass.getText().toString();

        if(e.isEmpty()||p.isEmpty()){
            Toast.makeText(ExistingUser.this,"Fill Detail",Toast.LENGTH_SHORT).show();
            return;

        }

        firebaseAuth.signInWithEmailAndPassword(e , p).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(ExistingUser.this,"Fail",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(ExistingUser.this,"Success",Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(ExistingUser.this , NavigationDrawer.class);
                    startActivity(in);
                    pd.dismiss();
                    finish();

                }
            }
        });

    }


}
