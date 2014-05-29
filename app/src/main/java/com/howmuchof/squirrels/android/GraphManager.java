package com.howmuchof.squirrels.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.howmuchof.squirrels.android.Squirrel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GraphManager {

    private final static int FORMAT_DATE = 0;
    private final static int FORMAT_TIME = 1;

    View imageView;
    Canvas canvas;
    Context context;
    GraphProperties gProps;

    ArrayList<GraphValues> graphLines = new ArrayList<GraphValues>();

    public GraphManager(Context context){
        gProps = new GraphProperties();
        this.context = context;
    }

    public GraphManager(List<List<Long>> data){
        for (List list: data){
            addValues((Integer)list.get(0), (Long)list.get(1));
        }
        gProps = new GraphProperties();
    }

    public GraphProperties getGraphProperties(){
        return gProps;
    }

    public int addValues(int verValue, long horValue){
        GraphValues graphLine = new GraphValues(verValue, horValue);

        if ((verValue < gProps.getMinVertValue()) || (gProps.getMinVertValue() == 0)){
            gProps.setMinVertValue(verValue);
        }
        if ((verValue > gProps.getMaxVertValue()) || (gProps.getMaxVertValue() == 0)){
            gProps.setMaxVertValue(verValue);
        }
        graphLines.add(graphLine);

        Log.d("DRAWING", "Added values: " + verValue + " " + horValue);
        return graphLines.size();
    }

    private void drawGrid(Canvas canvas){
        Log.d("DRAWING", "Max/Min values: " + gProps.getMaxVertValue() + " " + gProps.getMinVertValue());
        float value = (float)(gProps.getMaxVertValue() - gProps.getMinVertValue())/10;
        float lineValue = gProps.getMinVertValue();
        int width = gProps.getGraphWidth(graphLines.size());//graphLines.size() * (gProps.getGraphIndent() + gProps.getColumnWidth());

        while (lineValue <= gProps.getMaxVertValue() + value) {
            drawGridLine(canvas, lineValue, width);
            drawVerticalLabel(canvas, lineValue);
            lineValue += value;
        }
    }

    private void drawGridLine(Canvas canvas, float lineValue, int width){
        Paint paint = gProps.getGridPaint();

        int yCoord = gProps.getGridYPos(lineValue);

        canvas.drawLine(gProps.getMarginLeft(), yCoord, width, yCoord, paint);
        Log.d("DRAWING", "Drawing line: " + gProps.getMarginLeft() + ", " + yCoord + ", "+ width + ", " + yCoord + ". Value: " + lineValue);
    }

    private void drawVerticalLabel(Canvas canvas, float value){
        int yCoord = gProps.getGridYPos(value);
        String label = String.format("%.2f", value);
        Paint paint = new Paint();
        paint.setTextSize(35);
        canvas.drawText(label, 10, yCoord+15, paint);
    }

    public void draw(Canvas canvas, View view){

        if (graphLines.size()<2){
            canvas.drawColor(Color.TRANSPARENT);
            return;
        }

        this.canvas = canvas;
        gProps.setCanvas(canvas);
        imageView = view;

        canvas.drawColor(Color.TRANSPARENT);
        drawGrid(canvas);
        for (int i = 0; i < graphLines.size(); i++){
            drawGraph(graphLines.get(i).yValue, graphLines.get(i).xValue, i);
        }
    }


    private void drawGraph(int vertValue, long horzValue, int curGraph){

        Paint barPaint = gProps.getBarPaint();
        Paint bar3DPaint = gProps.getBar3DPaint();
        GraphLine gl = gProps.getGraphBarCoordinates(vertValue, curGraph);

        canvas.drawRect(gl.getBarTopX(), gl.getBarTopY(), gl.getBarBotX(), gl.getBarBotY(), barPaint);

        drawHorizontalLabel(horzValue, gl.getBarTopX()+gProps.getColumnWidth()/2);

        //canvas.drawLine(gl.getBarTopX() + 3, gl.getBarTopY() - 3, gl.getBarBotX() + 3, gl.getBarTopY() - 3, bar3DPaint);
        //canvas.drawLine(gl.getBarBotX()+3, gl.getBarTopY()-3, gl.getBarBotX() + 3,
        //       gProps.getHeight() - gProps.getTopBottomIndent(), bar3DPaint);

        Log.d("DRAWING", "Drawing rect "+ curGraph + ": " + gl.getBarTopX() + " " + gl.getBarTopY() +
                " " + (gl.getBarBotX()) );//+ ". Offset: " + offset);
    }

    private void drawHorizontalLabel(Long value, int xPos){
        Paint paint = gProps.getHorzLabelPaint();

        if (gProps.getXFormat() == GraphProperties.HOR_VALUES_DATE_FORMAT){
            Date d = new Date(value);
            canvas.drawText(formatDate(d,FORMAT_TIME),xPos, gProps.getHeight()-gProps.getTopBottomIndent()/2,paint);
            canvas.drawText(formatDate(d,FORMAT_DATE),xPos, gProps.getHeight()-gProps.getTopBottomIndent()/4+5,paint);
        }
        else {
            canvas.drawText(String.valueOf(value),xPos, gProps.getHeight()-gProps.getTopBottomIndent()/3,paint);
        }
    }

    protected class GraphValues{
        public int yValue;
        public long xValue;

        public GraphValues(int yValue, long xValue){
            this.xValue = xValue;
            this.yValue = yValue;
        }
    }

    public String formatDate(Date date, int requestType){
        String result = "";
        DateFormat dateFormat;

        if (date != null){
            try {
                if (requestType == FORMAT_DATE) {

                    String format = Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);
                    if (TextUtils.isEmpty(format)) {
                        dateFormat = android.text.format.DateFormat.getDateFormat(context);
                    } else {
                        dateFormat = new SimpleDateFormat(format);
                    }
                    result = dateFormat.format(date);
                }
                else if (requestType == FORMAT_TIME) {
                    dateFormat = android.text.format.DateFormat.getTimeFormat(context);
                    result = " " + dateFormat.format(date);
                }
            }
            catch (Exception e){
                Log.d("CODE_ERROR","Couldn't resolve date with parameters: Date '" + date +
                        "' and RequestType '" + requestType + "'");
            }
        }

        return result;
    }
}