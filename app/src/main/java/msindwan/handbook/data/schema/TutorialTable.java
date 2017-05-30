/*
 * Created by Mayank Sindwani on 2017-05-11.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.data.schema;

import android.database.sqlite.SQLiteDatabase;

import java.util.Locale;

/**
 * TutorialTable:
 * Defines the schema for tutorials.
 */
@SuppressWarnings("WeakerAccess")
public class TutorialTable {

    // Table properties.
    public static final String TABLE_NAME        = "tutorials";
    public static final String COL_ID            = "_id";
    public static final String COL_NAME          = "name";
    public static final String COL_DESCRIPTION   = "description";
    public static final String COL_DATE_CREATED  = "date_created";
    public static final String COL_LAST_MODIFIED = "last_modified";
    public static final String COL_NUM_VIEWS     = "num_views";
    public static final int COL_NAME_MAX_LENGTH  = 100;

    /**
     * Creates the tutorials table
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
                        COL_NAME,
                        COL_NAME_MAX_LENGTH
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s TEXT,",
                        COL_DESCRIPTION
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,",
                        COL_DATE_CREATED
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,",
                        COL_LAST_MODIFIED
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s INTEGER DEFAULT 0 );",
                        COL_NUM_VIEWS
                );

        db.execSQL(CREATE_TABLE);
    }
}
