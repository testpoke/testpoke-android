package com.testpoke.core.analytics;

import android.database.Cursor;
import com.testpoke.core.content.$_V;
import com.testpoke.core.content.PersistenceResolver;

/*
 * Created by Jansel Valentin on 6/30/2014.
 */
final class Collector {

    static byte[][] collectLogs(PersistenceResolver resolver, String uuid) {
        return tableClasifiedCollect($_V.V1.l, resolver, uuid);
    }

    static byte[][] collectExceptions(PersistenceResolver resolver, String uuid) {
        return tableClasifiedCollect($_V.V1.e, resolver, uuid);
    }

//    static byte[][] collectAlertLogcat(PersistenceResolver resolver, String uuid) {
//        return tableClasifiedCollect($_V.V1.a, resolver, uuid);
//    }


    static byte[][] collectEvents(PersistenceResolver resolver, String uuid ){
        return tableClasifiedCollect($_V.V1.ev,resolver,uuid);
    }

    static byte[] collectCrash(PersistenceResolver resolver,String uuid ){
        Cursor cursor = resolver.query($_V.V1.c, null, " uuid='" + uuid + "' ", null, null, null, null);
        byte[] crash = new byte[0];
        if (cursor.moveToFirst()) {
            crash = cursor.getBlob(2);
            cursor.close();
        }
        return crash;
    }
    
    static String collectGeo(PersistenceResolver resolver,String uuid ){
        return "";
    }

    private static byte[][] tableClasifiedCollect(String table, PersistenceResolver resolver, String uuid) {
        Cursor cursor = resolver.query(table, null, " uuid='" + uuid + "' ", null, null, null, null);
        byte[][] logs = null;
        if (cursor.moveToFirst()) {
            logs = new byte[cursor.getCount()][];
            int i = 0;
            do {
                logs[i++] = cursor.getBlob(2);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return logs;
    }
}
