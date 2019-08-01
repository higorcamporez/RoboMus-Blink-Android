package com.robomus.instrument;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.text.BoringLayout;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.illposed.osc.*;

import com.instacart.library.truetime.TrueTimeRx;
import com.robomus.instrument.Actions.PlayNote;
import com.robomus.util.Note;
import com.robomus.util.Notes;

import org.billthefarmer.mididriver.MidiDriver;

import io.reactivex.schedulers.Schedulers;


public class Smartphone extends Instrument{


    private OSCPortOut sender;
    private OSCPortIn receiver;
    private TextView textLog;
    private Activity activity;
    private Note lastNote;
    private AudioTrack audioTrack;
    private volatile Buffer buffer;
    private Boolean ntpTime = false;
    private volatile MidiDriver midiDriver;
    private Boolean emulateDelay;

    public Smartphone(String myIp, Activity activity, TextView textLog){

        super( "Smartphone", "/smartphone", 1234, myIp);

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        if(model.equals("Moto G 2014")){
            this.setMyOscAddress("/smartphone2");
            this.setName("Smartphone2");
        }
        this.activity = activity;
        this.textLog = textLog;
        this.polyphony = 1;
        this.typeFamily = "";
        this.specificProtocol = "</playNote;note_n; duration_i>";
        this.emulateDelay = false;
        try {
            this.receiver = new OSCPortIn(this.receivePort);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        this.buffer = new Buffer(this);


        this.midiDriver = new MidiDriver();

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 4000,
                AudioTrack.MODE_STATIC);

        //this.playSoundSmartphone(440,500);
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

    public void start() {
        this.buffer.start();
        this.listeningThread();
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
        Log.d("info","Started p=" + this.receivePort + " end=" + this.myOscAddress);

        OSCListener listener = new OSCListener() {

            public void acceptMessage(java.util.Date time, OSCMessage oscMessage) {

                writeOnScreen(oscMessage);
                String header = getHeader(oscMessage);

                switch (header){
                    case "handshake":
                        receiveHandshake(oscMessage);
                        break;
                    case "playNote":

                        Long delay = calculateDelay(
                                lastNote, new Note(oscMessage.getArguments().get(1).toString())
                                );

                        buffer.addMessage(
                                new RoboMusMessage(
                                        time,
                                        oscMessage,
                                        new PlayNote(midiDriver, oscMessage, delay, getSmartphone())
                                )
                        );
                        break;
                    default:


                }

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



        this.audioTrack.write(generatedSnd, 0, generatedSnd.length);
        this.audioTrack.play();



    }

    void playNote(OSCMessage oscMessage){

        Log.i("Smartphone:playNote()", "inicio");

        long start = System.currentTimeMillis();
        Long idMessage = Long.parseLong(oscMessage.getArguments().get(0).toString());
        String symbolNote = oscMessage.getArguments().get(1).toString();
        Short duration = Short.parseShort(oscMessage.getArguments().get(2).toString());

        Note note = new Note(symbolNote);
        //calculando atraso mecânico
        Long delay = calculateDelay(this.lastNote,note);

        //simulando atraso mecânico
        /*
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        playSoundSmartphone(note.getFrequency(), duration);

        long t = System.currentTimeMillis() - start;
        Log.i("Buffer", "tempo playnote "+t);

        this.lastNote = note;
        //Log.i("smartphone:playnote", delay.toString());
        /*
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } */

        //envia o atraso mecânico
        OSCMessage oscMessage1 = new OSCMessage(this.serverOscAddress+"/delay"+this.myOscAddress);
        oscMessage1.addArgument(idMessage);
        oscMessage1.addArgument(delay);
        try {
            sender.send(oscMessage1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeOnScreen(oscMessage1);
    }

    public Long calculateDelay(Note Note1, Note Note2){
        Long delay;
        if(this.lastNote == null){
            delay = (long)100;
        }else{
            delay = Long.valueOf(
                    (Math.abs(Notes.getDistance(Note1, Note2, true) * 10))
            ) + 10;
        }

        return delay;
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

        //acerta o relógio
        requestTimeToNTPServer();

    }
    public void requestTimeToNTPServer(){
        TrueTimeRx.build()
                .withLoggingEnabled(true)
                .withSharedPreferences(activity)
                .initializeRx(this.severIpAddress)
                .subscribeOn(Schedulers.io())
                .subscribe(date -> {
                    //Log.v("TrueTime", "TrueTime was initialized and we have a time: " + System.currentTimeMillis());
                    Log.v("TrueTime", "TrueTime was initialized and we have a time: " + date);
                    ntpTime = true;
                }, throwable -> {
                    Log.v("TrueTime", "TrueTime was not initialized");
                    throwable.printStackTrace();
                    ntpTime = false;
                });
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

    public void writeOnScreen(OSCMessage oscMessage){

        String log = oscMessage.getAddress()+" ";
        List l = oscMessage.getArguments();
        log += "[";
        for (Object l1 : l) {
            log +=l1+",";
        }
        log += ']';

        writeOnScreen(log);

    }

    public void writeOnScreen(String text){

        final String finalLog = text;
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                textLog.append("\n"+ finalLog);

            }
        });
    }

    public Smartphone getSmartphone(){ return this;}

    public OSCPortOut getSender() {
        return sender;
    }

    public Note getLastNote() {
        return lastNote;
    }

    public void setLastNote(Note lastNote) {
        this.lastNote = lastNote;
    }

    public Boolean getEmulateDelay() {
        return emulateDelay;
    }

    public void setEmulateDelay(Boolean emulateDelay) {
        this.emulateDelay = emulateDelay;
    }


}
