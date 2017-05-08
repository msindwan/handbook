package msindwan.alfred.views.dashboard.tabs;

import android.widget.RelativeLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import msindwan.alfred.R;

/**
 * Created by Mayank Sindwani on 2017-05-04.
 */

public class TutorialTableCell extends RelativeLayout {

    public TutorialTableCell(Context context) {
        super(context);
        init(context);
    }

    public TutorialTableCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.dashboard_tabs_tutorial_table_cell, this);
        TextView title = (TextView)this.findViewById(R.id.tutorial_cell_title);
        TextView numSteps = (TextView)this.findViewById(R.id.tutorial_cell_num_steps);

        title.setText("Getting Started with Alfred");
        numSteps.setText("8 Steps");
    }

}