package com.testpoke.core.analytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;
import com.testpoke.TestPoke;

import java.io.Serializable;

/*
 * Created by Jansel Valentin on 5/25/14.
 */

final class Latest implements Serializable {
    private static final Time current = new Time();

    final String uuid;
    final String time;
    final boolean handled;

    private Latest( String uuid, String time, boolean handled ){
        this.uuid = uuid;
        this.time = time;
        this.handled = handled;
    }

    public static Latest load( Context context ){
        SharedPreferences prefs = context.getSharedPreferences( "com.testpoke.latest", Context.MODE_PRIVATE );
        return new Latest(prefs.getString("uuid",""), prefs.getString("time",""), prefs.getBoolean("handled",false) );
    }

    public static void save( Context context ){
        SharedPreferences prefs = context.getSharedPreferences( "com.testpoke.latest", Context.MODE_PRIVATE );
        current.setToNow();

        prefs.edit()
             .putString( "uuid", IA.k().uuid() )
             .putString( "time",current.format3339(false) )
             .putBoolean( "handled", TestPoke.getSettings().getOptions().isSessionAutoHandled())
             .commit();
    }
}
