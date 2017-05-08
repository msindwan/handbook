package msindwan.alfred.views.dashboard.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import msindwan.alfred.R;

/**
 * Created by Mayank Sindwani on 2017-05-03.
 */

public class AllTab extends AbstractDashboardTab {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dashboard_tabs_all_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.populateTutorials((TableLayout)view.findViewById(R.id.all_tab_tutorials_table));
    }

}
