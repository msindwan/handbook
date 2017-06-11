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
import android.util.Pair;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Tutorial:
 * Represents the data in a tutorial.
 */
public class Tutorial implements Parcelable {

    private ArrayList<Step> m_steps;
    private String m_description;
    private int m_numViews;
    private String m_name;
    private Long m_id;

    // Constructors.
    public Tutorial() {
        m_steps = new ArrayList<>();
        m_numViews = 0;
    }

    // Parcelable constructor.
    private Tutorial(Parcel in) {
        m_steps = new ArrayList<>();
        m_description = in.readString();
        m_name = in.readString();
        m_id = (Long)in.readValue(Long.class.getClassLoader());
        m_numViews = in.readInt();
        in.readTypedList(m_steps, Step.CREATOR);
    }

    /**
     * Flatten Tutorial in to a Parcel.
     *
     * @param out The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(m_description);
        out.writeString(m_name);
        out.writeValue(m_id);
        out.writeValue(m_numViews);
        out.writeTypedList(m_steps);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. (Not required for Tutorial)

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
         * Create a new instance of the Tutorial class.
         *
         * @param in The Parcel to read the object's data from.
         * @return a new instance of the Tutorial class.
         */
        @Contract("_ -> !null")
        public Tutorial createFromParcel(Parcel in) {
            return new Tutorial(in);
        }

        /**
         * Create a new array of the Tutorial class.
         *
         * @param size Size of the array.
         * @return an array of the Tutorial class.
         */
        @Contract(value = "_ -> !null", pure = true)
        public Tutorial[] newArray(int size) {
            return new Tutorial[size];
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
     * Getter for the number of views.
     *
     * @return the number of views.
     */
    public int getNumViews() {
        return m_numViews;
    }

    /**
     * Setter for the number of views.
     *
     * @param views The view count to set.
     */
    public void setNumViews(int views) {
        m_numViews = views;
    }

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
     * Getter for the description.
     *
     * @return the tutorial description.
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * Setter for the description.
     *
     * @param description The description to set.
     */
    public void setDescription(String description) {
        m_description = description;
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
    public Step getStep(int index) {
        return m_steps.get(index);
    }

    /**
     * Adds a step to the tutorial.
     *
     * @param step The step to add.
     * @return true if successful; false otherwise.
     */
    public boolean addStep(Step step) {
        long index = m_steps.size();
        step.setIndex(index);
        return m_steps.add(step);
    }

    /**
     * Removes the specified step from the tutorial.
     *
     * @param index The position of the step to remove.
     * @return the step that was removed.
     */
    public Step removeStep(int index) {
        Step step = m_steps.remove(index);
        step.setIndex(null);

        // Update the step indices.
        for (int i = index; i < m_steps.size(); i++) {
            m_steps.get(i).setIndex((long)i);
        }
        return step;
    }

    /**
     * Swaps two steps in the array.
     *
     * @param i the index of an element to swap.
     * @param j the index of an element to swap.
     */
    public void swapSteps(int i, int j) {
        if (i == j) return;

        Step a = m_steps.get(i);
        Step b = m_steps.get(j);

        // Update indices.
        a.setIndex((long)j);
        b.setIndex((long)i);

        // Swap the elements.
        m_steps.set(j, a);
        m_steps.set(i, b);
    }

    /**
     * Collects and combines the requirements from each step.
     *
     * @return A collection of all of the requirements from
     * each step.
     */
    public Collection<Requirement> getAllRequirements() {
        HashMap<Pair<String, String>, Requirement> requirements;
        requirements = new HashMap<>();

        for (Step step : m_steps) {
            for (int i = 0; i < step.getNumRequirements(); i++) {
                Requirement r = step.getRequirement(i);
                Pair<String, String> p = new Pair<>(r.getName(), r.getUnit());

                // If the requirement already exists, update the amount (if any).
                if (requirements.containsKey(p)) {
                    Requirement existingRequirement = requirements.get(p);
                    if (existingRequirement.getAmount() != null && r.getAmount() != null) {
                        existingRequirement.setAmount(
                                existingRequirement.getAmount() + r.getAmount()
                        );
                    }
                } else {
                    // Create a new copy of the requirement.
                    requirements.put(p, new Requirement(r));
                }

            }
        }

        return requirements.values();
    }

    /**
     * Filters out deleted steps.
     *
     * @return the filtered set of steps.
     */
    public ArrayList<Step> getActiveSteps() {
        ArrayList<Step> filteredSteps = new ArrayList<>();
        // Filter out deleted steps.
        for (int i = 0; i < getNumSteps(); i++) {
            if (!getStep(i).isDeleted()) {
                filteredSteps.add(getStep(i));
            }
        }
        return filteredSteps;
    }

}
