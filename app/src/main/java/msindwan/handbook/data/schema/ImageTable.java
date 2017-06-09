/*
 * Created by Mayank Sindwani on 2017-06-06.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.data.schema;

import android.database.sqlite.SQLiteDatabase;

import java.util.Locale;

/**
 * ImageTable:
 * Defines the schema for steps.
 */
@SuppressWarnings("WeakerAccess")
public class ImageTable {

    // Table properties.
    public static final String TABLE_NAME        = "images";
    public static final String COL_ID            = "_id";
    public static final String COL_URI           = "uri";
    public static final String COL_STEP_ID       = "step_id";
    public static final int COL_URI_MAX_LENGTH   = 100;

    /**
     * Creates the images table
     *
     * @param db The database instance to execute against.
     */
    public static void createTable(SQLiteDatabase db) {
        String CREATE_TABLE =
                String.format(
                        Locale.getDefault(),
                        "CREATE TABLE %s (",
                        TABLE_NAME
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s INTEGER PRIMARY KEY,",
                        COL_ID
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s VARCHAR(%d) NOT NULL,",
                        COL_URI,
                        COL_URI_MAX_LENGTH
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s INTEGER NOT NULL,",
                        COL_STEP_ID
                ) +
                String.format(
                        Locale.getDefault(),
                        "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE);",
                        COL_STEP_ID,
                        StepTable.TABLE_NAME,
                        StepTable.COL_ID
                );

        db.execSQL(CREATE_TABLE);
    }
}
