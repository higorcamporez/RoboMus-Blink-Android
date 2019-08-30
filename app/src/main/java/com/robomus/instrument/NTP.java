package com.robomus.instrument;

import android.content.Context;
import android.util.Log;

import com.instacart.library.truetime.TrueTimeRx;

import io.reactivex.schedulers.Schedulers;

public class NTP extends Thread{

    private Context activity;
    private String serverIpAddress;
    private boolean timeStatus;
    private Smartphone smartphone;

    public NTP(Smartphone smartphone){
        this.activity = smartphone.getActivity();
        this.smartphone = smartphone;
    }

    public void requestTimeToNTPServer() {
        this.serverIpAddress = smartphone.getServerIpAddress();
        timeStatus = false;
        TrueTimeRx.build()
                .withLoggingEnabled(true)
                .withSharedPreferences(activity)
                 .initializeRx(serverIpAddress)
                .subscribeOn(Schedulers.io())
                .subscribe(date -> {
                    //Log.v("TrueTime", "TrueTime was initialized and we have a time: " + System.currentTimeMillis());
                    Log.v("TrueTime", "TrueTime was initialized and we have a time: " + date);
                    timeStatus = true;
                }, throwable -> {
                    Log.v("TrueTime", "TrueTime was not initialized");
                    timeStatus = false;
                    throwable.printStackTrace();
                });
    }

    @Override
    public void run() {

        while(true){
            boolean first = false;
            this.requestTimeToNTPServer();
            long t = System.currentTimeMillis();
            while(System.currentTimeMillis() - t < 30000){
                if(isTimeStatus() && !first){
                    smartphone.writeOnScreen("TrueTime was initialized");
                    first = true;

                }
            }
            if(!isTimeStatus()){
                smartphone.writeOnScreen("TrueTime was NOT initialized, try again!");
            }

        }
    }

    public boolean isTimeStatus() {
        return timeStatus;
    }
}
