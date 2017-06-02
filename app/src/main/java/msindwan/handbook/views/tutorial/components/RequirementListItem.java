/*
 * Created by Mayank Sindwani on 2017-05-20.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.views.tutorial.components;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import msindwan.handbook.R;
import msindwan.handbook.models.Requirement;

/**
 * RequirementListItem:
 * Defines a widget representing a single requirement item.
 */
public class RequirementListItem extends RelativeLayout {

    private Requirement m_requirement;
    private Button m_deleteButton;

    // Constructors.
    public RequirementListItem(Context context, Requirement requirement, View tag) {
        super(context);
        m_requirement = requirement;
        setTag(tag);
        init(context);
    }

    /**
     * Initializes the component on mount.
     *
     * @param context The initialization context.
     */
    private void init(Context context) {
        inflate(context, R.layout.tutorial_requirement_list_item, this);
        m_deleteButton = (Button)findViewById(R.id.tutorial_requirement_delete);
        paint();
    }

    /**
     * Getter for the requirement.
     *
     * @return the list item's requirement.
     */
    public Requirement getRequirement() {
        return m_requirement;
    }

    /**
     * Sets the click listener for the remove button.
     *
     * @param listener the listener to bind.
     */
    public void setRequirementOnRemoveListener(OnClickListener listener) {
        m_deleteButton.setTag(this);
        m_deleteButton.setOnClickListener(listener);
    }

    /**
     * Toggles the visibility of the delete button.
     *
     * @param toggle the flag to determine whether or not
     *               to display the delete button
     */
    public void toggleDeleteButton(boolean toggle) {
        m_deleteButton.setVisibility(toggle ? VISIBLE : GONE);
    }

    /**
     * Renders the item based on the state of the requirement model.
     */
    public void paint() {
        TextView nameText = (TextView)findViewById(R.id.tutorial_requirement_item);
        nameText.setText(m_requirement.toString());
    }
}
