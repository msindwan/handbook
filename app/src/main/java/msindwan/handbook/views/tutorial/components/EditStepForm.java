/*
 * Created by Mayank Sindwani on 2017-05-21.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.views.tutorial.components;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import msindwan.handbook.R;
import msindwan.handbook.models.Step;
import msindwan.handbook.views.common.EditFormView;
import msindwan.handbook.views.widgets.Accordion;

/**
 * StepView:
 * Defines a view that edits a step in a tutorial.
 */
public class EditStepForm extends RelativeLayout implements EditFormView {

    private Button m_addRequirementButton;
    private ImageButton m_moveDownButton;
    private ImageButton m_moveUpButton;
    private ImageButton m_removeButton;
    private LinearLayout m_stepLayout;
    private EditText m_instructions;
    private EditText m_title;

    private Step m_step;

    // Constructors.
    public EditStepForm(Context context, Step step, View tag) {
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
        inflate(context, R.layout.tutorial_editor_step_panel, this);
        m_removeButton         = (ImageButton)findViewById(R.id.tutorial_editor_remove_step);
        m_moveUpButton         = (ImageButton)findViewById(R.id.tutorial_editor_move_up);
        m_moveDownButton       = (ImageButton)findViewById(R.id.tutorial_editor_move_down);
        m_addRequirementButton = (Button)findViewById(R.id.tutorial_editor_add_requirement);
        m_stepLayout           = (LinearLayout)findViewById(R.id.tutorial_editor_requirements);
        m_instructions         = (EditText)findViewById(R.id.tutorial_editor_instructions);
        m_title                = (EditText)findViewById(R.id.tutorial_editor_title);

        m_instructions.setText(m_step.getInstructions());
        m_title.setText(m_step.getTitle());

        m_instructions.addTextChangedListener(onInstructionsChanged);
        m_title.addTextChangedListener(onTitleChanged);

        Accordion.Panel parent = (Accordion.Panel)getTag();
        m_removeButton.setTag(parent);
        m_moveUpButton.setTag(parent);
        m_moveDownButton.setTag(parent);
        m_addRequirementButton.setTag(parent);
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
     * Toggles the visibility of the move up button.
     *
     * @param toggle whether or not to display the button.
     *        true if visible; false otherwise.
     */
    public void toggleMoveUpButton(Boolean toggle) {
        m_moveUpButton.setVisibility(toggle ? VISIBLE : GONE);
    }

    /**
     * Toggles the visibility of the move down button.
     *
     * @param toggle whether or not to display the button.
     *        true if visible; false otherwise.
     */
    public void toggleMoveDownButton(Boolean toggle) {
        m_moveDownButton.setVisibility(toggle ? VISIBLE : GONE);
    }

    /**
     * Sets the listener for removing a step view.
     *
     * @param listener the listener to set.
     */
    public void setOnRemoveListener(View.OnClickListener listener) {
        m_removeButton.setOnClickListener(listener);
    }

    /**
     * Sets the listener for moving a step view up.
     *
     * @param listener the listener to set.
     */
    public void setOnMoveUpListener(View.OnClickListener listener) {
        m_moveUpButton.setOnClickListener(listener);
    }

    /**
     * Sets the listener for moving a step view down.
     *
     * @param listener the listener to set.
     */
    public void setOnMoveDownListener(View.OnClickListener listener) {
        m_moveDownButton.setOnClickListener(listener);
    }

    /**
     * Sets the listener for adding a requirement.
     *
     * @param listener the listener to set.
     */
    public void setOnAddRequirementListener(View.OnClickListener listener) {
        m_addRequirementButton.setOnClickListener(listener);
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

    /**
     * Validates the step and sets text field errors.
     *
     * @return True if valid; false otherwise.
     */
    @Override
    public boolean validate() {
        String title = m_step.getTitle();
        String instructions = m_step.getInstructions();

        if (title == null || title.isEmpty()) {
            m_title.requestFocus();
            m_title.setError("Title is Required");
            return false;
        }
        if (instructions == null || instructions.isEmpty()) {
            m_instructions.requestFocus();
            m_instructions.setError("Instructions are Required");
            return false;
        }
        return true;
    }

    /**
     * Listener for title changes.
     */
    private TextWatcher onTitleChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            m_step.setTitle(m_title.getText().toString());
        }
    };

    /**
     * Listener for instruction changes.
     */
    private TextWatcher onInstructionsChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            m_step.setInstructions(m_instructions.getText().toString());
        }
    };
}
