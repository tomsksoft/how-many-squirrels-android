package com.howmuchof.squirrels.android;

import java.util.Date;

/**
 * Created by tieru on 6/24/14.
 */
public class Duration {

    long time;
    public Duration(){

    }

    public Duration(long time){
        this.time = time;
    }

    public Duration(long timeBegin, long timeEnd){
        this.time = Math.abs(timeBegin - timeEnd);
    }

    public void setTime(long timeBegin, long timeEnd){
        this.time = Math.abs(timeBegin - timeEnd);
    }

    public void setTime(Date date){
        this.time = date.getTime();
    }

    public void setDayTime(Date date1, Date date2){
        /*if (date1 > date2){
            date2 += 1000*60*60*24;
            time = date2 - date1;
        }
        else {
            time = date2 - date1;
        }*/
    }

    public void setDayTime(Long date1, Long date2){
        if (date1 > date2){
            date2 += 1000*60*60*24;
            time = date2 - date1;
        }
        else {
            time = date2 - date1;
        }
    }

    public long getTime(){
        return time;
    }

    public long getDays(){
        return time/(1000*60*60*24);
    }

    public long getHours(){
        long time = this.time%(1000*60*60*24);
        return time/(1000*60*60);
    }

    public long getMinutes(){
        long time = this.time%(1000*60*60*24);
        time %= (1000*60*60);
        return time/(1000*60);
    }

    /*public int getSeconds(){
        int time = (int)this.time%(1000*60*60*24);
        time %= (1000*60*60);
        time %= (1000*60);
        return time/1000;
    }*/
}
