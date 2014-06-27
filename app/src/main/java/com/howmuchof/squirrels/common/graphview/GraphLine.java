package com.howmuchof.squirrels.common.graphview;

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
  * Created by Viacheslav Voronov on 5/1/2014
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
