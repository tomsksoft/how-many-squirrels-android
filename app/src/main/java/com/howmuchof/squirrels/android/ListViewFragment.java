package com.howmuchof.squirrels.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.PaintDrawable;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LinXi on 4/13/2014.
 */
public class ListViewFragment extends Fragment implements View.OnClickListener{

    static final int REQUEST_CODE_ADD = 0;
    static final int REQUEST_CODE_UPDATE = 1;
    static final int GRAPH_VIEW_TAB = 2;
    static final int ENUM_DATATYPE = 2;

    Button addBtn;
    Button deleteBtn;
    Button okBtn;
    Button cancelBtn;
    LinearLayout topObjLinearLayout;
    ListView listView;
    DBHelper dbHelper;
    Boolean deleteMode;
    List<Squirrel> objList;
    List<Integer> itemsToRemove;
    int backgColor;
    boolean popUpIsRan;
    int currentObject;
    Button currentTypeButton;
    int type;
    String[] values;

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
        addListViewListener(view);
        dbHelper = new DBHelper(getActivity());
        topObjLinearLayout = (LinearLayout) view.findViewById(R.id.object_nav);
        initBackgroundColor();
        return view;
    }

    public void onResume(){
        currentObject = -1;
        createTopNavPanel();
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
        intent.putExtra("type", currentObject);
        startActivityForResult(intent, REQUEST_CODE_ADD);
    }

    private void launchModifyDataDialog(Squirrel squirrel){
        popUpIsRan = true;
        Intent intent = new Intent(getActivity(), ModifyDataActivity.class);
        intent.putExtra("id", squirrel.getID());
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
        objList = dbHelper.getDataFromDBSortedByDate(currentObject);

        for (Squirrel s: objList) {
            HashMap<String, String> map = new HashMap<String, String>();
            Log.d("MYAPP","Amount: " + s.getAmount() + " type: " + type + " SecAmount: " + s.getSecAmount());
            map.put("amount", getAmountValue(s.getAmount(), s.getSecAmount()));

            date = formatDate(new Date(s.getDate()));
            map.put("date", date);
            items.add(map);
        }

        ListAdapter adapter = new SimpleAdapter(getActivity(), items,
                R.layout.listview_item, new String[]{"amount", "date"},
                new int[]{R.id.amount_textview, R.id.date_textview});

        listView.setAdapter(adapter);
    }

    private String getAmountValue(double value, double secValue){
        Log.d("MYAPP", "Got value: " + value);
        String text = "";
        switch(type){
            case 0:
                text = String.valueOf((int)value);
                break;
            case 1:
                text = String.valueOf(value);
                break;
            case 2:
                text = values[(int)value];
                break;
            case 3:
                Date date = new Date();
                date.setTime((long)value);
                text = formatDate(date);
                break;
            case 4:
                Duration duration = new Duration();
                duration.setDayTime((long)value, (long)secValue);
                text = formatDuration(duration);
                break;
        }
        return text;
    }

    void addListViewListener(View view){
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

    private void createTopNavPanel(){
        topObjLinearLayout.removeAllViews();
        ScrollView sv = new ScrollView(getActivity());
        sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.HORIZONTAL);
        sv.addView(ll);

        List<DataType> types = dbHelper.getTypes();
        for(DataType dt: types){
            Button b = new Button(getActivity());
            b.setText(dt.getName());
            b.setId(dt.getID());
            b.setBackgroundColor(Color.parseColor("#DBDBDB"));
            if (currentObject == -1){
                type = dt.getType();
                if (type == ENUM_DATATYPE){
                    String description = dt.getDesctiption();
                    values = description.split(",");
                }
                currentObject = dt.getID();
                currentTypeButton = b;
            }
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentTypeButton.setTextColor(Color.BLACK);
                    currentTypeButton.setTypeface(null, Typeface.NORMAL);
                    currentTypeButton = (Button) view;
                    currentTypeButton.setTextColor(Color.parseColor("#5BCDF9"));
                    currentTypeButton.setTypeface(null, Typeface.BOLD);
                    currentObject = view.getId();
                    DataType dt = dbHelper.getType(currentObject);
                    type = dt.getType();
                    if (type == ENUM_DATATYPE){
                        String description = dt.getDesctiption();
                        values = description.split(",");
                    }
                    fillListView();
                }
            });
            ll.addView(b);
        }
        if (currentTypeButton != null) {
            currentTypeButton.setTextColor(Color.parseColor("#5BCDF9"));
            currentTypeButton.setTypeface(null, Typeface.BOLD);
        }
        topObjLinearLayout.addView(sv);
    }

    private String formatDuration(Duration duration){
        String value = "";

        /*if (duration.getDays() > 0){
            value += String.valueOf(duration.getDays());
            value += getResources().getString(R.string.dimension_days);
            value += " ";
        }*/
        if (duration.getHours() > 0){
            value += String.valueOf(duration.getHours());
            value += getResources().getString(R.string.dimension_hours);
            value += " ";
        }
        if (duration.getMinutes() > 0){
            value += String.valueOf(duration.getMinutes());
            value += getResources().getString(R.string.dimension_minutes);
            value += " ";
        }
        if ("".equals(value)){
            value = "0" + getResources().getString(R.string.dimension_minutes);
        }
        return value;
    }
}
