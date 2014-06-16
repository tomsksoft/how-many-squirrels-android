package com.howmuchof.squirrels.android;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.howmuchof.squirrels.common.graphview.GraphManager;
import com.howmuchof.squirrels.common.graphview.GraphProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by LinXi on 4/20/2014.
 */
public class GraphViewFragment extends Fragment implements View.OnClickListener{

    static final int DATE_PICKER = 0;
    static final int TIME_PICKER = 1;
    static final int DATE_SINCE = 0;
    static final int DATE_UNTIL = 1;

    DBHelper dbHelper;
    ImageView imageView;
    GraphManager graphManager;
    Context context;

    EditText dateSinceEdit;
    EditText timeSinceEdit;
    EditText dateUntilEdit;
    EditText timeUntilEdit;

    boolean pickerIsActive;
    int focusedField;
    Date dateSince;
    Date dateUntil;
    float mx;
    int maxX;
    int minX;
    int totalX;
    boolean redraw;
    int visibleWidth;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.graph_view_fragment, container, false);
        dbHelper = new DBHelper(getActivity());
        context = getActivity();

        dateSinceEdit = (EditText) view.findViewById(R.id.date_since_edit_text);
        timeSinceEdit = (EditText) view.findViewById(R.id.time_since_edit_text);
        dateUntilEdit = (EditText) view.findViewById(R.id.date_until_edit_text);
        timeUntilEdit = (EditText) view.findViewById(R.id.time_until_edit_text);
        dateSinceEdit.setOnFocusChangeListener(focusChangeListener);
        timeSinceEdit.setOnFocusChangeListener(focusChangeListener);
        dateUntilEdit.setOnFocusChangeListener(focusChangeListener);
        timeUntilEdit.setOnFocusChangeListener(focusChangeListener);

        dateSince = new Date();
        dateUntil = new Date();

        ImageView vertGraphLabels = (ImageView) view.findViewById(R.id.vert_graph_labels_image_view);
        vertGraphLabels.setImageDrawable(new VertLabelsGraphDrawable());
        imageView = (ImageView) view.findViewById(R.id.imageView);
        setOnTouchListener(imageView);
        initRange();
        return view;
    }

    public void onResume(){
        imageView.setImageDrawable(new GraphDrawable());
        initRange();
        showGraphics();
        totalX = minX;
        super.onResume();
    }

    private boolean checkDBData(){
        if (2 <= dbHelper.getRowCount()) {
            return true;
        }
        return false;
    }

    private void showGraphics(){
        if (checkDBData()){
            graphManager = new GraphManager(context);
            graphManager.getGraphProperties().setXFormat(GraphProperties.HOR_VALUES_DATE_FORMAT);
            initGraphValues();
        }
        else {
            graphManager = new GraphManager(context);
            Toast.makeText(getActivity(), R.string.graphViewPage_notEnoughData, Toast.LENGTH_LONG).show();
        }
        imageView.invalidate();
        redraw = true;
    }

    private void initGraphValues(){

        getGraphData(dbHelper.getDataFromDB(dateSince.getTime(), dateUntil.getTime()));
    }

    private void initRange(){
        Long dateValue = dbHelper.getDate(true);
        if (dateValue > -1) {
            dateSinceEdit.setText(formatDate(dateValue, DATE_PICKER));
            timeSinceEdit.setText(formatDate(dateValue, TIME_PICKER));
            dateSince.setTime(dateValue);
        }
        dateValue = dbHelper.getDate(false);
        if (dateValue > -1) {
            dateUntilEdit.setText(formatDate(dateValue, DATE_PICKER));
            timeUntilEdit.setText(formatDate(dateValue, TIME_PICKER));
            dateUntil.setTime(dateValue);
        }
    }

    private void getGraphData(List<Squirrel> squirrels){
        for(Squirrel squirrel: squirrels){
            graphManager.addValues(squirrel.getAmount(), squirrel.getDate());
        }
    }

    @Override
    public void onClick(View view) {

    }

    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View view, boolean gainFocus) {
            switch (view.getId()){
                case R.id.date_since_edit_text:{
                    if (gainFocus) {
                        if (!pickerIsActive) {
                            showPicker(DATE_PICKER, DATE_SINCE);
                        }
                        view.clearFocus();

                    }
                    break;
                }
                case R.id.time_since_edit_text:{
                    if (gainFocus) {
                        if (!pickerIsActive){
                            showPicker(TIME_PICKER, DATE_SINCE);
                        }
                        view.clearFocus();
                    }
                    break;
                }
                case R.id.date_until_edit_text:{
                    if (gainFocus) {
                        if (!pickerIsActive) {
                            showPicker(DATE_PICKER, DATE_UNTIL);
                        }
                        view.clearFocus();
                    }
                    break;
                }
                case R.id.time_until_edit_text:{
                    if (gainFocus) {
                        if (!pickerIsActive){
                            showPicker(TIME_PICKER, DATE_UNTIL);
                        }
                        view.clearFocus();
                    }
                    break;
                }
            }
        }
    };

    public void showPicker(int pickerID, int field) {
        pickerIsActive = true;
        focusedField = field;
        if (pickerID == TIME_PICKER){
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(), "timePickerGraph");
        }
        else if (pickerID == DATE_PICKER){
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePickerGraph");
        }
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = new GregorianCalendar();
            if (focusedField == DATE_SINCE) {
                c.setTime(dateSince);
            }
            else if (focusedField == DATE_UNTIL) {
                c.setTime(dateUntil);
            }
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    android.text.format.DateFormat.is24HourFormat(context));
        }

        public void onTimeSet(TimePicker view, int hour, int minute) {
            Calendar c = new GregorianCalendar();
            if (focusedField == DATE_SINCE) {
                c.setTime(dateSince);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE,minute);
                dateSince = c.getTime();
                timeSinceEdit.setText(formatDate(dateSince.getTime(), TIME_PICKER));
            }
            else if (focusedField == DATE_UNTIL) {
                c.setTime(dateUntil);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE,minute);
                dateUntil = c.getTime();
                timeUntilEdit.setText(formatDate(dateUntil.getTime(), TIME_PICKER));
            }
            showGraphics();
        }

        @Override
        public void onDismiss(DialogInterface dialog){
            pickerIsActive = false;
            super.onDismiss(dialog);
        }
    }

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = new GregorianCalendar();
            if (focusedField == DATE_SINCE) {
                c.setTime(dateSince);
            }
            else if (focusedField == DATE_UNTIL) {
                c.setTime(dateUntil);
            }
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = new GregorianCalendar();
            if (focusedField == DATE_SINCE) {
                c.setTime(dateSince);
                c.set(year, month, day);
                dateSince = c.getTime();
                dateSinceEdit.setText(formatDate(dateSince.getTime(), DATE_PICKER));
            }
            else if (focusedField == DATE_UNTIL) {
                c.setTime(dateUntil);
                c.set(year, month, day);
                dateUntil = c.getTime();
                dateUntilEdit.setText(formatDate(dateUntil.getTime(), DATE_PICKER));
            }
            showGraphics();
        }
        @Override
        public void onDismiss(DialogInterface dialog){
            pickerIsActive = false;
            super.onDismiss(dialog);
        }
    }

    public String formatDate(long dateValue, int requestType){
        Date date = new Date();
        date.setTime(dateValue);
        String result = "";
        DateFormat dateFormat;

        if (date != null){
            try {
                if (requestType == DATE_PICKER) {

                    String format = Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);
                    if (TextUtils.isEmpty(format)) {
                        dateFormat = android.text.format.DateFormat.getDateFormat(context);
                    } else {
                        dateFormat = new SimpleDateFormat(format);
                    }
                    result = dateFormat.format(date);
                }
                else if (requestType == TIME_PICKER) {
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


    private class GraphDrawable extends Drawable {

        @Override
        public void draw(Canvas canvas) {
            Log.d("DRAWING", "Got inside of draw method");
            if (redraw){
                if (visibleWidth ==0) {
                    visibleWidth = canvas.getWidth();
                }
                graphManager.changeCanvasSize(canvas, imageView);
                maxX = graphManager.getGraphProperties().getGraphWidth(graphManager.getSize()) - visibleWidth;
                if (maxX < 0){
                    maxX = 0;
                }
                minX = imageView.getScrollX();
                totalX = minX;
                redraw = false;
                Log.d("DRAWINGSIZE", "MaxX = " + maxX + " MinX = " + minX);
            }
            graphManager.draw(canvas);
        }

        @Override
        public int getOpacity() {return 0;}

        @Override
        public void setAlpha(int alpha) {}

        @Override
        public void setColorFilter(ColorFilter cf) {}

    }

    private class VertLabelsGraphDrawable extends Drawable {

        @Override
        public void draw(Canvas canvas) {
            graphManager.getGraphProperties().setCanvas(canvas);
            graphManager.drawVerticalLabels(canvas);
        }

        @Override
        public int getOpacity() {return 0;}

        @Override
        public void setAlpha(int alpha) {}

        @Override
        public void setColorFilter(ColorFilter cf) {}

    }

    private void setOnTouchListener(View view){
        view.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                float curX;
                int scrollByX;

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mx = event.getX();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        curX = event.getX();
                        scrollByX = (int) (mx - curX);
                        if (curX > mx){
                            if ((totalX + scrollByX) <= 0){
                                scrollByX = 0 - totalX;
                                totalX = 0;
                            }
                            else {
                                totalX += scrollByX;
                            }
                        }
                        else if (curX < mx){
                            if ((totalX + scrollByX) >= maxX){
                                scrollByX = maxX - totalX;
                                totalX = maxX;
                            }
                            else {
                                totalX += scrollByX;
                            }
                        }

                        imageView.scrollBy(scrollByX, 0);
                        mx = curX;
                        break;
                }
                return true;
            }
        });
    }

}
