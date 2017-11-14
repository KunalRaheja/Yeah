package com.example.kunalraheja.yeah;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo =(ImageView)findViewById(R.id.imageView);
        logo.setImageResource(R.drawable.logo);
        new Mytask().execute();
    }

    class Mytask extends AsyncTask<Void , Void , Void >{

        @Override
        protected Void doInBackground(Void... params) {
            for(int i = 0 ; i<3 ; i++){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent in = new Intent(SplashScreen.this , ProfileSetup.class);
            startActivity(in);
            finish();

        }
    }
}
