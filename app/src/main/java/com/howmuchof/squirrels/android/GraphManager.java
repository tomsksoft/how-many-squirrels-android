package com.howmuchof.squirrels.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.howmuchof.squirrels.android.Squirrel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GraphManager {


    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yy");
    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    View imageView;
    Canvas canvas;
    Canvas gridLabelsCanvas;
    Bitmap gridLabelsBitmap;
    float mx;
//    int offset;

    GraphProperties gProps;

    ArrayList<GraphValues> graphLines = new ArrayList<GraphValues>();

    public GraphManager(){
        gProps = new GraphProperties();
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

        while (lineValue < gProps.getMaxVertValue() + value) {
            drawGridLine(canvas, lineValue);
            lineValue += value;
            drawVerticalLabel(canvas, lineValue);
        }
    }

    private void drawGridLine(Canvas canvas, float lineValue ){
        Paint paint = gProps.getGridPaint();

        int yCoord = gProps.getGridYPos(lineValue);
        int width = graphLines.size() * (gProps.getGraphIndent() + gProps.getColumnWidth());
        canvas.drawLine(gProps.getMarginLeft(), yCoord, width, yCoord, paint);
        Log.d("DRAWING", "Drawing line: " + gProps.getMarginLeft() + ", " + yCoord + ", "+ width + ", " + yCoord + ". Value: " + lineValue);


    }

    private void drawVerticalLabel(Canvas canvas, float value){
        int yCoord = gProps.getGridYPos(value);
        String label = String.format("%.2f", value);
        Paint paint = new Paint();
        paint.setTextSize(30);
        canvas.drawText(label, 10, yCoord+10, paint);
    }

    public void draw(Canvas canvas, View view){
        this.canvas = canvas;
        gProps.setCanvas(canvas);
        imageView = view;

        int height = canvas.getHeight();
        setOnTouchListener(view);
        //offset = 0;

        FrameLayout.LayoutParams viewLp =
                (FrameLayout.LayoutParams) imageView.getLayoutParams();
        int viewMarginTop = viewLp.topMargin;
        int viewMarginBottom = viewLp.bottomMargin;
        int viewMarginRight = viewLp.rightMargin;
        int viewMarginLeft = viewLp.leftMargin;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(gProps.getGraphWidth(graphLines.size()) +
                gProps.getMarginColumnLeft() + gProps.getMarginLeft() + viewMarginLeft + viewMarginRight,
                height + viewMarginBottom + viewMarginTop + 1000);
        lp.setMargins(viewMarginLeft,viewMarginTop,viewMarginRight,viewMarginBottom);
        view.setLayoutParams(lp);

        drawGrid(canvas);
        for (int i = 0; i < graphLines.size(); i++){
            drawGraph(graphLines.get(i).yValue, graphLines.get(i).xValue, i);
        }


        gridLabelsBitmap = Bitmap.createBitmap(gProps.getMarginColumnLeft() + gProps.getMarginLeft(),
                gProps.getHeight(), Bitmap.Config.ARGB_8888);
        gridLabelsCanvas = new Canvas(gridLabelsBitmap);
        //drawGridBitmap();
    }

    private void drawGridBitmap(){
        gridLabelsCanvas.drawColor(Color.WHITE);
        drawGrid(gridLabelsCanvas);
/*        for (int i = 0; i < graphLines.size(); i++){
            drawGraph(graphLines.get(i).yValue, graphLines.get(i).xValue, i);
        }*/
    }

    private void drawGraph(int vertValue, long horzValue, int curGraph){

        Paint barPaint = gProps.getBarPaint();
        Paint bar3DPaint = gProps.getBar3DPaint();
        GraphLine gl = gProps.getGraphBarCoordinates(vertValue, curGraph);

        canvas.drawRect(gl.getBarTopX(), gl.getBarTopY(), gl.getBarBotX(), gl.getBarBotY(), barPaint);

        drawHorizontalLabel(horzValue, gl.getBarTopX()+gProps.getColumnWidth()/2);

        canvas.drawLine(gl.getBarTopX() + 3, gl.getBarTopY() - 3, gl.getBarBotX() + 3, gl.getBarTopY() - 3, bar3DPaint);
        canvas.drawLine(gl.getBarBotX()+3, gl.getBarTopY()-3, gl.getBarBotX() + 3,
                gProps.getHeight() - gProps.getTopBottomIndent(), bar3DPaint);

        Log.d("DRAWING", "Drawing rect "+ curGraph + ": " + gl.getBarTopX() + " " + gl.getBarTopY() +
                " " + (gl.getBarBotX()) );//+ ". Offset: " + offset);
    }

    private void drawHorizontalLabel(Long value, int xPos){
        Paint paint = gProps.getHorzLabelPaint();

        if (gProps.getXFormat() == GraphProperties.HOR_VALUES_DATE_FORMAT){
            Date d = new Date(value);
            canvas.drawText(timeFormat.format(d),xPos, gProps.getHeight()-gProps.getTopBottomIndent()/2,paint);
            canvas.drawText(dateFormat.format(d),xPos, gProps.getHeight()-gProps.getTopBottomIndent()/4+5,paint);
        }
        else {
            canvas.drawText(String.valueOf(value),xPos, gProps.getHeight()-gProps.getTopBottomIndent()/3,paint);
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
/*                    offset -= mx - curX;
                    if (offset > 0) {
                        offset = 0;
                    }
                    else if (offset < gProps.getGraphWidth()){
                        offset = gProps.getGraphWidth();
                    }
                    if (offset%30 == 0) {
                       drawGraphBitmap();
                    }*/

                    imageView.scrollBy((int) (mx - curX), 0);
                    mx = curX;
                    canvas.drawBitmap(gridLabelsBitmap, 0, 0, null);
                    imageView.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                   curX = event.getX();
                    /*offset += mx - curX;
                    if (offset%10 == 0) {
                        drawGraphBitmap();
                    }*/
                    imageView.scrollBy((int) (mx - curX), 0);
                    break;
            }
            return true;
            }
        });
    }

    protected class GraphValues{
        public int yValue;
        public long xValue;

        public GraphValues(int yValue, long xValue){
            this.xValue = xValue;
            this.yValue = yValue;
        }
    }
}