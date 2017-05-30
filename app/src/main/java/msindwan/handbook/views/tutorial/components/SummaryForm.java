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

    private TextView m_name;
    private TextView m_description;
    private LinearLayout m_requirements;
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
        m_name = (TextView) findViewById(R.id.tutorial_viewer_name);
        m_description = (TextView) findViewById(R.id.tutorial_viewer_description);

        m_name.setText(m_tutorial.getName());
        m_description.setText(m_tutorial.getDescription());
    }
}
