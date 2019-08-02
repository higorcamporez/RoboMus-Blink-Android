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
    private byte[] noteOff;
    private Long delay;
    private Smartphone smartphone;
    private Long idMessage;
    private Note note;
    private Short duration;
    private Thread threadNoteOff;

    public PlayNote(MidiDriver midiDriver, OSCMessage oscMessage, Long delay, Smartphone smartphone) {

        this.idMessage = Long.parseLong(oscMessage.getArguments().get(0).toString());
        String symbolNote = oscMessage.getArguments().get(1).toString();
        this.duration = Short.parseShort(oscMessage.getArguments().get(2).toString());

        this.note = new Note(symbolNote);

        this.event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = (byte) note.getMidiValue();
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        this.noteOff = new byte[3];
        noteOff[0] = (byte) (0x80 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        noteOff[1] = (byte) note.getMidiValue();
        noteOff[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        this.midiDriver = midiDriver;
        this.delay = delay;
        this.smartphone = smartphone;

        this.threadNoteOff = new Thread(
                new NoteOff(this.duration, this.noteOff, this.midiDriver)
        );


    }

    @Override
    public void play() {

        if(smartphone.getEmulateDelay()){
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.midiDriver.write(this.event);

        this.threadNoteOff.start();

        smartphone.setLastNote(this.note);



        if(smartphone.getEmulateDelay()){
            //envia o atraso mecânico
            OSCMessage oscMessage1 = new OSCMessage(
                    smartphone.getServerOscAddress()+"/delay"+smartphone.getMyOscAddress()
            );

            oscMessage1.addArgument(idMessage);
            oscMessage1.addArgument(delay);

            try {
                smartphone.getSender().send(oscMessage1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



}
