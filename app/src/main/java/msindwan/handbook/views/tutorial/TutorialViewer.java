package msindwan.handbook.views.tutorial;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.Locale;

import msindwan.handbook.R;
import msindwan.handbook.data.DatabaseHelper;
import msindwan.handbook.models.Step;
import msindwan.handbook.models.Tutorial;
import msindwan.handbook.views.tutorial.components.StepForm;
import msindwan.handbook.views.tutorial.components.RequirementListItem;
import msindwan.handbook.views.tutorial.components.SummaryForm;
import msindwan.handbook.views.widgets.Accordion;

/**
 * Created by Mayank Sindwani on 2017-05-25.
 *
 */

public class TutorialViewer extends AppCompatActivity {

    private Accordion m_accordion;
    private Tutorial m_tutorial;
    private TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.tutorial_viewer);
        init(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("tutorial", m_tutorial);
        outState.putInt("activePanel", m_accordion.getActivePanel());
        super.onSaveInstanceState(outState);
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    getMenuInflater().inflate(R.menu.tutorial_editor_actionbar, menu);
    //    return super.onCreateOptionsMenu(menu);
    //}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
                // TODO: Show error message
                finish();
                return;
            }

            // Fetch the corresponding tutorial.
            DatabaseHelper helper = DatabaseHelper.getInstance(this);
            helper.fetch(m_tutorial, Integer.parseInt(tutorial_id));
        } else {
            // Otherwise, retrieve the old state and render the view
            // accordingly.
            m_tutorial = savedInstanceState.getParcelable("tutorial");
            activePanel = savedInstanceState.getInt("activePanel", 0);
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(m_tutorial.getName());
        }

        // Add the summary panel.
        Accordion.Panel panel = new Accordion.Panel(this);
        SummaryForm summary = new SummaryForm(this, m_tutorial, panel);
        panel.setPanelView(summary);
        panel.setTitle(m_tutorial.getName());
        m_accordion.addPanel(panel);

        // Add step views.
        for (int i = 0; i < m_tutorial.getNumSteps(); i++) {
            Step step = m_tutorial.getStep(i);

            // Create the views.
            panel = new Accordion.Panel(this);
            final StepForm stepView = new StepForm(this, step, panel);

            stepView.setPlayOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Step s = stepView.getStep();
                    int state = (int)v.getTag();

                    switch (state) {
                        case 0:
                            v.setTag(1);
                            t1.speak(s.getTitle(), TextToSpeech.QUEUE_ADD, null);
                            t1.speak(s.getInstructions(), TextToSpeech.QUEUE_ADD, null);
                            break;
                        case 1:
                            v.setTag(0);
                            t1.stop();
                            break;
                    }
                }
            });

            panel.setPanelView(stepView);
            m_accordion.addPanel(panel);
            panel.setTitle(
                String.format(Locale.getDefault(), "Step %d - %s", i + 1, step.getTitle())
            );

            if (step.getNumRequirements() > 0) {
                TextView requirementPlaceholder = (TextView)
                        findViewById(R.id.tutorial_viewer_requirement_placeholder);
                requirementPlaceholder.setVisibility(View.GONE);
            }

            // Add requirement list items.
            for (int j = 0; j < step.getNumRequirements(); j++) {
                RequirementListItem item = new RequirementListItem(
                        this,
                        step.getRequirement(j),
                        stepView
                );
                stepView.addRequirementListItem(item);
            }
        }

        m_accordion.setActivePanel(m_accordion.getPanel(activePanel));
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.getDefault());
                }
            }
        });
    }

}
