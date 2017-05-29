package msindwan.handbook.views.dashboard.components;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.support.v4.app.LoaderManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.Locale;

import msindwan.handbook.R;
import msindwan.handbook.data.DataContentProvider;
import msindwan.handbook.data.DatabaseHelper;
import msindwan.handbook.data.schema.TutorialTable;
import msindwan.handbook.models.Tutorial;
import msindwan.handbook.views.tutorial.TutorialEditor;
import msindwan.handbook.views.tutorial.TutorialViewer;

/**
 * Created by Mayank Sindwani on 2017-05-03.
 *
 * TutorialListFragment:
 * Defines the list fragment for tutorials on the dashboard.
 */
public class TutorialListFragment
        extends
            ListFragment
        implements
            SearchView.OnQueryTextListener,
            LoaderManager.LoaderCallbacks<Cursor> {

    // Table projection.
    private static final String[] TUTORIALS_PROJECTION = new String[] {
            TutorialTable.COL_ID,
            TutorialTable.COL_NAME,
            TutorialTable.COL_LAST_MODIFIED
    };
    private TutorialListAdapter m_adapter;
    private String m_searchFilter;

    /**
     * TutorialListAdapter:
     * Defines a custom adapter for the tutorial list fragment.
     */
    private class TutorialListAdapter extends SimpleCursorAdapter {

        // Constructor.
        private TutorialListAdapter(
                Context context,
                int layout,
                Cursor c,
                String[] from,
                int[] to,
                int flags) {
            super(context, layout, c, from, to, flags);
        }

        /**
         * Get a View that displays the data at the specified position in the data set.
         *
         * @param position The position of the item within the adapter's data set of the item whose view
         *        we want.
         * @param convertView The old view to reuse, if possible.
         * @param parent The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view = super.getView(position, convertView, parent);

            // Set the view tag as the item ID.
            Cursor cursor = (Cursor)m_adapter.getItem(position);
            long id = cursor.getLong(cursor.getColumnIndex(TutorialTable.COL_ID));
            view.setTag(id);

            // Bind the event listeners.
            ImageButton menuButton =
                    (ImageButton)view.findViewById(R.id.dashboard_list_item_menu_button);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().openContextMenu(view);
                }
            });
            return view;
        }
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        setEmptyText(getResources().getText(R.string.no_tutorials));

        // Create an adapter for tutorials.
        m_adapter = new TutorialListAdapter(getActivity(),
                R.layout.dashboard_list_item, null,
                new String[] {
                    TutorialTable.COL_NAME,
                    TutorialTable.COL_LAST_MODIFIED
                },
                new int[] {
                    R.id.dashboard_list_item_title,
                    R.id.dashboard_list_item_subtitle
                }, 0);

        setListAdapter(m_adapter);

        // Initialize the loader.
        getLoaderManager().initLoader(0, null, this);
        registerForContextMenu(getListView());
    }

    /**
     * Called when a context menu for the {@code view} is about to be shown.
     *
     * @param menu The context menu element.
     * @param v The current view.
     * @param menuInfo Info pertaining to the context menu.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Create the list item context menu.
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.dashboard_list_context_menu, menu);
    }

    /**
     * This hook is called whenever an item in a context menu is selected.
     *
     * @param item The context menu item that was selected.
     * @return boolean Return false to allow normal context menu processing to
     *         proceed, true to consume it here.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info
                = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        long id = (long)info.targetView.getTag();

        switch (item.getItemId()) {
            case R.id.dashboard_list_item_menu_edit:
                // Create an editor intent provided the tutorial id.
                Intent intent = new Intent(getContext(), TutorialEditor.class);
                intent.putExtra("tutorial_id", Long.toString(id));
                startActivity(intent);
                return true;

            case R.id.dashboard_list_item_menu_share:
                // TODO: Implement import / export
                break;

            case R.id.dashboard_list_item_menu_delete:
                DatabaseHelper helper = DatabaseHelper.getInstance(getContext());
                SQLiteDatabase db = helper.getWritableDatabase();

                Tutorial tutorial = new Tutorial();
                tutorial.setId(id);

                // Delete the tutorial.
                db.beginTransaction();
                {
                    helper.delete(tutorial);
                    db.setTransactionSuccessful();
                }
                db.endTransaction();

                ContentResolver resolver = getActivity().getContentResolver();
                resolver.notifyChange(DataContentProvider.TUTORIAL_URI, null);
                return true;
        }

        return super.onContextItemSelected(item);
    }

    /**
     * Initialize the contents of the Fragment host's standard options menu.
     *
     * @param menu The options menu in which you place
     * @param inflater The menu inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Create the search bar.
        MenuItem item = menu.add("Search");
        SearchView sv = new SearchView(getActivity());

        item.setIcon(android.R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        // Bind the change listener.
        sv.setOnQueryTextListener(this);
        item.setActionView(sv);

        // NOTE: This is a little hacky. Here the text view colour is changed to
        // white in order to display correctly on the primary colour for the app.
        // TODO: Find a more elegant way of changing the search bar text colour.
        int id = sv
                .getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);

        TextView textView = (TextView) sv.findViewById(id);
        textView.setTextColor(
                ContextCompat.getColor(getContext(), android.R.color.white));
    }


    /**
     * This method will be called when an item in the list is selected.
     *
     * @param l The ListView where the click happened
     * @param v The view that was clicked within the ListView
     * @param position The position of the view in the list
     * @param id The row id of the item that was clicked
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Long tutorial_id = (long)v.getTag();
        Intent intent = new Intent(getContext(), TutorialViewer.class);
        intent.putExtra("tutorial_id", tutorial_id.toString());
        startActivity(intent);
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     *
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        // Update the search filter and reload the list.
        m_searchFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    /**
     * Called when the user submits the query.
     *
     * @param query the query text that is to be submitted
     *
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String orderBy = getArguments().getString("order", TutorialTable.COL_NAME);
        String order = orderBy.equals(TutorialTable.COL_NAME) ? "ASC" : "DESC";
        String select;

        // Apply the search filter to the query (if any).
        if (m_searchFilter != null) {
            select = String.format(
                    Locale.getDefault(),
                    "(%s LIKE '%%%s%%')",
                    TutorialTable.COL_NAME,
                    m_searchFilter
            );
        } else {
            select = String.format(
                    Locale.getDefault(),
                    "(%s NOTNULL)",
                    TutorialTable.COL_NAME
            );
        }

        // Return the new cursor.
        return new CursorLoader(
                getActivity(),
                DataContentProvider.TUTORIAL_URI,
                TUTORIALS_PROJECTION,
                select,
                null,
                orderBy + " COLLATE LOCALIZED " + order);
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the new cursor.
        m_adapter.swapCursor(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // The cursor is closing, so remove its reference.
        m_adapter.swapCursor(null);
    }
}