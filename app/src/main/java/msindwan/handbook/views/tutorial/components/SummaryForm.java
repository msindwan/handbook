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
import msindwan.handbook.models.Tutorial;

/**
 * SummaryView:
 * Defines a view representing a tutorial's summary.
 */
public class SummaryForm extends RelativeLayout {

    private LinearLayout m_requirementLayout;
    private Tutorial m_tutorial;

    // Constructors.
    public SummaryForm(Context context, Tutorial tutorial, View tag) {
        super(context);
        m_tutorial = tutorial;
        setTag(tag);
        init(context);
    }

    /**
     * Initializes the component on mount.
     *
     * @param context The initialization context.
     */
    private void init(Context context) {
        inflate(context, R.layout.tutorial_viewer_summary_panel, this);
        TextView name = (TextView) findViewById(R.id.tutorial_viewer_name);
        TextView description = (TextView) findViewById(R.id.tutorial_viewer_description);
        m_requirementLayout = (LinearLayout) findViewById(R.id.tutorial_viewer_requirements);

        name.setText(m_tutorial.getName());
        description.setText(m_tutorial.getDescription());
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
