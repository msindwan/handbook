package msindwan.alfred.data.schema;

import android.database.sqlite.SQLiteDatabase;

import java.util.Locale;

/**
 * Created by Mayank Sindwani on 2017-05-18.
 *
 * TutorialTable:
 * Defines the schema for tutorials.
 */
@SuppressWarnings("WeakerAccess")
public class RequirementTable {

    // Table properties.
    public static final String TABLE_NAME        = "requirements";
    public static final String COL_ID            = "_id";
    public static final String COL_NAME          = "name";
    public static final String COL_AMOUNT        = "amount";
    public static final String COL_UNIT          = "unit";
    public static final String COL_OPTIONAL      = "optional";
    public static final String COL_STEP_ID       = "step_id";
    public static final int COL_NAME_MAX_LENGTH  = 100;
    public static final int COL_UNIT_MAX_LENGTH  = 25;

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
                        "%s REAL,",
                        COL_AMOUNT
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s VARCHAR(%d),",
                        COL_UNIT,
                        COL_UNIT_MAX_LENGTH
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s INTEGER NOT NULL,",
                        COL_STEP_ID
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s INTEGER NOT NULL DEFAULT 0,",
                        COL_OPTIONAL
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
