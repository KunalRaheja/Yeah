package com.example.kunalraheja.yeah;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class GeofireActvity extends AppCompatActivity {

    String SHARED_BOL_NOTIFICATIONS="NOTIFICATIONS" , SHARED_FLOAT_DISTANCE="RADIUS";

    TextView tv_dis, text ,infor;
    SeekBar seekBar;
    Button save;
    CheckBox checkBox;
    float distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofire_actvity);

        SharedPreferences sp1 = getPreferences(MODE_PRIVATE);
        boolean b_noti = sp1.getBoolean(SHARED_BOL_NOTIFICATIONS,false);
        float radius=sp1.getFloat(SHARED_FLOAT_DISTANCE, (float) 0.25);

        int i_radius= (int) (radius/0.25);

        tv_dis =(TextView)findViewById(R.id.gf_dis);
        checkBox=(CheckBox)findViewById(R.id.gf_checkBox);
        text=(TextView)findViewById(R.id.gf_textView);
        infor=(TextView)findViewById(R.id.gf_infor);
        seekBar=(SeekBar)findViewById(R.id.gf_seekBar);
        seekBar.setProgress(i_radius);
        checkBox.setChecked(b_noti);
        tv_dis.setText(String.valueOf(radius)+" Km");

        if(!checkBox.isChecked()){
            seekBar.setEnabled(false);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    seekBar.setEnabled(false);
                }
                else {
                    seekBar.setEnabled(true);
                }

            }
        });
        seekBar.setMax(8);
        seekBar.setMinimumWidth(1);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                distance= (float) (progress*(0.25));
                tv_dis.setText(String.valueOf(distance)+" Km");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        save=(Button)findViewById(R.id.gf_bt_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("DATA",MODE_PRIVATE);
                SharedPreferences.Editor ed1 = sp.edit();

                ed1.putBoolean("CHECK",checkBox.isChecked());
                if(checkBox.isChecked())
                ed1.putFloat(SHARED_FLOAT_DISTANCE,distance);

                ed1.commit();

                RequireItems.showRectangleToast(getApplicationContext(),"Setting Saved !!");

            }
        });
    }



}
