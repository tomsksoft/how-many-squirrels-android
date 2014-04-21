package com.howmuchof.squirrels.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
    ActionBar.Tab mainTab, graphViewTab, listViewTab;
    Fragment mainFragment = new MainFragmentActivity();
    Fragment listViewFragment = new ListViewFragment();
    Fragment graphViewFragment = new GraphViewFragment();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }

        mainTab = actionBar.newTab().setText(R.string.mainTabName);
        graphViewTab = actionBar.newTab().setText(R.string.graphViewTabName);
        listViewTab = actionBar.newTab().setText(R.string.listViewTabName);

        mainTab.setTabListener(new TabListener(mainFragment));
        graphViewTab.setTabListener(new TabListener(graphViewFragment));
        listViewTab.setTabListener(new TabListener(listViewFragment));

        actionBar.addTab(mainTab);
        actionBar.addTab(listViewTab);
        actionBar.addTab(graphViewTab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,0,R.string.settingsPageTitle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return true;
    }

    public class TabListener implements ActionBar.TabListener {
        Fragment fragment;

        public TabListener(Fragment fragment) {
           this.fragment = fragment;
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.fragment_container, fragment);
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.remove(fragment);
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            // nothing done here
        }
    }
}
