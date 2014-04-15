package com.howmuchof.squirrels.android;

import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    Button btnAdd;
    ListView listView;
    DBHelper dbHelper;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.listview_fragment, container, false);
        btnAdd = (Button) view.findViewById(R.id.add_line);
        btnAdd.setOnClickListener(this);

        listView = (ListView) view.findViewById(R.id.listView);

        dbHelper = new DBHelper(getActivity());
        fillListView();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_line:{
                Intent intent = new Intent(getActivity(), AddDataActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK)
            fillListView();
    }

    private void fillListView(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        Calendar calendar = new GregorianCalendar();
        String time = "";
        String date = "";
        List<Squirrel> objList = dbHelper.getDataFromDB(db);

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
                new int[]{R.id.amount, R.id.date, R.id.time});

        listView.setAdapter(adapter);

    };
}

