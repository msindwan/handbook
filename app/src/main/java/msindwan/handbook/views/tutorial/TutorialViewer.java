/*
 * Created by Mayank Sindwani on 2017-05-30.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.views.tutorial;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import msindwan.handbook.R;
import msindwan.handbook.data.DatabaseHelper;
import msindwan.handbook.models.Requirement;
import msindwan.handbook.models.Step;
import msindwan.handbook.models.Tutorial;
import msindwan.handbook.views.tutorial.components.StepForm;
import msindwan.handbook.views.tutorial.components.RequirementListItem;
import msindwan.handbook.views.tutorial.components.SummaryForm;
import msindwan.handbook.views.widgets.Accordion;


/**
 * TutorialViewer:
 * Defines a view component for viewing tutorials.
 */
// TODO: Add image support
// TODO: Accept voice commands to play, pause, etc
public class TutorialViewer extends AppCompatActivity {

    private static final int TTS_STATE_STOP = 0;
    private static final int TTS_STATE_PLAY = 1;
    private static final String END_OF_SPEECH = "eos";

    private ImageButton m_playButton;
    private TextView m_currentStep;
    private Accordion m_accordion;
    private Tutorial m_tutorial;
    private TextToSpeech m_tts;

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

    @Override
    protected void onPause() {
        stopSpeech();
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        m_tts.shutdown();
    }

