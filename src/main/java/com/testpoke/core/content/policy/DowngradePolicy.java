package com.testpoke.core.content.policy;

import android.database.sqlite.SQLiteDatabase;

/*
 * Created by Jansel Valentin on 5/3/14.
 */
public interface DowngradePolicy<TParam> extends VersionPolicy {
    void handleDowngrade(TParam param, SQLiteDatabase db, int oldVersion, int newVersion);
}
