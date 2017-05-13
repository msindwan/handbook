package msindwan.alfred.data.schema;

import android.database.sqlite.SQLiteDatabase;

import java.util.Locale;

/**
 * Created by Mayank Sindwani on 2017-05-11.
 *
 */

public class TutorialStepTable {

    public static final String TABLE_NAME = "tutorial_steps";

    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String TUTORIAL_ID = "tutorial_id";
    public static final String STEP_INDEX = "step_index";


    public static final int MAX_TITLE_LENGTH = 100;

    public static void createTable(SQLiteDatabase db) {
        String CREATE_TABLE = String.format(Locale.getDefault(), "CREATE TABLE %s (", TABLE_NAME) +
                String.format(Locale.getDefault(), "%s INTEGER PRIMARY KEY AUTOINCREMENT,", _ID) +
                String.format(Locale.getDefault(), "%s VARCHAR(%d),", TITLE, MAX_TITLE_LENGTH) +
                String.format(Locale.getDefault(), "%s TEXT,", DESCRIPTION) +
                String.format(Locale.getDefault(), "%s INTEGER,", TUTORIAL_ID) +
                String.format(Locale.getDefault(), "%s INTEGER,", STEP_INDEX) +
                String.format(Locale.getDefault(),
                        "FOREIGN KEY(%s) REFERENCES %s(%s));",
                        TUTORIAL_ID,
                        TutorialTable.COL_NAME,
                        TutorialTable.COL_ID);

        db.execSQL(CREATE_TABLE);
    }


}
