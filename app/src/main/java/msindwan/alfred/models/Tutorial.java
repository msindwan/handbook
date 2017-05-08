package msindwan.alfred.models;

import java.util.ArrayList;

/**
 * Created by Mayank Sindwani on 2017-05-06.
 *
 * Tutorial:
 * Represents a tutorial to display.
 */
public class Tutorial {

    private ArrayList<TutorialStep> m_steps;
    private String m_name;

    // Constructor.
    public Tutorial(String name) {
        m_name = name;
        m_steps = new ArrayList<>();
    }

    // Getter for the name.
    public String getName() {
        return m_name;
    }

    // Setter for the name.
    public void setName(String name) {
        m_name = name;
    }

    // Returns the number of steps in the tutorial.
    public int getNumSteps() {
        return m_steps.size();
    }

    // Returns the specified tutorial step.
    public TutorialStep getStep(int index) {
        return m_steps.get(index);
    }

    // Adds a step to the tutorial.
    public boolean addStep(TutorialStep step) {
        return m_steps.add(step);
    }

    // Removes the specified step from the tutorial.
    public TutorialStep removeStep(int index) {
        return m_steps.remove(index);
    }

}
