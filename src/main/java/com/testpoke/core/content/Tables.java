package com.testpoke.core.content;

import android.database.sqlite.SQLiteDatabase;

/*
 * Created by Jansel Valentin on 5/4/14.
 */
final class Tables {
    static void executeOver(SQLiteDatabase db) {
	
        db.execSQL( "CREATE TABLE IF NOT EXISTS session(_id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, _ade677c68 TEXT, _ba8868af2 TEXT, start TEXT,start_reason INTEGER, end TEXT, end_reason INTEGER, handled INTEGER, zone TEXT)" );

        db.execSQL( "CREATE TABLE IF NOT EXISTS log(_id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, pack BLOB )" );

        db.execSQL( "CREATE TABLE IF NOT EXISTS ex(_id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, pack BLOB ) ");

        db.execSQL( "CREATE TABLE IF NOT EXISTS alerts(_id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, pack BLOB ) ");

        db.execSQL( "CREATE TABLE IF NOT EXISTS events(_id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, pack BLOB )" );

        db.execSQL( "CREATE TABLE IF NOT EXISTS crash(_id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, pack BLOB )" );
    }
}
