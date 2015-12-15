package com.testpoke.core.net.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import com.testpoke.core.net.NetworkHelper;
import com.testpoke.core.util.log.TP;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by Jansel Valentin on 5/4/14.
 */
public final class ConnectivityWatcher {
    private static ConnectionState latestState = ConnectionState.DISCONNECTED;

    private static List<ConnectivityListener> listeners;
    private static BroadcastReceiver receiver;
    private static boolean started;

    public static void start(Context context) {
        if (started || null == context)
            return;

        latestState = NetworkHelper.isNetworkReady(context) ? ConnectionState.CONNECTED : ConnectionState.DISCONNECTED;

        listeners = new ArrayList<ConnectivityListener>();
        receiver = new ConnectivityReceiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(receiver, filter);
        started = true;
    }

    public static void stop(Context context) {
        if (!started || null == context)
            return;
        context.unregisterReceiver(receiver);
        started = false;
        listeners.clear();
        listeners = null;
    }

    public static void registerListener(ConnectivityListener listener) {
        if (null == listener || null == listeners)
            return;
        if (listeners.contains(listener))
            return;
        listeners.add(listener);
        listener.onChangeDetected(latestState);

    }

    public static void unregisterListener(ConnectivityListener listener) {
        if (null == listener || null == listeners)
            return;

        listeners.remove(listener);
    }

    static void notifyListeners(ConnectionState state) {
        if (null == listeners)
            return;

        TP.i("Device is " + state + " from network");

        final ConnectionState lLatestState = latestState = state;
        new Thread( new Runnable() {
            @Override
            public void run() {
                for (int i = listeners.size() - 1; 0 <= i; --i) {
                    ConnectivityListener listener = listeners.get(i);
                    listener.onChangeDetected(lLatestState);
                }
            }
        }).start();
    }
}
