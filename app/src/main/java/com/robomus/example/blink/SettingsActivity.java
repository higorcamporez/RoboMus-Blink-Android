package com.robomus.example.blink;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.example.blink.R;

public class SettingsActivity extends AppCompatActivity {
    private EditText EditTextOscAddress;
    private Switch delaySwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.EditTextOscAddress = (EditText) findViewById(R.id.oscAddress);
        this.delaySwitch = (Switch) findViewById(R.id.delaySwitch);

        Intent it = getIntent();
        EditTextOscAddress.setText(it.getStringExtra("oscAddress"));
        delaySwitch.setChecked(it.getBooleanExtra("delaySwitch", true));


        EditTextOscAddress.setText(it.getStringExtra("oscAddress"));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed(); // Implemented by activity
            }
        });

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public void onBackPressed(){
        Intent it = new Intent();
        it.putExtra("delaySwitch", delaySwitch.isChecked());
        it.putExtra("oscAddress", EditTextOscAddress.getText().toString());
        setResult(1, it);
        super.onBackPressed();
    }
}
