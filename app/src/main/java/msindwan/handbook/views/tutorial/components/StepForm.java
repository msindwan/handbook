/*
 * Created by Mayank Sindwani on 2017-05-21.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.views.tutorial.components;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import msindwan.handbook.R;
import msindwan.handbook.models.Step;
import msindwan.handbook.views.widgets.Carousel;

/**
 * StepView:
 * Defines a view that edits a step in a tutorial.
 */
public class StepForm extends Fragment {

    /**
     * StepImagePager:
     * Defines a pager for the image carousel.
     */
    private class StepImagePager extends PagerAdapter {

        private Step m_step;

        // Constructor.
        private StepImagePager(Step step) {
            m_step = step;
        }

        @Override
        public int getCount() {
            return m_step.getNumImages();
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Return a view for the step image at the current position.
            ImageView imageView = new ImageView(getContext());
            imageView.setImageURI(m_step.getImage(position).getImageURI());

            container.addView(imageView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            return imageView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutorial_viewer_step_panel, container, false);
        Bundle arguments = getArguments();

        // If a step was provided...
        if (arguments != null && arguments.containsKey("step")) {
            Step step = getArguments().getParcelable("step");

            if (step != null) {
                // Mount view components.
                TextView instructions = (TextView) view.findViewById(R.id.tutorial_viewer_instructions);
                TextView requirementPlaceholder = (TextView)
                        view.findViewById(R.id.tutorial_viewer_requirement_placeholder);
                LinearLayout requirements = (LinearLayout)
                        view.findViewById(R.id.tutorial_viewer_requirements);

                // Update the instructions.
                instructions.setText(step.getInstructions());

                // Render the requirements.
                for (int i = 0; i < step.getNumRequirements(); i++) {
                    requirementPlaceholder.setVisibility(View.GONE);
                    RequirementListItem item = new RequirementListItem(getContext());
                    item.setRequirement(step.getRequirement(i));
                    item.toggleDeleteButton(false);
                    requirements.addView(item);
                }

                // Render images through the carousel.
                Carousel mViewPager = (Carousel) view.findViewById(R.id.pager);
                if (step.getNumImages() > 0) {
                    mViewPager.setAdapter(new StepImagePager(step));
                    mViewPager.setIndicatorMargin(0,-35,0,0);
                } else {
                    mViewPager.setVisibility(View.GONE);
                }

            }
        }

        return view;
    }
}
