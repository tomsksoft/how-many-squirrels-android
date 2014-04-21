package com.howmuchof.squirrels.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by LinXi on 4/13/2014.
 */
public class SettingsActivity extends Activity implements View.OnClickListener {

    final static String SHAREDPREFNAME = "HowMuchOfShPref";
    final static String SHARED_PREF_OBJECT_FIELD_NAME = "object_name";

    EditText objNameEditText;
    Button applyBtn;
    DBHelper dbHelper;
    SharedPreferences sPref;
    SharedPreferences.Editor shEditor;
    String currentObjectName = "";
    AlertDialog.Builder ad;
    Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.show();
        }
        objNameEditText = (EditText) findViewById(R.id.objEditText);
        applyBtn = (Button) findViewById(R.id.applyBtn);
        applyBtn.setOnClickListener(this);
        context = this;
        sPref = context.getSharedPreferences(SHAREDPREFNAME, Activity.MODE_PRIVATE);
        shEditor = sPref.edit();
        dbHelper = new DBHelper(context);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        createConfirmDialog();
        initObjectname();
    }

    protected void createConfirmDialog(){
        ad = new AlertDialog.Builder(context);
        ad.setTitle(R.string.settingsPage_ConfirmAction);
        ad.setMessage(R.string.settingsPage_WarningCleanData);
        ad.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                String objectName = objNameEditText.getText().toString().trim();
                setCountedObjectName(objectName);
            }
        });
        ad.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
    }
    private void initObjectname(){
        currentObjectName = getCountedObjectName();
        if (currentObjectName.length() != 0) {
            objNameEditText.setText(currentObjectName);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.applyBtn:{
                setObjectName();
                break;
            }
        }
    }

    private void setObjectName(){
        String objectName = objNameEditText.getText().toString().trim();
        if (objectName.length() != 0) {
            if (currentObjectName.length() != 0)
                ad.show();
            else
                setCountedObjectName(objectName);
            currentObjectName = objectName;
        }
        else
            Toast.makeText(context, R.string.settingsPage_EmptyStr, Toast.LENGTH_LONG).show();
    }

    protected String getCountedObjectName(){
        return sPref.getString(SHARED_PREF_OBJECT_FIELD_NAME, "");
    }

    protected void setCountedObjectName(String str){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("objects", null, null);
        shEditor.putString(SHARED_PREF_OBJECT_FIELD_NAME, str);
        shEditor.commit();
        Toast.makeText(context,R.string.settingsPage_TextIsSet, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
