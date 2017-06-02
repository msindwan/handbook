/*
 * Created by Mayank Sindwani on 2017-06-01.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.views.widgets;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import msindwan.handbook.R;

/**
 * AsyncProgressDialog:
 * Represents a fragment for persisting and displaying a progress dialog
 * during an asynchronous task.
 */

public class AsyncProgressDialog extends Fragment {

    public static final String ARG_TITLE = "title";
    public static final String ARG_MESSAGE = "message";

    private boolean m_isTaskRunning = false;
    private ProgressDialog m_progressDialog;
    private AsyncDialogTask m_task;

    /**
     * Interface for the async task.
     */
    public interface AsyncDialogTask {
        void run();
    }

    /**
     * AsyncDialogTaskWrapper:
     * Defines a wrapper to lock/unlock the running task's dialog on post
     * and pre execute.
     */
    private class AsyncDialogTaskWrapper extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // Start the dialog.
            m_isTaskRunning = true;
            m_progressDialog.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            // Run the task.
           if (m_task != null) {
               m_task.run();
           }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            // Dismiss the dialog.
            if (m_progressDialog != null) {
                m_progressDialog.dismiss();
            }
            m_isTaskRunning = false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_progressDialog = new ProgressDialog(getActivity());

        String title = null;
        String message = getResources().getString(R.string.loading);

        // Set dialog parameters through arguments.
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            message = getArguments().getString(ARG_MESSAGE, message);
        }

        m_progressDialog.setTitle(title);
        m_progressDialog.setMessage(message);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (m_isTaskRunning) {
            // If the activity is being recreated after reconfiguration,
            // show the dialog again if the task is still running.
            m_progressDialog.show();
        }
    }

    @Override
    public void onDetach() {
        // Dismiss the dialog before detaching the fragment.
        if (m_progressDialog != null && m_progressDialog.isShowing()) {
            m_progressDialog.dismiss();
        }
        super.onDetach();
    }

    /**
     * Sets the task to the provided task interface.
     *
     * @param task The task to invoke on execute.
     */
    public void setTask(AsyncDialogTask task) {
        m_task = task;
    }

    /**
     * Executes the async task.
     */
    public void execute() {
       if (!m_isTaskRunning) {
           new AsyncDialogTaskWrapper().execute();
       }
    }
}
