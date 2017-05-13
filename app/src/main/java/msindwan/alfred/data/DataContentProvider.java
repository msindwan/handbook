package msindwan.alfred.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Locale;

import msindwan.alfred.data.schema.TutorialStepTable;
import msindwan.alfred.data.schema.TutorialTable;

/**
 * Created by Mayank Sindwani on 2017-05-11.
 *
 */
public class DataContentProvider extends ContentProvider {

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String AUTHORITY = "msindwan.alfred.data.DataContentProvider";

    static final String DATABASE_NAME = "alfred";
    static final String BASE_PATH = "alfred";
    static final int DATABASE_VERSION = 1;

    private DatabaseHelper m_dbHelper;

    public static final Uri TUTORIAL_URI = Uri.parse(
            "content://" + AUTHORITY + "/" + BASE_PATH + "/tutorials");

    private static final int TUTORIAL    = 0;
    private static final int TUTORIAL_ID = 1;

    static {
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/tutorials", TUTORIAL);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/tutorials/#", TUTORIAL_ID);
    }

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private class DatabaseHelper extends SQLiteOpenHelper {

        private DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            TutorialTable.createTable(db);
            TutorialStepTable.createTable(db);
        }

        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Upgrade tables here
        }

    }

    @Override
    public boolean onCreate() {
        m_dbHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(
            @NonNull Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        SQLiteDatabase db = m_dbHelper.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (URI_MATCHER.match(uri)) {

            case TUTORIAL:
                qb.setTables(TutorialTable.TABLE_NAME);
                break;

            case TUTORIAL_ID:
                qb.setTables(TutorialTable.TABLE_NAME);
                qb.appendWhere(
                        String.format(
                                Locale.getDefault(),
                                "%s = %s ",
                                TutorialTable.COL_ID,uri.getPathSegments().get(1)
                        )
                );
                break;

        }

        if (sortOrder == null || sortOrder.isEmpty()){
            sortOrder = TutorialTable.COL_NAME;
        }

        Cursor cursor = qb.query(
                db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        Context context = getContext();
        if (context == null) {
            // TODO Throw a more appropriate exception with a description.
            throw new SQLException("");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)){
            case TUTORIAL:
                return "vnd.android.cursor.dir/vnd.alfred.tutorials";
            case TUTORIAL_ID:
                return "vnd.android.cursor.item/vnd.alfred.tutorials";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = m_dbHelper.getWritableDatabase();
        Uri uriToReturn;

        switch (URI_MATCHER.match(uri)){

            case TUTORIAL:
                long id = db.insert(TutorialTable.TABLE_NAME, "", values);
                uriToReturn = ContentUris.withAppendedId(TUTORIAL_URI, id);
                getContext().getContentResolver().notifyChange(uriToReturn, null);
                break;

            default:
                throw new SQLException("Failed to insert row into " + uri);
        }

        return uriToReturn;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(
            @NonNull Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {
        return 0;
    }
}
