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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Locale;

import msindwan.handbook.R;
import msindwan.handbook.models.Image;
import msindwan.handbook.models.Step;
import msindwan.handbook.views.widgets.FileUploader;

/**
 * StepView:
 * Defines a view that edits a step in a tutorial.
 */
public class EditStepForm extends RelativeLayout {

    private Button m_addRequirementButton;
    private ImageButton m_moveDownButton;
    private ImageButton m_moveUpButton;
    private ImageButton m_removeButton;
    private LinearLayout m_stepLayout;
    private FileUploader m_uploader;
    private EditText m_instructions;
    private EditText m_title;
    private Step m_step;

    // Constructors.
    public EditStepForm(Context context) {
        super(context);
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
        m_uploader             = (FileUploader)findViewById(R.id.tutorial_editor_image_uploader);

        m_instructions.addTextChangedListener(onInstructionsChanged);
        m_title.addTextChangedListener(onTitleChanged);
    }

    /**
     * Setter for the step.
     *
     * @param step the step to set.
     */
    public void setStep(Step step) {
        m_step = step;
        m_title.setText(m_step.getTitle());
        m_instructions.setText(m_step.getInstructions());
    }

    /**
     * Getter for the step.
     *
     * @return the step.
     */
    public Step getStep() {
        return m_step;
    }

    /**
     * Validates the step view.
     *
     * @return True if valid; false otherwise.
     */
    public boolean validate() {
        String title = m_step.getTitle();
        String instructions = m_step.getInstructions();

        if (title == null || title.isEmpty()) {
            m_title.requestFocus();
            m_title.setError(getResources().getString(R.string.required));
            return false;
        }
        if (instructions == null || instructions.isEmpty()) {
            m_instructions.requestFocus();
            m_instructions.setError(getResources().getString(R.string.required));
            return false;
        }
        return true;
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
     * Returns the requirement list item at the specified index.
     *
     * @param index The index of the desired list item.
     * @return The list item.
     */
    public RequirementListItem getRequirementListItem(int index) {
        return (RequirementListItem)m_stepLayout.getChildAt(index);
    }

    /**
     * Adds an image to the image uploader.
     *
     * @param item The uploader item to add.
     */
    public void addFileUploaderItem(FileUploader.FileUploaderItem item) {
        m_uploader.addFileUploaderItem(item);
    }

    /**
     * Removes an image from the uploader.
     *
     * @param item The uploader item to remove.
     */
    public void removeFileUploaderItem(FileUploader.FileUploaderItem item) {
        m_uploader.removeFileUploaderItem(item);
    }

    /**
     * Gets an item from the uploader at a specified index.
     *
     * @param index The index of the item to fetch.
     */
    public FileUploader.FileUploaderItem getUploaderItem(int index) {
        return m_uploader.getFileUploaderItem(index);
    }

    /**
     * Adds an image to the uploader.
     *
     * @param image The image to add.
     * @return The file uploader item.
     */
    public FileUploader.FileUploaderItem addImage(Image image) {
        FileUploader.FileUploaderItem item;
        item = new FileUploader.FileUploaderItem(getContext());
        item.setTag(this);
        item.setArguments(image);
        item.setTitle(image.getName());
        item.setSubtitle(String.format(Locale.getDefault(), "%d KB", image.getSize()));
        addFileUploaderItem(item);
        return item;
    }

    /**
     * Sets the listener for the uploader zone.
     *
     * @param listener The listener to set.
     */
    public void setUploaderZoneClickListener(View.OnClickListener listener) {
        m_uploader.setZoneClickListener(listener);
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
            if (m_step != null) {
                m_step.setTitle(m_title.getText().toString());
            }
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
            if (m_step != null) {
                m_step.setInstructions(m_instructions.getText().toString());
            }
        }
    };
}
