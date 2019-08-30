package com.robomus.example.blink;


import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import com.example.blink.R;
import com.instacart.library.truetime.TrueTime;
import com.instacart.library.truetime.TrueTimeRx;


import com.robomus.instrument.Smartphone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.billthefarmer.mididriver.MidiDriver;
import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Smartphone smartphone;
    private volatile ParcelFileDescriptor mFileDescriptor;
    private UsbManager mUsbManager;
    private UsbAccessory mAccessory;

    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button btn_handshake = findViewById(R.id.btn_handshake);
        Button btn_settings = findViewById(R.id.settings);

        final TextView textLog = findViewById(R.id.textLog);
        //textLog.setMovementMethod(new ScrollingMovementMethod());

        //
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //
        //pegando ip
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String myIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());


        //android accessory communication
        Intent intent = getIntent();
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mAccessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

        if (mAccessory == null) {
            textLog.append("Not started by the accessory directly" +
                    System.getProperty("line.separator"));
            mFileDescriptor = null;
        }else{
            mFileDescriptor = mUsbManager.openAccessory(mAccessory);
        }


        //end accessory

        this.smartphone = new Smartphone(myIp, this,textLog, mFileDescriptor);

        btn_handshake.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0) {
                smartphone.stop();
                smartphone.start();
                smartphone.sendHandshake();
                textLog.setText("Handshake sent. waiting...");

            }
        });

        btn_settings.setOnClickListener(new View.OnClickListener(){
                public void onClick(View arg0) {
                    smartphone.sendUsbMessage(new byte[]{1});
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    intent.putExtra("oscAddress", smartphone.getMyOscAddress());
                    intent.putExtra("delaySwitch", smartphone.getEmulateDelay());
                    intent.putExtra("constantDelay", smartphone.getConstantDelay());
                    intent.putExtra("serverIp", smartphone.getServerIpAddress());
                    startActivityForResult(intent,1);
                }
            }
        );


        /*
        MidiDriver midiDriver = new MidiDriver();
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

        // Send the MIDI event to the synthesizer.

        midiDriver.queueEvent(event);
        event[1] = (byte) 0x3e;
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] e = new byte[3];
        e[0] = (byte)(0x80 | 0x00);
        e[1] = (byte) 0x3C;  // 0x3C = middle C
        e[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        midiDriver.write(e);


        for (int i = 0; i < 20; i++) {


            midiDriver.write(event);
            //Log.i("tocou", String.valueOf(System.currentTimeMillis()));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

        }
        */


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:

                String oscAddress = data.getExtras().getString("oscAddress");
                Boolean emulateDelay = data.getExtras().getBoolean("delaySwitch");
                Long constantDelay = data.getExtras().getLong("constantDelay");
                this.smartphone.setMyOscAddress(oscAddress);
                this.smartphone.setEmulateDelay(emulateDelay);
                this.smartphone.setConstantDelay(constantDelay);

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
