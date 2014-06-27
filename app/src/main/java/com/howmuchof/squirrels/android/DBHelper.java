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
        super(context, "how-much-of-2.0", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" ("
                + "id integer primary key autoincrement, "
                + "type integer, "
                + "amount real, "
                + "sec_amount real, "
                + "date integer" + ");");

        db.execSQL("create table " + "types" +" ("
                + "id integer primary key autoincrement, "
                + "type integer, "
                + "description text, "
                + "name text" + ");");

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

    public List<Squirrel> getDataFromDB(long startDate, long endDate, int type){
        SQLiteDatabase db = getWritableDatabase();
        String countQuery;
        List<Squirrel> list = new ArrayList<Squirrel>();
        if (endDate > 0) {
            countQuery = "SELECT * FROM " + TABLE_NAME + " WHERE date>="+startDate
                    + " AND date<=" + endDate + " AND type=" + type + " ORDER BY date";
        }
        else {
            countQuery = "SELECT * FROM " + TABLE_NAME + " WHERE date>="+startDate
                    + " AND type=" + type + " ORDER BY date";
        }
        Cursor cursor = db.rawQuery(countQuery, null);
        int idColIndex = cursor.getColumnIndex("id");
        int amountColIndex = cursor.getColumnIndex("amount");
        int dateColIndex = cursor.getColumnIndex("date");
        int secondaryAmountColIndex = cursor.getColumnIndex("sec_amount");

        if (!cursor.moveToFirst())
            return list;

        do{
            Squirrel s = new Squirrel();
            s.setID(cursor.getInt(idColIndex));
            s.setAmount(cursor.getDouble(amountColIndex));
            s.setDate(cursor.getLong(dateColIndex));
            s.setType(type);
            s.setSecAmount(cursor.getLong(secondaryAmountColIndex));
            list.add(s);
        } while (cursor.moveToNext());
        cursor.close();
        return list;
    }

    public List<Squirrel> getDataFromDBSortedByDate(int type){
        SQLiteDatabase db = getWritableDatabase();
        String countQuery;
        List<Squirrel> list = new ArrayList<Squirrel>();

        countQuery = "SELECT * FROM " + TABLE_NAME + " WHERE type="+type+" ORDER BY date DESC,id DESC";
        Cursor cursor = db.rawQuery(countQuery, null);
        int idColIndex = cursor.getColumnIndex("id");
        int amountColIndex = cursor.getColumnIndex("amount");
        int dateColIndex = cursor.getColumnIndex("date");
        int secondaryAmountColIndex = cursor.getColumnIndex("sec_amount");

        if (!cursor.moveToFirst())
            return list;

        do{
            Squirrel s = new Squirrel();
            s.setID(cursor.getInt(idColIndex));
            s.setAmount(cursor.getDouble(amountColIndex));
            s.setDate(cursor.getLong(dateColIndex));
            s.setType(type);
            s.setSecAmount(cursor.getLong(secondaryAmountColIndex));
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

    public int getRowCount(int type){
        SQLiteDatabase db = getWritableDatabase();
        String countQuery = "SELECT * FROM " + TABLE_NAME + " WHERE type="+type;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public long getDate(boolean earliest, int type){
        SQLiteDatabase db = getWritableDatabase();
        long value;
        String query;
        if (earliest) {
            query = "SELECT date FROM " + TABLE_NAME + " WHERE type="+type+" ORDER BY date ASC LIMIT 1";
        }
        else {
            query = "SELECT date FROM " + TABLE_NAME + " WHERE type="+type+" ORDER BY date DESC LIMIT 1";
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

    public void removeTypeAndValues(List<Integer> rowList){
        SQLiteDatabase db = getWritableDatabase();
        for (int id: rowList){
            db.delete(TABLE_NAME, "type = "+id, null);
            db.delete("types","id = "+id,null);
        }
    }

    public int checkType(String type){
        SQLiteDatabase db = getWritableDatabase();
        String countQuery = "SELECT * FROM types WHERE name=" +type;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public List<DataType> getTypes(){
        SQLiteDatabase db = getWritableDatabase();
        List<DataType> list = new ArrayList<DataType>();

        String query = "SELECT * FROM " + "types";

        Cursor cursor = db.rawQuery(query, null);
        int idColIndex = cursor.getColumnIndex("id");
        int typeColIndex = cursor.getColumnIndex("type");
        int nameColIndex = cursor.getColumnIndex("name");
        int descriptionColIndex = cursor.getColumnIndex("description");

        if (!cursor.moveToFirst())
            return list;

        do{
            DataType types = new DataType();
            types.setID(cursor.getInt(idColIndex));
            types.setType(cursor.getInt(typeColIndex));
            types.setName(cursor.getString(nameColIndex));
            types.setDescription(cursor.getString(descriptionColIndex));
            types.setAmount(getRowCount(types.getID()));
            list.add(types);
        } while (cursor.moveToNext());
        cursor.close();
        return list;
    }

    public DataType getType(int id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM types WHERE id=" + id;
        Cursor cursor = db.rawQuery(query, null);
        int idColIndex = cursor.getColumnIndex("id");
        int typeColIndex = cursor.getColumnIndex("type");
        int nameColIndex = cursor.getColumnIndex("name");
        int descriptionColIndex = cursor.getColumnIndex("description");

        if (!cursor.moveToFirst()) {
            Log.d("MYAPP", "DataType with ID " + id + " isn't found");
            return null;
        }

        DataType type = new DataType();
        type.setID(cursor.getInt(idColIndex));
        type.setType(cursor.getInt(typeColIndex));
        type.setName(cursor.getString(nameColIndex));
        type.setDescription(cursor.getString(descriptionColIndex));
        type.setAmount(getRowCount(type.getID()));
        cursor.close();
        return type;
    }

    public Squirrel getObject(int id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +" WHERE id=" + id;
        Cursor cursor = db.rawQuery(query, null);
        int typeColIndex = cursor.getColumnIndex("type");
        int amountColIndex = cursor.getColumnIndex("amount");
        int dateColIndex = cursor.getColumnIndex("date");
        int secondaryAmountColIndex = cursor.getColumnIndex("sec_amount");

        if (!cursor.moveToFirst()) {
            Log.d("MYAPP", "Object with ID " + id + " wasn't found");
            return null;
        }

        Squirrel s = new Squirrel();
        s.setID(id);
        s.setAmount(cursor.getDouble(amountColIndex));
        s.setDate(cursor.getLong(dateColIndex));
        s.setType(cursor.getInt(typeColIndex));
        s.setSecAmount(cursor.getDouble(secondaryAmountColIndex));

        Log.d("MYAPP", "Ojbect ID: " + id + " Object type value: " + s.getType() + " Type column: "+ typeColIndex);

        cursor.close();
        return s;
    }
}