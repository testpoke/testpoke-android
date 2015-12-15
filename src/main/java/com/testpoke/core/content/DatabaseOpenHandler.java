package com.testpoke.core.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/*
 * Created by Jansel Valentin) on 5/3/14.
 */
final class DatabaseOpenHandler implements DatabaseHandler {

    private Context androidContext;
    private PersistenceContext context;

    public DatabaseOpenHandler(PersistenceContext context, Context androidContext) {
        this.context = context;
        this.androidContext = androidContext;
    }

    public void onCreate(SQLiteDatabase db) {
        Tables.executeOver(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        context.getUpgradePolicy().handleUpgrade(androidContext, db, oldVersion, newVersion);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        context.getDowngradePolicy().handleDowngrade(androidContext, db, oldVersion, newVersion);
    }

}
