package com.robomus.instrument;

import com.illposed.osc.OSCMessage;
import com.robomus.instrument.Actions.Action;

import java.util.Date;

public class RoboMusMessage implements Comparable<RoboMusMessage>{
    private Date timestamp;
    private OSCMessage oscMessage;
    private String actionName;
    private Action action;

    public RoboMusMessage(Date timestamp, OSCMessage oscMessage, String actionName) {
        this.timestamp = timestamp;
        this.oscMessage = oscMessage;
        this.actionName = actionName;
    }

    public RoboMusMessage(Date timestamp, OSCMessage oscMessage, Action action) {
        this.timestamp = timestamp;
        this.oscMessage = oscMessage;
        this.action = action;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public OSCMessage getOscMessage() {
        return oscMessage;
    }

    public void setOscMessage(OSCMessage oscMessage) {
        this.oscMessage = oscMessage;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public void play(){
        this.action.play();
    }

    @Override
    public int compareTo(RoboMusMessage roboMusMessage) {
        if(this.timestamp.getTime() < roboMusMessage.getTimestamp().getTime()){
            return -1;
        }else if(this.timestamp.getTime() < roboMusMessage.getTimestamp().getTime()){
            return 1;
        }else{
            return 0;
        }
    }
}
