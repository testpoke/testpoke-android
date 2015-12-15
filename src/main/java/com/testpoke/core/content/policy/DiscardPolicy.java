package com.testpoke.core.content.policy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.testpoke.core.util.log.TP;

/*
 * Created by Jansel Valentin on 5/3/14.
 */
public final class DiscardPolicy implements UpgradePolicy<Context>, DowngradePolicy<Context> {
    static{
        TP.d("Configured DiscardPolicy Version Change");
    }

    @Override
    public void handleDowngrade(Context context,SQLiteDatabase db, int oldVersion, int newVersion) {
        TP.d("Discarding Downgrade due to DiscardPolicy set");
    }

    @Override
    public void handleUpgrade(Context context,SQLiteDatabase db, int oldVersion, int newVersion) {
        TP.d("Discarding Upgrade due to DiscardPolicy set");
    }
}
