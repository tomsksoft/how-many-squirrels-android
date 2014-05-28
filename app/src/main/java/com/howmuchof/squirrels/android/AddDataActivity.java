package com.howmuchof.squirrels.android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by LinXi on 4/13/2014.
 */
public class AddDataActivity extends Activity implements View.OnClickListener{

    static final int DATE_PICKER = 0;
    static final int TIME_PICKER = 1;

    EditText dateEdit;
    EditText timeEdit;
    EditText amountEdit;
    Button minimizeBtn;
    Button runGraphViewBtn;
    RadioButton selectTimeRb;
    RadioButton currentTimeRb;
    DBHelper dbHelper;
    boolean pickerIsActive;
    Context context;
    Date date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        context = this;
        dateEdit = (EditText) findViewById(R.id.date_edit);
        timeEdit = (EditText) findViewById(R.id.time_edit);
        amountEdit = (EditText) findViewById(R.id.amount_edit);
        minimizeBtn = (Button) findViewById(R.id.add_and_minimize);
        runGraphViewBtn = (Button) findViewById(R.id.add_and_go_to_graph_view);
        selectTimeRb = (RadioButton) findViewById(R.id.select_time);
        currentTimeRb = (RadioButton) findViewById(R.id.current_time);

        selectTimeRb.setOnClickListener(this);
        currentTimeRb.setOnClickListener(this);
        dateEdit.setOnFocusChangeListener(focusChangeListener);
        timeEdit.setOnFocusChangeListener(focusChangeListener);
        minimizeBtn.setOnClickListener(this);
        runGraphViewBtn.setOnClickListener(this);

        date = GregorianCalendar.getInstance().getTime();
        dateEdit.setText(formatDate(date, DATE_PICKER));
        timeEdit.setText(formatDate(date, TIME_PICKER));

        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_and_minimize:{
                addDataAndMinimize();
                break;
            }
            case R.id.add_and_go_to_graph_view:{
                addDataAndGoToGraphView();
                break;
            }
            case R.id.current_time: {
                timeEdit.setEnabled(false);
                dateEdit.setEnabled(false);
                break;
            }
            case R.id.select_time:{
                timeEdit.setEnabled(true);
                dateEdit.setEnabled(true);
                break;
            }
        }
    }

    private long createDBLine(){
        if (amountEdit.getText().toString().length() == 0) {
            Toast.makeText(this,R.string.dataPage_errInputAmount, Toast.LENGTH_SHORT).show();
            return -1;
        }
        ContentValues cv = new ContentValues();

        cv.put("amount", Integer.parseInt(amountEdit.getText().toString()));
        cv.put("date", date.getTime());

        return dbHelper.getWritableDatabase().insert("objects", null, cv);
    }

    private void addDataAndMinimize(){
        if (createDBLine() != -1) {
            dbHelper.close();
            Toast.makeText(this, R.string.dataPage_successAddLine, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("gotographview", false);
            setResult(RESULT_OK, intent);
            finish();
        }
        else {
            Toast.makeText(this, R.string.dataPage_failAddLine, Toast.LENGTH_SHORT).show();
        }
    }

    private void addDataAndGoToGraphView(){
        if (createDBLine() != -1) {
            dbHelper.close();
            Toast.makeText(this, R.string.dataPage_successAddLine, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("gotographview", true);
            setResult(RESULT_OK, intent);
            finish();
        }
        else {
            Toast.makeText(this, R.string.dataPage_failAddLine, Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View view, boolean gainFocus) {
            switch (view.getId()){
                case R.id.date_edit:{
                    if (gainFocus) {
                        if (!pickerIsActive) {
                            showPicker(DATE_PICKER);
                        }
                        view.clearFocus();
                    }
                    break;
                }
                case R.id.time_edit:{
                    if (gainFocus) {
                        if (!pickerIsActive){
                            showPicker(TIME_PICKER);
                        }
                        view.clearFocus();
                    }
                    break;
                }
            }
        }
    };

    public void showPicker(int pickerID) {
        pickerIsActive = true;

        if (pickerID == TIME_PICKER){
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(), "timePicker");
        }
        else if (pickerID == DATE_PICKER){
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePicker");
        }
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = new GregorianCalendar();
            c.setTime(date);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    android.text.format.DateFormat.is24HourFormat(context));
        }

        public void onTimeSet(TimePicker view, int hour, int minute) {
            Calendar c = new GregorianCalendar();
            c.setTime(date);
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE,minute);
            date = c.getTime();

            timeEdit.setText(formatDate(date, TIME_PICKER));
        }

        @Override
        public void onDismiss(DialogInterface dialog){
            pickerIsActive = false;
        }
    }

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = new GregorianCalendar();
            c.setTime(date);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = new GregorianCalendar();
            c.setTime(date);
            c.set(year, month, day);
            date = c.getTime();
            dateEdit.setText(formatDate(date, DATE_PICKER));
        }
        @Override
        public void onDismiss(DialogInterface dialog){
            pickerIsActive = false;
        }
    }

    public String formatDate(Date date, int requestType){
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
}
