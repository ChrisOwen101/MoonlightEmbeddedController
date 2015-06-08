package com.marche.moonlightembeddedcontroller.Events;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class PairEvent {

    public String pairCode = "";
    public boolean didPair = false;

    public PairEvent(String pairCode){
        this.pairCode = pairCode;
    }

    public PairEvent(boolean didPair){
        this.didPair = didPair;
    }

}
