package com.robomus.instrument;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Environment;
import android.util.Log;


import com.instacart.library.truetime.TrueTimeRx;


import org.billthefarmer.mididriver.MidiDriver;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

public class Buffer extends Thread {

    private volatile List<RoboMusMessage> messages;
    private volatile byte generatedSnd[];
    private MidiDriver midiDriver;
    private byte[] event;
    private byte[] noteOff;
    private volatile Smartphone smartphone;
    private volatile FileWriter writer;

    public Buffer() {

        final int duration = 500;
        final int frequency = 440;
        final int sampleRate = 8000;
        final int numSamples = (int) ( (float)(duration/1000) * (float)sampleRate );
        final double sample[] = new double[numSamples];
        this.generatedSnd = new byte[2 * numSamples];


        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/frequency));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                8000, AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, 4000,
                AudioTrack.MODE_STATIC);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();

    }

    public Buffer(Smartphone smartphone) {
        //this();
        this.messages = new ArrayList<>();
        this.smartphone = smartphone;

        this.event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = (byte) 0x3C;
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        this.noteOff = new byte[3];
        noteOff[0] = (byte) (0x80 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        noteOff[1] = (byte) 0x3C;
        noteOff[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        this.midiDriver = new MidiDriver();
        midiDriver.start();
        //this.midiDriver.write(event);



    }


    public void addMessage(RoboMusMessage roboMusMessage){
        synchronized (this) {
            this.messages.add(roboMusMessage);
        }

        // ordenar
        //Collections.sort(this.messages);

    }
    public void playSoundSmartPhone(double frequency, double duration){
        final int sampleRate = 8000;
        final int numSamples = (int) ( (duration/1000) * sampleRate );
        final double sample[] = new double[numSamples];
        byte generatedSnd[] = new byte[2 * numSamples];

        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/frequency));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }

        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples,
                AudioTrack.MODE_STATIC);

        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();

    }

    @Override
    public void run() {
        while(true){
            //Log.i(getClass().getName(), "antes " + this.messages.size());
            synchronized (this) {
                if(this.messages.size()>0){
                    //Log.i(getClass().getName(), "depois " + this.messages.size());

                    if (this.messages.get(0).getTimestamp().getTime() -
                            TrueTimeRx.now().getTime() <= 0 &&
                            this.messages.get(0).getTimestamp().getTime() -
                                    TrueTimeRx.now().getTime() >= -5
                    ) {

                        //Long start = TrueTimeRx.now().getTime();
                        Long start = System.nanoTime();
                        //this.midiDriver.write(this.event);
                        //this.midiDriver.write(this.noteOff);

                        this.messages.get(0).play();

                        //Long end = TrueTimeRx.now().getTime();
                        Long end = (System.nanoTime()/1000);
                        start = start/1000;
                        //retirar do buffer;
                        RoboMusMessage roboMusMessage = this.messages.remove(0);
                        Long id = (Long)roboMusMessage.getOscMessage().getArguments().get(0);
                        String line = String.format("%d,%d,%d\n", id, start, end);
                        smartphone.writeOnFile(line);

                    }else if(TrueTimeRx.now().getTime() > this.messages.get(0).getTimestamp().getTime()
                            ){
                        RoboMusMessage roboMusMessage = this.messages.remove(0);
                        //Log.i(this.getName(), "Message Removed: ");
                        smartphone.writeOnScreen("Message Removed: ");
                        smartphone.writeOnScreen(roboMusMessage.getOscMessage());
                    }
                }

            }
        }
    }
}
