package msindwan.handbook.views.tutorial.components;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import msindwan.handbook.R;
import msindwan.handbook.models.Requirement;

/**
 * Created by Mayank Sindwani on 2017-05-20.
 *
 * RequirementListItem:
 * Defines a widget representing a single requirement item.
 */
@SuppressWarnings("unused")
public class RequirementListItem extends RelativeLayout {

    private Requirement m_requirement;
    private Button m_deleteButton;

    // Constructors.
    public RequirementListItem(Context context, Requirement requirement, View tag) {
        super(context);
        m_requirement = requirement;
        setTag(tag);
        init(context);
    }

    /**
     * Initializes the component on mount.
     *
     * @param context The initialization context.
     */
    private void init(Context context) {
        inflate(context, R.layout.tutorial_requirement_list_item, this);
        m_deleteButton = (Button)findViewById(R.id.tutorial_requirement_delete);
        TextView nameText = (TextView)findViewById(R.id.tutorial_requirement_item);

        // Get the requirement fields.
        String title = m_requirement.getName();
        Double amount = m_requirement.getAmount();
        String unit = m_requirement.getUnit();

        if (amount != null && amount != 0) {
            String sAmount = Double.toString(amount);

            // Remove trailing zeros.
            sAmount = !sAmount.contains(".") ?
                    sAmount :
                    sAmount.replaceAll("0*$", "").replaceAll("\\.$", "");

            title += (" - " + sAmount);

            // Append a unit (if any).
            if (unit != null) {
                title += (" " + unit);
            }
        }

        if (m_requirement.isOptional()) {
            title += " (optional)";
        }

        nameText.setText(title);
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
        m_deleteButton.setTag(this);
        m_deleteButton.setOnClickListener(listener);
    }

    /**
     * Toggles the visibility of the delete button.
     *
     * @param toggle the flag to determine whether or not
     *               to display the delete button
     */
    public void toggleDeleteButton(boolean toggle) {
        m_deleteButton.setVisibility(toggle ? VISIBLE : GONE);
    }
}
