package msindwan.alfred.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.Contract;


/**
 * Created by Mayank Sindwani on 2017-05-18.
 *
 * Requirement:
 * Represents the data in a requirement.
 */
@SuppressWarnings("unused")
public class Requirement implements Parcelable {

    private Boolean m_deleted;
    private Boolean m_optional;
    private Double m_amount;
    private String m_name;
    private String m_unit;
    private Long m_stepId;
    private Long m_id;

    // Constructors.
    public Requirement() {
        m_optional = false;
        m_deleted = false;
    }

    private Requirement(Parcel in) {
        m_name = in.readString();
        m_unit = in.readString();
        m_amount = (double)in.readValue(double.class.getClassLoader());
        m_stepId = (long)in.readValue(long.class.getClassLoader());
        m_optional = (Boolean)in.readValue(Boolean.class.getClassLoader());
        m_deleted = (Boolean)in.readValue(Boolean.class.getClassLoader());
        m_id = (long)in.readValue(long.class.getClassLoader());
    }

    /**
     * Flatten Requirement in to a Parcel.
     *
     * @param out The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(m_name);
        out.writeString(m_unit);
        out.writeValue(m_amount);
        out.writeValue(m_stepId);
        out.writeValue(m_optional);
        out.writeValue(m_deleted);
        out.writeValue(m_id);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. (Not required for Requirement)

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
    public static final Parcelable.Creator<Requirement> CREATOR = new
            Parcelable.Creator<Requirement>() {
        /**
         * Create a new instance of the Requirement class.
         *
         * @param in The Parcel to read the object's data from.
         * @return a new instance of the Requirement class.
         */
        @Contract("_ -> !null")
        public Requirement createFromParcel(Parcel in) {
            return new Requirement(in);
        }

        /**
         * Create a new array of the Requirement class.
         *
         * @param size Size of the array.
         * @return an array of the Requirement class.
         */
        @Contract(value = "_ -> !null", pure = true)
        public Requirement[] newArray(int size) {
            return new Requirement[size];
        }
    };

    /**
     * Getter for the id.
     *
     * @return the requirement's id.
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
     * Getter for the name.
     *
     * @return the requirement's name.
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
     * Getter for the unit.
     *
     * @return the requirement's unit.
     */
    public String getUnit() {
        return m_unit;
    }

    /**
     * Setter for the unit.
     *
     * @param unit The unit to set.
     */
    public void setUnit(String unit) {
        m_unit = unit;
    }

    /**
     * Getter for the amount.
     *
     * @return the requirement's amount.
     */
    public Double getAmount() {
        return m_amount;
    }

    /**
     * Setter for the amount.
     *
     * @param amount The amount to set.
     */
    public void setAmount(Double amount) {
        m_amount = amount;
    }

    /**
     * Getter for the step id.
     *
     * @return the requirement's step id.
     */
    public Long getStepId() {
        return m_stepId;
    }

    /**
     * Setter for the step id.
     *
     * @param id The id to set.
     */
    public void setStepId(Long id) {
        m_stepId = id;
    }

    /**
     * Getter for the optional flag.
     *
     * @return the requirement's optional flag.
     */
    public Boolean isOptional() {
        return m_optional;
    }

    /**
     * Setter for the optional flag.
     *
     * @param optional The optional flag value.
     */
    public void setOptional(Boolean optional) {
        m_optional = optional;
    }

    public void setDeleted(Boolean deleted) {
        m_deleted = deleted;
    }

    public Boolean isDeleted() {
        return m_deleted;
    }
}