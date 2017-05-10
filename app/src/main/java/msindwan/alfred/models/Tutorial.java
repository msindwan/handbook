package msindwan.alfred.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by Mayank Sindwani on 2017-05-06.
 *
 * Tutorial:
 * Represents a tutorial to display.
 */
public class Tutorial implements Parcelable {

    private ArrayList<TutorialStep> m_steps;
    private String m_name;

    // Constructor.
    public Tutorial() {
        m_steps = new ArrayList<>();
    }

    private Tutorial(Parcel in) {
        m_steps = new ArrayList<>();
        m_name = in.readString();
        in.readTypedList(m_steps, TutorialStep.CREATOR);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return m_name;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(m_name);
        out.writeTypedList(m_steps);
    }

    public static final Parcelable.Creator<Tutorial> CREATOR = new Parcelable.Creator<Tutorial>() {
        public Tutorial createFromParcel(Parcel in) {
            return new Tutorial(in);
        }

        public Tutorial[] newArray(int size) {
            return new Tutorial[size];
        }
    };


    /**
     * Getter for the name.
     * @return : The tutorial name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Setter for the name.
     * @param name : The name to set.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * Returns the number of steps in the tutorial.
     * @return : The number of steps.
     */
    public int getNumSteps() {
        return m_steps.size();
    }

    /**
     * Returns the specified tutorial step.
     * @param index : The position of the desired step.
     * @return : The step at the specified position.
     */
    public TutorialStep getStep(int index) {
        return m_steps.get(index);
    }

    /**
     * Adds a step to the tutorial.
     * @param step : The step to add.
     * @return True if successful; false otherwise.
     */
    public boolean addStep(TutorialStep step) {
        return m_steps.add(step);
    }

    /**
     * Removes the specified step from the tutorial.
     * @param index : The position of the step to remove.
     * @return : The step that was removed.
     */
    public TutorialStep removeStep(int index) {
        return m_steps.remove(index);
    }

}
