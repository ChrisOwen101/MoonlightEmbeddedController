package com.marche.moonlightembeddedcontroller.Events;

import java.util.ArrayList;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class GotGamesEvent {

    ArrayList<String> gameNames = new ArrayList<>();

    public GotGamesEvent( ArrayList<String> gameNames){
        this.gameNames = gameNames;
    }

}
