package com.robomus.instrument;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.illposed.osc.OSCMessage;
import com.instacart.library.truetime.TrueTimeRx;
import com.robomus.util.Note;
import com.robomus.util.OSCMessageUtil;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Buffer extends Thread {

    private volatile List<RoboMusMessage> messages;
    private Smartphone smartphone;
    private AudioTrack audioTrack;

    public Buffer(Smartphone smartphone) {
        this.messages = new ArrayList<>();
        this.smartphone = smartphone;

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 4000,
                AudioTrack.MODE_STATIC);
    }

    public void addMessage(RoboMusMessage roboMusMessage){
        this.messages.add(roboMusMessage);
        // ordenar
        Collections.sort(this.messages);

    }

    @Override
    public void run() {
        while(true){
            //Log.i(getClass().getName(), "antes " + this.messages.size());
            if(!this.messages.isEmpty()){
                //Log.i(getClass().getName(), "depois " + this.messages.size());

                if(this.messages.get(0).getTimestamp().getTime() <
                        TrueTimeRx.now().getTime()){

                    this.messages.get(0).play();

                    //retirar do buffer;
                    this.messages.remove(0);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
