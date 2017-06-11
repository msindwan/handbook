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
package msindwan.handbook.views.tutorial.components;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import msindwan.handbook.R;
import msindwan.handbook.models.Requirement;
import msindwan.handbook.models.Tutorial;

/**
 * SummaryView:
 * Defines a view representing a tutorial's summary.
 */
public class SummaryForm extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutorial_viewer_summary_panel, container, false);
        Bundle arguments = getArguments();

        // If a tutorial is provided...
        if (arguments != null && arguments.containsKey("tutorial")) {
            Tutorial tutorial = getArguments().getParcelable("tutorial");

            if (tutorial != null) {
                // Mount components.
                TextView description = (TextView)
                        view.findViewById(R.id.tutorial_viewer_description);
                LinearLayout requirements = (LinearLayout)
                        view.findViewById(R.id.tutorial_viewer_requirements);
                TextView requirementPlaceholder = (TextView)
                        view.findViewById(R.id.tutorial_viewer_requirement_placeholder);

                // Set the description.
                description.setText(tutorial.getDescription());

                // Render all of the requirements.
                for (Requirement requirement : tutorial.getAllRequirements()) {
                    requirementPlaceholder.setVisibility(View.GONE);
                    RequirementListItem item = new RequirementListItem(getContext());
                    item.setRequirement(requirement);
                    item.toggleDeleteButton(false);
                    requirements.addView(item);
                }
            }
        }
        return view;
    }
}
