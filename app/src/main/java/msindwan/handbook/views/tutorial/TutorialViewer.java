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
package msindwan.handbook.views.tutorial;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Locale;

import msindwan.handbook.R;
import msindwan.handbook.data.DatabaseHelper;
import msindwan.handbook.models.Requirement;
import msindwan.handbook.models.Step;
import msindwan.handbook.models.Tutorial;
import msindwan.handbook.util.Support;
import msindwan.handbook.views.tutorial.components.StepForm;
import msindwan.handbook.views.tutorial.components.SummaryForm;
import msindwan.handbook.views.widgets.Carousel;


/**
 * TutorialViewer:
 * Defines a view component for viewing tutorials.
 */
public class TutorialViewer extends AppCompatActivity {

    private static final String END_OF_SPEECH = "eos";
    private static final int TTS_STATE_STOP = 0;
    private static final int TTS_STATE_PLAY = 1;

    private FloatingActionButton m_playButton;
    private TextView m_title;
    private Carousel m_carousel;
    private Tutorial m_tutorial;
    private TextToSpeech m_tts;

    /**
     * TutorialTabPager:
     * Defines the fragment state pager for tutorial tabs.
     */
    private class TutorialViewerPager extends FragmentStatePagerAdapter {

        private int m_numTabs;

        // Constructor.
        private TutorialViewerPager(FragmentManager fm, int numTabs) {
            super(fm);
            m_numTabs = numTabs;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            // Return a summary fragment.
            if (position == 0) {
                SummaryForm form = new SummaryForm();
                args.putParcelable("tutorial", m_tutorial);
                form.setArguments(args);
                return form;
            }
            // Return a step fragment.
            Fragment stepForm = new StepForm();
            args.putParcelable("step", m_tutorial.getStep(position - 1));
            stepForm.setArguments(args);
            return stepForm;
        }

        @Override
        public int getCount() {
            return m_numTabs;
        }
    }

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
     * Initializes the component on mount.
     */
    private void init(Bundle savedInstanceState) {
        m_tts = new TextToSpeech(getApplicationContext(), onTTSInitListener);
        m_tts.setOnUtteranceProgressListener(onUtteranceProgressListener);

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
        }

        // Update the action bar.
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(m_tutorial.getName());
        }

        m_playButton = (FloatingActionButton)findViewById(R.id.tutorial_viewer_play_button);
        m_carousel = (Carousel)findViewById(R.id.tutorial_viewer_carousel);
        m_title = (TextView)findViewById(R.id.tutorial_viewer_title);

        m_title.setText(m_tutorial.getName());
        m_carousel.setViewPagerListener(onPageChangeListener);
        m_carousel.setAdapter(
                new TutorialViewerPager(
                        getSupportFragmentManager(),
                        m_tutorial.getNumSteps() + 1
                )
        );

        m_playButton.setOnClickListener(onPlayButtonClick);
        m_playButton.setTag(TTS_STATE_STOP);
    }

    /**
     * Uses the text-to-speech interface for the tutorial.
     *
     * @param index the page of the tutorial to recite.
     */
    private void tutorialToSpeech(int index) {
        m_playButton.setTag(TTS_STATE_PLAY);
        m_playButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.mipmap.ic_stop_black_24dp));

        if (index == 0) {
            // Speak the tutorial name.
            Support.speak(m_tts, m_tutorial.getName(),TextToSpeech.QUEUE_ADD);

            // Speak the collection of requirements for the tutorial.
            Collection<Requirement> requirements = m_tutorial.getAllRequirements();
            if (!requirements.isEmpty()) {
                Support.speak(m_tts, getResources().getString(R.string.requirements_tts), TextToSpeech.QUEUE_ADD);

                for (Requirement r : requirements) {
                    Support.speak(m_tts, r.toString(), TextToSpeech.QUEUE_ADD);
                }
            }

            // Speak the description.
            Support.speak(m_tts, m_tutorial.getDescription(),TextToSpeech.QUEUE_ADD, END_OF_SPEECH);
        } else {
            Step step = m_tutorial.getStep(index - 1);

            // Speak the step title.
            Support.speak(m_tts,
                getResources()
                    .getString(
                        R.string.nth_step_title,
                        index,
                        step.getTitle()
                    ),
                TextToSpeech.QUEUE_ADD
            );

            // Speak the step requirements.
            if (step.getNumRequirements() > 0) {
                Support.speak(m_tts, getResources().getString(R.string.requirements_tts), TextToSpeech.QUEUE_ADD);

                for (int i = 0; i < step.getNumRequirements(); i++) {
                    Requirement r = step.getRequirement(i);
                    Support.speak(m_tts, r.toString(), TextToSpeech.QUEUE_ADD);
                }
            }

            Support.speak(m_tts, getResources().getString(R.string.instructions_tts), TextToSpeech.QUEUE_ADD);
            Support.speak(m_tts, step.getInstructions(), TextToSpeech.QUEUE_ADD, END_OF_SPEECH);
        }
    }

    /**
     * Stops the text-to-speech instance.
     */
    private void stopSpeech() {
        m_tts.stop();
        m_playButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.mipmap.ic_play_arrow_black_24dp));
        m_playButton.setTag(TTS_STATE_STOP);
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
     * Listener for the play button.
     */
    private View.OnClickListener onPlayButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ((int)m_playButton.getTag() == TTS_STATE_PLAY) {
                stopSpeech();
            } else {
                tutorialToSpeech(m_carousel.getActivePage());
            }
        }
    };

    /**
     * Listener for view pager changes.
     */
    private ViewPager.OnPageChangeListener onPageChangeListener
            = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {}
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageSelected(int position) {
            stopSpeech();
            if (position == 0) {
                m_title.setText(m_tutorial.getName());
            } else {
                Step step = m_tutorial.getStep(position - 1);
                m_title.setText(
                    getResources().getString(
                            R.string.nth_step_title, position, step.getTitle()
                    )
                );
            }
        }
    };

    /**
     * Listener for utterance progress.
     */
    private UtteranceProgressListener onUtteranceProgressListener
            = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {}
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
        public void onError(String s) {}
    };
}
