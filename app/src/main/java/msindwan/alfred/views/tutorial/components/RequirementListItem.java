package msindwan.alfred.views.tutorial.components;

import android.content.Context;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import msindwan.alfred.R;
import msindwan.alfred.models.Requirement;

/**
 * Created by Mayank Sindwani on 2017-05-20.
 *
 * RequirementListItem:
 * Defines a widget representing a single requirement item.
 */
@SuppressWarnings("unused")
public class RequirementListItem extends RelativeLayout {

    private Requirement m_requirement;
    private TextView m_name;

    // Constructors.
    public RequirementListItem(Context context, Requirement requirement) {
        super(context);
        m_requirement = requirement;
        init(context);
    }

    /**
     * Initializes the component on mount.
     *
     * @param context The initialization context.
     */
    private void init(Context context) {
        inflate(context, R.layout.tutorial_requirement_list_item, this);
        TextView nameText = (TextView)findViewById(R.id.tutorial_requirement_item);
        nameText.setText(m_requirement.getName());
    }

    /**
     * Getter for the requirement.
     *
     * @return the list item's requirement.
     */
    public Requirement getRequirement() {
        return m_requirement;
    }

    /**
     * Sets the click listener for the remove button.
     *
     * @param listener the listener to bind.
     */
    public void setRequirementOnRemoveListener(OnClickListener listener) {
        Button deleteButton = (Button)findViewById(R.id.tutorial_requirement_delete);
        deleteButton.setTag(this);
        deleteButton.setOnClickListener(listener);
    }
}
