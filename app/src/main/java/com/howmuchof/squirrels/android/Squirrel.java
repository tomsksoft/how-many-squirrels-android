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
    int id;
    int type;
    double amount;
    double secAmount;
    long date;

    public Squirrel(){

    }

    public Squirrel (int id, int type, double amount, long date){
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public void setID(int id){
        this.id = id;
    }

    public void setType(int type){
        this.type = type;
    }

    public void setAmount(double amount){
        this.amount = amount;
    }
    public void setSecAmount(double amount){
        secAmount = amount;
    }

    public void setDate(long date){
        this.date = date;
    }

    public int getID(){
        return id;
    }

    public int getType(){
        return type;
    }

    public double getAmount(){
        return amount;
    }
    public double getSecAmount(){
        return secAmount;
    }

    public long getDate(){
        return date;
    }

    public String toString(){
        return id + " " + type +" " + amount + " " + date;
    }
}
