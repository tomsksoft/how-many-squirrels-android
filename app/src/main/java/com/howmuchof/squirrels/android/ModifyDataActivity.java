package com.howmuchof.squirrels.android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by LinXi on 4/17/2014.
 */
public class ModifyDataActivity extends Activity implements View.OnClickListener{

    static final int DATE_PICKER_ID = 0;
    static final int TIME_PICKER_ID = 1;

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    EditText dateEdit;
    EditText timeEdit;
    EditText amountEdit;

    DBHelper dbHelper;
    int id;
    boolean pickerIsActive;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_data);
        Intent intent = getIntent();
        Bundle extra=getIntent().getExtras();

        if (extra == null){
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        id = intent.getIntExtra("id", -1);
        long date = intent.getLongExtra("date", 0);
        int amount = intent.getIntExtra("amount", 0);

        dateEdit = (EditText) findViewById(R.id.date_edit);
        timeEdit = (EditText) findViewById(R.id.time_edit);
        amountEdit = (EditText) findViewById(R.id.amount_edit);

        dateEdit.setOnFocusChangeListener(focusChangeListener);
        timeEdit.setOnFocusChangeListener(focusChangeListener);

        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(date);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        dateEdit.setText(getFormattedDayValue(day) + "-" + getFormattedMonthValue(month) + "-" + year);
        timeEdit.setText(hour + ":" + getFormattedMinuteValue(minute));

        amountEdit.setText(String.valueOf(amount));
        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancel_button:{
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            }
            case R.id.update_button: {
                updateDBLine();
                break;
            }
            case R.id.set_current_time:{
                setCurrentTime();
                break;
            }
        }
    }

    private void updateDBLine(){
        if (amountEdit.getText().toString().length() == 0) {
            Toast.makeText(this,R.string.dataPage_errInputAmount, Toast.LENGTH_LONG).show();
            return;
        }
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Calendar calendar = new GregorianCalendar();
        Date date = new Date();
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            date = dateFormat.parse(dateEdit.getText().toString() +" " + timeEdit.getText().toString());
        }
        catch(ParseException pe){
            Log.d("APP_ERROR", "Can't parse a date" + dateEdit.getText().toString() + " " + timeEdit.getText().toString());
        }
        calendar.setTime(date);

        cv.put("amount", Integer.parseInt(amountEdit.getText().toString()));
        cv.put("date", calendar.getTimeInMillis());

        int updateResult = db.update("objects", cv, "id = ?", new String[]{String.valueOf(id)});
        if (updateResult > 0){
            dbHelper.close();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            Toast.makeText(this,R.string.dataPage_successUpdateLine, Toast.LENGTH_SHORT).show();
            finish();
        }
        else
            Toast.makeText(this, R.string.dataPage_failUpdateLine, Toast.LENGTH_SHORT).show();

    }

    private void setCurrentTime(){
        Calendar c = GregorianCalendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        String date;
        String time;

        if (minute < 10){
            time = hour + ":" + "0" + minute;
        }
        else{
            time = hour + ":" + minute;
        }

        if (month < 10){
            date = day + "-" + "0" + month + "-" + year;
        }
        else{
            date = day + "-" + month + "-" + year;
        }

        dateEdit.setText(date);
        timeEdit.setText(time);
    }

    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View view, boolean gainFocus) {
            switch (view.getId()){
                case R.id.date_edit:{
                    if (gainFocus) {
                        if (!pickerIsActive) {
                            showPicker(DATE_PICKER_ID);
                        }
                        view.clearFocus();
                    }
                    break;
                }
                case R.id.time_edit:{
                    if (gainFocus) {
                        if (!pickerIsActive){
                            showPicker(TIME_PICKER_ID);
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

        if (pickerID == TIME_PICKER_ID){
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(), "timePicker");
        }
        else if (pickerID == DATE_PICKER_ID){
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePicker");
        }
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            Calendar c = new GregorianCalendar();
            Date date = new Date();
            try{
                date = dateFormat.parse(dateEdit.getText().toString() +" " + timeEdit.getText().toString());
            }
            catch(ParseException pe){
                Log.d("APP_ERROR", "Can't parse a date" + dateEdit.getText().toString() + " " + timeEdit.getText().toString());
            }
            c.setTime(date);

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hour, int minute) {
            timeEdit.setText(hour + ":" + getFormattedMinuteValue(minute));
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
            Calendar c = new GregorianCalendar();
            Date date = new Date();
            try{
                date = dateFormat.parse(dateEdit.getText().toString() +" " + timeEdit.getText().toString());
            }
            catch(ParseException pe){
                Log.d("APP_ERROR", "Can't parse a date" + dateEdit.getText().toString() + " " + timeEdit.getText().toString());
            }
            c.setTime(date);

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            dateEdit.setText(getFormattedDayValue(day) + "-" + getFormattedMonthValue(month) + "-" + year);
        }

        @Override
        public void onDismiss(DialogInterface dialog){
            pickerIsActive = false;
        }
    }

    private String getFormattedMonthValue(int month){
        month++;
        if (month < 10) {
            return "0" + month;
        }
        else {
            return String.valueOf(month);
        }
    }

    private String getFormattedMinuteValue(int minute){

        if (minute < 10) {
            return "0" + minute;
        }
        else {
            return String.valueOf(minute);
        }
    }

    private String getFormattedDayValue(int day){

        if (day < 10) {
            return "0" + day;
        }
        else {
            return String.valueOf(day);
        }
    }
}
