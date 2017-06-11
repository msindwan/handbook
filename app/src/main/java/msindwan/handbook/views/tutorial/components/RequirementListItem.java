/*
 * Copyright (C) 2017 Mayank Sindwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package msindwan.handbook.views.tutorial.components;

import android.content.Context;
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
    public RequirementListItem(Context context) {
        super(context);
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
     * Setter for the requirement.
     * @param requirement The requirement to set.
     */
    public void setRequirement(Requirement requirement) {
        m_requirement = requirement;
        TextView nameText = (TextView)findViewById(R.id.tutorial_requirement_item);
        nameText.setText(m_requirement.toString());
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
}
