package msindwan.alfred.views.tutorial.components;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import msindwan.alfred.R;
import msindwan.alfred.models.Tutorial;

/**
 * Created by Mayank Sindwani on 2017-05-21.
 *
 * SummaryView:
 * Defines a view representing a tutorial's summary.
 */
public class EditSummaryView extends RelativeLayout {

    private EditText m_name;
    private EditText m_description;
    private Tutorial m_tutorial;

    // Constructors.
    public EditSummaryView(Context context, Tutorial tutorial, View tag) {
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
        inflate(context, R.layout.tutorial_editor_summary_panel, this);

        m_name = (EditText)findViewById(R.id.tutorial_editor_name);
        m_description = (EditText)findViewById(R.id.tutorial_editor_description);

        m_name.setText(m_tutorial.getName());
        m_description.setText(m_tutorial.getDescription());

        m_name.addTextChangedListener(onNameChanged);
        m_description.addTextChangedListener(onDescriptionChanged);
    }

    /**
     * Validates the tutorial and sets text field errors.
     *
     * @return True if valid; false otherwise.
     */
    public boolean validate() {
        String name = m_tutorial.getName();
        String description = m_tutorial.getDescription();

        if (name == null || name.isEmpty()) {
            m_name.setError("Title is Required");
            return false;
        }
        if (description == null || description.isEmpty()) {
            m_description.setError("Instructions are Required");
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
            m_tutorial.setName(m_name.getText().toString());
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
            m_tutorial.setDescription(m_description.getText().toString());
        }
    };

}
