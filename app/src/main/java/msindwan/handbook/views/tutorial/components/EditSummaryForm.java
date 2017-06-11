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
import android.widget.EditText;
import android.widget.RelativeLayout;

import msindwan.handbook.R;
import msindwan.handbook.models.Tutorial;

/**
 * SummaryView:
 * Defines a view representing a tutorial's summary.
 */
public class EditSummaryForm extends RelativeLayout {

    private EditText m_name;
    private EditText m_description;
    private Tutorial m_tutorial;

    // Constructors.
    public EditSummaryForm(Context context) {
        super(context);
        init(context);
    }

    /**
     * Initializes the component on mount.
     *
     * @param context The initialization context.
     */
    private void init(Context context) {
        inflate(context, R.layout.tutorial_editor_summary_panel, this);
        m_name = (EditText)findViewById(R.id.tutorial_editor_name);
        m_description = (EditText)findViewById(R.id.tutorial_editor_description);

        m_name.addTextChangedListener(onNameChanged);
        m_description.addTextChangedListener(onDescriptionChanged);
    }

    /**
     * Setter for the tutorial.
     *
     * @param tutorial the tutorial to set.
     */
    public void setTutorial(Tutorial tutorial) {
        m_tutorial = tutorial;
        m_name.setText(m_tutorial.getName());
        m_description.setText(m_tutorial.getDescription());
    }

    /**
     * Getter for the tutorial.
     *
     * @return the tutorial.
     */
    public Tutorial getTutorial() {
        return m_tutorial;
    }

    /**
     * Validates the summary view.
     *
     * @return True if valid; false otherwise.
     */
    public boolean validate() {
        String name = m_tutorial.getName();
        String description = m_tutorial.getDescription();

        if (name == null || name.isEmpty()) {
            m_name.requestFocus();
            m_name.setError(getResources().getString(R.string.required));
            return false;
        }
        if (description == null || description.isEmpty()) {
            m_description.requestFocus();
            m_description.setError(getResources().getString(R.string.required));
            return false;
        }
        return true;
    }


    /**
     * Listener for name changes.
     */
    private TextWatcher onNameChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (m_tutorial != null) {
                m_tutorial.setName(m_name.getText().toString());
            }
        }
    };

    /**
     * Listener for description changes.
     */
    private TextWatcher onDescriptionChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (m_tutorial != null) {
                m_tutorial.setDescription(m_description.getText().toString());
            }
        }
    };

}
