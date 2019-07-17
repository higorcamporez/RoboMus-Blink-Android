package com.robomus.instrument;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.illposed.osc.*;

import com.robomus.util.Note;

public class Smartphone extends Instrument{

    private OSCPortOut sender;
    private OSCPortIn receiver;
    private TextView textLog;
    private Activity activity;

    public Smartphone(String myIp, Activity activity, TextView textLog){

        super( "Smartphone", "/Smartphone", 1234, myIp);

        this.activity = activity;
        this.textLog = textLog;
        this.polyphony = 1;
        this.typeFamily = "";
        this.specificProtocol = "</playNote;note_n; duration_i>";

        try {
            this.receiver = new OSCPortIn(this.receivePort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Note note = new Note("A4");

        listeningThread();

    }

    public Smartphone(String OscAddress, int receivePort, String myIp){

        super( "Smartphone", OscAddress, receivePort, myIp);

        this.polyphony = 1;
        this.typeFamily = "";
        this.specificProtocol = "</playNote;note_s; duration_i>";

        try {
            this.receiver = new OSCPortIn(this.receivePort);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        listeningThread();

    }
    public String getHeader(OSCMessage oscMessage){
        String header = (String) oscMessage.getAddress();

        if(header.startsWith("/"))
            header = header.substring(1);

        String[] split = header.split("/", -1);

        if (split.length >= 2) {
            header = split[1];
        }else{
            header = null;
        }
        return header;
    }

    public void listeningThread(){
        Log.d("info","Inicio p=" + this.receivePort + " end=" + this.myOscAddress);

        OSCListener listener = new OSCListener() {

            public void acceptMessage(java.util.Date time, OSCMessage oscMessage) {

                String header = getHeader(oscMessage);

                switch (header){
                    case "handshake":
                        receiveHandshake(oscMessage);
                        break;
                    case "playNote":
                        playNote(oscMessage);
                        break;
                }

                String log = oscMessage.getAddress()+" ";
                    List l = oscMessage.getArguments();
                    log += "[";
                    for (Object l1 : l) {
                        log +=l1+",";
                    }
                    log += ']';

                final String finalLog = log;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        textLog.append("\n"+ finalLog);

                    }
                });

            }
        };
        this.receiver.addListener(this.myOscAddress+"/*", listener);
        this.receiver.startListening();
    }

    public void playSoundSmartphone(double frequency, double duration){
        final int sampleRate = 8000;
        final int numSamples = (int) ( (float)(duration/1000) * (float)sampleRate );
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

    void playNote(OSCMessage oscMessage){

        Log.i("Smartphone:playNote()", "inicio");

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Long idMessage = Long.parseLong(oscMessage.getArguments().get(0).toString());
        String symbolNote = oscMessage.getArguments().get(1).toString();
        Short duration = Short.parseShort(oscMessage.getArguments().get(2).toString());

        Note note = new Note(symbolNote);

        playSoundSmartphone(note.getFrequency(), duration);

        //envia o atraso mecanico
        OSCMessage oscMessage1 = new OSCMessage(this.serverOscAddress+"/delay"+this.myOscAddress);
        oscMessage1.addArgument(idMessage);
        oscMessage1.addArgument(new Long(500));
        try {
            sender.send(oscMessage1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void receiveHandshake(OSCMessage oscMessage){

        this.serverName = oscMessage.getArguments().get(0).toString();
        this.serverOscAddress = oscMessage.getArguments().get(1).toString();
        this.severIpAddress = oscMessage.getArguments().get(2).toString();
        this.sendPort = Integer.parseInt(oscMessage.getArguments().get(3).toString());
        /*
        //log screen
        final String s = "handshake: Format OSC = [oscAdd, ip, port]\n Adress:"+ oscMessage.getAddress()+
                " ["+ this.serverOscAddress+", "+ this.severIpAddress+", "+this.sendPort+"]\n";
        final TextView txtLog = textLog;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                txtLog.append(s);

            }
        });
        //end log screen
        */

        //Initializing the OSC sender
        this.sender = null;

        try {
            this.sender = new OSCPortOut(InetAddress.getByName(this.severIpAddress), this.sendPort);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
    public void sendHandshake(){

        List args = new ArrayList<>();

        //instrument attributes
        args.add(this.name);
        args.add(this.myOscAddress);
        args.add(this.myIp);
        args.add(this.receivePort);
        args.add(this.polyphony);
        args.add(this.typeFamily);
        args.add(this.specificProtocol);



        OSCMessage msg = new OSCMessage("/handshake/instrument", args);
        OSCPortOut sender = null;

        //send de msg with the broadcast ip
        String s = this.myIp;
        String[] ip = s.split("\\.");
        String broadcastIp = ip[0]+"."+ip[1]+"."+ip[2]+".255";
        //temporario
        //String broadcastIp = "172.20.25.91";
        try {
            sender = new OSCPortOut(InetAddress.getByName(broadcastIp), this.receivePort);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        try {
            if(sender != null)
                sender.send(msg);
            else
                Log.d("ninfo","nulllll");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
