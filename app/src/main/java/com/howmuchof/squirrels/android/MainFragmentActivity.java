package com.howmuchof.squirrels.android;

import android.app.Fragment;
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
    Button btn1;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        btn1 = (Button) view.findViewById(R.id.go_to_settings);
        btn1.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.go_to_settings:{
                btn1.setText("Переход к настройкам");
            }
        }
    }
}
