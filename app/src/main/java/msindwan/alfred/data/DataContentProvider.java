package msindwan.alfred.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Locale;
import msindwan.alfred.data.schema.TutorialTable;

/**
 * Created by Mayank Sindwani on 2017-05-11.
 *
 * DataContentProvider:
 * The data access layer for the application.
 */
public class DataContentProvider extends ContentProvider {

    // Database config parameters.
    private static final String AUTHORITY = "msindwan.alfred.data.DataContentProvider";
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static final String BASE_PATH = "alfred";

    // Define provider URIs.
    public static final Uri TUTORIAL_URI = Uri.parse(
            "content://" + AUTHORITY + "/" + BASE_PATH + "/tutorials"
    );
    private static final int TUTORIAL_URI_KEY = 0;
    private static final int TUTORIAL_ID_URI_KEY = 1;

    static {
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/tutorials", TUTORIAL_URI_KEY);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/tutorials/#", TUTORIAL_ID_URI_KEY);
    }

    @Override
    public boolean onCreate() {
        return false;
    }
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)){
            case TUTORIAL_URI_KEY:
                return "vnd.android.cursor.dir/vnd.alfred.tutorials";
            case TUTORIAL_ID_URI_KEY:
                return "vnd.android.cursor.item/vnd.alfred.tutorials";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
    // The data content provider only exposes URIs to query data.
    // Data manipulation thus far is restricted to within the
    // context of the app.
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues v) {
        return null;
    }
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] args) {
        return -1;
    }
    @Override
    public int update(@NonNull Uri uri, ContentValues v, String selection, String[] args) {
        return -1;
    }
    @Override
    public Cursor query(
            @NonNull Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        SQLiteDatabase db = DatabaseHelper.getInstance(getContext()).getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Determine how to handle the URI.
        switch (URI_MATCHER.match(uri)) {

            case TUTORIAL_URI_KEY:
                qb.setTables(TutorialTable.TABLE_NAME);
                break;

            case TUTORIAL_ID_URI_KEY:
                qb.setTables(TutorialTable.TABLE_NAME);
                qb.appendWhere(
                        String.format(
                                Locale.getDefault(),
                                "%s = %s ",
                                TutorialTable.COL_ID,
                                uri.getPathSegments().get(1)
                        )
                );
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (sortOrder == null || sortOrder.isEmpty()){
            sortOrder = TutorialTable.COL_NAME;
        }

        // Create the cursor for the query.
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

        // Send an update notification.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
}