    /**
     * Support wrapper for tts.
     *
     * @param text The text to recite.
     * @param queue The speak queue flag.
     * @param utteranceId The utterance ID.
     */
    public void speak(String text, int queue, String utteranceId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            m_tts.speak(text, queue, null, utteranceId);
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
            //noinspection deprecation
            m_tts.speak(text, queue, params);
        }
    }

    /**
     * Support wrapper for tts
     * @param text The text to recite.
     * @param queue The speak queue flag.
     */
    public void speak(String text, int queue) {
        speak(text, queue, null);
    }

    /**
     * Initializes the component on mount.
     */
    private void init(Bundle savedInstanceState) {
        int activePanel = 0;

        m_accordion   = (Accordion)findViewById(R.id.tutorial_panels);
        m_playButton  = (ImageButton)findViewById(R.id.tutorial_viewer_play);
        m_currentStep = (TextView)findViewById(R.id.tutorial_viewer_step);
        m_tts = new TextToSpeech(getApplicationContext(), onTTSInitListener);
        m_tts.setOnUtteranceProgressListener(onUtteranceProgressListener);


        m_accordion.setAccordionListener(accordionListener);
        m_playButton.setOnClickListener(onPlayButtonClick);
        m_playButton.setTag(TTS_STATE_STOP);

        // Preserve the state of the view.
        if(savedInstanceState == null
                || !savedInstanceState.containsKey("tutorial")
                || savedInstanceState.get("tutorial") == null) {
            // If no tutorial is saved in the current context, fetch the tutorial argument.
            String tutorial_id = getIntent().getStringExtra("tutorial_id");

            m_tutorial = new Tutorial();
            if (tutorial_id == null) {
                Toast.makeText(
                    this,
                    getResources().getString(R.string.unknown_error),
                    Toast.LENGTH_SHORT
                ).show();
                finish();
                return;
            }

            // Fetch the corresponding tutorial.
            DatabaseHelper helper = DatabaseHelper.getInstance(this);
            helper.fetch(m_tutorial, Integer.parseInt(tutorial_id));
            m_tutorial.setNumViews(m_tutorial.getNumViews() + 1);
            helper.update(m_tutorial);
        } else {
            // Otherwise, retrieve the old state and render the view
            // accordingly.
            m_tutorial = savedInstanceState.getParcelable("tutorial");
            activePanel = savedInstanceState.getInt("activePanel", 0);
        }

        // Update the action bar.
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

        // Combine requirements and display them in the tutorial summary.
        for (Requirement requirement : m_tutorial.getAllRequirements()) {
            RequirementListItem item = new RequirementListItem(
                this,
                requirement,
                summary
            );
            item.toggleDeleteButton(false);
            summary.addRequirementListItem(item);
        }

        // Add step views.
        for (int i = 0; i < m_tutorial.getNumSteps(); i++) {
            Step step = m_tutorial.getStep(i);

            // Create the views.
            panel = new Accordion.Panel(this);
            final StepForm stepView = new StepForm(this, step, panel);

            panel.setPanelView(stepView);
            m_accordion.addPanel(panel);
            panel.setTitle(
                getResources().getString(R.string.nth_step_title, i + 1, step.getTitle())
            );

            // Add requirement list items.
            for (int j = 0; j < step.getNumRequirements(); j++) {
                RequirementListItem item = new RequirementListItem(
                        this,
                        step.getRequirement(j),
                        stepView
                );
                item.toggleDeleteButton(false);
                stepView.addRequirementListItem(item);
            }
        }

        m_accordion.setActivePanel(m_accordion.getPanel(activePanel));
        m_currentStep.setText(getResources().getString(
            R.string.nth_step,
            activePanel,
            m_tutorial.getNumSteps()
        ));
    }

    /**
     * Uses the text-to-speech interface for the specified panel.
     *
     * @param panelIndex the panel to recite.
     */
    private void panelToSpeech(int panelIndex) {
        m_playButton.setTag(TTS_STATE_PLAY);
        m_playButton.setImageResource(R.mipmap.ic_stop_black_24dp);

        if (panelIndex == 0) {
            // Speak the tutorial name.
            speak(m_tutorial.getName(),TextToSpeech.QUEUE_ADD);

            // Speak the collection of requirements for the tutorial.
            Collection<Requirement> requirements = m_tutorial.getAllRequirements();
            if (!requirements.isEmpty()) {
                speak(getResources().getString(R.string.requirements_tts), TextToSpeech.QUEUE_ADD);

                for (Requirement r : requirements) {
                    speak(r.toString(), TextToSpeech.QUEUE_ADD);
                }
            }

            // Speak the description.
            speak(m_tutorial.getDescription(),TextToSpeech.QUEUE_ADD, END_OF_SPEECH);
        } else {
            Step step = m_tutorial.getStep(panelIndex - 1);

            // Speak the step title.
            speak(
                getResources()
                    .getString(
                        R.string.nth_step_title,
                        panelIndex,
                        step.getTitle()
                    ),
                TextToSpeech.QUEUE_ADD
            );

            // Speak the step requirements.
            if (step.getNumRequirements() > 0) {
                speak(getResources().getString(R.string.requirements_tts), TextToSpeech.QUEUE_ADD);

                for (int i = 0; i < step.getNumRequirements(); i++) {
                    Requirement r = step.getRequirement(i);
                    speak(r.toString(), TextToSpeech.QUEUE_ADD);
                }
            }

            speak(getResources().getString(R.string.instructions_tts), TextToSpeech.QUEUE_ADD);
            speak(step.getInstructions(), TextToSpeech.QUEUE_ADD, END_OF_SPEECH);
        }
    }

    /**
     * Stops the text-to-speech instance.
     */
    private void stopSpeech() {
        m_tts.stop();
        m_playButton.setTag(TTS_STATE_STOP);
        m_playButton.setImageResource(R.mipmap.ic_play_arrow_black_24dp);
    }

    /**
     * Listener for tts initialization.
     */
    private TextToSpeech.OnInitListener onTTSInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if(status != TextToSpeech.ERROR) {
                m_tts.setLanguage(Locale.getDefault());
            }
        }
    };

    /**
     * Listener for accordion actions.
     */
    private Accordion.AccordionListener accordionListener = new Accordion.AccordionListener() {
        @Override
        public boolean onHeaderClick(Accordion.Panel panel) {
            m_currentStep.setText(getResources().getString(
                R.string.nth_step,
                m_accordion.getPanelIndex(panel),
                m_tutorial.getNumSteps()
            ));
            stopSpeech();
            return true;
        }
    };

    /**
     * Listener for the play button.
     */
    private View.OnClickListener onPlayButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ((int)v.getTag() == TTS_STATE_PLAY) {
                stopSpeech();
            } else {
                panelToSpeech(m_accordion.getActivePanel());
            }
        }
    };

    /**
     * Listener for utterance progress.
     */
    private UtteranceProgressListener onUtteranceProgressListener
            = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
        }
        @Override
        public void onDone(final String s) {
            if (s.equals(END_OF_SPEECH)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        stopSpeech();
                    }
                });
            }
        }
        @Override
        public void onError(String s) {
        }
    };
}
