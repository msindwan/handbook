/*
 * Created by Mayank Sindwani on 2017-06-05.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.views.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import msindwan.handbook.R;

/**
 * FileUploader:
 * Defines a custom file uploader widget for android.
 */
public class FileUploader extends RelativeLayout {

    /**
     * FileUploaderItem:
     * Defines a view for uploaded items.
     */
    @SuppressWarnings("unused")
    public static class FileUploaderItem extends LinearLayout {

        private ProgressBar m_spinner;
        private ImageView m_thumbnail;
        private Button m_removeButton;
        private TextView m_subTitle;
        private TextView m_title;
        private Object m_args;

        // Constructors.
        public FileUploaderItem(Context context) {
            super(context);
            init(context);
        }

        /**
         * Initializes the component on mount.
         *
         * @param context The initialization context.
         */
        private void init(Context context) {
            inflate(context, R.layout.file_uploader_item, this);
            m_thumbnail = (ImageView)findViewById(R.id.file_uploader_item_preview);
            m_removeButton = (Button)findViewById(R.id.file_uploader_delete);
            m_spinner = (ProgressBar)findViewById(R.id.file_uploader_icon_progress_bar);
            m_title = (TextView)findViewById(R.id.file_uploader_item_title);
            m_subTitle = (TextView)findViewById(R.id.file_uploader_item_subtitle);
            m_removeButton.setTag(this);
        }

        /**
         * Sets the remove listener.
         *
         * @param listener the listener to set.
         */
        public void setOnRemoveListener(View.OnClickListener listener) {
            m_removeButton.setOnClickListener(listener);
        }

        /**
         * Sets the title of the item.
         *
         * @param title The title to set.
         */
        public void setTitle(String title) {
            m_title.setText(title);
        }

        /**
         * Sets the subtitle of the item.
         *
         * @param subtitle The subtitle to set.
         */
        public void setSubtitle(String subtitle) {
            m_subTitle.setText(subtitle);
        }

        /**
         * Holds an arbitrary reference for the item.
         *
         * @param args The arguments to set.
         */
        public void setArguments(Object args) {
            m_args = args;
        }

        /**
         * Returns the arguments that were set.
         *
         * @return The arguments.
         */
        public Object getArguments() {
            return m_args;
        }

        /**
         * Toggles the preview between the spinner and the thumbnail.
         *
         * @param visible True if the thumbnail should be shown;
         *                false if the spinner should be shown.
         */
        public void togglePreview(boolean visible) {
            m_thumbnail.setVisibility(visible ? View.VISIBLE : View.GONE);
            m_spinner.setVisibility(!visible ? View.VISIBLE: View.GONE);
        }

        /**
         * Cross fades in the icon.
         */
        private void crossFadeThumbnail() {
            if (m_thumbnail.getVisibility() == View.VISIBLE)
                return;

            m_thumbnail.setAlpha(0f);
            m_thumbnail.setVisibility(View.VISIBLE);
            m_thumbnail.animate()
                .alpha(1f)
                .setDuration(getResources().getInteger(
                        android.R.integer.config_shortAnimTime))
                .setListener(null);

            m_spinner.animate()
                .alpha(0f)
                .setDuration(getResources().getInteger(
                        android.R.integer.config_shortAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        m_spinner.setVisibility(View.GONE);
                    }
                });
        }

        /**
         * The uri to set as the preview.
         *
         * @param uri The uri to set.
         */
        public void setPreview(String uri) {
            m_thumbnail.setImageURI(Uri.parse(uri));
            crossFadeThumbnail();
        }

        /**
         * The bitmap to set as the preview.
         *
         * @param bitmap The bitmap to set.
         */
        public void setPreview(Bitmap bitmap) {
            m_thumbnail.setImageBitmap(bitmap);
            crossFadeThumbnail();
        }
    }

    private LinearLayout m_items;
    private LinearLayout m_zone;

    // Constructors.
    public FileUploader(Context context) {
        super(context);
        init(context);
    }

    public FileUploader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Initializes the component on mount.
     *
     * @param context The initialization context.
     */
    private void init(Context context) {
        inflate(context, R.layout.file_uploader, this);
        m_zone = (LinearLayout)findViewById(R.id.file_uploader_zone);
        m_items = (LinearLayout)findViewById(R.id.file_uploader_items);
        m_zone.setTag(this);
    }

    /**
     * Adds a file uploader item to the container.
     *
     * @param item The item to add.
     */
    public void addFileUploaderItem(FileUploaderItem item) {
        m_items.addView(item, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
    }

    /**
     * Returns a file uploader item in the container.
     *
     * @param index The index of the desired item.
     * @return The file uploader item.
     */
    public FileUploaderItem getFileUploaderItem(int index) {
        return (FileUploaderItem)m_items.getChildAt(index);
    }

    /**
     * Removes a file uploader item from the container.
     *
     * @param item The file uploader item to remove.
     */
    public void removeFileUploaderItem(FileUploaderItem item) {
        m_items.removeView(item);
    }

    /**
     * Creates an intent to select files.
     *
     * @return the file chooser intent.
     */
    public Intent select() {
        final Activity activity = (Activity)getContext();
        try {

                final List<Intent> cameraIntents = new ArrayList<>();
                final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                final PackageManager packageManager = activity.getPackageManager();
                final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
                for (ResolveInfo res : listCam) {
                    final String packageName = res.activityInfo.packageName;
                    final Intent intent = new Intent(captureIntent);
                    intent.setPackage(packageName);
                    cameraIntents.add(intent);
                }
                Intent galleryIntent;

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                galleryIntent.setType("image/*");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }

                final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
                chooserIntent.putExtra(
                        Intent.EXTRA_INITIAL_INTENTS,
                        cameraIntents.toArray(new Parcelable[]{}));

                return chooserIntent;
        } catch (Exception e) {
            Toast.makeText(
                    getContext(),
                    getResources().getString(R.string.unknown_error),
                    Toast.LENGTH_SHORT
            ).show();
            Log.e(getResources().getString(R.string.app_name), "exception", e);
        }
        return null;
    }

    /**
     * Sets the click listener for the uploader.
     *
     * @param listener the event listener to set.
     */
    public void setZoneClickListener(View.OnClickListener listener) {
        m_zone.setOnClickListener(listener);
    }
}
