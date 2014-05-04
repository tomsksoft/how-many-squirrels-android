package com.howmuchof.squirrels.android;

import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by LinXi on 4/20/2014.
 */
public class GraphViewFragment extends Fragment implements View.OnClickListener{

    DBHelper dbHelper;
    View view;
    ImageView imageView;
    GraphManager graphManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.graph_view_fragment, container, false);

        imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setImageDrawable(new GraphDrawable());

        dbHelper = new DBHelper(getActivity());
        graphManager = new GraphManager();
        initGraph();

        return view;
    }

    private void initGraph(){
        getGraphData(dbHelper.getDataFromDB(0,0));
    }

    private void getGraphData(List<Squirrel> squirrels){
        for(Squirrel squirrel: squirrels){
            graphManager.addValues(squirrel.getAmount(), squirrel.getDate());
        }
    }

    @Override
    public void onClick(View view) {

    }

    private class GraphDrawable extends Drawable {

        Rect rect;

        @Override
        public void draw(Canvas canvas) {
            Log.d("DRAWING", "Got inside of draw method");
            graphManager.getGraphProperties().setXFormat(GraphProperties.HOR_VALUES_DATE_FORMAT);

            graphManager.draw(canvas, getView());
        }

        @Override
        public int getOpacity() {return 0;}

        @Override
        public void setAlpha(int alpha) {}

        @Override
        public void setColorFilter(ColorFilter cf) {}

    }

    @Override
    protected void finalize() throws Throwable {
        view.invalidate();
        super.finalize();
    }
}
