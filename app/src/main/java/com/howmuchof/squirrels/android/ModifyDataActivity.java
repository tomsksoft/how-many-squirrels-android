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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by LinXi on 4/17/2014.
 */
public class ModifyDataActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    static final int DATE_MAIN = 0;
    static final int DATE_SINCE = 1;
    static final int DATE_UNTIL = 2;
    static final int DATE_PICKER = 0;
    static final int TIME_PICKER = 1;

    EditText dateEdit;
    EditText timeEdit;
    EditText amountEdit;
    EditText amountDateEdit;
    EditText amountTimeEdit;
    //EditText amountDateEditUntil;
    EditText amountTimeEditUntil;
    Spinner spinner;

    Date date;
    Date dateSince;
    Date dateUntil;
    DataType datatype;
    String[] values;
    int spinnerValue;
    int focusedDateField;

    DBHelper dbHelper;
    int id;
    boolean pickerIsActive;
    Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_data);
        context = this;
        Intent intent = getIntent();
        Bundle extra=getIntent().getExtras();

        if (extra == null){
            setResult(RESULT_CANCELED, intent);
            finish();
        }

        id = intent.getIntExtra("id", -1);

        dateEdit = (EditText) findViewById(R.id.date_edit);
        timeEdit = (EditText) findViewById(R.id.time_edit);
        amountEdit = (EditText) findViewById(R.id.amount_edit);

        dateEdit.setOnFocusChangeListener(focusChangeListener);
        timeEdit.setOnFocusChangeListener(focusChangeListener);


        dbHelper = new DBHelper(this);
        Squirrel s = dbHelper.getObject(id);
        datatype = dbHelper.getType(s.getType());
        date = new Date(s.getDate());
        amountFieldInit(s);
    }

    private void amountFieldInit(Squirrel s){

        dateEdit.setText(formatDate(date, DATE_PICKER));
        timeEdit.setText(formatDate(date, TIME_PICKER));

        switch(datatype.getType()){
            case 0:
                amountEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                amountEdit.setText(String.valueOf((int)s.getAmount()));
                break;
            case 1:
                amountEdit.setText(String.valueOf(s.getAmount()));
                break;
            case 2:
                values = datatype.getDesctiption().split(",");
                spinner = (Spinner) findViewById(R.id.spinner);
                amountEdit.setVisibility(View.GONE);
                initSpinner(s);
                break;
            case 3:
                amountDateEdit = (EditText) findViewById(R.id.amount_date_edit);
                amountTimeEdit = (EditText) findViewById(R.id.amount_time_edit);
                amountDateEdit.setOnFocusChangeListener(focusChangeListener);
                amountDateEdit.setVisibility(View.VISIBLE);
                amountTimeEdit.setOnFocusChangeListener(focusChangeListener);
                LinearLayout ll = (LinearLayout) findViewById(R.id.layout_date_since);
                ll.setVisibility(View.VISIBLE);
                TextView textView = (TextView) findViewById(R.id.since_text_view);
                textView.setVisibility(View.INVISIBLE);
                amountEdit.setVisibility(View.GONE);
                dateSince = new Date((long)s.getAmount());
                amountDateEdit.setText(formatDate(dateSince, DATE_PICKER));
                amountTimeEdit.setText(formatDate(dateSince, TIME_PICKER));
                break;
            case 4:
                //amountDateEdit = (EditText) findViewById(R.id.amount_date_edit);
                amountTimeEdit = (EditText) findViewById(R.id.amount_time_edit);
                //amountDateEditUntil = (EditText) findViewById(R.id.amount_date_edit_until);
                amountTimeEditUntil = (EditText) findViewById(R.id.amount_time_edit_until);
                //amountDateEdit.setOnFocusChangeListener(focusChangeListener);
                amountTimeEdit.setOnFocusChangeListener(focusChangeListener);
                //amountDateEditUntil.setOnFocusChangeListener(focusChangeListener);
                amountTimeEditUntil.setOnFocusChangeListener(focusChangeListener);
                ll = (LinearLayout) findViewById(R.id.layout_date_since);
                ll.setVisibility(View.VISIBLE);
                ll = (LinearLayout) findViewById(R.id.layout_date_until);
                ll.setVisibility(View.VISIBLE);
                dateSince = new Date((long)s.getAmount());
                //amountDateEdit.setText(formatDate(dateSince, DATE_PICKER));
                amountTimeEdit.setText(formatDate(dateSince, TIME_PICKER));
                dateUntil = new Date((long)s.getSecAmount());
                //amountDateEditUntil.setText(formatDate(dateUntil, DATE_PICKER));
                amountTimeEditUntil.setText(formatDate(dateUntil, TIME_PICKER));
                amountEdit.setVisibility(View.GONE);
                break;
        }
    }

    private void initSpinner(Squirrel s){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);
        spinner.setAdapter(adapter);
        spinner.setPrompt(getResources().getString(R.string.settingsPage_chooseType));
        spinner.setSelection((int)s.getAmount());
        spinner.setOnItemSelectedListener(this);
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
        double amount = getAmount();
        if ((amount == 0) && (datatype.getType() != 2)){
            Toast.makeText(this,R.string.dataPage_errInputAmount, Toast.LENGTH_LONG).show();
            return;
        }
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        cv.put("amount", amount);
        if (datatype.getType() == 4) {
            cv.put("sec_amount", dateUntil.getTime());
        }
        cv.put("date", date.getTime());

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

    private double getAmount(){
        Double amount = 0.0;
        switch(datatype.getType()){
            case 0:
                amount = (double) Integer.parseInt(amountEdit.getText().toString());
                break;
            case 1:
                amount = Double.parseDouble(amountEdit.getText().toString());
                break;
            case 2:
                amount = (double) spinnerValue;
                break;
            case 3:
                amount = (double)dateSince.getTime();
                break;
            case 4:
                amount = (double) dateSince.getTime();
                break;
        }
        return amount;
    }

    private void setCurrentTime(){
        date = GregorianCalendar.getInstance().getTime();
        dateEdit.setText(formatDate(date, DATE_PICKER));
        timeEdit.setText(formatDate(date, TIME_PICKER));
    }

    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View view, boolean gainFocus) {
            switch (view.getId()){
                case R.id.date_edit:{
                    if (gainFocus) {
                        if (!pickerIsActive) {
                            showPicker(DATE_PICKER, DATE_MAIN);
                        }
                        view.clearFocus();
                    }
                    break;
                }
                case R.id.time_edit:{
                    if (gainFocus) {
                        if (!pickerIsActive){
                            showPicker(TIME_PICKER, DATE_MAIN);
                        }
                        view.clearFocus();
                    }
                    break;
                }
                case R.id.amount_date_edit:{
                    if (gainFocus) {
                        if (!pickerIsActive) {
                            showPicker(DATE_PICKER, DATE_SINCE);
                        }
                        view.clearFocus();
                    }
                }
                break;
                case R.id.amount_time_edit:{
                    if (gainFocus) {
                        if (!pickerIsActive){
                            showPicker(TIME_PICKER, DATE_SINCE);
                        }
                        view.clearFocus();
                    }
                    break;
                }
                /*case R.id.amount_date_edit_until:{
                    if (gainFocus) {
                        if (!pickerIsActive) {
                            showPicker(DATE_PICKER, DATE_UNTIL);
                        }
                        view.clearFocus();
                    }
                    break;
                }*/
                case R.id.amount_time_edit_until:{
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


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spinnerValue = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void showPicker(int pickerID, int fieldID) {
        focusedDateField = fieldID;
        pickerIsActive = true;

        if (pickerID == TIME_PICKER){
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(), "timePickerModify");
        }
        else if (pickerID == DATE_PICKER){
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePickerModify");
        }
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = new GregorianCalendar();
            if (focusedDateField == DATE_MAIN) {
                c.setTime(date);
            }
            else if(focusedDateField == DATE_SINCE){
                c.setTime(dateSince);
            }
            else if(focusedDateField == DATE_UNTIL){
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

            if (focusedDateField == DATE_MAIN){
                c.setTime(date);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE,minute);
                date = c.getTime();
                timeEdit.setText(formatDate(date, TIME_PICKER));
            }
            else if (focusedDateField == DATE_SINCE){
                c.setTime(dateSince);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE,minute);
                dateSince = c.getTime();
                amountTimeEdit.setText(formatDate(dateSince, TIME_PICKER));
            }
            else if (focusedDateField == DATE_UNTIL){
                c.setTime(dateUntil);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE,minute);
                dateUntil = c.getTime();
                amountTimeEditUntil.setText(formatDate(dateUntil, TIME_PICKER));
            }
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
            if (focusedDateField == DATE_MAIN) {
                c.setTime(date);
            }
            else if(focusedDateField == DATE_SINCE){
                c.setTime(dateSince);
            }
            else if(focusedDateField == DATE_UNTIL){
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
            if (focusedDateField == DATE_MAIN){
                c.setTime(date);
                c.set(year, month, day);
                date = c.getTime();
                dateEdit.setText(formatDate(date, DATE_PICKER));
            }
            else if (focusedDateField == DATE_SINCE){
                c.setTime(dateSince);
                c.set(year, month, day);
                dateSince = c.getTime();
                amountDateEdit.setText(formatDate(dateSince, DATE_PICKER));
            }
            /*else if (focusedDateField == DATE_UNTIL){
                c.setTime(dateUntil);
                c.set(year, month, day);
                dateUntil = c.getTime();
                amountDateEditUntil.setText(formatDate(dateUntil, DATE_PICKER));
            }*/
        }
        @Override
        public void onDismiss(DialogInterface dialog){
            pickerIsActive = false;
            super.onDismiss(dialog);
        }
    }

    public String formatDate(Date date, int requestType){
        String result = "";
        java.text.DateFormat dateFormat;

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
