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
