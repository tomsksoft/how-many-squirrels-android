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

import com.howmuchof.squirrels.android.DataType;
import com.howmuchof.squirrels.android.Duration;
import com.howmuchof.squirrels.android.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraphManager {

    private final static int FORMAT_DATE = 0;
    private final static int FORMAT_TIME = 1;

    View imageView;
    Canvas canvas;
    Context context;
    GraphProperties gProps;
    DataType datatype;
    String[] enumValues;

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

    public void setDataType(DataType datatype){
        this.datatype = datatype;
        gProps.setDataType(datatype.getType());
    }

    public GraphProperties getGraphProperties(){
        return gProps;
    }

    public int addValues(double verValue, long horValue){
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

        double value = getGridLineValue();
        double lineValue = gProps.getMinVertValue();
        int width = gProps.getGraphWidth(graphLines.size());

        if (value == 0){
            value =  gProps.getMaxVertValue();
        }
        double maxValue;
        if (datatype.getType() == 2) {
            maxValue = enumValues.length - 1;
        }
        else {
            maxValue = gProps.getMaxVertValue() + value;
        }

        while (lineValue <= maxValue) {
            drawGridLine(lineValue, width);
            lineValue += value;
        }
    }

    private void drawGridLine(double lineValue, int width){
        Paint paint = gProps.getGridPaint();

        int yCoord = gProps.getGridYPos(lineValue);

        canvas.drawLine(gProps.getMarginLeft(), yCoord, width, yCoord, paint);
        Log.d("DRAWING", "Drawing line: " + gProps.getMarginLeft() + ", " + yCoord + ", "+ width + ", " + yCoord + ". Value: " + lineValue);
    }

    private double getGridLineValue(){
        switch(datatype.getType()){
            case 0:
                if (graphLines.size() < 10){
                    if ((gProps.getMaxVertValue() - gProps.getMinVertValue())<graphLines.size()){
                        return ((gProps.getMaxVertValue() - gProps.getMinVertValue())/(gProps.getMaxVertValue() - gProps.getMinVertValue()));
                    }
                    return (gProps.getMaxVertValue() - gProps.getMinVertValue())/graphLines.size();
                }
                return (gProps.getMaxVertValue() - gProps.getMinVertValue())/gProps.getVertLabelsAmount(graphLines.size());
            case 1:
                return (gProps.getMaxVertValue() - gProps.getMinVertValue())/gProps.getVertLabelsAmount(graphLines.size());
            case 2:
                return 1;
            case 3:
                return (gProps.getMaxVertValue() - gProps.getMinVertValue())/gProps.getVertLabelsAmount(graphLines.size());
            case 4:
                return (gProps.getMaxVertValue() - gProps.getMinVertValue())/gProps.getVertLabelsAmount(graphLines.size());
            default:
                return 0;
        }
    }

    public void drawVerticalLabels(Canvas canvas){
        if (graphLines.size() < 2){
            return;
        }
        double lineValue = gProps.getMinVertValue();
        double maxValue;
        double value = getGridLineValue();
        if (value == 0){
            value =  gProps.getMaxVertValue();
        }

        Paint paint = new Paint();
        paint.setTextSize(35);

        switch(datatype.getType()){
            case 2:
                enumValues = datatype.getDesctiption().split(",");
                gProps.setMinVertValue(0);
                gProps.setMaxVertValue(enumValues.length - 1);
                maxValue = gProps.getMaxVertValue();
                break;
            case 3:
                maxValue = gProps.getMaxVertValue() + value;
                paint.setTextSize(28);
                break;
            case 4:
                gProps.setMinVertValue(-1000*60*15);
                maxValue = gProps.getMaxVertValue();
                break;
            default:
                maxValue = gProps.getMaxVertValue() + value;
                break;
        }

        while (lineValue <= maxValue) {
            Log.d("DRAWING", "Vertical Label value: " + lineValue);
            int yCoord = gProps.getGridYPos(lineValue);
            if (datatype.getType() == 3){
                canvas.drawText(formatDate(new Date((long)lineValue),FORMAT_DATE), 0, yCoord - 7, paint);
                canvas.drawText(formatDate(new Date((long)lineValue),FORMAT_TIME), 0, yCoord + 22, paint);
            }
            else {
                canvas.drawText(getVerticalLabel(lineValue), 0, yCoord + 15, paint);
            }
            lineValue += value;
        }
    }

    private String getVerticalLabel(double value){
        switch(datatype.getType()){
            case 0:
                return String.format("%.1f", value);
            case 1:
                return String.valueOf(value);
            case 2:
                if ((int)value == -1){
                    return "";
                }
                return enumValues[(int)value];
            case 4:
                if (value < 0){
                    return "";
                }
                return formatDuration(new Duration((long)value));
            default:
                return String.valueOf(value);
        }
    }

    public void changeLayoutSize(View imageView){
        LinearLayout.LayoutParams viewLp =
                (LinearLayout.LayoutParams) imageView.getLayoutParams();
        viewLp.width = gProps.getGraphWidth(graphLines.size());
        imageView.setLayoutParams(viewLp);
        Log.d("DRAWINGSIZE", "Width: " + viewLp.width);
    }

    public void draw(Canvas canvas){

        if (graphLines.size()<2){
            Log.d("DRAWING", "Graph bars: " + graphLines.size());
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

    private void drawGraph(double vertValue, long horzValue, int curGraph){

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
        public double yValue;
        public long xValue;

        public GraphValues(double yValue, long xValue){
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

    private String formatDuration(Duration duration){
        String value = "";

        if (duration.getDays() > 0){
            value += String.valueOf(duration.getDays());
            value += context.getResources().getString(R.string.dimension_days);
            value += " ";
        }
        if (duration.getHours() > 0){
            value += String.valueOf(duration.getHours());
            value += context.getResources().getString(R.string.dimension_hours);
            value += " ";
        }
        if (duration.getMinutes() > 0){
            value += String.valueOf(duration.getMinutes());
            value += context.getResources().getString(R.string.dimension_minutes);
            value += " ";
        }
        if ("".equals(value)){
            value = "0" + context.getResources().getString(R.string.dimension_minutes);
        }
        return value;
    }
}