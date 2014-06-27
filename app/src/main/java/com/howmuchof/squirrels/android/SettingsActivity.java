package com.howmuchof.squirrels.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LinXi on 4/13/2014.
 */
public class SettingsActivity extends Activity implements View.OnClickListener {

    final static int REQUEST_CODE_ADD = 0;
    final static int REQUEST_CODE_MODIFY = 1;
    final static String REM_BACKGROUND_COLOR = "#FF8282";

    Button addBtn;
    Button deleteBtn;
    Button okBtn;
    Button cancelBtn;
    ListView listView;
    DBHelper dbHelper;
    Boolean deleteMode;
    List<DataType> typesList;
    List<Integer> itemsToRemove;
    AlertDialog.Builder ad;
    boolean popUpIsRan;
    String[] dataTypes;
    int backgColor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dataTypes = new String[]{
            getResources().getString(R.string.dataType_integer),
            getResources().getString(R.string.dataType_decimal),
            getResources().getString(R.string.dataType_enum),
            getResources().getString(R.string.dataType_momentOfTime),
            getResources().getString(R.string.dataType_timePeriod),
        };

        addBtn = (Button) findViewById(R.id.add_line_btn);
        addBtn.setOnClickListener(this);
        deleteBtn = (Button) findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(this);
        okBtn = (Button) findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(this);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(this);
        deleteMode = false;
        itemsToRemove = new ArrayList<Integer>();
        listViewActions();

        dbHelper = new DBHelper(this);
        initBackgroundColor();

        getActionBar().setDisplayHomeAsUpEnabled(true);
        createConfirmDialog();
    }

    public void onResume(){
        fillListView();
        super.onResume();
    }

    protected void createConfirmDialog(){
        ad = new AlertDialog.Builder(this);
        ad.setTitle(R.string.settingsPage_ConfirmAction);
        ad.setMessage(R.string.settingsPage_WarningCleanData);
        ad.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dbHelper.removeTypeAndValues(itemsToRemove);
                fillListView();
                itemsToRemove = new ArrayList<Integer>();
            }
        });
        ad.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                itemsToRemove = new ArrayList<Integer>();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_line_btn:{
                if (!popUpIsRan) {
                    launchAddDataDialog();
                }
                break;
            }
            case R.id.delete_btn:{
                launchDeletionMode();
                break;
            }
            case R.id.ok_btn:{
                realizeDeletions(true);
                break;
            }
            case R.id.cancel_btn:{
                realizeDeletions(false);
                break;
            }
        }
    }

    private void launchAddDataDialog(){
        popUpIsRan = true;
        Intent intent = new Intent(this, ModifyTypeActivity.class);
        intent.putExtra("requestcode", REQUEST_CODE_ADD);
        startActivityForResult(intent, REQUEST_CODE_ADD);
    }

    void launchDeletionMode(){
        deleteMode = true;
        deleteBtn.setVisibility(View.INVISIBLE);
        addBtn.setVisibility(View.INVISIBLE);
        okBtn.setVisibility(View.VISIBLE);
        okBtn.setEnabled(false);
        cancelBtn.setVisibility(View.VISIBLE);
    }

    void realizeDeletions(boolean toDelete){
        okBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setVisibility(View.VISIBLE);
        addBtn.setVisibility(View.VISIBLE);
        if (toDelete) {
            ad.show();
        }
        fillListView();
        deleteMode = false;
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

    void listViewActions(){
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DataType type = typesList.get(position);

                if (deleteMode){
                    int index = itemsToRemove.indexOf(type.getID());
                    if (-1 != index){
                        view.setBackgroundColor(backgColor);
                        itemsToRemove.remove(index);
                        if (itemsToRemove.size() == 0){
                            okBtn.setEnabled(false);
                        }
                    }
                    else{
                        view.setBackgroundColor(Color.parseColor(REM_BACKGROUND_COLOR));
                        itemsToRemove.add(type.getID());
                        if (itemsToRemove.size() == 1){
                            okBtn.setEnabled(true);
                        }
                    }
                }
                else{
                    if (!popUpIsRan) {
                        launchModifyDataDialog(type);
                    }
                }
            }
        });
    }

    private void launchModifyDataDialog(DataType type){
        popUpIsRan = true;
        Intent intent = new Intent(this, ModifyTypeActivity.class);
        intent.putExtra("id", type.getID());
        intent.putExtra("name", type.getName());
        intent.putExtra("description", type.getDesctiption());
        intent.putExtra("type", type.getType());
        intent.putExtra("amount", type.getAmount());
        intent.putExtra("requestcode", REQUEST_CODE_MODIFY);
        startActivityForResult(intent, REQUEST_CODE_MODIFY);
    }

    private void fillListView(){
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        typesList = dbHelper.getTypes();
        if (typesList.isEmpty()){
            return;
        }
        for (DataType t: typesList) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", t.getName());
            map.put("type", dataTypes[t.getType()]);
            map.put("amount", String.valueOf(t.getAmount()));
            items.add(map);
        }

        ListAdapter adapter = new SimpleAdapter(this, items,
                R.layout.settings_item, new String[]{"name", "type", "amount"},
                new int[]{R.id.name, R.id.type, R.id.amount});

        listView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        popUpIsRan = false;
        if (resultCode == Activity.RESULT_OK) {
            fillListView();
        }
    }

    private void initBackgroundColor(){
        TypedValue a = new TypedValue();
        this.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            backgColor = a.data;
        }
    }
}
