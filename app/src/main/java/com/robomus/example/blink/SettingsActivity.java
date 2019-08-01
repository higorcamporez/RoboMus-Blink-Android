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
    private EditText editTextOscAddress;
    private Switch delaySwitch;
    private EditText editTextConstantDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.editTextOscAddress = (EditText) findViewById(R.id.oscAddress);
        this.editTextConstantDelay = (EditText) findViewById(R.id.constantDelay);
        this.delaySwitch = (Switch) findViewById(R.id.delaySwitch);

        Intent it = getIntent();
        this.editTextOscAddress.setText(it.getStringExtra("oscAddress"));
        this.delaySwitch.setChecked(it.getBooleanExtra("delaySwitch", true));

        this.editTextOscAddress.setText(it.getStringExtra("oscAddress"));
        this.editTextConstantDelay.setText(((Long)it.getLongExtra("constantDelay", 0)).toString());

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
        it.putExtra("oscAddress", editTextOscAddress.getText().toString());
        it.putExtra("constantDelay", Long.parseLong(editTextOscAddress.getText().toString()));

        setResult(1, it);
        super.onBackPressed();
    }
}
