package msindwan.alfred.views.tutorial.components;

import android.content.Context;
import android.widget.RelativeLayout;

import msindwan.alfred.R;
import msindwan.alfred.views.widgets.Accordion;

/**
 * Created by Mayank Sindwani on 2017-05-21.
 *
 * SummaryView:
 * Defines a view representing a tutorial's summary.
 */
public class EditSummaryView extends RelativeLayout {

    // Constructors.
    public EditSummaryView(Context context, Accordion.Panel parent) {
        super(context);
        setTag(parent);
        init(context);
    }

    /**
     * Initializes the component on mount.
     *
     * @param context The initialization context.
     */
    private void init(Context context) {
        inflate(context, R.layout.tutorial_editor_summary_panel, this);
    }

}
