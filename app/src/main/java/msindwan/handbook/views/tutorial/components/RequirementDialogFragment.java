/*
 * Created by Mayank Sindwani on 2017-05-20.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.views.tutorial.components;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import msindwan.handbook.R;
import msindwan.handbook.data.schema.RequirementTable;
import msindwan.handbook.models.Requirement;

/**
 * Requirement Dialog:
 * Defines the dialog fragment for adding requirements.
 */
public class RequirementDialogFragment extends DialogFragment {

    // Arguments.
    public static final String ARG_STEP_INDEX = "step_index";

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

        nameText.setFilters(new InputFilter[] {
            new InputFilter.LengthFilter(RequirementTable.COL_NAME_MAX_LENGTH)
        });

        unitText.setFilters(new InputFilter[] {
            new InputFilter.LengthFilter(RequirementTable.COL_UNIT_MAX_LENGTH)
        });

        // Create the dialog.
        builder.setTitle(getResources().getString(R.string.add_requirement));
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
                            nameText.setError(getResources().getString(R.string.required));
                            return;
                        }

                        if (amount.isEmpty() && !unit.isEmpty()) {
                            amountText.setError(getResources().getString(R.string.required));
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
                            int stepIndex = getArguments().getInt(ARG_STEP_INDEX);
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
