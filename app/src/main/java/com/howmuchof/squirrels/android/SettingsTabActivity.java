package com.howmuchof.squirrels.android;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by LinXi on 4/13/2014.
 */
public class SettingsTabActivity extends Fragment implements View.OnClickListener {

    final static String SHAREDPREFNAME = "HowMuchOfShPref";
    final static String SHARED_PREF_OBJECT_FIELD_NAME = "object_name";
    final static int CHANGE_OBJ_NAME_DIALOG = 1;
    EditText objNameInput;
    Button applyBtn;
    DBHelper dbHelper;
    Context context;
    SharedPreferences sPref;
    SharedPreferences.Editor shEditor;
    String currentObjectName = "";
    AlertDialog.Builder ad;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        context = getActivity();
        objNameInput = (EditText) view.findViewById(R.id.objEditText);
        applyBtn = (Button) view.findViewById(R.id.applyBtn);
        applyBtn.setOnClickListener(this);

        sPref = context.getSharedPreferences(SHAREDPREFNAME, context.MODE_PRIVATE);
        shEditor = sPref.edit();
        dbHelper = new DBHelper(context);
        createConfirmDialog();
        currentObjectName = getCountedObjectName();
        if (currentObjectName.length() != 0)
            objNameInput.setText(currentObjectName);
        return view;
    }

    protected void createConfirmDialog(){
        ad = new AlertDialog.Builder(context);
        ad.setTitle(R.string.settingsPage_ConfirmAction);
        ad.setMessage(R.string.settingsPage_WarningCleanData);
        ad.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                String objectName = objNameInput.getText().toString().trim();
                setCountedObjectName(objectName);
            }
        });
        ad.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.applyBtn:{
                String objectName = objNameInput.getText().toString().trim();
                if (objectName.length() != 0) {
                    if (currentObjectName.length() != 0)
                        ad.show();
                    else
                        setCountedObjectName(objectName);
                    currentObjectName = objectName;
                }
                else
                    Toast.makeText(context, R.string.settingsPage_EmptyStr, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    protected String getCountedObjectName(){
        return sPref.getString(SHARED_PREF_OBJECT_FIELD_NAME, "");
    }

    protected void setCountedObjectName(String str){
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("objects", null, null);
        shEditor.putString(SHARED_PREF_OBJECT_FIELD_NAME, str);
        shEditor.commit();
        Toast.makeText(context,R.string.settingsPage_TextIsSet, Toast.LENGTH_SHORT).show();
    }

}
