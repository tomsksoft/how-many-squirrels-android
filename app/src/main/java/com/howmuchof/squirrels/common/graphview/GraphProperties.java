package com.howmuchof.squirrels.common.graphview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by tieru on 5/1/14.
 */
public class GraphProperties {

    public static int HOR_VALUES_DEFAULT_FORMAT = 0;
    public static int HOR_VALUES_DATE_FORMAT = 1;

    int xFormat = 0;
    Canvas canvas;

    int height;
    int width;

    int minVertValue;
    int maxVertValue;

    int maxHorzValue;
    int minHorzValue;

    Paint gridPaint;
    Paint horzLabelsPaint;
    Paint vertLabelsPaint;
    Paint barPaint;
    Paint bar3DPaint;

    int topBottomIndent;
    int graphIndent;
    int columnWidth;
    int marginLeft;
    int marginColumnLeft;

    public GraphProperties(){
        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#C9C9C9"));
        gridPaint.setStrokeWidth(2);

        barPaint = new Paint();
        barPaint.setColor(Color.BLUE);

        bar3DPaint = new Paint();
        bar3DPaint.setColor(Color.parseColor("#53DED5"));
        bar3DPaint.setStrokeWidth(5);

        topBottomIndent = 90;
        graphIndent = 15;
        columnWidth = 160;
        marginLeft = 10;
        marginColumnLeft = 40;

        horzLabelsPaint = new Paint();
        horzLabelsPaint.setTextSize(35);
        horzLabelsPaint.setTextAlign(Paint.Align.CENTER);

    }

    public Paint getHorzLabelPaint(){
        return horzLabelsPaint;
    }

    public Paint getVertLabelsPaint(){
        return vertLabelsPaint;
    }

    public Paint getGridPaint(){
        return gridPaint;
    }

    public Paint getBarPaint(){
        return barPaint;
    }

    public Paint getBar3DPaint(){
        return bar3DPaint;
    }

    public int getMinVertValue(){
        return minVertValue;
    }

    public int getMaxVertValue(){
        return maxVertValue;
    }

    public void setMinVertValue (int value){
        minVertValue = value;
    }

    public void setMaxVertValue (int value){
        maxVertValue = value;
    }

    public void setXFormat(int format){
        if ((format == HOR_VALUES_DATE_FORMAT)||(format == HOR_VALUES_DEFAULT_FORMAT)){
            xFormat = format;
        }
    }

    public int getXFormat(){
        return xFormat;
    }

    public void setCanvas(Canvas canvas){
        this.canvas = canvas;
        height = canvas.getHeight();
        width = canvas.getWidth();
    }

    public int getTopBottomIndent(){
        return topBottomIndent;
    }

    public void setTopBottomIndent(int value){
        topBottomIndent = value;
    }

    public int getGraphIndent(){
        return graphIndent;
    }

    public void setGraphIndent(int value){
        graphIndent = value;
    }

    public int getColumnWidth(){
        return columnWidth;
    }

    public int getMarginLeft(){
        return marginLeft;
    }

    public int getMarginColumnLeft(){
        return marginColumnLeft;
    }

    public void setColumnWidth(int value){
        columnWidth = value;
    }

    public void setMarginLeft(int value){
        marginLeft = value;
    }

    public void setMarginColumnLeft(int value){
        marginColumnLeft = value;
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public GraphLine getGraphBarCoordinates(double yValue, int curGraph){
        int topY = height - (int)((height - topBottomIndent*2)*((yValue - minVertValue+1)/(maxVertValue - minVertValue + 2))+topBottomIndent);
        int topX = curGraph * (columnWidth + graphIndent) + marginColumnLeft + marginLeft;
        int botY = height - topBottomIndent;
        int botX = topX + columnWidth;

        GraphLine graphLine = new GraphLine();
        graphLine.setGraphBarValues(topX, topY, botX, botY);
        return graphLine;
    }

    public int getGridYPos(float value){
        return height - (int)((height - topBottomIndent*2)*((value - minVertValue+1)/(maxVertValue - minVertValue + 2))+topBottomIndent);
    }

    public int getGraphWidth(int graphAmount){
        return graphAmount * (graphIndent + columnWidth) + marginColumnLeft + marginLeft;
    }
}
