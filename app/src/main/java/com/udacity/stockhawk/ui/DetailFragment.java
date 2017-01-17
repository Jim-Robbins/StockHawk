package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.R;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";
    static final String DETAIL_TRANSITION_ANIMATION = "DTA";
    private Uri mUri;
    private boolean mTransitionAnimation;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_OPEN,
            Contract.Quote.COLUMN_PREVIOUS_CLOSE,
            Contract.Quote.COLUMN_AVG_DAILY_VOLUME,
            Contract.Quote.COLUMN_VOLUME,
            Contract.Quote.COLUMN_DAYS_HIGH,
            Contract.Quote.COLUMN_DAYS_LOW,
            Contract.Quote.COLUMN_YEAR_HIGH,
            Contract.Quote.COLUMN_YEAR_LOW,
            Contract.Quote.COLUMN_MARKET_CAP,
            Contract.Quote.COLUMN_DIVIDEND_YIELD,
            Contract.Quote.COLUMN_EPS,
            Contract.Quote.COLUMN_PE_RATIO,
            Contract.Quote.COLUMN_ONE_YEAR_TARGET,
            Contract.Quote.COLUMN_CREATED
    };

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
        mSymbolView.setText(data.getString(Contract.Quote.POSITION_SYMBOL));
        mBidPriceView.setText(data.getString(Contract.Quote.POSITION_PRICE));

        mChangeView.setText(data.getString(Contract.Quote.POSITION_ABSOLUTE_CHANGE));
        mChangePercentView.setText("("+data.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE)+")");
//        int color;
//        if (data.getInt(data.getColumnIndex(Contract.ISUP)) == 1) {
//            color = ResourcesCompat.getColor(getResources(), R.color.material_green_700, null);
//        } else {
//            color = ResourcesCompat.getColor(getResources(), R.color.material_red_700, null);
//        }
//        mChangeView.setTextColor(color);
//        mChangePercentView.setTextColor(color);

        setLabelDetailText(mDaysLowView,
                getActivity().getString(R.string.detail_stock_days_low_label),
                data.getString(Contract.Quote.POSITION_DAYS_LOW));
        //mDaysLowView.setText(data.getString(COL_STOCK_DAY_LOW));

        //mDaysHighView.setText(data.getString(COL_STOCK_DAY_HIGH));
        setLabelDetailText(mDaysHighView,
                getActivity().getString(R.string.detail_stock_days_high_label),
                data.getString(Contract.Quote.POSITION_DAYS_HIGH));

        //mYearLowView.setText(data.getString(COL_STOCK_YEAR_LOW));
        setLabelDetailText(mYearLowView,
                getActivity().getString(R.string.detail_stock_year_low_label),
                data.getString(Contract.Quote.POSITION_YEAR_LOW));
        //mYearHighView.setText(data.getString(COL_STOCK_YEAR_HIGH));
        setLabelDetailText(mYearHighView,
                getActivity().getString(R.string.detail_stock_year_high_label),
                data.getString(Contract.Quote.POSITION_YEAR_HIGH));
        //mOpenView.setText(data.getString(COL_STOCK_OPEN));
        setLabelDetailText(mOpenView,
                getActivity().getString(R.string.detail_stock_open_label),
                data.getString(Contract.Quote.POSITION_OPEN));
        //mCloseView.setText(data.getString(COL_STOCK_CLOSE));
        setLabelDetailText(mCloseView,
                getActivity().getString(R.string.detail_stock_close_label),
                data.getString(Contract.Quote.POSITION_PREVIOUS_CLOSE));
        //mVolumeView.setText(data.getString(COL_STOCK_VOL));
        setLabelDetailText(mVolumeView,
                getActivity().getString(R.string.detail_stock_volume_label),
                data.getString(Contract.Quote.POSITION_VOLUME));
        //mAvgVolumeView.setText(data.getString(COL_STOCK_AVG_VOL));
        setLabelDetailText(mAvgVolumeView,
                getActivity().getString(R.string.detail_stock_avg_volume_label),
                data.getString(Contract.Quote.POSITION_AVG_DAILY_VOLUME));
        //mMarketCapView.setText(data.getString(COL_STOCK_MARKET_CAP));
        setLabelDetailText(mMarketCapView,
                getActivity().getString(R.string.detail_stock_market_cap_label),
                data.getString(Contract.Quote.POSITION_MARKET_CAP));
        //mDividendView.setText(data.getString(COL_STOCK_DIVIDEND));
        setLabelDetailText(mDividendView,
                getActivity().getString(R.string.detail_stock_dividend_label),
                data.getString(Contract.Quote.POSITION_DIVIDEND_YIELD));
        //mEPSView.setText(data.getString(COL_STOCK_EPS));
        setLabelDetailText(mEPSView,
                getActivity().getString(R.string.detail_stock_eps_label),
                data.getString(Contract.Quote.POSITION_EPS));
        //mPEView.setText(data.getString(COL_STOCK_PE));
        setLabelDetailText(mPEView,
                getActivity().getString(R.string.detail_stock_pe_label),
                data.getString(Contract.Quote.POSITION_PE_RATIO));
        //mOneYrTargetView.setText(data.getString(COL_STOCK_ONE_YR_TGT));
        setLabelDetailText(mOneYrTargetView,
                getActivity().getString(R.string.detail_stock_one_yr_target_label),
                data.getString(Contract.Quote.POSITION_ONE_YEAR_TARGET));
    }

    private void setLabelDetailText(View view, String labelTxt, String dataTxt) {
        ((TextView) view.findViewById(R.id.detail_text_label)).setText(labelTxt);
        ((TextView) view.findViewById(R.id.detail_text_data)).setText(dataTxt);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}