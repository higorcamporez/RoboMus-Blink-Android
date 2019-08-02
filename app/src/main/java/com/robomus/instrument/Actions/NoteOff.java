package com.robomus.instrument.Actions;

import org.billthefarmer.mididriver.MidiDriver;

public class NoteOff implements Runnable {

    private Short duration;
    private byte[] noteOff;
    private MidiDriver midiDriver;

    public NoteOff(Short duration, byte[] noteOff, MidiDriver midiDriver) {
        this.duration = duration;
        this.noteOff = noteOff;
        this.midiDriver = midiDriver;
    }

    @Override
    public void run() {

        try {
            System.out.println("pausou, pai vei!");
            Thread.sleep(this.duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.midiDriver.write(this.noteOff);

    }

}
