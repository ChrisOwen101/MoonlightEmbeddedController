package com.marche.moonlightembeddedcontroller.Events;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class LimelightDownloadedEvent {

    public int percentage;
    public boolean done = false;

    public LimelightDownloadedEvent(int percentage){
        this.percentage = percentage;
    }

    public LimelightDownloadedEvent(boolean done){
        this.done = done;
    }
}
