package com.robomus.instrument.Actions;

import android.util.Log;

import com.illposed.osc.OSCMessage;
import com.robomus.util.Note;

import org.billthefarmer.mididriver.MidiDriver;

public class PlayNote implements Action {
    MidiDriver midiDriver;
    byte[] event;

    public PlayNote(MidiDriver midiDriver, OSCMessage oscMessage) {

        Long idMessage = Long.parseLong(oscMessage.getArguments().get(0).toString());
        String symbolNote = oscMessage.getArguments().get(1).toString();
        Short duration = Short.parseShort(oscMessage.getArguments().get(2).toString());

        Note note = new Note(symbolNote);

        byte[] event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = (byte) note.getMidiValue();
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        this.midiDriver = midiDriver;
        this.event = event;
    }

    @Override
    public void play() {
        this.midiDriver.write(this.event);
    }
}
