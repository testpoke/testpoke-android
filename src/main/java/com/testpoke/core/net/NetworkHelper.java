package com.testpoke.core.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 * Created by Jansel ValentinJansel V. Rodriguez (jrodr) on 5/4/14.
 */
public final class NetworkHelper {
    public static boolean isNetworkReady(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean enable = false;
        if (conManager != null) {
            NetworkInfo info = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            enable |= null !=info && info.isConnected();
            enable |= isWifi(context);
        }
        return enable;
    }

    public static boolean isWifi(Context context){
        ConnectivityManager cm = ( ConnectivityManager ) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if( null != cm ) {
            NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return null !=  info && info.isConnected();
        }
        return false;
    }
}

