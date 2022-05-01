package com.honestmc.laryngoscopeapp.Tools;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by b.jiang on 2016/12/1.
 */

public class InetAddressUtils {

    public static boolean isReachable(String ipAddress) {
        int timeout = 5000;
        InetAddress inet = null;
        try {
            inet = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("Sending Ping Request to " + ipAddress);
        boolean value = false;
        try {
            value = inet.isReachable(timeout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(value ? "Host is reachable" : "Host is NOT reachable");
        return value;
    }
}
