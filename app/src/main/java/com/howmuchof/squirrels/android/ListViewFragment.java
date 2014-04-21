package com.howmuchof.squirrels.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LinXi on 4/13/2014.
 */
public class ListViewFragment extends Fragment implements View.OnClickListener{

    static final int REQUEST_CODE_ADD = 0;
    static final int REQUEST_CODE_UPDATE = 1;
    Button addBtn;
    Button deleteBtn;
    Button okBtn;
    Button cancelBtn;
    ListView listView;
    DBHelper dbHelper;
    Boolean deleteMode;
    List<Squirrel> objList;
    List<Integer> itemsToRemove;

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
        fillListView();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_line_btn:{
                Intent intent = new Intent(getActivity(), AddDataActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            fillListView();
        }
    }

    private void fillListView(){
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        Calendar calendar = new GregorianCalendar();
        String time;
        String date;
        objList = dbHelper.getDataFromDB();

        for (Squirrel s: objList){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("amount", String.valueOf(s.getAmount()));
            calendar.setTimeInMillis(s.getDate());
            time = calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                    calendar.get(Calendar.MINUTE);
            date = calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                    (calendar.get(Calendar.MONTH)+1) + "-" +
                    calendar.get(Calendar.YEAR);
            map.put("time", time);
            map.put("date", date);
            items.add(map);
        }

        ListAdapter adapter = new SimpleAdapter(getActivity(), items,
                R.layout.listview_item, new String[]{"amount", "date", "time"},
                new int[]{R.id.amount_textview, R.id.date_textview, R.id.time_textview});

        listView.setAdapter(adapter);

    }

    void listViewActions(View view){
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Squirrel squirrel = objList.get(position);

                if (deleteMode){
                    view.setBackgroundColor(Color.parseColor("#FF8282"));
                    itemsToRemove.add(squirrel.getID());
                }
                else{
                    Intent intent = new Intent(getActivity(), ModifyDataActivity.class);
                    intent.putExtra("id", squirrel.getID());
                    intent.putExtra("date", squirrel.getDate());
                    intent.putExtra("amount", squirrel.getAmount());

                    startActivityForResult(intent, REQUEST_CODE_UPDATE);
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
        cancelBtn.setVisibility(View.VISIBLE);
    }
}

