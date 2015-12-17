package com.testpoke.core.analytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;
import com.testpoke.TestPoke;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/*
 * Created by Jansel Valentin on 5/25/14.
 */

final class Latest implements Serializable {
    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private static Calendar calendar = GregorianCalendar.getInstance();

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

        String time;
        try{
            calendar.setTimeInMillis(System.currentTimeMillis());
            time = format.format(calendar.getTime());
        }catch ( Exception e){
            time = calendar.getTime().toString();
        }

        prefs.edit()
             .putString( "uuid", IA.k().uuid() )
             .putString( "time",time )
             .putBoolean( "handled", TestPoke.getSettings().getOptions().isSessionAutoHandled())
             .commit();
    }
}
