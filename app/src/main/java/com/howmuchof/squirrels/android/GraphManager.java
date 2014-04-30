package com.howmuchof.squirrels.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.howmuchof.squirrels.android.Squirrel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GraphManager {

    public static int HOR_VALUES_DEFAULT_FORMAT = 0;
    public static int HOR_VALUES_DATE_FORMAT = 1;
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yy");
    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    int xFormat = 0;
    int amountOfGraphLines = 0;
    int minVertValue = 0;
    int maxVertValue = 0;

    View imageView;
    float mx, my;

    private static int topBottomIndent = 50;
    private static int graphIndent = 10;
    private static int columnWidth = 105;
    private static int marginLeft = 90;
    private static int marginColumnLeft = 40;

    private int viewHeight;
    private int viewWidth;

    ArrayList<GraphLine> graphLines = new ArrayList<GraphLine>();

    public GraphManager(){
    }

    public GraphManager(List<List<Long>> data){
        for (List list: data){
            addValues((Integer)list.get(0), (Long)list.get(1));
        }
    }

    public int getMinVertValue(){
        return minVertValue;
    }

    public int getMaxVertValue(){
        return maxVertValue;
    }

    public int addValues(int verValue, long horValue){

        GraphLine graphLine = new GraphLine(verValue, horValue);


        if ((verValue < minVertValue) || (minVertValue == 0)){
            minVertValue = verValue;
        }
        if ((verValue > maxVertValue) || (maxVertValue == 0)){
            maxVertValue = verValue;
        }
        graphLines.add(graphLine);
        Log.d("DRAWING", "Added values: " + verValue + " " + horValue);
        return ++amountOfGraphLines;
    }

    public void setXFormat(int format){
        if ((format == HOR_VALUES_DATE_FORMAT)||(format == HOR_VALUES_DEFAULT_FORMAT)){
            xFormat = format;
        }
    }

    private void drawGrid(Canvas canvas){
        float value = (float)(maxVertValue - minVertValue)/10;
        float lineValue = minVertValue;

        while (lineValue < maxVertValue + value) {
            drawGridLine(canvas, lineValue);
            lineValue += value;
        };

    }

    private void drawGridLine(Canvas canvas, float lineValue ){
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#C9C9C9"));
        paint.setStrokeWidth(2);

        int yCoord = viewHeight - (int)(((lineValue-minVertValue+1)/(maxVertValue-minVertValue+1)) *(viewHeight - topBottomIndent*2));
        int width = graphLines.size() * (graphIndent + columnWidth);
        canvas.drawLine(marginLeft, yCoord, width, yCoord, paint);
        Log.d("DRAWING", "Drawing line: " + marginLeft + ", " + yCoord + ", "+ width + ", " + yCoord + ". Value: " + lineValue);



        drawVerticalLabel(canvas, String.format("%.2f", lineValue), yCoord);
    }

    private void drawVerticalLabel(Canvas canvas, String value, int yCoord){
        Paint paint = new Paint();
        paint.setTextSize(30);
        canvas.drawText(value, 10, yCoord+10, paint);
    }


    private List<String> getVerticalLabels(){
        List<String> labels = new ArrayList<String>();

        return labels;
    }

    public void draw(Canvas canvas, View view){
        imageView = view;
        setOnTouchListener(view);
        viewHeight = canvas.getHeight();
        viewWidth = canvas.getWidth();

        drawGrid(canvas);

        for (int i = 0; i < graphLines.size(); i++){
            drawGraph(canvas, graphLines.get(i).yValue, graphLines.get(i).xValue, i);
        }
    }

    private void drawGraph(Canvas canvas, int vertValue, long horzValue, int curGraph){
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        int height = canvas.getHeight();

        int topPosY = height - (int)(((float)(vertValue-minVertValue+1)/(float)(maxVertValue-minVertValue+1)) *(height - topBottomIndent*2));
        int topPosX = curGraph * (columnWidth + graphIndent) + marginLeft + marginColumnLeft;
        canvas.drawRect(topPosX, topPosY, topPosX + columnWidth, height - topBottomIndent, paint);

        paint.setColor(Color.BLACK);
        drawHorizontalLabel(canvas, horzValue, topPosX+columnWidth/2);

        paint.setColor(Color.parseColor("#53DED5"));
        paint.setStrokeWidth(5);
        canvas.drawLine(topPosX+3, topPosY-3, topPosX + columnWidth + 3, topPosY - 3, paint);
        canvas.drawLine(topPosX + columnWidth + 3, topPosY-3, topPosX + columnWidth + 3, height - topBottomIndent, paint);

        Log.d("DRAWING", "Drawing rect "+ curGraph + ": " + topPosX + " " + topPosY + " " + (topPosX + columnWidth) + " " + height);
    }

    private void drawHorizontalLabel(Canvas canvas, Long value, int xPos){
        Paint paint = new Paint();
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);

        if (xFormat == HOR_VALUES_DATE_FORMAT){
            Date d = new Date(value);
            canvas.drawText(timeFormat.format(d),xPos, viewHeight-topBottomIndent/2,paint);
            paint.setTextSize(20);
            canvas.drawText(dateFormat.format(d),xPos, viewHeight-topBottomIndent/4+5,paint);
        }
        else {
            canvas.drawText(String.valueOf(value),xPos, viewHeight-topBottomIndent/3,paint);
        }

    }

    private void setOnTouchListener(View view){
        view.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
            float curX;

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    mx = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    curX = event.getX();
                    imageView.scrollBy((int) (mx - curX), 0);
                    mx = curX;
                    break;
                case MotionEvent.ACTION_UP:
                    curX = event.getX();
                    imageView.scrollBy((int) (mx - curX), 0);
                    break;
            }
            return true;
            }
        });
    }

    protected class GraphLine{
        public int yValue;
        public long xValue;

        public GraphLine(int yValue, long xValue){
            this.xValue = xValue;
            this.yValue = yValue;
        }

    }
}