package com.udacity.stockhawk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

public class Utility {
    /**
     * Checking Network is Connected - make sure to setup the android.permission.ACCESS_NETWORK_STATE
     * permission, to verify network availability: https://guides.codepath.com/android/Sending-and-Managing-Network-Requests
     *
     * @return true if we have a connection
     */
    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Checking the Internet is Connected -To verify if the device is actually connected to the internet,
     * we can use the following method of pinging the Google DNS servers to check for the expected exit value.
     *
     * @return true if we get a response
     */
    public static Boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
