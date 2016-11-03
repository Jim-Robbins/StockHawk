package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.rest.RecyclerViewItemClickListener;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.touch_helper.SimpleItemTouchHelperCallback;

/**
 * Encapsulates fetching the stock list and displaying it as a
 * {@link android.support.v7.widget.RecyclerView} layout.
 */
public class StockListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String LOG_TAG = StockListFragment.class.getSimpleName();
    private QuoteCursorAdapter mCursorAdapter;
    private RecyclerView mRecyclerView;
    private boolean mHoldForTransition;
    private long mInitialSelectedTicker = -1;
    private ItemTouchHelper mItemTouchHelper;

    private static final int QUOTE_LOADER = 0;

    private static final String[] QUOTE_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE,
            QuoteColumns.ISUP
    };

    static final int COL_QUOTE_ID = 0;
    static final int COL_QUOTE_SYMBOL = 1;
    static final int COL_QUOTE_BIDPRICE = 2;
    static final int COL_QUOTE_PERCENT_CHANGE = 3;
    static final int COL_QUOTE_CHANGE = 4;
    static final int COL_QUOTE_ISUP = 5;
    private Cursor mCursor;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri tickerUri);
        public void onUserInput(String tag, String symbol);
    }

    public StockListFragment() {
        super();
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();

        getLoaderManager().restartLoader(QUOTE_LOADER, null, this);
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mRecyclerView) {
            mRecyclerView.clearOnScrollListeners();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Get a reference to the RecyclerView, and attach this adapter to it.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        // Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getLoaderManager().initLoader(QUOTE_LOADER, null, this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mCursorAdapter = new QuoteCursorAdapter(getActivity(), null);
        mRecyclerView.addOnItemTouchListener(recyclerViewItemClickListener);
        mRecyclerView.setAdapter(mCursorAdapter);

        // Setup the (+) button for adding new quotes
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToRecyclerView(mRecyclerView);
        fab.setOnClickListener(fabOnClickListener);

        // Enable swipe to delete quotes
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        // If there's instance state, mine it for useful information.
        if (savedInstanceState != null) {
            //TODO:Restore cursor state
        }
        return rootView;
    }

    private RecyclerViewItemClickListener recyclerViewItemClickListener =
            new RecyclerViewItemClickListener(getActivity(), new RecyclerViewItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    mCursor.moveToPosition(position);
                    Uri quoteUri = QuoteProvider.Quotes.withSymbol(mCursor.getString(COL_QUOTE_SYMBOL));
                    ((Callback) getActivity())
                            .onItemSelected(quoteUri);
                }
            });

    private View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Utils.isConnected(getActivity())) {
                new MaterialDialog.Builder(getActivity()).title(R.string.symbol_search)
                        .content(R.string.content_test)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // On FAB click, receive user input. Make sure the stock doesn't already exist
                                // in the DB and proceed accordingly
                                Cursor c = getActivity().getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                                        new String[]{QuoteColumns.SYMBOL}, QuoteColumns.SYMBOL + "= ?",
                                        new String[]{input.toString()}, null);
                                if (c.getCount() != 0) {
                                    Utils.setTickerStatus(getActivity(), StockTaskService.TICKER_STATUS_EXISTS);
                                    updateSnackBarStatus();
                                    return;
                                } else {
                                    ((Callback) getActivity())
                                            .onUserInput("add",input.toString());
                                }
                            }
                        })
                        .show();
            } else {
                updateSnackBarStatus();
            }

        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This narrows the return to only the stocks that are most current.
        return new CursorLoader(getActivity(), QuoteProvider.Quotes.CONTENT_URI,
                new String[]{
                        QuoteColumns._ID,
                        QuoteColumns.SYMBOL,
                        QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE,
                        QuoteColumns.CHANGE,
                        QuoteColumns.ISUP
                },
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        updateSnackBarStatus();
        mCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_ticker_status_key))) {
            updateSnackBarStatus();
        }
    }

    public void setInitialSelectedTicker(long initialSelectedTicker) {
        mInitialSelectedTicker = initialSelectedTicker;
    }

    /*
       Updates the empty list view with contextually relevant information that the user can
       use to determine why they aren't seeing weather.
    */
    private void updateSnackBarStatus() {
        @StockTaskService.TickerStatus int tickerStatus = Utils.getTickerStatus(getActivity());
        if (tickerStatus > 0) {
            int message = R.string.empty_ticker_list;
            Snackbar snackbar = Snackbar
                    .make(getView(), message, Snackbar.LENGTH_LONG);

            switch (tickerStatus) {
                case StockTaskService.TICKER_STATUS_ADDED:
                    message = R.string.input_symbol_added;
                    break;
                case StockTaskService.TICKER_STATUS_EXISTS:
                    message = R.string.input_already_saved;
                    break;
                case StockTaskService.TICKER_STATUS_INVALID:
                    message = R.string.empty_ticker_list_invalid_ticker;
                    break;
                case StockTaskService.TICKER_STATUS_SERVER_DOWN:
                    snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                    message = R.string.empty_ticker_list_server_down;
                    break;
                case StockTaskService.TICKER_STATUS_SERVER_INVALID:
                    snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                    message = R.string.empty_ticker_list_server_error;
                    break;
                default:
                    if (!Utils.isConnected(getActivity())) {
                        message = R.string.empty_ticker_list_no_network;
                        snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(R.string.action_retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Utils.isConnected(getActivity())) {
                                    ((Callback) getActivity()).onUserInput("init","");
                                } else {
                                    updateSnackBarStatus();
                                }
                            }
                        });
                    }
            }

            snackbar.setText(message);
            snackbar.show();
        }
    }
}
