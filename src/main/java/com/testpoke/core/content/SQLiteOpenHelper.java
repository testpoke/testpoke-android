package com.testpoke.core.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/*
 * Created by Jansel Valentin on 5/3/14.
 */
final class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {

    private PersistenceProvider provider;

    public SQLiteOpenHelper( PersistenceProvider provider ){
        this( provider.getAndroidContext(),
              provider.getPersistenceContext().getDatabaseName(),
              null,
              provider.getPersistenceContext().getDatabaseVersion() );
        this.provider = provider;
    }

    SQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        provider.getHandler().onCreate( db );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        provider.getHandler().onUpgrade(db,oldVersion,newVersion);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        provider.getHandler().onDowngrade(db,oldVersion,newVersion);
        onCreate(db);
    }
}
