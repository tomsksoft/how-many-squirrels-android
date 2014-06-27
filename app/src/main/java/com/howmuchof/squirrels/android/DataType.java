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

public class DataType {
    int id;
    int type;
    String name;
    String description;
    int amount;

    public DataType(){

    }

    public DataType (int id, int type, String name){
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public void setID(int id){
        this.id = id;
    }

    public void setType(int type){
        this.type = type;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }

    public int getID(){
        return id;
    }

    public int getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    public String getDesctiption(){
        return description;
    }

    public int getAmount(){
        return amount;
    }

    public String toString(){
        return id + " " + type+ " " + name;
    }
}
