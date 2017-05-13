package msindwan.alfred.data.schema;

import android.database.sqlite.SQLiteDatabase;

import java.util.Locale;

/**
 * Created by Mayank Sindwani on 2017-05-11.
 *
 */

public class TutorialTable {

    public static final String TABLE_NAME = "tutorials";

    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_DATE_CREATED = "date_created";
    public static final String COL_LAST_MODIFIED = "last_modified";
    public static final String COL_NUM_VIEWS = "num_views";

    private static final int NAME_MAX_LENGTH = 100;

    public static void createTable(SQLiteDatabase db) {
        String CREATE_TABLE =
                String.format(
                    Locale.getDefault(),
                    "CREATE TABLE %s (",
                    TABLE_NAME
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT,",
                        COL_ID
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s VARCHAR(%d),",
                        COL_NAME,
                        NAME_MAX_LENGTH
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s DATETIME DEFAULT CURRENT_TIMESTAMP,",
                        COL_DATE_CREATED
                ) +
                String.format(
                        Locale.getDefault(),
                        "%s DATETIME DEFAULT CURRENT_TIMESTAMP,",
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
