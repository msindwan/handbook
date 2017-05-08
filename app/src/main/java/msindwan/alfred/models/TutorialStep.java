package msindwan.alfred.models;

/**
 * Created by Mayank Sindwani on 2017-05-06.
 *
 * Tutorial Step:
 * Represents the data pertaining to a single step
 * in a tutorial.
 */
public class TutorialStep {

    private String m_description;
    private String m_title;

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
