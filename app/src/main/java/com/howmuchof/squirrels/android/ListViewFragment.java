package com.howmuchof.squirrels.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/*
  * How many squirrels: tool for young naturalist
  *
  * This application is created within the internship
  * in the Education Department of Tomsksoft, http://tomsksoft.com
  * Idea and leading: Sergei Borisov
  *
  * This software is licensed under a GPL v3
  * http://www.gnu.org/licenses/gpl.txt
  *
  * Created by Viacheslav Voronov on 4/13/2014
  */

public class ListViewFragment extends Fragment implements View.OnClickListener{

    static final int REQUEST_CODE_ADD = 0;
    static final int REQUEST_CODE_UPDATE = 1;
    static final int GRAPH_VIEW_TAB = 2;
    Button addBtn;
    Button deleteBtn;
    Button okBtn;
    Button cancelBtn;
    ListView listView;
    DBHelper dbHelper;
    Boolean deleteMode;
    List<Squirrel> objList;
    List<Integer> itemsToRemove;
    int backgColor;
    boolean popUpIsRan;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.listview_fragment, container, false);
        addBtn = (Button) view.findViewById(R.id.add_line_btn);
        addBtn.setOnClickListener(this);
        deleteBtn = (Button) view.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(this);
        okBtn = (Button) view.findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(this);
        cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(this);
        deleteMode = false;
        itemsToRemove = new ArrayList<Integer>();

        listViewActions(view);

        dbHelper = new DBHelper(getActivity());
        //fillListView();
        initBackgroundColor();
        return view;
    }

    public void onResume(){
        fillListView();
        super.onResume();
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
        Intent intent = new Intent(getActivity(), AddDataActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD);
    }

    private void launchModifyDataDialog(Squirrel squirrel){
        popUpIsRan = true;
        Intent intent = new Intent(getActivity(), ModifyDataActivity.class);
        intent.putExtra("id", squirrel.getID());
        intent.putExtra("date", squirrel.getDate());
        intent.putExtra("amount", squirrel.getAmount());
        startActivityForResult(intent, REQUEST_CODE_UPDATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        popUpIsRan = false;
        if (resultCode == Activity.RESULT_OK) {
            boolean goToGraphView = data.getBooleanExtra("gotographview", false);
            if (goToGraphView) {
                ActionBar actionBar = getActivity().getActionBar();
                if (null != actionBar) {
                    actionBar.selectTab(actionBar.getTabAt(GRAPH_VIEW_TAB));
                }
            }
            else {
                fillListView();
            }
        }
    }

    private void fillListView(){
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        String date;
        objList = dbHelper.getDataFromDBSortedByDate();

        for (Squirrel s: objList) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("amount", String.valueOf(s.getAmount()));

            date = formatDate(new Date(s.getDate()));
            map.put("date", date);
            items.add(map);
        }

        ListAdapter adapter = new SimpleAdapter(getActivity(), items,
                R.layout.listview_item, new String[]{"amount", "date"},
                new int[]{R.id.amount_textview, R.id.date_textview});

        listView.setAdapter(adapter);

    }

    void listViewActions(View view){
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Squirrel squirrel = objList.get(position);

                if (deleteMode){
                    int index = itemsToRemove.indexOf(squirrel.getID());
                    if (-1 != index){
                        view.setBackgroundColor(backgColor);
                        itemsToRemove.remove(index);
                        if (itemsToRemove.size() == 0){
                            okBtn.setEnabled(false);
                        }
                    }
                    else{
                        view.setBackgroundColor(Color.parseColor("#FF8282"));
                        itemsToRemove.add(squirrel.getID());
                        if (itemsToRemove.size() == 1){
                            okBtn.setEnabled(true);
                        }
                    }
                }
                else{
                    if (!popUpIsRan) {
                        launchModifyDataDialog(squirrel);
                    }
                }
            }
        });
    }

    void realizeDeletions(boolean toDelete){
        okBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setVisibility(View.VISIBLE);
        addBtn.setVisibility(View.VISIBLE);
        if (toDelete) {
            dbHelper.deleteRows(dbHelper.getWritableDatabase(), itemsToRemove);
            fillListView();
        }
        else {
            fillListView();
        }
        deleteMode = false;
        itemsToRemove = new ArrayList<Integer>();
    }

    void launchDeletionMode(){
        deleteMode = true;
        deleteBtn.setVisibility(View.INVISIBLE);
        addBtn.setVisibility(View.INVISIBLE);
        okBtn.setVisibility(View.VISIBLE);
        okBtn.setEnabled(false);
        cancelBtn.setVisibility(View.VISIBLE);

    }

    private void initBackgroundColor(){
        TypedValue a = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            backgColor = a.data;
        }
    }

    public String formatDate(Date date){
        String result = "";
        java.text.DateFormat dateFormat;

        if (date != null){
            try {
                String format = Settings.System.getString(getActivity().getContentResolver(), Settings.System.DATE_FORMAT);
                if (TextUtils.isEmpty(format)) {
                    dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
                } else {
                    dateFormat = new SimpleDateFormat(format);
                }
                result = dateFormat.format(date);

                dateFormat = android.text.format.DateFormat.getTimeFormat(getActivity());
                result += " " + dateFormat.format(date);

            }
            catch (Exception e){
                Log.d("CODE_ERROR","Couldn't resolve date with parameters: Date '" + date + "'");
            }
        }

        return result;
    }

}
