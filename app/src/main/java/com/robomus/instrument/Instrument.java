/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.robomus.instrument;

/**
 *
 * @author Higor
 */
public abstract class Instrument {
    
    protected String name; // nome do instrumento   
    protected int polyphony; // quantidade de notas
    protected String myOscAddress; //endereço do OSC do instrumento
    protected String serverOscAddress; //endereço do OSC do instrumento
    protected String serverName;
    protected String serverIpAddress; // endereco do servidor
    protected int sendPort; // porta para envio msgOSC
    protected int receivePort; // porta pra receber msgOSC
    protected String typeFamily; //tipo do instrumento
    protected String specificProtocol; //procolo especifico do robo
    protected String myIp;

    public Instrument(String name, String OscAddress,
                      int receivePort, String myIp) {
        
        this.name = name;
        ///this.polyphony = polyphony;
        this.myOscAddress = OscAddress;
        this.receivePort = receivePort;
        //this.typeFamily = typeFamily;
        //this.specificProtocol = specificProtocol;
        this.myIp = myIp;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPolyphony() {
        return polyphony;
    }

    public void setPolyphony(int polyphony) {
        this.polyphony = polyphony;
    }

    public String getMyOscAddress() {
        return myOscAddress;
    }

    public void setMyOscAddress(String myOscAddress) {
        this.myOscAddress = myOscAddress;
    }

    public String getServerOscAddress() {
        return serverOscAddress;
    }

    public void setServerOscAddress(String serverOscAddress) {
        this.serverOscAddress = serverOscAddress;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerIpAddress() {
        return this.serverIpAddress;
    }

    public void setServerIpAddress(String severIpAddress) {
        this.serverIpAddress = severIpAddress;
    }

    public int getSendPort() {
        return sendPort;
    }

    public void setSendPort(int sendPort) {
        this.sendPort = sendPort;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public void setReceivePort(int receivePort) {
        this.receivePort = receivePort;
    }

    public String getTypeFamily() {
        return typeFamily;
    }

    public void setTypeFamily(String typeFamily) {
        this.typeFamily = typeFamily;
    }

    public String getSpecificProtocol() {
        return specificProtocol;
    }

    public void setSpecificProtocol(String specificProtocol) {
        this.specificProtocol = specificProtocol;
    }

    public String getMyIp() {
        return myIp;
    }

    public void setMyIp(String myIp) {
        this.myIp = myIp;
    }
}
