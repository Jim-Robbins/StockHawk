package com.sam_chordas.android.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;

import static android.support.v4.content.res.ResourcesCompat.getColor;

/**
 * Created by jim on 11/4/16.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";
    static final String DETAIL_TRANSITION_ANIMATION = "DTA";
    private Uri mUri;
    private boolean mTransitionAnimation;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE,
            QuoteColumns.BIDPRICE,
            QuoteColumns.OPEN,
            QuoteColumns.CLOSE,
            QuoteColumns.AVG_DAILY_VOL,
            QuoteColumns.VOLUME,
            QuoteColumns.DAY_HIGH,
            QuoteColumns.DAY_LOW,
            QuoteColumns.YEAR_HIGH,
            QuoteColumns.YEAR_LOW,
            QuoteColumns.MARKET_CAP,
            QuoteColumns.DIV_YIELD,
            QuoteColumns.EPS,
            QuoteColumns.PE,
            QuoteColumns.ONE_YR_TARGET,
            QuoteColumns.CREATED,
            QuoteColumns.ISUP,
            QuoteColumns.ISCURRENT
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these must change.
    public static final int COL_STOCK_ID = 0;
    public static final int COL_STOCK_SYMBOL = 1;
    public static final int COL_STOCK_PERCENT_CHANGE = 2;
    public static final int COL_STOCK_CHANGE = 3;
    public static final int COL_STOCK_BID_PRICE = 4;
    public static final int COL_STOCK_OPEN = 5;
    public static final int COL_STOCK_CLOSE = 6;
    public static final int COL_STOCK_AVG_VOL = 7;
    public static final int COL_STOCK_VOL = 8;
    public static final int COL_STOCK_DAY_HIGH = 9;
    public static final int COL_STOCK_DAY_LOW = 10;
    public static final int COL_STOCK_YEAR_LOW = 11;
    public static final int COL_STOCK_YEAR_HIGH = 12;
    public static final int COL_STOCK_MARKET_CAP = 13;
    public static final int COL_STOCK_DIVIDEND = 14;
    public static final int COL_STOCK_EPS = 15;
    public static final int COL_STOCK_PE = 16;
    public static final int COL_STOCK_ONE_YR_TGT = 17;
    public static final int COL_STOCK_CREATED = 18;
    public static final int COL_STOCK_ISUP = 19;
    public static final int COL_STOCK_ISCURRENT = 20;

    private TextView mSymbolView;
    private TextView mBidPriceView;
    private TextView mChangeView;
    private TextView mChangePercentView;
    private LinearLayout mDaysLowView;
    private LinearLayout mDaysHighView;
    private LinearLayout mYearLowView;
    private LinearLayout mYearHighView;
    private LinearLayout mOpenView;
    private LinearLayout mCloseView;
    private LinearLayout mVolumeView;
    private LinearLayout mAvgVolumeView;
    private LinearLayout mMarketCapView;
    private LinearLayout mDividendView;
    private LinearLayout mEPSView;
    private LinearLayout mPEView;
    private LinearLayout mOneYrTargetView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            mTransitionAnimation = arguments.getBoolean(DetailFragment.DETAIL_TRANSITION_ANIMATION, false);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mSymbolView = (TextView) rootView.findViewById(R.id.detail_stock_symbol);
        mBidPriceView = (TextView) rootView.findViewById(R.id.detail_stock_bid_price);
        mChangeView = (TextView) rootView.findViewById(R.id.detail_stock_change);
        mChangePercentView = (TextView) rootView.findViewById(R.id.detail_stock_change_percent);
        mDaysLowView = (LinearLayout) rootView.findViewById(R.id.detail_stock_days_low);
        mDaysHighView = (LinearLayout) rootView.findViewById(R.id.detail_stock_days_high);
        mYearLowView = (LinearLayout) rootView.findViewById(R.id.detail_stock_year_low);
        mYearHighView = (LinearLayout) rootView.findViewById(R.id.detail_stock_year_high);
        mOpenView = (LinearLayout) rootView.findViewById(R.id.detail_stock_open);
        mCloseView = (LinearLayout) rootView.findViewById(R.id.detail_stock_close);
        mVolumeView = (LinearLayout) rootView.findViewById(R.id.detail_stock_vol);
        mAvgVolumeView = (LinearLayout) rootView.findViewById(R.id.detail_stock_avg_vol);
        mMarketCapView = (LinearLayout) rootView.findViewById(R.id.detail_stock_market_cap);
        mDividendView = (LinearLayout) rootView.findViewById(R.id.detail_stock_dividend);
        mEPSView = (LinearLayout) rootView.findViewById(R.id.detail_stock_eps);
        mPEView = (LinearLayout) rootView.findViewById(R.id.detail_stock_pe);
        mOneYrTargetView = (LinearLayout) rootView.findViewById(R.id.detail_stock_one_yr_target_price);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // Read weather condition ID from cursor
            setTextViews(data);
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        Toolbar toolbarView = (Toolbar) getView().findViewById(R.id.toolbar);
//
//        // We need to start the enter transition after the data has loaded
//        if ( mTransitionAnimation ) {
//            activity.supportStartPostponedEnterTransition();
//
//            if ( null != toolbarView ) {
//                activity.setSupportActionBar(toolbarView);
//
//                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
//                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            }
//        } else {
//            if ( null != toolbarView ) {
//                Menu menu = toolbarView.getMenu();
//                if ( null != menu ) menu.clear();
//            }
//        }
    }

    private void setTextViews(Cursor data) {
        mSymbolView.setText(data.getString(COL_STOCK_SYMBOL));
        mBidPriceView.setText(data.getString(COL_STOCK_BID_PRICE));

        mChangeView.setText(data.getString(COL_STOCK_CHANGE));
        mChangePercentView.setText("("+data.getString(COL_STOCK_PERCENT_CHANGE)+")");
        int color;
        if (data.getInt(data.getColumnIndex(QuoteColumns.ISUP)) == 1) {
            color = ResourcesCompat.getColor(getResources(), R.color.material_green_700, null);
        } else {
            color = ResourcesCompat.getColor(getResources(), R.color.material_red_700, null);
        }
        mChangeView.setTextColor(color);
        mChangePercentView.setTextColor(color);

        setLabelDetailText(mDaysLowView,
                getActivity().getString(R.string.detail_stock_days_low_label),
                data.getString(COL_STOCK_DAY_LOW));
        //mDaysLowView.setText(data.getString(COL_STOCK_DAY_LOW));

        //mDaysHighView.setText(data.getString(COL_STOCK_DAY_HIGH));
        setLabelDetailText(mDaysHighView,
                getActivity().getString(R.string.detail_stock_days_high_label),
                data.getString(COL_STOCK_DAY_HIGH));

        //mYearLowView.setText(data.getString(COL_STOCK_YEAR_LOW));
        setLabelDetailText(mYearLowView,
                getActivity().getString(R.string.detail_stock_year_low_label),
                data.getString(COL_STOCK_YEAR_LOW));
        //mYearHighView.setText(data.getString(COL_STOCK_YEAR_HIGH));
        setLabelDetailText(mYearHighView,
                getActivity().getString(R.string.detail_stock_year_high_label),
                data.getString(COL_STOCK_YEAR_HIGH));
        //mOpenView.setText(data.getString(COL_STOCK_OPEN));
        setLabelDetailText(mOpenView,
                getActivity().getString(R.string.detail_stock_open_label),
                data.getString(COL_STOCK_OPEN));
        //mCloseView.setText(data.getString(COL_STOCK_CLOSE));
        setLabelDetailText(mCloseView,
                getActivity().getString(R.string.detail_stock_close_label),
                data.getString(COL_STOCK_CLOSE));
        //mVolumeView.setText(data.getString(COL_STOCK_VOL));
        setLabelDetailText(mVolumeView,
                getActivity().getString(R.string.detail_stock_volume_label),
                data.getString(COL_STOCK_VOL));
        //mAvgVolumeView.setText(data.getString(COL_STOCK_AVG_VOL));
        setLabelDetailText(mAvgVolumeView,
                getActivity().getString(R.string.detail_stock_avg_volume_label),
                data.getString(COL_STOCK_AVG_VOL));
        //mMarketCapView.setText(data.getString(COL_STOCK_MARKET_CAP));
        setLabelDetailText(mMarketCapView,
                getActivity().getString(R.string.detail_stock_market_cap_label),
                data.getString(COL_STOCK_MARKET_CAP));
        //mDividendView.setText(data.getString(COL_STOCK_DIVIDEND));
        setLabelDetailText(mDividendView,
                getActivity().getString(R.string.detail_stock_dividend_label),
                data.getString(COL_STOCK_DIVIDEND));
        //mEPSView.setText(data.getString(COL_STOCK_EPS));
        setLabelDetailText(mEPSView,
                getActivity().getString(R.string.detail_stock_eps_label),
                data.getString(COL_STOCK_EPS));
        //mPEView.setText(data.getString(COL_STOCK_PE));
        setLabelDetailText(mPEView,
                getActivity().getString(R.string.detail_stock_pe_label),
                data.getString(COL_STOCK_PE));
        //mOneYrTargetView.setText(data.getString(COL_STOCK_ONE_YR_TGT));
        setLabelDetailText(mOneYrTargetView,
                getActivity().getString(R.string.detail_stock_one_yr_target_label),
                data.getString(COL_STOCK_ONE_YR_TGT));
    }

    private void setLabelDetailText(View view, String labelTxt, String dataTxt) {
        ((TextView) view.findViewById(R.id.detail_text_label)).setText(labelTxt);
        ((TextView) view.findViewById(R.id.detail_text_data)).setText(dataTxt);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
