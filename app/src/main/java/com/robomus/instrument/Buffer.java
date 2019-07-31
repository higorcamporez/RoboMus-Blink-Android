package com.robomus.instrument;


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

    public Buffer(Smartphone smartphone) {
        this.messages = new ArrayList<>();
    }

    public void addMessage(RoboMusMessage roboMusMessage){
        synchronized (this) {
            this.messages.add(roboMusMessage);
        }

        // ordenar
        //Collections.sort(this.messages);

    }

    @Override
    public void run() {
        while(true){
            //Log.i(getClass().getName(), "antes " + this.messages.size());
            if(this.messages.size()>0){
                //Log.i(getClass().getName(), "depois " + this.messages.size());
                synchronized (this) {

                    if (this.messages.get(0).getTimestamp().getTime() <
                            TrueTimeRx.now().getTime()) {

                        this.messages.get(0).play();

                        //retirar do buffer;
                        this.messages.remove(0);

                    }
                }

            }
        }
    }
}
