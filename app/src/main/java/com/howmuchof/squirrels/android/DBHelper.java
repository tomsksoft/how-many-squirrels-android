package com.howmuchof.squirrels.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LinXi on 4/14/2014.
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "how-much-of", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table objects ("
                + "id integer primary key autoincrement,"
                + "amount integer, "
                + "date integer" + ");");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public List<Squirrel> getDataFromDB(SQLiteDatabase db){
        List<Squirrel> list = new ArrayList<Squirrel>();
        Cursor c = db.query("objects", null, null, null, null, null, null);
        int idColIndex = c.getColumnIndex("id");
        int amountColIndex = c.getColumnIndex("amount");
        int dateColIndex = c.getColumnIndex("date");

        if (c.moveToFirst()){
            do{
                Squirrel s = new Squirrel();
                s.setID(c.getInt(idColIndex));
                s.setAmount(c.getInt(amountColIndex));
                s.setDate(c.getLong(dateColIndex));
                list.add(s);
            } while (c.moveToNext());
        }
        return list;
    };
}