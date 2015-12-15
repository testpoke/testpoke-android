package com.testpoke.core.net.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.testpoke.core.net.NetworkHelper;

/*
 * Created by Jansel Valentin on 5/4/14.
 */
public final class ConnectivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectionState state = ConnectionState.DISCONNECTED;
        if (NetworkHelper.isNetworkReady(context))
            state = ConnectionState.CONNECTED;
        ConnectivityWatcher.notifyListeners(state);
    }
}
