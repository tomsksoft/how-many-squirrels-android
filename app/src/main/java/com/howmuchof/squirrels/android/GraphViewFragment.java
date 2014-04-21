package com.howmuchof.squirrels.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by LinXi on 4/20/2014.
 */
public class GraphViewFragment extends Fragment implements View.OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.graph_view_fragment, container, false);

        return view;
    }


    @Override
    public void onClick(View view) {

    }
}
