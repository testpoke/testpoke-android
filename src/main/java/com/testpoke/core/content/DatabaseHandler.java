package com.testpoke.core.content;

import android.database.sqlite.SQLiteDatabase;

/*
 * Created by Jansel Valentin on 5/3/14.
 */
interface DatabaseHandler {

    void onCreate(SQLiteDatabase db);

    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion);

}
