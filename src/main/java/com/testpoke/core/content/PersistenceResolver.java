package com.testpoke.core.content;

import android.content.ContentValues;
import android.database.Cursor;

import java.sql.SQLException;

/*
 * Created by Jansel Valentin on 5/4/14.
 */
public abstract interface PersistenceResolver {

    public abstract long insert(String table, String nullColumnHack, ContentValues values);

    public abstract long insertOrThrow(String table, String nullColumnHack, ContentValues values) throws SQLException;

    public abstract long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm);

    public abstract int delete(String table, String whereClause, String[] whereArgs);

    public abstract int update(String table, ContentValues values, String whereClause, String[] whereArgs);

    public abstract int updateWithOnConflict(String table, ContentValues values, String whereClause, String[] whereArgs, int conflictAlgorithm) ;

    public abstract long replace(String table, String nullColumnHack, ContentValues initialValues);

    public abstract long replaceOrThrow(String table, String nullColumnHack, ContentValues initialValues);

    public abstract long updateOrInsert(String table, String nullColumnHack, ContentValues initialValues, String whereClause, String[] whereArgs);

    public abstract Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy);

    public abstract void close();
}
