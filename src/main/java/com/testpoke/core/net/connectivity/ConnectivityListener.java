package com.testpoke.core.net.connectivity;

/*
 * Created by Jansel Valentin on 5/4/14.
 */
public interface ConnectivityListener {
    void onChangeDetected(ConnectionState state);
}
