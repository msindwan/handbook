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
package msindwan.handbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.Contract;

/**
 * Requirement:
 * Represents the data in a requirement.
 */
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

    // Copy constructor.
    Requirement(Requirement rToCopy) {
        m_deleted = rToCopy.isDeleted();
        m_optional = rToCopy.isOptional();
        m_amount = rToCopy.getAmount();
        m_name = rToCopy.getName();
        m_unit = rToCopy.getUnit();
        m_stepId = rToCopy.getStepId();
        m_id = rToCopy.getId();
    }

    // Parcelable constructor.
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

    /**
     * Returns a string representation of the requirement.
     *
     * @return  a string representation of the requirement.
     */
    @Override
    public String toString() {
        // Get the requirement fields.
        String requirementAsString = m_name;
        Double amount = m_amount;
        String unit = m_unit;

        if (requirementAsString == null) {
            return null;
        }

        if (amount != null && amount != 0) {
            String sAmount = Double.toString(amount);

            // Remove trailing zeros.
            sAmount = !sAmount.contains(".") ?
                    sAmount :
                    sAmount.replaceAll("0*$", "").replaceAll("\\.$", "");

            requirementAsString += (" : " + sAmount);

            // Append a unit (if any).
            if (unit != null) {
                requirementAsString += (" " + unit);
            }
        }

        if (isOptional()) {
            requirementAsString += " (optional)";
        }

        return requirementAsString;
    }

}