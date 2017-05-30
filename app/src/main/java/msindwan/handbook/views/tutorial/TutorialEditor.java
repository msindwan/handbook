/*
 * Created by Mayank Sindwani on 2017-05-04.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.views.tutorial;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import java.util.Locale;

import msindwan.handbook.data.DataContentProvider;
import msindwan.handbook.data.DatabaseHelper;
import msindwan.handbook.models.Requirement;
import msindwan.handbook.models.Step;
import msindwan.handbook.models.Tutorial;
import msindwan.handbook.R;
import msindwan.handbook.views.common.EditFormView;
import msindwan.handbook.views.tutorial.components.RequirementDialogFragment;
import msindwan.handbook.views.tutorial.components.RequirementListItem;
import msindwan.handbook.views.tutorial.components.EditStepForm;
import msindwan.handbook.views.tutorial.components.EditSummaryForm;
import msindwan.handbook.views.widgets.Accordion;

/**
 * TutorialEditor:
 * Defines a view component for creating and editing tutorials.
 */
public class TutorialEditor extends AppCompatActivity {

    private static final int NUM_INITIAL_STEPS = 2;

    private Accordion m_accordion;
    private Tutorial m_tutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.tutorial_editor);
        init(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("tutorial", m_tutorial);
        outState.putInt("activePanel", m_accordion.getActivePanel());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tutorial_editor_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tutorial_editor_add_step:
                // Add a new step.
                Step step = new Step();
                m_tutorial.addStep(step);
                addStepView(step);
                return true;

            case R.id.tutorial_editor_save:
                // Validate each panel.
                for (int i = 0; i < m_accordion.getNumPanels(); i++) {
                    Accordion.Panel panel = m_accordion.getPanel(i);
                    EditFormView editPanel = (EditFormView) panel.getPanelView();
                    if (!editPanel.validate()) {
                        m_accordion.setActivePanel(panel);
                        return true;
                    }
                }

                final ProgressDialog progress = new ProgressDialog(this);
                progress.setMessage("Saving Tutorial");
                progress.show();

                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        DatabaseHelper helper = DatabaseHelper.getInstance(TutorialEditor.this);
                        SQLiteDatabase db = helper.getWritableDatabase();

                        // TODO: Handle insert / update errors.
                        // TODO: Fix issue where state is deleted while saving
                        db.beginTransaction();
                        {
                            if (m_tutorial.getId() == null) {
                                helper.insert(m_tutorial);
                            } else {
                                helper.update(m_tutorial);
                            }
                            db.setTransactionSuccessful();
                        }
                        db.endTransaction();

                        // Notify the content provider.
                        ContentResolver resolver = getContentResolver();
                        resolver.notifyChange(DataContentProvider.TUTORIAL_URI, null);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        finish();
                        progress.dismiss();
                    }
                };

                t.start();
                return true;

            case android.R.id.home:
                // Exit.
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initializes the component on mount.
     */
    @SuppressWarnings("ConstantConditions")
    private void init(Bundle savedInstanceState) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        int activePanel = 0;
        m_accordion = (Accordion)findViewById(R.id.tutorial_panels);

        // Preserve the state of the view.
        if(savedInstanceState == null
                || !savedInstanceState.containsKey("tutorial")
                || savedInstanceState.get("tutorial") == null) {
            // If no tutorial is saved in the current context, fetch the tutorial argument.
            String tutorial_id = getIntent().getStringExtra("tutorial_id");

            m_tutorial = new Tutorial();
            if (tutorial_id == null) {
                // If no tutorial was passed, create a new instance.
                for (int i = 0; i < NUM_INITIAL_STEPS; i++) {
                    m_tutorial.addStep(new Step());
                }
            } else {
                // Fetch the corresponding tutorial.
                DatabaseHelper helper = DatabaseHelper.getInstance(this);
                helper.fetch(m_tutorial, Integer.parseInt(tutorial_id));
            }
        } else {
            // Otherwise, retrieve the old state and render the view
            // accordingly.
            m_tutorial = savedInstanceState.getParcelable("tutorial");
            activePanel = savedInstanceState.getInt("activePanel", 0);
        }

        // Add the summary panel.
        Accordion.Panel panel = new Accordion.Panel(this);
        EditSummaryForm summary = new EditSummaryForm(this, m_tutorial, panel);
        panel.setPanelView(summary);
        panel.setTitle("Summary");
        m_accordion.addPanel(panel);

        // Add step views.
        for (int i = 0; i < m_tutorial.getNumSteps(); i++) {
            Step step = m_tutorial.getStep(i);
            Accordion.Panel view = addStepView(step);
            EditStepForm stepVew = (EditStepForm)view.getPanelView();

            // Add requirement list items.
            for (int j = 0; j < step.getNumRequirements(); j++) {
                RequirementListItem item = new RequirementListItem(
                        this,
                        step.getRequirement(j),
                        stepVew
                );
                stepVew.addRequirementListItem(item);
                item.setRequirementOnRemoveListener(onRequirementRemoved);
            }
        }

        m_accordion.setActivePanel(m_accordion.getPanel(activePanel));
    }

    /**
     * Re-renders the step views and updates the steps
     * based on the state of the accordion.
     */
    private void repaintStepViews() {
        // Set the panel titles based on the index.
        for (int i = 1; i < m_accordion.getNumPanels(); i++) {
            Accordion.Panel panel = m_accordion.getPanel(i);
            panel.setTitle(String.format(Locale.getDefault(), "Step %d", i));
        }

        EditStepForm view;

        // Adjust move buttons according to the index and the number of panels.
        if (m_accordion.getNumPanels() > 2) {

            // Disable the "move up" button for the first panel.
            view = (EditStepForm)m_accordion.getPanel(1).getPanelView();
            view.toggleMoveUpButton(false);
            view.toggleMoveDownButton(true);

            // Disable the move down button for the last panel.
            view = (EditStepForm)m_accordion.getPanel(m_accordion.getNumPanels() - 1).getPanelView();
            view.toggleMoveUpButton(true);
            view.toggleMoveDownButton(false);

            // Enable both buttons for the views between the first and last step.
            for (int i = 2; i < m_accordion.getNumPanels() - 1; i++) {
                view = (EditStepForm)m_accordion.getPanel(i).getPanelView();
                view.toggleMoveUpButton(true);
                view.toggleMoveDownButton(true);
            }

        } else if (m_accordion.getNumPanels() == 2) {
            // If only one step is present, disable movement.
            view = (EditStepForm)m_accordion.getPanel(1).getPanelView();
            view.toggleMoveUpButton(false);
            view.toggleMoveDownButton(false);
        }
    }

    /**
     * Adds a step view to the editor.
     *
     * @return the added view instance.
     */
    private Accordion.Panel addStepView(Step step) {
        // Create the views.
        Accordion.Panel panel = new Accordion.Panel(this);
        EditStepForm stepView = new EditStepForm(this, step, panel);

        // Bind listeners.
        stepView.setOnAddRequirementListener(onAddRequirement);
        stepView.setOnRemoveListener(onRemoveStepView);
        stepView.setOnMoveDownListener(onMoveDown);
        stepView.setOnMoveUpListener(onMoveUp);

        panel.setPanelView(stepView);
        m_accordion.addPanel(panel);
        repaintStepViews();
        return panel;
    }

    /**
     * Removes a step view from the editor.
     *
     * @param panel the parent panel to remove.
     */
    private void removeStepView(Accordion.Panel panel) {
        // Find the corresponding step index.
        Step step = ((EditStepForm)panel.getPanelView()).getStep();

        // Remove the parent panel and its corresponding step.
        m_accordion.removePanel(panel);

        if (step.getId() != null) {
            step.setDeleted(true);
        } else {
            m_tutorial.removeStep(step.getIndex().intValue());
        }

        repaintStepViews();
    }

    /**
     * Moves a panel up or down.
     * @param panel the panel to move.
     * @param newIndex the index of the panel to swap with.
     */
    private void movePanel(Accordion.Panel panel, int newIndex) {
        Accordion.Panel nextPanel = m_accordion.getPanel(newIndex);
        Step a = ((EditStepForm)panel.getPanelView()).getStep();
        Step b = ((EditStepForm)nextPanel.getPanelView()).getStep();

        m_accordion.movePanel(panel, newIndex);
        m_tutorial.swapSteps(a.getIndex().intValue(), b.getIndex().intValue());
        repaintStepViews();
    }

    // Event Handlers.

    /**
     * Listener for removing a step view.
     */
    private View.OnClickListener onRemoveStepView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            removeStepView((Accordion.Panel)v.getTag());
        }
    };

    /**
     * Listener for moving a step view up.
     */
    private View.OnClickListener onMoveUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Accordion.Panel panel = (Accordion.Panel)v.getTag();
            int panelIndex = m_accordion.getPanelIndex(panel);
            movePanel(panel, panelIndex - 1);
        }
    };

    /**
     * Listener for moving a step view down.
     */
    private View.OnClickListener onMoveDown = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Accordion.Panel panel = (Accordion.Panel)v.getTag();
            int panelIndex = m_accordion.getPanelIndex(panel);
            movePanel(panel, panelIndex + 1);
        }
    };

    /**
     * Listener for adding requirements.
     */
    private View.OnClickListener onAddRequirement = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Create a new dialog.
            RequirementDialogFragment dialog = new RequirementDialogFragment();
            Accordion.Panel panel = (Accordion.Panel)v.getTag();

            // Find the step index and pass it as an argument to the dialog.
            int stepIndex = m_accordion.getPanelIndex(panel) - 1;
            Bundle bundle = new Bundle();
            bundle.putInt("stepIndex", stepIndex);
            dialog.setArguments(bundle);

            // Set the requirement dialog listener.
            dialog.setRequirementDialogListener(onSubmitRequirement);
            dialog.show(getSupportFragmentManager(), "RequirementDialogFragment");
        }
    };

    /**
     * Listener for submitting requirements.
     */
    private RequirementDialogFragment.RequirementDialogListener onSubmitRequirement
            = new RequirementDialogFragment.RequirementDialogListener() {
        @Override
        public void onSubmit(int stepIndex, final Requirement requirement) {
            // Add the new requirement.
            final EditStepForm view = (EditStepForm)m_accordion.getPanel(stepIndex + 1).getPanelView();
            final RequirementListItem item = new RequirementListItem(TutorialEditor.this, requirement, view);
            item.setRequirementOnRemoveListener(onRequirementRemoved);
            view.getStep().addRequirement(requirement);
            view.addRequirementListItem(item);
        }
    };

    /**
     * Listener for removing requirements.
     */
    private View.OnClickListener onRequirementRemoved = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Remove the requirement from the step and view.
            RequirementListItem item = (RequirementListItem) v.getTag();
            EditStepForm view = (EditStepForm)item.getTag();

            Requirement requirement = item.getRequirement();
            Step step = view.getStep();

            // If it exists in the db, mark it for deletion.
            // Otherwise, remove it from the tutorial.
            if (requirement.getId() != null) {
                requirement.setDeleted(true);
            } else {
                step.removeRequirement(requirement);
            }
            view.removeRequirementListItem(item);
        }
    };
}

