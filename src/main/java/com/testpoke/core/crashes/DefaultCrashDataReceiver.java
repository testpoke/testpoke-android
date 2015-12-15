package com.testpoke.core.crashes;


import android.content.ContentValues;
import android.content.Context;
import android.text.format.Time;
import com.testpoke.api.Session;
import com.testpoke.api.Sessions;
import com.testpoke.core.analytics.IA;
import com.testpoke.core.analytics.KNULLS;
import com.testpoke.core.analytics.OCReason;
import com.testpoke.core.content.$_V;
import com.testpoke.core.content.PersistenceProvider;
import com.testpoke.core.content.PersistenceResolver;
import com.testpoke.core.ia.Constants;
import com.testpoke.core.util.Dump;
import com.testpoke.core.util.log.TP;

/*
 * Created by Jansel Valentin on 5/20/14.
 */
/*package*/ class DefaultCrashDataReceiver implements CrashDataReceiver<AfterCrashData> {
    private Context context;
    private PersistenceResolver resolver;

    DefaultCrashDataReceiver( Context context ){
        this.context = context;
    }

    @Override
    public void receive(AfterCrashData data) {
        forceSessionClose();
        hook(data);
    }

    @Override
    public void handleUnsentCrash(AfterCrashData data) {
        final String uuid = IA.k().uuid();

        if (null == uuid || null == data)
            return;

        if( null == resolver )
            resolver = PersistenceProvider.getDefault(context).getResolver();

        ContentValues values = new ContentValues();
        values.put("uuid", uuid);
        values.put("pack", data.toByteArray());

        try {
            resolver.insert($_V.V1.c, "pack", values);
        } catch (Exception ex) {
            Dump.printStackTraceCause(ex);
        }
    }


    private void forceSessionClose(){
        Session active = Sessions.getActive(context);
        
        if( null == IA.k() || KNULLS._N.equals(active) ) {
            TP.i("No session crashed to be closed");
            return;
        }
        final Time end = new Time();
        end.setToNow();

        TP.d("TestPoke is closing active session due on crash happens");


        if( null == resolver )
            resolver = PersistenceProvider.getDefault(context).getResolver();

        ContentValues values = new ContentValues();
        values.put( "uuid", IA.k().uuid() );
        values.put("end_reason", OCReason.CRASHED );
        values.put("end", end.format3339(false));

        long updated = resolver.updateOrInsert($_V.V1.s, Constants._ade677c68,values,"uuid='" + IA.k().uuid() + "'",null);
        values.clear();
    }


    private void hook( CrashData data ){
        if( Thread.getDefaultUncaughtExceptionHandler() instanceof UncaughtHandler){
            CrashHook hook = ((UncaughtHandler) Thread.getDefaultUncaughtExceptionHandler()).getCrashHook();
            if( null != hook ){
                hook.hookQuickly( data );
            }
        }
    }
}
