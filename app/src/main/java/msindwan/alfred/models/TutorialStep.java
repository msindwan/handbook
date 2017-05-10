package msindwan.alfred.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mayank Sindwani on 2017-05-06.
 *
 * Tutorial Step:
 * Represents the data pertaining to a single step
 * in a tutorial.
 */
public class TutorialStep implements Parcelable {

    private String m_description;
    private String m_title;

    public TutorialStep() { }

    private TutorialStep(Parcel in) {
        m_title = in.readString();
        m_description = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return m_title;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(m_title);
        out.writeString(m_description);
    }

    public static final Parcelable.Creator<TutorialStep> CREATOR = new Parcelable.Creator<TutorialStep>() {
        public TutorialStep createFromParcel(Parcel in) {
            return new TutorialStep(in);
        }

        public TutorialStep[] newArray(int size) {
            return new TutorialStep[size];
        }
    };

    // Getter for the description.
    public String getDescription() {
        return m_description;
    }

    // Setter for the description.
    public void setDescription(String description) {
        m_description = description;
    }

    // Getter for the title.
    public String getTitle() {
        return m_title;
    }

    // Setter for the title.
    public void setTitle(String title) {
        m_title = title;
    }
}
