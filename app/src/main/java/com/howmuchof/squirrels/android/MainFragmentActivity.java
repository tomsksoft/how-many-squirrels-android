package com.howmuchof.squirrels.android;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by LinXi on 4/13/2014.
 */
public class MainFragmentActivity extends Fragment implements View.OnClickListener {
    Button listViewBtn;
    Button graphViewBtn;
    Button settingsBtn;

    static final int LIST_VIEW_TAB = 1;
    static final int GRAPH_VIEW_TAB = 2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        listViewBtn = (Button) view.findViewById(R.id.go_to_list_view_activity);
        graphViewBtn = (Button) view.findViewById(R.id.go_to_graph_view_activity);
        settingsBtn = (Button) view.findViewById(R.id.go_to_settings);

        listViewBtn.setOnClickListener(this);
        graphViewBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);

        SharedPreferences sPref = getActivity().getSharedPreferences("HowMuchOfShPref", getActivity().MODE_PRIVATE);
        String objName = sPref.getString("object_name", "");
        if (objName.length() != 0) {
            settingsBtn.setVisibility(View.INVISIBLE);
        }
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.go_to_list_view_activity:{
                ActionBar actionBar = getActivity().getActionBar();
                if (null != actionBar){
                    actionBar.selectTab(actionBar.getTabAt(LIST_VIEW_TAB));
                }
                break;
            }
            case R.id.go_to_graph_view_activity:{
                ActionBar actionBar = getActivity().getActionBar();
                if (null != actionBar){
                    actionBar.selectTab(actionBar.getTabAt(GRAPH_VIEW_TAB));
                }
                break;
            }
            case R.id.go_to_settings:{
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
