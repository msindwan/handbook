package msindwan.alfred.views.tutorial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import java.util.Locale;

import msindwan.alfred.models.Requirement;
import msindwan.alfred.models.Step;
import msindwan.alfred.models.Tutorial;
import msindwan.alfred.R;
import msindwan.alfred.views.tutorial.components.RequirementDialogFragment;
import msindwan.alfred.views.tutorial.components.RequirementListItem;
import msindwan.alfred.views.tutorial.components.EditStepView;
import msindwan.alfred.views.tutorial.components.EditSummaryView;
import msindwan.alfred.views.widgets.Accordion;

/**
 * Created by Mayank Sindwani on 2017-05-04.
 *
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
                // TODO: Validate each panel and save the tutorial.
                // e.g
                // if (m_tutorial.getId() != null) {
                //     DatabaseHelper.getInstance(this).update(m_tutorial);
                // } else {
                //     DatabaseHelper.getInstance(this).insert(m_tutorial);
                // }
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

        m_accordion = (Accordion)findViewById(R.id.tutorial_panels);

        // Add the summary panel.
        Accordion.Panel panel = new Accordion.Panel(this);
        EditSummaryView summary = new EditSummaryView(this, panel);
        panel.setPanelView(summary);
        panel.setTitle("Summary");
        m_accordion.addPanel(panel);

        // Preserve the state of the view.
        if(savedInstanceState == null
                || !savedInstanceState.containsKey("tutorial")
                || savedInstanceState.get("tutorial") == null) {
            // If no tutorial is saved in the current context, create/fetch the tutorial.
            m_tutorial = getIntent().getParcelableExtra("tutorial");
            if (m_tutorial == null) {
                m_tutorial = new Tutorial();
                for (int i = 0; i < NUM_INITIAL_STEPS; i++) {
                    Step step = new Step();
                    m_tutorial.addStep(step);
                    addStepView(step);
                }
            }
        } else {
            // Otherwise, retrieve the old state and render the view
            // accordingly.
            m_tutorial = savedInstanceState.getParcelable("tutorial");
            for (int i = 0; i < m_tutorial.getNumSteps(); i++) {
                addStepView(m_tutorial.getStep(i));
            }
            int activePanel = savedInstanceState.getInt("activePanel", 0);
            m_accordion.setActivePanel(m_accordion.getPanel(activePanel));
        }

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

        EditStepView view;

        // Adjust move buttons according to the index and the number of panels.
        if (m_accordion.getNumPanels() > 2) {

            // Disable the "move up" button for the first panel.
            view = (EditStepView)m_accordion.getPanel(1).getPanelView();
            view.toggleMoveUpButton(false);
            view.toggleMoveDownButton(true);

            // Disable the move down button for the last panel.
            view = (EditStepView)m_accordion.getPanel(m_accordion.getNumPanels() - 1).getPanelView();
            view.toggleMoveUpButton(true);
            view.toggleMoveDownButton(false);

            // Enable both buttons for the views between the first and last step.
            for (int i = 2; i < m_accordion.getNumPanels() - 1; i++) {
                view = (EditStepView)m_accordion.getPanel(i).getPanelView();
                view.toggleMoveUpButton(true);
                view.toggleMoveDownButton(true);
            }

        } else if (m_accordion.getNumPanels() == 2) {
            // If only one step is present, disable movement.
            view = (EditStepView)m_accordion.getPanel(1).getPanelView();
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
        EditStepView stepView = new EditStepView(this, step, panel);

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
        // Find the index of the step.
        int stepIndex = m_accordion.getPanelIndex(panel) - 1;

        // Remove the parent panel and its corresponding step.
        m_accordion.removePanel(panel);
        m_tutorial.removeStep(stepIndex);

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
            // Find the panel index.
            Accordion.Panel panel = (Accordion.Panel)v.getTag();
            int panelIndex = m_accordion.getPanelIndex(panel);

            // If there is at least one panel before it, move it up.
            if (panelIndex > 1) {
                m_accordion.movePanel(panel, panelIndex - 1);
                m_tutorial.swapSteps(panelIndex, panelIndex - 1);
                repaintStepViews();
            }
        }
    };

    /**
     * Listener for moving a step view down.
     */
    private View.OnClickListener onMoveDown = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Find the panel index.
            Accordion.Panel panel = (Accordion.Panel)v.getTag();
            int panelIndex = m_accordion.getPanelIndex(panel);

            // If there is at least one panel after it, move it down.
            if (panelIndex < m_accordion.getNumPanels() - 1) {
                m_accordion.movePanel(panel, panelIndex + 1);
                m_tutorial.swapSteps(panelIndex, panelIndex + 1);
                repaintStepViews();
            }
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
            final EditStepView view = (EditStepView)m_accordion.getPanel(stepIndex + 1).getPanelView();
            final RequirementListItem item = new RequirementListItem(TutorialEditor.this, requirement);
            item.setRequirementOnRemoveListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Remove the requirement from the step and view.
                    Step step = view.getStep();
                    step.removeRequirement(requirement);
                    view.removeRequirementListItem(item);
                }
            });

            view.getStep().addRequirement(requirement);
            view.addRequirementListItem(item);
        }
    };
}

