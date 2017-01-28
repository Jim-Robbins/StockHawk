package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.util.Utility;

/**
 * Created by jim on 1/26/17.
 */

public class WidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = WidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListProvider();
    }

    private class ListProvider implements RemoteViewsFactory {
        private Cursor data = null;

        @Override
        public void onCreate() {
            Log.d(LOG_TAG, "Widget List Provider: onCreate()");
        }

        @Override
        public void onDataSetChanged() {
            if (data != null) {
                data.close();
            }

            final long identityToken = Binder.clearCallingIdentity();
            data = getContentResolver().query(Contract.Quote.URI,
                    Contract.Quote.WIDGET_COLUMNS.toArray(new String[]{}),
                    null,
                    null,
                    Contract.Quote.COLUMN_SYMBOL + " ASC");
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            if (data != null) {
                data.close();
                data = null;
            }
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position == AdapterView.INVALID_POSITION ||
                    data == null || !data.moveToPosition(position)) {
                return null;
            }
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item_quote);
            views.setTextViewText(R.id.stock_symbol, data.getString(Contract.Quote.POSITION_SYMBOL));
            views.setTextViewText(R.id.price, Utility.getDollarFormat().format(data.getFloat(Contract.Quote.POSITION_PRICE)));

            float rawAbsoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
            float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

            if (rawAbsoluteChange > 0) {
                views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            } else {
                views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
            }

            String change = Utility.getDollarFormatWithPlus().format(rawAbsoluteChange);
            String percentage = Utility.getPercentageFormat().format(percentageChange / 100);

            Context context = getBaseContext();
            if (PrefUtils.getDisplayMode(context)
                    .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
                views.setTextViewText(R.id.change, change);
            } else {
                views.setTextViewText(R.id.change, percentage);
            }

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(getPackageName(), R.layout.widget_list_item_quote);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if (data.moveToPosition(position))
                return data.getLong(Contract.Quote.POSITION_SYMBOL);
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    };
}
