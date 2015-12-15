package com.testpoke.core.content.policy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.testpoke.core.util.log.TP;

/*
 * Created by Jansel Valentin on 5/3/14.
 */
public final class RejectPolicy implements UpgradePolicy<Context>, DowngradePolicy<Context> {
    static{
        TP.d("Configured RejectPolicy Version Change");
    }

    @Override
    public void handleDowngrade(Context context,SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new VersionPolicyException( "Downgrade Operation is not allowed by configured policy" );
    }

    @Override
    public void handleUpgrade(Context context,SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new VersionPolicyException( "Downgrade Operation is not allowed by configured policy" );
    }
}
