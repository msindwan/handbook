package msindwan.alfred.views.dashboard.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import msindwan.alfred.data.schema.TutorialTable;

/**
 * Created by Mayank Sindwani on 2017-05-03.
 *
 * TutorialTabPager:
 * Defines the fragment state pager for tutorial tabs.
 */
public class TutorialTabPager extends FragmentStatePagerAdapter {

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
