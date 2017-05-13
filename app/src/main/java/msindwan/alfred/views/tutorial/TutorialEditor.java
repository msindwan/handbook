package msindwan.alfred.views.tutorial;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import msindwan.alfred.data.DataContentProvider;
import msindwan.alfred.data.schema.TutorialTable;
import msindwan.alfred.models.Tutorial;
import msindwan.alfred.models.TutorialStep;

import msindwan.alfred.R;

/**
 * Created by Mayank Sindwani on 2017-05-04.
 *
 * TutorialEditor:
 * Defines a view component for creating and editing tutorials.
 */
public class TutorialEditor extends AppCompatActivity {

    private Tutorial m_tutorial;
    private int m_activeTab;

    // View components.
    private ArrayList<TutorialTab> m_editorTabs;

    private EditText m_editorFormStepDescription;
    private EditText m_editorFormStepTitle;
    private EditText m_editorTutorialName;

    private LinearLayout m_editorTabLayout;
    private LinearLayout m_editorForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_tutorial_editor);
        init(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the state of the tutorial and active tab.
        outState.putParcelable("tutorial", m_tutorial);
        outState.putInt("activeTab", m_activeTab);
        super.onSaveInstanceState(outState);
    }

    /**
     * Initializes the component on mount.
     */
    private void init(Bundle savedInstanceState) {
        // Initialize components.
        m_editorTabs                = new ArrayList<>();
        m_editorTabLayout           = (LinearLayout)findViewById(R.id.tutorial_editor_tab_layout);
        m_editorForm                = (LinearLayout)findViewById(R.id.editor_form);
        m_editorFormStepDescription = (EditText)findViewById(R.id.editor_form_step_description);
        m_editorFormStepTitle       = (EditText)findViewById(R.id.editor_form_step_title);
        m_editorTutorialName        = (EditText)findViewById(R.id.tutorial_name);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        // Bind listeners.
        m_editorFormStepDescription.addTextChangedListener(onEditorFormStepDescriptionChanged);
        m_editorFormStepTitle.addTextChangedListener(onEditorFormStepTitleChanged);
        m_editorTutorialName.addTextChangedListener(onTutorialNameChanged);

        TutorialTab tab;

        // Initialize the activity data or retrieve the saved state.
        if(savedInstanceState == null || !savedInstanceState.containsKey("tutorial")) {
            m_tutorial = new Tutorial();

            // Add the initial step.
            TutorialStep initialStep = new TutorialStep();
            m_tutorial.addStep(initialStep);

            m_activeTab = 0;
            tab = addTab(m_activeTab);
        } else {
            m_tutorial = savedInstanceState.getParcelable("tutorial");

            // TODO: Use a fallback instead of throwing an exception
            if(m_tutorial == null) {
                throw new RuntimeException();
            }

            for (int i = 0; i < m_tutorial.getNumSteps(); i++) {
                addTab(i);
            }

            m_activeTab = savedInstanceState.getInt("activeTab", 0);
            tab = m_editorTabs.get(m_activeTab);
        }

        m_editorTutorialName.setText(m_tutorial.getName());
        tab.setBackgroundColour(R.color.colorSecondaryDark);
    }

    /**
     * Adds a step to the tutorial.
     *
     * @return : The position of the step in the tutorial
     */
    private TutorialTab addTab(int tabIndex) {
        TutorialTab newTab = new TutorialTab(this);

        // Add tab event listeners.
        newTab.setButtonOnClickListener(onTabClick);
        newTab.setRemoveOnClickListener(onTabRemoveClick);

        // Add the new step and the corresponding tab.
        m_editorTabLayout.addView(newTab);
        m_editorTabs.add(newTab);

        newTab.setText(String.format(Locale.getDefault(), "%d", tabIndex + 1));
        newTab.setBackgroundColour(R.color.colorPrimary);
        return newTab;
    }

    /**
     * Removes the step corresponding to the tab.
     *
     * @param tabIndex : The step's tab position.
     */
    private void removeTab(int tabIndex) {
        // Remove the tab.
        m_editorTabLayout.removeView(m_editorTabs.get(tabIndex));
        m_editorTabs.remove(tabIndex);

        // Update the tab step labels.
        for (int i = tabIndex; i < m_editorTabs.size(); i++) {
            m_editorTabs.get(i).setText(String.format(Locale.getDefault(), "%d", i + 1));
        }

        // Update the active tab.
        if (tabIndex == m_activeTab) {
            setActiveTab(Math.max(0, tabIndex - 1));
        } else if (tabIndex < m_activeTab) {
            m_activeTab--;
        }
    }

    /**
     * Sets the state of the specified tab to "active".
     *
     * @param tabIndex : The position of the tab
     */
    private void setActiveTab(int tabIndex) {
        TutorialTab tab = m_editorTabs.get(tabIndex);

        // Deactivate all fragments.
        for (TutorialTab t : m_editorTabs) {
            t.setBackgroundColour(R.color.colorPrimary);
        }

        // Activate the specified tab and set the active tab.
        tab.setBackgroundColour(R.color.colorSecondaryDark);
        m_activeTab = tabIndex;

        // Apply the step information to the form.
        TutorialStep step = m_tutorial.getStep(tabIndex);

        m_editorForm.setAlpha(0f);

        // Populate the form with the step information.
        m_editorFormStepTitle.setText(step.getTitle());
        m_editorFormStepDescription.setText(step.getDescription());

        m_editorForm.animate()
                .alpha(1f)
                .setDuration(getResources().getInteger(
                        android.R.integer.config_shortAnimTime))
                .setListener(null);
    }


    // Event handlers.

    /**
     * Handler to create a new step.
     *
     * @param view : The element.
     */
    public void onCreateStep(View view) {
        // Create a new step.
        int tabIndex = m_tutorial.getNumSteps();
        m_tutorial.addStep(new TutorialStep());

        // Add the corresponding tab and activate it.
        addTab(tabIndex);
        setActiveTab(tabIndex);
    }


    /**
     * Handler to save the current tutorial.
     *
     * @param view : The element.
     */
    public void onSaveEditor(View view) {
        final ProgressDialog progress=new ProgressDialog(this);
        progress.setMessage("Saving Tutorial");
        progress.show();

        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(2);

                    // Defines an object to contain the new values to insert
                    ContentValues mNewValues = new ContentValues();
                    mNewValues.put(TutorialTable.COL_NAME, m_tutorial.getName());
                    getContentResolver().insert(DataContentProvider.TUTORIAL_URI, mNewValues);

                    progress.dismiss();
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();
    }


    /**
     * Handler to cancel creating or editing a tutorial.
     *
     * @param view : The element.
     */
    public void onCancelEditor(View view) {
        finish();
    }

    /**
     * Listener for tab clicks.
     */
    private View.OnClickListener onTabClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            TutorialTab tab = (TutorialTab)(v.getParent()).getParent();

            int tabIndex = m_editorTabs.indexOf(tab);

            // If the tab is already active, don't do anything.
            if (tabIndex != m_activeTab) {
                setActiveTab(tabIndex);
            }
        }

    };

    /**
     * Listener for removing fragments.
     */
    private View.OnClickListener onTabRemoveClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (m_editorTabs.size() > 1) {
                TutorialTab tab = (TutorialTab) (v.getParent()).getParent();
                int tabIndex = m_editorTabs.indexOf(tab);

                // Remove the step and the corresponding tab.
                m_tutorial.removeStep(tabIndex);
                removeTab(tabIndex);
            }
        }

    };

    /**
     * Listener for step title changes.
     */
    private TextWatcher onEditorFormStepTitleChanged = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            m_tutorial.getStep(m_activeTab).setTitle(m_editorFormStepTitle.getText().toString());
        }
    };

    /**
     * Listener for step description changes.
     */
    private TextWatcher onEditorFormStepDescriptionChanged = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            m_tutorial.getStep(m_activeTab).setDescription(m_editorFormStepDescription.getText().toString());
        }
    };

    /**
     * Listener for tutorial name changes.
     */
    private TextWatcher onTutorialNameChanged = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            m_tutorial.setName(m_editorTutorialName.getText().toString());
        }
    };

}
