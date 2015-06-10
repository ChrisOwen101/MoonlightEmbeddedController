package com.marche.moonlightembeddedcontroller.Events;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class GameLoadingEvent {

    public String text;
    public boolean done;

    public GameLoadingEvent(String text){
        this.text = text;
    }

    public GameLoadingEvent(Boolean done){
        this.done = done;
    }

}
