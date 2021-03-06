package com.howmuchof.squirrels.android;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/*
  * How many squirrels: tool for young naturalist
  *
  * This application is created within the internship
  * in the Education Department of Tomsksoft, http://tomsksoft.com
  * Idea and leading: Sergei Borisov
  *
  * This software is licensed under a GPL v3
  * http://www.gnu.org/licenses/gpl.txt
  *
  * Created by Viacheslav Voronov on 4/14/2014
  */

public class DBHelper extends SQLiteOpenHelper {

    private static String TABLE_NAME = "objects";
    public DBHelper(Context context) {
        super(context, "how-much-of", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" ("
                + "id integer primary key autoincrement,"
                + "amount integer, "
                + "date integer" + ");");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public List<Squirrel> getDataFromDB(){
        SQLiteDatabase db = getWritableDatabase();
        List<Squirrel> list = new ArrayList<Squirrel>();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        int idColIndex = c.getColumnIndex("id");
        int amountColIndex = c.getColumnIndex("amount");
        int dateColIndex = c.getColumnIndex("date");

        if (!c.moveToFirst())
            return list;

        do{
            Squirrel s = new Squirrel();
            s.setID(c.getInt(idColIndex));
            s.setAmount(c.getInt(amountColIndex));
            s.setDate(c.getLong(dateColIndex));
            list.add(s);
        } while (c.moveToNext());
        c.close();
        return list;
    }

    public List<Squirrel> getDataFromDB(long startDate, long endDate){
        SQLiteDatabase db = getWritableDatabase();
        String countQuery;
        List<Squirrel> list = new ArrayList<Squirrel>();
        if (endDate > 0) {
            countQuery = "SELECT * FROM " + TABLE_NAME + " WHERE date>="+startDate
                    + " AND date<=" + endDate + " ORDER BY date";
        }
        else {
            countQuery = "SELECT * FROM " + TABLE_NAME + " WHERE date>="+startDate
                    + " ORDER BY date";
        }
        Cursor cursor = db.rawQuery(countQuery, null);
        int idColIndex = cursor.getColumnIndex("id");
        int amountColIndex = cursor.getColumnIndex("amount");
        int dateColIndex = cursor.getColumnIndex("date");

        if (!cursor.moveToFirst())
            return list;

        do{
            Squirrel s = new Squirrel();
            s.setID(cursor.getInt(idColIndex));
            s.setAmount(cursor.getInt(amountColIndex));
            s.setDate(cursor.getLong(dateColIndex));
            list.add(s);
        } while (cursor.moveToNext());
        cursor.close();
        return list;
    }

    public List<Squirrel> getDataFromDBSortedByDate(){
        SQLiteDatabase db = getWritableDatabase();
        String countQuery;
        List<Squirrel> list = new ArrayList<Squirrel>();

        countQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY date DESC,id DESC";
        Cursor cursor = db.rawQuery(countQuery, null);
        int idColIndex = cursor.getColumnIndex("id");
        int amountColIndex = cursor.getColumnIndex("amount");
        int dateColIndex = cursor.getColumnIndex("date");

        if (!cursor.moveToFirst())
            return list;

        do{
            Squirrel s = new Squirrel();
            s.setID(cursor.getInt(idColIndex));
            s.setAmount(cursor.getInt(amountColIndex));
            s.setDate(cursor.getLong(dateColIndex));
            list.add(s);
        } while (cursor.moveToNext());
        cursor.close();
        return list;
    }

    public void deleteRows(SQLiteDatabase db, List<Integer> rowList){
        for (int id: rowList){
            db.delete(TABLE_NAME, "id = " + id, null);
        }
    }

    public int getRowCount(){
        SQLiteDatabase db = getWritableDatabase();
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public long getDate(boolean earliest){
        SQLiteDatabase db = getWritableDatabase();
        long value;
        String query;
        if (earliest) {
            query = "SELECT date FROM " + TABLE_NAME + " ORDER BY date ASC LIMIT 1";
        }
        else {
            query = "SELECT date FROM " + TABLE_NAME + " ORDER BY date DESC LIMIT 1";
        }

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            value = cursor.getLong(cursor.getColumnIndex("date"));
        }
        else {
            value = -1;
        }
        cursor.close();
        return value;
    }
}