package com.example.kunalraheja.yeah;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactUs extends AppCompatActivity {

    Button submit;
    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        submit=(Button)findViewById(R.id.contact_us_submit);
        text=(EditText)findViewById(R.id.contact_us_text);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!text.getText().toString().isEmpty()){
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("feedback").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference.push().setValue(text.toString());
                    Toast.makeText(getApplicationContext(),"Feedback Submit !!",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(ContactUs.this,"Empty!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
