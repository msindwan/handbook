package msindwan.alfred.views.tutorial.components;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import msindwan.alfred.R;
import msindwan.alfred.models.Requirement;

/**
 * Created by Mayank Sindwani on 2017-05-20.
 *
 * Requirement Dialog:
 * Defines the dialog fragment for adding requirements.
 */
@SuppressWarnings("unused")
public class RequirementDialogFragment extends DialogFragment {

    /**
     * Interface for listening to requirement dialog events.
     */
    public interface RequirementDialogListener {
        void onSubmit(int stepIndex, Requirement requirement);
    }

    private RequirementDialogListener m_listener;

    /**
     * Lifecycle event for creating the dialog.
     *
     * @param savedInstanceState the dialog's saved instance.
     * @return the dialog instance.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(
                R.layout.tutorial_editor_requirement_dialog,
                null
        );

        // Mount components.
        final EditText nameText = (EditText)view.findViewById(R.id.requirement_name);
        final EditText unitText = (EditText)view.findViewById(R.id.requirement_unit);
        final EditText amountText = (EditText)view.findViewById(R.id.requirement_amount);
        final CheckBox chkOptional = (CheckBox)view.findViewById(R.id.requirement_optional);

        // Create the dialog.
        builder.setTitle("Add a Requirement");
        builder.setView(view)
                .setPositiveButton(R.string.confirm, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RequirementDialogFragment.this.getDialog().cancel();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Retrieve form attributes.
                        String name = nameText.getText().toString();
                        String unit = unitText.getText().toString();
                        String amount = amountText.getText().toString();
                        Requirement requirement = new Requirement();

                        if (name.isEmpty()) {
                            nameText.setError("Name is required");
                            return;
                        }

                        if (amount.isEmpty() && !unit.isEmpty()) {
                            amountText.setError("Amount is required");
                            return;
                        }

                        requirement.setName(name);
                        requirement.setOptional(chkOptional.isChecked());

                        if (!unit.isEmpty()) {
                            requirement.setUnit(unit);
                        }
                        if (!amount.isEmpty()) {
                            requirement.setAmount(Double.parseDouble(amount));
                        }

                        // Fire the event listener.
                        if (m_listener != null) {
                            int stepIndex = getArguments().getInt("stepIndex");
                            m_listener.onSubmit(stepIndex, requirement);
                        }
                        dialog.cancel();
                    }
                });
            }
        });

        return dialog;
    }

    /**
     * Sets the listener for the requirement dialog.
     *
     * @param listener the event listener to set.
     */
    public void setRequirementDialogListener(RequirementDialogListener listener) {
        m_listener = listener;
    }
}
