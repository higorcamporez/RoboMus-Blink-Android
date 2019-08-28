package com.robomus.example.blink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.blink.R;
import com.instacart.library.truetime.TrueTimeRx;

import io.reactivex.schedulers.Schedulers;

public class SettingsActivity extends AppCompatActivity {
    private EditText editTextOscAddress;
    private Switch delaySwitch;
    private EditText editTextConstantDelay;
    private String serverIp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.editTextOscAddress = (EditText) findViewById(R.id.oscAddress);
        this.editTextConstantDelay = (EditText) findViewById(R.id.constantDelay);
        this.delaySwitch = (Switch) findViewById(R.id.delaySwitch);
        Button btnNtpSynch = (Button) findViewById(R.id.ntpSynch);

        Intent it = getIntent();
        this.editTextOscAddress.setText(it.getStringExtra("oscAddress"));
        this.delaySwitch.setChecked(it.getBooleanExtra("delaySwitch", true));

        this.editTextOscAddress.setText(it.getStringExtra("oscAddress"));
        this.editTextConstantDelay.setText(((Long)it.getLongExtra("constantDelay", 0)).toString());
        this.serverIp = it.getStringExtra("serverIp");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed(); // Implemented by activity
            }
        });
        Context context = getApplicationContext();
        btnNtpSynch.setOnClickListener(new View.OnClickListener(){
                   public void onClick(View arg0) {
                       if(serverIp != null){

                           TrueTimeRx.build()
                                   //.withConnectionTimeout(31428)
                                   //.withRetryCount(100)
                                   //.withLoggingEnabled(true)
                                   //.withSharedPreferences(this)
                                   .initializeRx(serverIp)
                                   .subscribeOn(Schedulers.io())
                                   .subscribe(date -> {
                                       //Log.v("tt", "TrueTime was initialized and we have a time: " + System.currentTimeMillis());
                                       Log.v("tt", "TrueTime was initialized and we have a time: " + date);

                                   }, throwable -> {
                                       Log.v("tt", "TrueTime deu ruim");
                                       /*
                                       Toast.makeText(context,
                                               "TrueTime was not initialized",
                                               Toast.LENGTH_SHORT
                                       ).show();
                                       throwable.printStackTrace();
                                       */
                                   });
                       }else{
                           Toast.makeText(getApplicationContext(),
                                   "No server IP",
                                   Toast.LENGTH_SHORT
                           ).show();
                       }

                   }
               }
            );

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public void onBackPressed(){
        Intent it = new Intent();
        it.putExtra("delaySwitch", delaySwitch.isChecked());
        it.putExtra("oscAddress", editTextOscAddress.getText().toString());
        it.putExtra("constantDelay", Long.parseLong(editTextConstantDelay.getText().toString()));

        setResult(1, it);
        super.onBackPressed();
    }
}
