package msindwan.alfred.views.dashboard.tabs;

/**
 * Created by Mayank Sindwani on 2017-05-03.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabPager extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TabPager(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        /*switch (position) {
            case 0:
                AllTab tab1 = new AllTab();
                return tab1;
            default:
                return null;
        }*/
        AllTab tab1 = new AllTab();
        return tab1;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
