/*
 * Created by Mayank Sindwani on 2017-05-04.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.views.tutorial;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import msindwan.handbook.data.DataContentProvider;
import msindwan.handbook.data.DatabaseHelper;
import msindwan.handbook.models.Image;
import msindwan.handbook.models.Requirement;
import msindwan.handbook.models.Step;
import msindwan.handbook.models.Tutorial;
import msindwan.handbook.R;
import msindwan.handbook.util.StringHelper;
import msindwan.handbook.views.tutorial.components.EditSummaryForm;
import msindwan.handbook.views.tutorial.components.RequirementDialogFragment;
import msindwan.handbook.views.tutorial.components.RequirementListItem;
import msindwan.handbook.views.tutorial.components.EditStepForm;
import msindwan.handbook.views.widgets.Accordion;
import msindwan.handbook.views.widgets.AsyncProgressDialog;
import msindwan.handbook.views.widgets.FileUploader;

/**
 * TutorialEditor:
 * Defines a view component for creating and editing tutorials.
 */
public class TutorialEditor extends AppCompatActivity {

    private static final int REQUEST_UPLOAD_IMAGE = 1;
    private static final int NUM_INITIAL_STEPS = 2;

    private AsyncProgressDialog m_saveDialog;
    private Accordion m_accordion;
    LoadStepImages m_imageLoader;
    private Tutorial m_tutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.tutorial_editor);
        init(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("tutorial", m_tutorial);
        super.onSaveInstanceState(outState);
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tutorial_editor_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     *         proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tutorial_editor_add_step:
                // Add a new step.
                m_tutorial.addStep(new Step());
                m_accordion.addPanel();
                m_accordion.setActivePanel(m_accordion.getNumPanels() - 1);
                repaintStepViews();
                return true;

            case R.id.tutorial_editor_save:
                EditSummaryForm summaryView;
                EditStepForm stepView;
                Step step;

                // Validate the tutorial summary.
                if (StringHelper.isEmpty(m_tutorial.getName())
                        || StringHelper.isEmpty(m_tutorial.getDescription())) {
                    m_accordion.setActivePanel(0);
                    summaryView = (EditSummaryForm)
                            m_accordion.getPanel(0).getPanelView();
                    return summaryView.validate();
                }

                // Validate the tutorial steps.
                ArrayList<Step> filteredSteps = m_tutorial.getActiveSteps();
                for (int i = 0; i < filteredSteps.size(); i++) {
                    step = filteredSteps.get(i);
                    if (StringHelper.isEmpty(step.getTitle())
                            || StringHelper.isEmpty(step.getInstructions())) {
                        m_accordion.setActivePanel(i + 1);
                        stepView = (EditStepForm)
                                m_accordion.getPanel(i + 1).getPanelView();
                        return stepView.validate();
                    }
                }

                m_saveDialog.execute();
                return true;

            case android.R.id.home:
                // Exit.
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Dispatch incoming result to the correct fragment.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_UPLOAD_IMAGE:
                FileUploader.FileUploaderItem item;
                EditStepForm stepView;
                Image image;
                Step step;

                // NOTE: It would be better to parameterize the corresponding panel
                // rather than relying on the active panel, but there doesn't seem
                // to be a clear way of passing data to the image selection intent.
                stepView = (EditStepForm)
                        m_accordion.getPanel(m_accordion.getActivePanel()).getPanelView();
                step = stepView.getStep();

                // Gather image uris.
                ArrayList<Uri> images = new ArrayList<>();
                if (intent.getData() == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        ClipData clipData = intent.getClipData();
                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item clipItem = clipData.getItemAt(i);
                                images.add(clipItem.getUri());
                            }
                        }
                    }
                } else {
                    images.add(intent.getData());
                }

                // Add the images to the step.
                for (Uri uri : images) {
                    image = new Image();
                    try {
                        image.read(uri, getContentResolver());
                        item = stepView.addImage(image);
                        item.setOnRemoveListener(onImageRemoveListener);
                        item.setPreview(image.getThumbnail(getContentResolver(), 64, 64));
                        step.addImage(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    /**
     * Initializes the component on mount.
     */
    private void init(Bundle savedInstanceState) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        m_accordion = (Accordion)findViewById(R.id.tutorial_panels);
        m_accordion.setAccordionListener(accordionListener);

        // Preserve the state of the view.
        if(savedInstanceState == null
                || !savedInstanceState.containsKey("tutorial")
                || savedInstanceState.get("tutorial") == null) {

            // Initialize the save dialog fragment once.
            m_saveDialog = new AsyncProgressDialog();
            Bundle args = new Bundle();
            args.putString(
                    AsyncProgressDialog.ARG_MESSAGE,
                    getResources().getString(R.string.saving_tutorial)
            );
            m_saveDialog.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, m_saveDialog).commit();
            m_saveDialog.setTask(onSave);

            // If no tutorial is saved in the current context, fetch the tutorial argument.
            String tutorial_id = getIntent().getStringExtra("tutorial_id");

            m_tutorial = new Tutorial();
            if (tutorial_id == null) {
                // If no tutorial was passed, create a new instance.
                for (int i = 0; i < NUM_INITIAL_STEPS; i++) {
                    m_tutorial.addStep(new Step());
                }
            } else {
                // Fetch the corresponding tutorial.
                DatabaseHelper helper = DatabaseHelper.getInstance(this);
                helper.fetch(m_tutorial, Integer.parseInt(tutorial_id));
            }
        } else {
            // Otherwise, retrieve the old state and render the view
            // accordingly.
            m_tutorial = savedInstanceState.getParcelable("tutorial");
            m_saveDialog = (AsyncProgressDialog)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }

        // Add the summary panel.
        Accordion.Panel panel = m_accordion.addPanel();
        panel.setTitle(getResources().getString(R.string.summary));


        // Add step views.
        for (int i = 0; i < m_tutorial.getNumSteps(); i++) {
            panel = m_accordion.addPanel();
            panel.setTitle(
                getResources().getString(R.string.nth_step_no_title, i + 1)
            );
        }
    }

    /**
     * Iterates through steps and updates titles and panel states
     * according to the state of the tutorial.
     */
    private void repaintStepViews() {
        // Update titles to reflect current index.
        for (int i = 1; i < m_accordion.getNumPanels(); i++) {
            // 0
            Accordion.Panel panel = m_accordion.getPanel(i);
            panel.setTitle(
                    getResources().getString(R.string.nth_step_no_title, i)
            );
        }

        int activePanelIndex = m_accordion.getActivePanel();
        if (activePanelIndex > 0) {
            Accordion.Panel activePanel = m_accordion.getPanel(m_accordion.getActivePanel());
            EditStepForm form = (EditStepForm)activePanel.getPanelView();

            form.toggleMoveUpButton(activePanelIndex != 1);
            form.toggleMoveDownButton(activePanelIndex != m_accordion.getNumPanels() - 1);
        }
    }

    /**
     * Moves a panel up or down.
     *
     * @param oldIndex the index of the panel to move.
     * @param newIndex the index of the panel to swap with.
     */
    private void movePanel(int oldIndex, int newIndex) {
        ArrayList<Step> filteredSteps = m_tutorial.getActiveSteps();

        Step a = filteredSteps.get(oldIndex - 1);
        Step b = filteredSteps.get(newIndex - 1);

        m_accordion.movePanel(oldIndex, newIndex);
        m_tutorial.swapSteps(a.getIndex().intValue(), b.getIndex().intValue());
        repaintStepViews();
    }

    /**
     * Listener for removing a step view.
     */
    private View.OnClickListener onRemoveStepView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Find the corresponding step index.
            Accordion.Panel panel = m_accordion.getPanel(m_accordion.getActivePanel());
            EditStepForm stepView = (EditStepForm)panel.getPanelView();
            Step step = stepView.getStep();

            // Remove the parent panel and its corresponding step.
            m_accordion.removePanel(m_accordion.getActivePanel());

            if (step.getId() != null) {
                step.setDeleted(true);
            } else {
                m_tutorial.removeStep(step.getIndex().intValue());
            }
            repaintStepViews();
        }
    };

    /**
     * Listener for moving a step view up.
     */
    private View.OnClickListener onMoveUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int panelIndex = m_accordion.getActivePanel();
            movePanel(panelIndex, panelIndex - 1);
        }
    };

    /**
     * Listener for moving a step view down.
     */
    private View.OnClickListener onMoveDown = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int panelIndex = m_accordion.getActivePanel();
            movePanel(panelIndex, panelIndex + 1);
        }
    };

    /**
     * Listener for adding requirements.
     */
    private View.OnClickListener onAddRequirement = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Create a new dialog.
            RequirementDialogFragment dialog = new RequirementDialogFragment();

            // Find the step index and pass it as an argument to the dialog.
            int stepIndex = m_accordion.getActivePanel() - 1;
            Bundle bundle = new Bundle();
            bundle.putInt(RequirementDialogFragment.ARG_STEP_INDEX, stepIndex);
            dialog.setArguments(bundle);

            // Set the requirement dialog listener.
            dialog.setRequirementDialogListener(onSubmitRequirement);
            dialog.show(getSupportFragmentManager(), "RequirementDialogFragment");
        }
    };

    /**
     * Listener for submitting requirements.
     */
    private RequirementDialogFragment.RequirementDialogListener onSubmitRequirement
            = new RequirementDialogFragment.RequirementDialogListener() {
        @Override
        public void onSubmit(int stepIndex, final Requirement requirement) {
            // Add the new requirement.
            final EditStepForm view = (EditStepForm)
                m_accordion.getPanel(stepIndex + 1).getPanelView();
            final RequirementListItem item = new RequirementListItem(TutorialEditor.this);
            item.setRequirement(requirement);
            item.setTag(view);
            item.setRequirementOnRemoveListener(onRequirementRemoved);
            Step step = view.getStep();

            for (int i = 0; i < step.getNumRequirements(); i++) {
                // Look for existing requirements that have the same unit and amount.
                Requirement oldRequirement = step.getRequirement(i);
                String name = oldRequirement.getName();
                String unit = oldRequirement.getUnit();

                // The name and optional flags must be equivalent.
                if (name.equals(requirement.getName())
                        && oldRequirement.isOptional() == requirement.isOptional()) {

                    if (StringHelper.equals(unit, requirement.getUnit())) {
                        if (oldRequirement.getAmount() != null && requirement.getAmount() != null) {
                            // The name and units are equal, so add the amount to the existing
                            // requirement.
                            oldRequirement.setAmount(
                                oldRequirement.getAmount() + requirement.getAmount()
                            );
                            view.getRequirementListItem(i).setRequirement(oldRequirement);
                        }
                        return;
                    }
                }
            }

            view.getStep().addRequirement(requirement);
            view.addRequirementListItem(item);
        }
    };

    /**
     * Listener for removing requirements.
     */
    private View.OnClickListener onRequirementRemoved = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Remove the requirement from the step and view.
            RequirementListItem item = (RequirementListItem) v.getTag();
            EditStepForm view = (EditStepForm)item.getTag();

            Requirement requirement = item.getRequirement();
            Step step = view.getStep();

            // If it exists in the db, mark it for deletion.
            // Otherwise, remove it from the tutorial.
            if (requirement.getId() != null) {
                requirement.setDeleted(true);
            } else {
                step.removeRequirement(requirement);
            }
            view.removeRequirementListItem(item);
        }
    };

    /**
     * Listener for file uploads.
     */
    private View.OnClickListener onImageUploadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FileUploader uploader = (FileUploader)v.getTag();
            Intent selectImage = uploader.select();
            startActivityForResult(selectImage, REQUEST_UPLOAD_IMAGE);
        }
    };

    /**
     * Listener for removing images.
     */
    private View.OnClickListener onImageRemoveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Remove the image from the step and view.
            FileUploader.FileUploaderItem item = (FileUploader.FileUploaderItem)v.getTag();
            EditStepForm view = (EditStepForm)item.getTag();

            Image image = (Image)item.getArguments();
            Step step = view.getStep();

            // If it exists in the db, mark it for deletion.
            // Otherwise, remove it from the tutorial.
            if (image.getId() != null) {
                image.setDeleted(true);
            } else {
                step.removeImage(image);
            }
            view.removeFileUploaderItem(item);
        }
    };

    /**
     * AsyncProgressDialog Task for saving tutorials.
     */
    private AsyncProgressDialog.AsyncDialogTask onSave = new AsyncProgressDialog.AsyncDialogTask() {
        @Override
        public void run() {
            DatabaseHelper helper = DatabaseHelper.getInstance(TutorialEditor.this);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.beginTransaction();

            try {
                if (m_tutorial.getId() == null) {
                    helper.insert(m_tutorial);
                } else {
                    helper.update(m_tutorial);
                }
                db.setTransactionSuccessful();
                db.endTransaction();

                // Notify the content provider.
                ContentResolver resolver = getContentResolver();
                resolver.notifyChange(DataContentProvider.TUTORIAL_URI, null);

                // Show the modal for an additional second to emphasize
                // UI responsiveness.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();

            } catch (SQLException e) {
                Log.e(getResources().getString(R.string.app_name), "exception", e);
                db.endTransaction();
                Toast.makeText(
                    TutorialEditor.this,
                    getResources().getString(R.string.failed_tutorial_save),
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
    };

    /**
     * Accordion Listener
     */
    private Accordion.AccordionListener accordionListener = new Accordion.AccordionListener() {

        @Override
        public boolean onHeaderClick(Accordion.Panel panel) {
            return true;
        }
        @Override
        public void onPanelExpanded(int position) {
            // If the panel is a step panel, start loading the panel images.
            if (position > 0) {
                EditStepForm stepView = (EditStepForm)m_accordion.getPanel(position).getPanelView();
                if (m_imageLoader != null) {
                    m_imageLoader.cancel(true);
                }
                m_imageLoader = new LoadStepImages(stepView);
                m_imageLoader.execute();
            }
        }
        @Override
        public View getItem(int position) {
            if (position == 0) {
                // Return a summary form.
                EditSummaryForm summaryForm = new EditSummaryForm(TutorialEditor.this);
                summaryForm.setTutorial(m_tutorial);
                return summaryForm;
            }

            // Create a new step view.
            ArrayList<Step> filteredSteps = m_tutorial.getActiveSteps();
            EditStepForm stepView = new EditStepForm(TutorialEditor.this);
            RequirementListItem requirementItem;
            Step step;
            int i;

            step = filteredSteps.get(position - 1);

            // Bind arguments and listeners.
            stepView.setStep(step);
            stepView.setOnMoveUpListener(onMoveUp);
            stepView.setOnMoveDownListener(onMoveDown);
            stepView.setUploaderZoneClickListener(onImageUploadListener);
            stepView.setOnRemoveListener(onRemoveStepView);
            stepView.setOnAddRequirementListener(onAddRequirement);
            stepView.toggleMoveUpButton(position != 1);
            stepView.toggleMoveDownButton(position != m_accordion.getNumPanels() - 1);

            // Render requirement list items.
            for (i = 0; i < step.getNumRequirements(); i++) {
                requirementItem = new RequirementListItem(TutorialEditor.this);
                requirementItem.setRequirement(step.getRequirement(i));
                requirementItem.setTag(stepView);
                requirementItem.setRequirementOnRemoveListener(onRequirementRemoved);
                stepView.addRequirementListItem(requirementItem);
            }

            // Render images.
            for (i = 0; i < step.getNumImages(); i++) {
                Image image = step.getImage(i);
                if (!image.isDeleted()) {
                    stepView.addImage(image);
                }
            }
            return stepView;
        }
    };

    /**
     * LoadStepImages:
     * Defines an asynchronous task to fetch thumbnails for each image.
     */
    private class LoadStepImages extends AsyncTask<String, Void, String> {
        EditStepForm m_stepView;
        Step m_step;

        private LoadStepImages(EditStepForm form) {
            m_stepView = form;
            m_step = m_stepView.getStep();
        }

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0, j = 0; i < m_step.getNumImages(); i++) {
                final Image image = m_step.getImage(i);
                if (!image.isDeleted()) {
                    // Read each image.
                    final int itemIndex = j;
                    j++;
                    try {
                        image.read(image.getImageURI(), getContentResolver());
                        final Bitmap thumbnail = image.getThumbnail(getContentResolver(), 64, 64);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Set the thumbnail for each item.
                                FileUploader.FileUploaderItem item;
                                item = m_stepView.getUploaderItem(itemIndex);
                                item.setPreview(thumbnail);
                                item.setTitle(image.getName());
                                item.setSubtitle(String.format(Locale.getDefault(), "%d KB", image.getSize()));
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return "Executed";
        }
        @Override
        protected void onPostExecute(String result) {
            // Bind event listeners after each item has been rendered.
            for (int i = 0, j = 0; i < m_step.getNumImages(); i++) {
                final Image image = m_step.getImage(i);
                if (!image.isDeleted()) {
                    final int itemIndex = j;
                    j++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FileUploader.FileUploaderItem item;
                            item = m_stepView.getUploaderItem(itemIndex);
                            item.setOnRemoveListener(onImageRemoveListener);
                        }
                    });
                }
            }
        }
        @Override
        protected void onPreExecute() {}
        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}

