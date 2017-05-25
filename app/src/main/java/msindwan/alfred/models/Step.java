package msindwan.alfred.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

/**
 * Created by Mayank Sindwani on 2017-05-06.
 *
 * Step:
 * Represents the data pertaining to a single step
 * in a tutorial.
 */
@SuppressWarnings("unused")
public class Step implements Parcelable {

    private ArrayList<Requirement> m_requirements;
    private Boolean m_deleted;
    private String m_instructions;
    private Long m_tutorialId;
    private String m_title;
    private Long m_index;
    private Long m_id;

    // Constructors.
    public Step() {
        m_requirements = new ArrayList<>();
        m_deleted = false;
    }

    private Step(Parcel in) {
        m_requirements = new ArrayList<>();
        m_deleted = (Boolean)in.readValue(Boolean.class.getClassLoader());
        m_title = in.readString();
        m_instructions = in.readString();
        m_tutorialId = (long)in.readValue(long.class.getClassLoader());
        m_index = (long)in.readValue(long.class.getClassLoader());
        m_id = (long)in.readValue(long.class.getClassLoader());
        in.readTypedList(m_requirements, Requirement.CREATOR);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. (Not required for Step)

     * @return 0 (no special objects)
     */
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten Step in to a Parcel.
     *
     * @param out The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     */
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(m_deleted);
        out.writeString(m_title);
        out.writeString(m_instructions);
        out.writeValue(m_tutorialId);
        out.writeValue(m_index);
        out.writeValue(m_id);
        out.writeTypedList(m_requirements);
    }

    /**
     * Interface that must be implemented and provided as a public CREATOR
     * field that generates instances of your Parcelable class from a Parcel.
     */
    public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {
        /**
         * Create a new instance of the Step class.
         *
         * @param in The Parcel to read the object's data from.
         * @return a new instance of the Step class.
         */
        @Contract("_ -> !null")
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        /**
         * Create a new array of the Step class.
         *
         * @param size Size of the array.
         * @return an array of the Step class.
         */
        @Contract(value = "_ -> !null", pure = true)
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    /**
     * Getter for the id.
     *
     * @return the tutorial id.
     */
    public Long getId() {
        return m_id;
    }

    /**
     * Setter for the id.
     *
     * @param id The id to set.
     */
    public void setId(Long id) {
        m_id = id;
    }

    /**
     * Getter for the index.
     *
     * @return the step index.
     */
    public Long getIndex() {
        return m_index;
    }

    /**
     * Setter for the index.
     *
     * @param index The index to set.
     */
    public void setIndex(Long index) {
        m_index = index;
    }

    /**
     * Getter for the tutorial id.
     *
     * @return the step's tutorial id.
     */
    public Long getTutorialId() {
        return m_tutorialId;
    }

    /**
     * Setter for the tutorial id.
     *
     * @param id the step's tutorial id.
     */
    public void setTutorialId(Long id) {
        m_tutorialId = id;
    }

    /**
     * Getter for the instructions.
     *
     * @return the step's instructions.
     */
    public String getInstructions() {
        return m_instructions;
    }

    /**
     * Setter for the instructions.
     *
     * @param instructions the instructions to set.
     */
    public void setInstructions(String instructions) {
        m_instructions = instructions;
    }

    /**
     * Getter for the title.
     *
     * @return the step's title.
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * Setter for the title.
     *
     * @param title the step's title.
     */
    public void setTitle(String title) {
        m_title = title;
    }

    /**
     * Returns the number of requirements in the step.
     *
     * @return the number of requirements.
     */
    public int getNumRequirements() {
        return m_requirements.size();
    }

    /**
     * Returns the specified step requirement.
     *
     * @param index The position of the desired requirement.
     * @return the requirement at the specified position.
     */
    public Requirement getRequirement(int index) {
        return m_requirements.get(index);
    }

    /**
     * Adds a step to the tutorial.
     *
     * @param requirement The step to add.
     * @return true if successful; false otherwise.
     */
    public boolean addRequirement(Requirement requirement) {
        return m_requirements.add(requirement);
    }

    /**
     * Removes the specified requirement from the step.
     *
     * @param index The position of the requirement to remove.
     * @return the requirement that was removed.
     */
    public Requirement removeRequirement(int index) {
        return m_requirements.remove(index);
    }

    /**
     * Removes the specified requirement from the step.
     *
     * @param requirement The requirement to remove.
     * @return true if the requirement existed in the array; false otherwise.
     */
    public boolean removeRequirement(Requirement requirement) {
        return m_requirements.remove(requirement);
    }

    /**
     * Setter for the deleted flag.
     *
     * @param deleted the deleted flag to set.
     */
    public void setDeleted(Boolean deleted) {
        m_deleted = deleted;
    }

    /**
     * Getter for the deleted flag.
     *
     * @return the deleted flag.
     */
    public Boolean isDeleted() {
        return m_deleted;
    }
}
