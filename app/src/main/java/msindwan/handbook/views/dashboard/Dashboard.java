/*
 * Created by Mayank Sindwani on 2017-05-04.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.views.dashboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import msindwan.handbook.R;
import msindwan.handbook.views.dashboard.components.TutorialTabPager;
import msindwan.handbook.views.tutorial.TutorialEditor;

/**
 * Dashboard:
 * Defines the main activity.
 */
public class Dashboard extends AppCompatActivity {

    private ViewPager m_tabViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        m_tabViewPager = (ViewPager)findViewById(R.id.pager);

        // Add tabs to the tab layout.
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.all)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.recent)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.frequent)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Create the tab pager adapter.
        m_tabViewPager.setAdapter(new TutorialTabPager(
                getSupportFragmentManager(),
                tabLayout.getTabCount()
        ));

        // Bind event listeners.
        tabLayout.addOnTabSelectedListener(onTabSelected);
        m_tabViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(tabLayout)
        );
    }

    /**
     * Handler to initiate a new intent for creating tutorials
     *
     * @param view The view element.
     */
    public void onCreateTutorial(View view) {
        Intent intent = new Intent(this, TutorialEditor.class);
        startActivity(intent);
    }

    /**
     * Listener for tab selection changes.
     */
    private TabLayout.OnTabSelectedListener onTabSelected = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            m_tabViewPager.setCurrentItem(tab.getPosition());
        }
        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }
        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };

}
