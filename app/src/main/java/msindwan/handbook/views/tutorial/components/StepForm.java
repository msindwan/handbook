/*
 * Created by Mayank Sindwani on 2017-05-21.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.views.tutorial.components;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import msindwan.handbook.R;
import msindwan.handbook.models.Step;

/**
 * StepView:
 * Defines a view that edits a step in a tutorial.
 */
public class StepForm extends RelativeLayout {

    private LinearLayout m_requirementLayout;
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
        TextView title = (TextView)findViewById(R.id.tutorial_viewer_title);
        TextView instructions = (TextView) findViewById(R.id.tutorial_viewer_instructions);
        m_requirementLayout = (LinearLayout) findViewById(R.id.tutorial_viewer_requirements);

        title.setText(m_step.getTitle());
        instructions.setText(m_step.getInstructions());
    }

    /**
     * Adds a requirement list item to the layout.
     *
     * @param item the item to add.
     */
    public void addRequirementListItem(RequirementListItem item) {
        TextView requirementPlaceholder = (TextView)
                findViewById(R.id.tutorial_viewer_requirement_placeholder);
        requirementPlaceholder.setVisibility(View.GONE);
        m_requirementLayout.addView(item);
    }
}
