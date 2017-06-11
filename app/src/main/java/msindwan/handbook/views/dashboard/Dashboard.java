/*
 * Copyright (C) 2017 Mayank Sindwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
