package com.robomus.instrument.Actions;

import com.illposed.osc.OSCMessage;
import com.robomus.instrument.Smartphone;

import java.io.FileOutputStream;
import java.io.IOException;

public class PlayUSB implements Action  {

    private byte[] bytes;
    private Smartphone smartphone;
    private volatile FileOutputStream mOutputStream = null;

    public PlayUSB(FileOutputStream mOutputStream, OSCMessage oscMessage) {
        byte b = ((Long)oscMessage.getArguments().get(0)).byteValue();
        this.bytes = new byte[]{b};
        this.mOutputStream = mOutputStream;
    }

    public PlayUSB(Smartphone smartphone, OSCMessage oscMessage) {
        byte b = ((Long)oscMessage.getArguments().get(0)).byteValue();
        this.bytes = new byte[]{b};
        this.smartphone = smartphone;
    }

    @Override
    public void play() {
        /*

        if (mOutputStream != null) {
            try {
                mOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } */
        this.smartphone.sendUsbMessage(this.bytes);
    }
}
