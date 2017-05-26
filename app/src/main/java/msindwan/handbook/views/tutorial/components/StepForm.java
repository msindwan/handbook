package msindwan.handbook.views.tutorial.components;

/**
 * Created by Mayank Sindwani on 2017-05-25.
 */

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import msindwan.handbook.R;
import msindwan.handbook.models.Step;

/**
 * Created by Mayank Sindwani on 2017-05-21.
 *
 * StepView:
 * Defines a view that edits a step in a tutorial.
 */
public class StepForm extends RelativeLayout {

    private TextView m_title;
    private TextView m_instructions;
    private LinearLayout m_stepLayout;

    private Step m_step;

    // Constructors.
    public StepForm(Context context, Step step, View tag) {
        super(context);
        m_step = step;
        setTag(tag);
        init(context);
    }

    /**
     * Initializes the component on mount.
     *
     * @param context The initialization context.
     */
    private void init(Context context) {
        inflate(context, R.layout.tutorial_viewer_step_panel, this);
        m_title = (TextView)findViewById(R.id.tutorial_viewer_title);
        m_instructions = (TextView) findViewById(R.id.tutorial_viewer_instructions);
        m_stepLayout = (LinearLayout) findViewById(R.id.tutorial_viewer_requirements);
        m_title.setText(m_step.getTitle());
        m_instructions.setText(m_step.getInstructions());
    }

    /**
     * Getter for the view's step.
     *
     * @return the view's step.
     */
    public Step getStep() {
        return m_step;
    }

    /**
     * Adds a requirement list item to the layout.
     *
     * @param item the item to add.
     */
    public void addRequirementListItem(RequirementListItem item) {
        m_stepLayout.addView(item);
    }

    /**
     * Removes a requirement list item from the layout.
     *
     * @param item the item to remove.
     */
    public void removeRequirementListItem(RequirementListItem item) {
        m_stepLayout.removeView(item);
    }
}
