package com.marche.moonlightembeddedcontroller.SSH;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

/**
 * Created by Chris on 16/06/2015.
 */
public class IPHelper {

    public static String getLocalIPSubnet(Activity act){
        WifiManager wm = (WifiManager) act.getSystemService(act.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        String[] result = ip.split("[.]");
        return result[0] + "." + result[1] + "." + result[2] + ".";
    }

}
