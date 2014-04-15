package com.howmuchof.squirrels.android;

        import android.app.Activity;
        import android.app.DatePickerDialog;
        import android.app.Dialog;
        import android.app.DialogFragment;
        import android.app.TimePickerDialog;
        import android.content.ContentValues;
        import android.content.Intent;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.text.format.DateFormat;
        import android.view.View;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.RadioButton;
        import android.widget.TimePicker;
        import android.widget.Toast;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.GregorianCalendar;

/**
 * Created by LinXi on 4/13/2014.
 */
public class AddDataActivity extends Activity implements View.OnClickListener{

    EditText editDate;
    EditText editTime;
    EditText editAmount;
    Button btnMinimize;
    RadioButton chBtnSelectTime;
    RadioButton chBtnCurrentTime;
    DBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        editDate = (EditText) findViewById(R.id.editDate);
        editTime = (EditText) findViewById(R.id.editTime);
        editAmount = (EditText) findViewById(R.id.editAmount);
        btnMinimize = (Button) findViewById(R.id.add_line);
        chBtnSelectTime = (RadioButton) findViewById(R.id.select_time);
        chBtnCurrentTime = (RadioButton) findViewById(R.id.current_time);

        chBtnSelectTime.setOnClickListener(this);
        chBtnCurrentTime.setOnClickListener(this);
        editDate.setOnFocusChangeListener(focusChangeListener);
        editTime.setOnFocusChangeListener(focusChangeListener);
        btnMinimize.setOnClickListener(this);

        final Calendar c = GregorianCalendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        editDate.setText(day + "-" + (++month) + "-" + year);
        editTime.setText(hour + ":" + minute);

        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_line:{
                if (createDBLine()){
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            }
            case R.id.current_time: {
                editTime.setEnabled(false);
                break;
            }
            case R.id.select_time:{
                editTime.setEnabled(true);
                break;
            }
        }
    }

    private boolean createDBLine(){


        if (editAmount.getText().toString().length() == 0) {
            Toast.makeText(this,R.string.listViewPage_errInputAmount, Toast.LENGTH_LONG).show();
            return false;
        }
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Calendar calendar = new GregorianCalendar();
        Date date = new Date();
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            date = dateFormat.parse(editDate.getText().toString() +" " + editTime.getText().toString());
        }catch(ParseException pe){}
        calendar.setTime(date);

        cv.put("amount", Integer.parseInt(editAmount.getText().toString()));
        cv.put("date", calendar.getTimeInMillis());

        if (db.insert("objects", null, cv) != -1){
            dbHelper.close();
            Toast.makeText(this,R.string.listViewPage_successAddLine, Toast.LENGTH_LONG).show();
            return true;
        }
        else
            return false;


    }

    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View view, boolean gainFocus) {
            switch (view.getId()){
                case R.id.editDate:{
                    if (gainFocus) {
                        showDatePickerDialog();
                        view.clearFocus();
                    }
                    break;
                }
                case R.id.editTime:{
                    if (gainFocus) {
                        showTimePickerDialog();
                        view.clearFocus();
                    }
                    break;
                }
            }
        };
    };

    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    };

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    };

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = GregorianCalendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            editTime.setText(hourOfDay + ":" + minute);
        }
    }

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = GregorianCalendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            editDate.setText(day + "-" + (++month) + "-" + year);
        }
    }
}
