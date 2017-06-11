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
package msindwan.handbook.models;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Image:
 * Represents the data in an image.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Image implements Parcelable {

    public static final int KILOBYTE = 1024;

    private boolean m_deleted;
    private Uri m_imageURI;
    private String m_name;
    private long m_size;
    private Long m_stepId;
    private Long m_id;

    // Constructors.
    public Image() {
        m_deleted = false;
        m_size = 0;
    }

    // Parcelable constructor.
    private Image(Parcel in) {
        m_name = in.readString();
        m_deleted = (Boolean)in.readValue(Boolean.class.getClassLoader());
        m_imageURI = in.readParcelable(Uri.class.getClassLoader());
        m_stepId = (long)in.readValue(long.class.getClassLoader());
        m_id = (long)in.readValue(long.class.getClassLoader());
        m_size = (long)in.readValue(long.class.getClassLoader());
    }

    /**
     * Flatten Requirement in to a Parcel.
     *
     * @param out The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(m_name);
        out.writeValue(m_deleted);
        out.writeValue(m_imageURI);
        out.writeValue(m_stepId);
        out.writeValue(m_id);
        out.writeValue(m_size);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. (Not required for Requirement)

     * @return 0 (no special objects)
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Interface that must be implemented and provided as a public CREATOR
     * field that generates instances of your Parcelable class from a Parcel.
     */
    public static final Parcelable.Creator<Image> CREATOR = new
            Parcelable.Creator<Image>() {
                /**
                 * Create a new instance of the Requirement class.
                 *
                 * @param in The Parcel to read the object's data from.
                 * @return a new instance of the Requirement class.
                 */
                @Contract("_ -> !null")
                public Image createFromParcel(Parcel in) {
                    return new Image(in);
                }

                /**
                 * Create a new array of the Requirement class.
                 *
                 * @param size Size of the array.
                 * @return an array of the Requirement class.
                 */
                @Contract(value = "_ -> !null", pure = true)
                public Image[] newArray(int size) {
                    return new Image[size];
                }
            };

    /**
     * Getter for the id.
     *
     * @return the requirement's id.
     */
    public Long getId() {
        return m_id;
    }

    /**
     * Setter for the id.
     *
     * @param id The id to set.
     */
    public void setId(Long id) {
        m_id = id;
    }

    /**
     * Getter for the name.
     *
     * @return the requirement's name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Setter for the name.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * Getter for the step id.
     *
     * @return the requirement's step id.
     */
    public Long getStepId() {
        return m_stepId;
    }

    /**
     * Setter for the step id.
     *
     * @param id The id to set.
     */
    public void setStepId(Long id) {
        m_stepId = id;
    }

    /**
     * Setter for the deleted flag.
     *
     * @param deleted the deleted flag to set.
     */
    public void setDeleted(Boolean deleted) {
        m_deleted = deleted;
    }

    /**
     * Getter for the deleted flag.
     *
     * @return the deleted flag.
     */
    public Boolean isDeleted() {
        return m_deleted;
    }

    /**
     * Getter for the image.
     *
     * @return the image byte array.
     */
    public Uri getImageURI() {
        return m_imageURI;
    }

    /**
     * Setter for the image.
     *
     * @param imageURI the image byte array to set.
     */
    public void setImageURI(Uri imageURI) {
        m_imageURI = imageURI;
    }

    /**
     * Getter for the size of the image.
     *
     * @return the size of the image.
     */
    public long getSize() {
        return m_size;
    }

    /**
     * Reads the image from the media store.
     *
     * @param resolver The content resolver to read from.
     * @return A bitmap of the image.
     * @throws IOException if the image cannot be read.
     */
    public Bitmap getImage(ContentResolver resolver) throws IOException {
        return MediaStore.Images.Media.getBitmap(resolver, m_imageURI);
    }

    /**
     * Returns a thumbnail for the image.
     *
     * @param resolver The content resolver to read from.
     * @param width The desired width of the thumbnail.
     * @param height The desired height of the thumbnail.
     * @return The bitmap of the thumbnail.
     * @throws IOException if the image cannot be read.
     */
    public Bitmap getThumbnail(ContentResolver resolver, int width, int height) throws IOException {
        return ThumbnailUtils.extractThumbnail(getImage(resolver), width, height);
    }

    /**
     * Reads the image at the specified uri into the model instance.
     *
     * @param image The uri of the image to read.
     * @param resolver he content resolver to read from.
     * @throws IOException if the image cannot be read.
     * @throws SecurityException if the file cannot be accessed.
     */
    public void read(Uri image, ContentResolver resolver) throws IOException, SecurityException {
        long dataSize = 0;
        String[] filePathColumn = {
                MediaStore.Images.Media.DISPLAY_NAME
        };

        // Resolve the image if possible.
        Cursor cursor = resolver.query(image, filePathColumn, null, null, null);
        if (cursor == null) {
            throw new IOException(
                String.format(
                    Locale.getDefault(),
                    "File at \"%s\" not found",
                    image.getPath()
                )
            );
        }

        // Set the name.
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            m_name = cursor.getString(columnIndex);
        }
        cursor.close();

        // Calculate the size (in KB).
        String scheme = image.getScheme();
        if(scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            InputStream fileInputStream = resolver.openInputStream(image);
            if (fileInputStream != null) {
                m_size = fileInputStream.available() / KILOBYTE;
            }
        } else if(image.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            File file = new File(image.getPath());
            m_size = file.length() / KILOBYTE;
        }
        setImageURI(image);
    }
}