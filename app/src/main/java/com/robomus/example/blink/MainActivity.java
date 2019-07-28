package com.robomus.example.blink;


import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.example.blink.R;
import com.instacart.library.truetime.TrueTime;
import com.instacart.library.truetime.TrueTimeRx;

import com.medavox.library.mutime.MissingTimeDataException;
import com.medavox.library.mutime.MuTime;
import com.medavox.library.mutime.Ntp;
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

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_handshake = findViewById(R.id.btn_handshake);
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
        Log.i("TrueTimeRx", TrueTimeRx.now().toString());


        /*


        try {
            Ntp.performNtpAlgorithm(InetAddress.getByName("192.168.0.101") );
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //get the real time in unix epoch format (milliseconds since midnight on 1 january 1970)
        try {
            Log.i("MuTime", String.valueOf(MuTime.hasTheTime()));
            long theActualTime = MuTime.now();//throws MissingTimeDataException if we don't know the time
            textLog.append(String.valueOf(System.currentTimeMillis() - theActualTime));
            textLog.append(new Date(theActualTime).toString());
        }
        catch (MissingTimeDataException e) {
            Log.e("MuTime", "failed to get the actual time:+e.getMessage()");
        }
         */
        //while (!TrueTimeRx.isInitialized());
        // you can now use this instead of your traditional new Date();
        //Date myDate = TrueTimeRx.now();
        //textLog.append(myDate.toString());
        //get the real time in unix epoch format (milliseconds since midnight on 1 january 1970)

        textLog.append("\ntime system: "+new Date(System.currentTimeMillis()).toString());

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
