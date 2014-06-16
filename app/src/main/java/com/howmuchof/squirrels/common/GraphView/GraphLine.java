package com.howmuchof.squirrels.android;

/**
 * Created by tieru on 5/1/14.
 */
public class GraphLine {
    int topX;
    int topY;
    int botX;
    int botY;

    public void setGraphBarValues(int topX, int topY, int botX, int botY){
        this.topX = topX;
        this.topY = topY;
        this.botX = botX;
        this.botY = botY;
    }

    public int getBarBotX() {
        return botX;
    }

    public int getBarBotY() {
        return botY;
    }

    public int getBarTopX() {
        return topX;
    }

    public int getBarTopY() {
        return topY;
    }
}
