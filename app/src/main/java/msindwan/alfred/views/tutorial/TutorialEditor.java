package msindwan.alfred.views.tutorial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Locale;
import msindwan.alfred.R;
import msindwan.alfred.models.Tutorial;
import msindwan.alfred.models.TutorialStep;

/**
 * Created by Mayank Sindwani on 2017-05-04.
 *
 */
public class TutorialEditor extends AppCompatActivity {

    private ArrayList<TutorialTab> m_tabs;
    private Tutorial m_tutorial;
    private int m_activeTab;

    private EditText m_editorFormStepDescription;
    private EditText m_editorFormStepTitle;
    private LinearLayout m_tab_layout;
    private LinearLayout m_editorForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_tutorial_editor);
        init();
    }

    private void init() {
        m_tutorial     = new Tutorial("New Tutorial");
        m_tabs         = new ArrayList<>();
        m_tab_layout   = (LinearLayout)findViewById(R.id.tutorial_editor_tab_layout);
        m_editorForm   = (LinearLayout)findViewById(R.id.editor_form);
        m_editorFormStepDescription = (EditText)findViewById(R.id.editor_form_step_description);
        m_editorFormStepTitle = (EditText)findViewById(R.id.editor_form_step_title);
        m_activeTab    = 0;

        TutorialStep step = new TutorialStep();
        TutorialTab tab = new TutorialTab(this);
        int tabIndex = m_tutorial.getNumSteps();

        tab.setButtonOnClickListener(onTabClick);
        tab.setRemoveOnClickListener(onTabRemoveClick);

        m_tutorial.addStep(step);
        m_tab_layout.addView(tab);
        m_tabs.add(tab);

        tab.setText(String.format(Locale.getDefault(), "%d", tabIndex + 1));
        tab.setBackgroundColour(R.color.colorSecondaryDark);
    }

    private void addStep() {
        TutorialStep step = new TutorialStep();
        TutorialTab tab = new TutorialTab(this);
        int tabIndex = m_tutorial.getNumSteps();

        tab.setButtonOnClickListener(onTabClick);
        tab.setRemoveOnClickListener(onTabRemoveClick);

        m_tutorial.addStep(step);
        m_tab_layout.addView(tab);
        m_tabs.add(tab);

        tab.setText(String.format(Locale.getDefault(), "%d", tabIndex + 1));
        setActiveTab(tabIndex);
    }

    private void removeStep(TutorialTab tab) {
        int index = m_tabs.indexOf(tab);

        m_tab_layout.removeView(tab);
        m_tutorial.removeStep(index);
        m_tabs.remove(index);

        for (int i = index; i < m_tabs.size(); i++) {
            m_tabs.get(i).setText(String.format(Locale.getDefault(), "%d", i + 1));
        }

        if (index == m_activeTab) {
            if (index == 0) {
                setActiveTab(0);
            } else {
                setActiveTab(index - 1);
            }
        } else if (index < m_activeTab) {
            m_activeTab--;
        }
    }

    private void setActiveTab(int index) {
        if (index == m_activeTab) return;

        for (TutorialTab t : m_tabs) {
            t.setBackgroundColour(R.color.colorPrimary);
        }
        TutorialTab tab = m_tabs.get(index);
        tab.setBackgroundColour(R.color.colorSecondaryDark);
        m_activeTab = index;

        TutorialStep step = m_tutorial.getStep(index);

        m_editorForm.setAlpha(0f);

        m_editorFormStepTitle.setText(step.getTitle());
        m_editorFormStepDescription.setText(step.getDescription());

        m_editorForm.animate()
                .alpha(1f)
                .setDuration(getResources().getInteger(
                        android.R.integer.config_shortAnimTime))
                .setListener(null);
    }

    private View.OnClickListener onTabClick = new View.OnClickListener() {
        public void onClick(View v) {
            TutorialTab tab = (TutorialTab)(v.getParent()).getParent();
            setActiveTab(m_tabs.indexOf(tab));
        }
    };

    private View.OnClickListener onTabRemoveClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (m_tabs.size() > 1) {
                TutorialTab tab = (TutorialTab) (v.getParent()).getParent();
                removeStep(tab);
            }
        }
    };

    public void onCreateStep(View view) { addStep(); }

    public void onCancelEditor(View view) { finish(); }
}
