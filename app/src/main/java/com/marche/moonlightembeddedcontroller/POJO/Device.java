package com.marche.moonlightembeddedcontroller.POJO;

import java.io.Serializable;

/**
 * Created by Chris on 08/06/2015.
 */
public class Device implements Serializable {

    public String directory = "limelight";
    public String ip;
    public String login;
    public String password;
    public String hostIP = "";

    public Device(String ip, String login, String password){
        this.ip = ip;
        this.login = login;
        this.password = password;
    }

}
