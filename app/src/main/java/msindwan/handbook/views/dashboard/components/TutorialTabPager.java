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
package msindwan.handbook.views.dashboard.components;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import msindwan.handbook.data.schema.TutorialTable;

/**
 * TutorialTabPager:
 * Defines the fragment state pager for tutorial tabs.
 */
public class TutorialTabPager extends FragmentPagerAdapter {

    private int m_numTabs;

    // Constructor.
    public TutorialTabPager(FragmentManager fm, int numTabs) {
        super(fm);
        m_numTabs = numTabs;
    }

    @Override
    public Fragment getItem(int position) {
        TutorialListFragment fragment = new TutorialListFragment();
        Bundle args = new Bundle();

        // Set the order according to the tab.
        switch (position) {
            case 0:
                args.putString("order", TutorialTable.COL_NAME);
                break;
            case 1:
                args.putString("order", TutorialTable.COL_LAST_MODIFIED);
                break;
            case 2:
                args.putString("order", TutorialTable.COL_NUM_VIEWS);
                break;
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return m_numTabs;
    }
}
