package com.testpoke.core.content.policy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.testpoke.core.content.$_V;
import com.testpoke.core.util.log.TP;

/*
 * Created by Jansel Valentin on 5/3/14.
 */
public final class AutoPolicy implements UpgradePolicy<Context>, DowngradePolicy<Context> {
    static{
        TP.d("Configured AutoPolicy Version Change");
    }

    public void handleDowngrade(Context context, SQLiteDatabase db, int oldVersion, int newVersion) {
        TP.d("Downgrading database from version " + oldVersion + " to version " + newVersion);
        drop(db);

    }

    @Override
    public void handleUpgrade(Context context, SQLiteDatabase db, int oldVersion, int newVersion) {
        TP.d("Upgrading database from version " + oldVersion + " to version " + newVersion);
        drop( db );
    }


    private void drop(SQLiteDatabase db ){
//        db.execSQL("DROP TABLE IF EXISTS " + $_V.V1.a);
        db.execSQL("DROP TABLE IF EXISTS " + $_V.V1.c);
        db.execSQL("DROP TABLE IF EXISTS " + $_V.V1.e);
        db.execSQL("DROP TABLE IF EXISTS " + $_V.V1.ev);
        db.execSQL("DROP TABLE IF EXISTS " + $_V.V1.l);
        db.execSQL("DROP TABLE IF EXISTS " + $_V.V1.s);
    }
}
