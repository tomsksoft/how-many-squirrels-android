package com.howmuchof.squirrels.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class MainActivity extends Activity {
    ActionBar.Tab mainTab, settingsTab, graphViewTab, listViewTab;
    Fragment mainFragment = new MainFragmentActivity();
    Fragment settingsFragment = new SettingsTabActivity();
    Fragment listViewFragment = new ListViewFragment();
    //Fragment graphViewFragment = new ListViewFragment();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mainTab = actionBar.newTab().setText(R.string.mainTabName);
        settingsTab = actionBar.newTab().setText(R.string.settingsTabName);
        listViewTab = actionBar.newTab().setText(R.string.listViewTabName);

        mainTab.setTabListener(new MyTabListener(mainFragment));
        settingsTab.setTabListener(new MyTabListener(settingsFragment));
        listViewTab.setTabListener(new MyTabListener(listViewFragment));

        actionBar.addTab(settingsTab);
        actionBar.addTab(mainTab);
        actionBar.addTab(listViewTab);
    }

    public class MyTabListener implements ActionBar.TabListener {
        Fragment fragment;

        public MyTabListener(Fragment fragment) {
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
