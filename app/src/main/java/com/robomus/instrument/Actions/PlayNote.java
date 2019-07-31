package com.robomus.instrument.Actions;

import android.util.Log;

import com.illposed.osc.OSCMessage;
import com.robomus.instrument.Smartphone;
import com.robomus.util.Note;

import org.billthefarmer.mididriver.MidiDriver;

import java.io.IOException;

public class PlayNote implements Action {
    private MidiDriver midiDriver;
    private byte[] event;
    private Long delay;
    private Smartphone smartphone;
    private Long idMessage;
    private Note note;

    public PlayNote(MidiDriver midiDriver, OSCMessage oscMessage, Long delay, Smartphone smartphone) {

        this.idMessage = Long.parseLong(oscMessage.getArguments().get(0).toString());
        String symbolNote = oscMessage.getArguments().get(1).toString();
        Short duration = Short.parseShort(oscMessage.getArguments().get(2).toString());

        this.note = new Note(symbolNote);

        byte[] event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = (byte) note.getMidiValue();
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        this.midiDriver = midiDriver;
        this.event = event;
        this.delay = delay;
        this.smartphone = smartphone;
    }

    @Override
    public void play() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.midiDriver.write(this.event);

        smartphone.setLastNote(this.note);
        //envia o atraso mecânico
        OSCMessage oscMessage1 = new OSCMessage(
                smartphone.getServerOscAddress()+"/delay"+smartphone.getMyOscAddress()
        );

        oscMessage1.addArgument(idMessage);
        oscMessage1.addArgument(delay);

        try {
            smartphone.getSender().send(oscMessage1);
            Log.i(this.getClass().getName(),"enviou delay");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
