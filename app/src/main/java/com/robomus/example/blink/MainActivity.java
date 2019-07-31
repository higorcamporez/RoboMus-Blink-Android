package com.robomus.example.blink;


import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.example.blink.R;
import com.instacart.library.truetime.TrueTime;
import com.instacart.library.truetime.TrueTimeRx;


import com.robomus.instrument.Smartphone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.billthefarmer.mididriver.MidiDriver;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    MidiDriver midiDriver = new MidiDriver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_handshake = findViewById(R.id.btn_handshake);
        Button btn_settings = findViewById(R.id.settings);

        final TextView textLog = findViewById(R.id.textLog);
        //
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //
        //pegando ip
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String myIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        final Smartphone smartphone = new Smartphone(myIp, this,textLog);

        btn_handshake.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0) {
                smartphone.sendHandshake();
                textLog.setText("Handshake sent. waiting...");

            }
        });

        btn_settings.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

                startActivity(intent);
            }
        }

        );
        /*
        TrueTimeRx.build()
                //.withConnectionTimeout(31428)
                //.withRetryCount(100)
                //.withLoggingEnabled(true)
                .withSharedPreferences(this)
                .initializeRx("192.168.0.101")
                .subscribeOn(Schedulers.io())
                .subscribe(date -> {
                    Log.v("tt", "TrueTime was initialized and we have a time: " + System.currentTimeMillis());
                    Log.v("tt", "TrueTime was initialized and we have a time: " + date);
                    textLog.append("\ntime serve: "+ date.toString());
                }, throwable -> {
                    Log.v("tt", "TrueTime deu ruim");
                    throwable.printStackTrace();
                });
                */
        midiDriver.start();

        // Get the configuration.
        int[] config = midiDriver.config();

        // Print out the details.
        Log.d(this.getClass().getName(), "maxVoices: " + config[0]);
        Log.d(this.getClass().getName(), "numChannels: " + config[1]);
        Log.d(this.getClass().getName(), "sampleRate: " + config[2]);
        Log.d(this.getClass().getName(), "mixBufferSize: " + config[3]);

        byte[] event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = (byte) 0x3C;  // 0x3C = middle C
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        // Internally this just calls write() and can be considered obsoleted:
        //midiDriver.queueEvent(event);

        // Send the MIDI event to the synthesizer.
        midiDriver.queueEvent(event);
        event[1] = (byte) 0x3e;  // 0x3C = middle C
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        midiDriver.write(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
