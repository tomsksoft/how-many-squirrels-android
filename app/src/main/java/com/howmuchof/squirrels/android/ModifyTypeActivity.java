package com.howmuchof.squirrels.android;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by LinXi on 6/17/2014.
 */
public class ModifyTypeActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    final static int REQUEST_CODE_ADD = 0;
    final static int REQUEST_CODE_MODIFY = 1;

    EditText nameEdit;
    EditText descriptionEdit;
    Spinner spinner;
    Button okBtn;
    Button cancelBtn;

    DBHelper dbHelper;
    int id;
    boolean pickerIsActive;
    Context context;
    String[] dataTypes;
    int type;
    int actionType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_type);

        context = this;
        Intent intent = getIntent();
        Bundle extra=getIntent().getExtras();

        if (extra == null){
            setResult(RESULT_CANCELED, intent);
            finish();
        }

        okBtn = (Button) findViewById(R.id.ok);
        nameEdit = (EditText) findViewById(R.id.name);
        descriptionEdit = (EditText) findViewById(R.id.description);
        spinnerInit();

        actionType = intent.getIntExtra("requestcode", 0);
        if (actionType == REQUEST_CODE_MODIFY){
            id = intent.getIntExtra("id", -1);
            String name = intent.getStringExtra("name");
            String desciption = intent.getStringExtra("description");
            type = intent.getIntExtra("type", 0);
            int amount= intent.getIntExtra("amount", 0);

            nameEdit.setText(name);
            descriptionEdit.setText(desciption);
            spinner.setSelection(type);

            if (amount > 0){
                spinner.setEnabled(false);
            }
            okBtn.setText(R.string.apply);
        }
        else {
            okBtn.setText(R.string.add);
        }
        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancel:{
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            }
            case R.id.ok: {
                if (actionType == REQUEST_CODE_MODIFY) {
                    updateDBLine();
                }
                else {
                    addDBLine();
                }
                break;
            }
        }
    }

    private void spinnerInit(){
        dataTypes = new String[]{
                getResources().getString(R.string.dataType_integer),
                getResources().getString(R.string.dataType_decimal),
                getResources().getString(R.string.dataType_enum),
                getResources().getString(R.string.dataType_momentOfTime),
                getResources().getString(R.string.dataType_timePeriod),
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setPrompt(getResources().getString(R.string.settingsPage_chooseType));
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(this);
    }

    private void updateDBLine(){
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String name = nameEdit.getText().toString();
        if (name.isEmpty()){
            Toast.makeText(this, R.string.settingsPage_emptyName, Toast.LENGTH_SHORT).show();
            return;
        }
        cv.put("name", name);
        cv.put("description", descriptionEdit.getText().toString());
        cv.put("type", type);

        int updateResult = db.update("types", cv, "id = ?", new String[]{String.valueOf(id)});
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

    private void addDBLine(){
        String name = nameEdit.getText().toString();
        if (name.isEmpty()){
            Toast.makeText(this, R.string.settingsPage_emptyName, Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("type", type);
        cv.put("description", descriptionEdit.getText().toString());

        if (dbHelper.getWritableDatabase().insert("types", null, cv) >= 0){
            dbHelper.close();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            Toast.makeText(this,R.string.dataPage_successAddLine, Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            Toast.makeText(this, R.string.dataPage_failAddLine, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        type = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
