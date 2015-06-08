package com.marche.moonlightembeddedcontroller.POJO;

/**
 * Created by Chris on 08/06/2015.
 */
public class Device {

    public String directory;
    public String ip;
    public String login;
    public String password;

    public Device(String ip, String login, String password){
        this.ip = ip;
        this.login = login;
        this.password = password;
    }

}
