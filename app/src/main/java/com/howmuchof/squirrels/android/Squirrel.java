package com.howmuchof.squirrels.android;

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

public class Squirrel {
    int _id;
    int _amount;
    long _date;

    public Squirrel(){

    }

    public Squirrel (int id, int amount, long date){
        this._id = id;
        this._amount = amount;
        this._date = date;
    }

    public void setID(int id){
        this._id = id;
    }

    public void setAmount(int amount){
        this._amount = amount;
    }

    public void setDate(long date){
        this._date = date;
    }

    public int getID(){
        return _id;
    }

    public int getAmount(){
        return _amount;
    }

    public long getDate(){
        return _date;
    }

    public String toString(){
        return _id + " " + _amount + " " + _date;
    }
}
