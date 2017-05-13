package msindwan.alfred.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Mayank Sindwani on 2017-05-06.
 *
 * TutorialTable:
 * Represents a tutorial to display.
 */
public class Tutorial implements Parcelable {

    private ArrayList<TutorialStep> m_steps;
    private String m_name;

    // Constructors.
    public Tutorial() {
        m_steps = new ArrayList<>();
    }

    private Tutorial(Parcel in) {
        m_steps = new ArrayList<>();
        m_name = in.readString();
        in.readTypedList(m_steps, TutorialStep.CREATOR);
    }

    /**
     * Flatten TutorialTable in to a Parcel.
     *
     * @param out The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(m_name);
        out.writeTypedList(m_steps);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. (Not required for TutorialTable)

     * @return 0 (no special objects)
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Interface that must be implemented and provided as a public CREATOR
     * field that generates instances of your Parcelable class from a Parcel.
     */
    public static final Parcelable.Creator<Tutorial> CREATOR = new Parcelable.Creator<Tutorial>() {
        /**
         * Create a new instance of the TutorialTable class.
         *
         * @param in The Parcel to read the object's data from.
         * @return a new instance of the TutorialTable class.
         */
        public Tutorial createFromParcel(Parcel in) {
            return new Tutorial(in);
        }

        /**
         * Create a new array of the TutorialTable class.
         *
         * @param size Size of the array.
         * @return an array of the TutorialTable class.
         */
        public Tutorial[] newArray(int size) {
            return new Tutorial[size];
        }
    };

    /**
     * Getter for the name.
     *
     * @return the tutorial name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Setter for the name.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * Returns the number of steps in the tutorial.
     *
     * @return the number of steps.
     */
    public int getNumSteps() {
        return m_steps.size();
    }

    /**
     * Returns the specified tutorial step.
     *
     * @param index The position of the desired step.
     * @return the step at the specified position.
     */
    public TutorialStep getStep(int index) {
        return m_steps.get(index);
    }

    /**
     * Adds a step to the tutorial.
     *
     * @param step The step to add.
     * @return true if successful; false otherwise.
     */
    public boolean addStep(TutorialStep step) {
        return m_steps.add(step);
    }

    /**
     * Removes the specified step from the tutorial.
     *
     * @param index The position of the step to remove.
     * @return the step that was removed.
     */
    public TutorialStep removeStep(int index) {
        return m_steps.remove(index);
    }

}
