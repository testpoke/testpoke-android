package com.testpoke.core.content;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/*
 * Created by Jansel Valentin on 5/4/14.
 */
final class SQLPersistenceResolver implements PersistenceResolver {

    private android.database.sqlite.SQLiteOpenHelper helper;

    SQLPersistenceResolver(android.database.sqlite.SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    @Override
    public long insert(String table, String nullColumnHack, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert(table, nullColumnHack, values);

    }

    @Override
    public long insertOrThrow(String table, String nullColumnHack, ContentValues values) throws SQLException {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insertOrThrow(table, nullColumnHack, values);

    }

    @Override
    public long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);

    }

    @Override
    public int delete(String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(table, whereClause, whereArgs);

    }

    @Override
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.update(table, values, whereClause, whereArgs);

    }

    @Override
    public int updateWithOnConflict(String table, ContentValues values, String whereClause, String[] whereArgs, int conflictAlgorithm) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.updateWithOnConflict(table, values, whereClause, whereArgs, conflictAlgorithm);
    }

    @Override
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        SQLiteDatabase db = helper.getReadableDatabase();
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    @Override
    public long replace(String table, String nullColumnHack, ContentValues initialValues) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.replace(table, nullColumnHack, initialValues);
    }

    @Override
    public long replaceOrThrow(String table, String nullColumnHack, ContentValues initialValues) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.replaceOrThrow(table, nullColumnHack, initialValues);
    }

    @Override
    public long updateOrInsert(String table, String nullColumnHack, ContentValues initialValues, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long l = 0;
        if (0 == (l = db.update(table, initialValues, whereClause, whereArgs)))
            l = db.insert(table, nullColumnHack, initialValues);
        return l;
    }

    @Override
    public void close() {
        if( null != helper ) {
            helper.close();
        }
    }
}
