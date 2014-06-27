package com.howmuchof.squirrels.common.graphview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraphManager {

    private final static int FORMAT_DATE = 0;
    private final static int FORMAT_TIME = 1;

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

    public int getSize(){
        return graphLines.size();
    }

    private void drawGrid(){
        Log.d("DRAWING", "Max/Min values: " + gProps.getMaxVertValue() + " " + gProps.getMinVertValue());
        float value = (float)(gProps.getMaxVertValue() - gProps.getMinVertValue())/10;
        if (value == 0){
            value =  (float)gProps.getMaxVertValue();
        }
        float lineValue = gProps.getMinVertValue();
        int width = gProps.getGraphWidth(graphLines.size());

        while (lineValue <= gProps.getMaxVertValue() + value) {
            drawGridLine(lineValue, width);
            lineValue += value;
        }
    }

    private void drawGridLine(float lineValue, int width){
        Paint paint = gProps.getGridPaint();

        int yCoord = gProps.getGridYPos(lineValue);

        canvas.drawLine(gProps.getMarginLeft(), yCoord, width, yCoord, paint);
        Log.d("DRAWING", "Drawing line: " + gProps.getMarginLeft() + ", " + yCoord + ", "+ width + ", " + yCoord + ". Value: " + lineValue);
    }

    public void drawVerticalLabels(Canvas canvas){
        if (graphLines.size() < 2){
            return;
        }
        float value = (float)(gProps.getMaxVertValue() - gProps.getMinVertValue())/10;
        if (value == 0){
            value =  (float)gProps.getMaxVertValue();
        }
        float lineValue = gProps.getMinVertValue();

        while (lineValue <= gProps.getMaxVertValue() + value) {
            Log.d("DRAWING", "Vertical Label value: " + lineValue);
            int yCoord = gProps.getGridYPos(lineValue);
            String label = String.format("%.2f", lineValue);
            Paint paint = new Paint();
            paint.setTextSize(35);
            canvas.drawText(label, 0, yCoord+15, paint);

            lineValue += value;
        }
    }

    public void changeCanvasSize(View imageView){
        LinearLayout.LayoutParams viewLp =
                (LinearLayout.LayoutParams) imageView.getLayoutParams();
        viewLp.width = gProps.getGraphWidth(graphLines.size());
        imageView.setLayoutParams(viewLp);
        Log.d("DRAWINGSIZE", "Width: " + viewLp.width);
    }

    public void draw(Canvas canvas){

        if (graphLines.size()<2){
            canvas.drawColor(Color.TRANSPARENT);
            return;
        }

        this.canvas = canvas;
        gProps.setCanvas(canvas);

        canvas.drawColor(Color.TRANSPARENT);
        drawGrid();
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
                " " + (gl.getBarBotX()));
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