package msindwan.alfred.views.dashboard.tabs;

/**
 * Created by Mayank Sindwani on 2017-05-04.
 */

import android.support.v4.app.Fragment;
import android.widget.TableLayout;
import android.widget.TableRow;

import msindwan.alfred.R;

public abstract class AbstractDashboardTab extends Fragment {

    protected void populateTutorials(TableLayout table) {
        for (int i = 0; i < 1; i++) {
            TableRow tutorialRow = new TableRow(this.getContext());
            tutorialRow.setBackgroundResource(R.drawable.row_divider);
            TutorialTableCell tutorialTableCell = new TutorialTableCell(this.getContext());

            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1.0f);

            tutorialRow.addView(tutorialTableCell, params);
            table.addView(tutorialRow);
        }
    }

}
