package msindwan.alfred.data.schema;

import android.database.sqlite.SQLiteDatabase;

import java.util.Locale;

/**
 * Created by Mayank Sindwani on 2017-05-11.
 *
 * TutorialTable:
 * Defines the schema for steps.
 */
@SuppressWarnings("WeakerAccess")
public class StepTable {

    // Table properties.
    public static final String TABLE_NAME        = "steps";
    public static final String COL_ID            = "_id";
    public static final String COL_TITLE         = "title";
    public static final String COL_INSTRUCTIONS  = "instructions";
    public static final String COL_TUTORIAL_ID   = "tutorial_id";
    public static final String COL_INDEX         = "step_index";
    public static final int COL_TITLE_MAX_LENGTH = 100;

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
                        COL_TITLE,
                        COL_TITLE_MAX_LENGTH
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s TEXT,",
                        COL_INSTRUCTIONS
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s INTEGER NOT NULL,",
                        COL_TUTORIAL_ID
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s INTEGER NOT NULL,",
                        COL_INDEX
                ) +
                String.format(
                        Locale.getDefault(),
                        "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE);",
                        COL_TUTORIAL_ID,
                        TutorialTable.TABLE_NAME,
                        TutorialTable.COL_ID
                );

        db.execSQL(CREATE_TABLE);
    }
}
