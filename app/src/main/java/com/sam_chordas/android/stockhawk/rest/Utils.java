package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.service.StockTaskService;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

    private static String LOG_TAG = Utils.class.getSimpleName();

    public static boolean showPercent = true;

    public static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        try {
            if(change.indexOf("ul") > 0) return change;
            Log.d(LOG_TAG,change);
            double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
            change = String.format("%.2f", round);
            StringBuffer changeBuffer = new StringBuffer(change);
            changeBuffer.insert(0, weight);
            changeBuffer.append(ampersand);
            change = changeBuffer.toString();
        } catch (Error e) {
            Log.e(LOG_TAG, "Not able to truncate string:"+ change);
        }
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        try {
            String change = jsonObject.getString("Change");
            builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
            builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
            builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                    jsonObject.getString("ChangeinPercent"), true));
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
            builder.withValue(QuoteColumns.OPEN, jsonObject.getString("Open"));
            builder.withValue(QuoteColumns.CLOSE, jsonObject.getString("PreviousClose"));
            builder.withValue(QuoteColumns.AVG_DAILY_VOL, jsonObject.getString("AverageDailyVolume"));
            builder.withValue(QuoteColumns.VOLUME, jsonObject.getString("Volume"));
            builder.withValue(QuoteColumns.DAY_HIGH, jsonObject.getString("DaysHigh"));
            builder.withValue(QuoteColumns.DAY_LOW, jsonObject.getString("DaysLow"));
            builder.withValue(QuoteColumns.YEAR_HIGH, jsonObject.getString("YearHigh"));
            builder.withValue(QuoteColumns.YEAR_LOW, jsonObject.getString("YearLow"));
            builder.withValue(QuoteColumns.MARKET_CAP, jsonObject.getString("MarketCapitalization"));
            builder.withValue(QuoteColumns.DIV_YIELD, jsonObject.getString("DividendYield"));
            builder.withValue(QuoteColumns.EPS, jsonObject.getString("EarningsShare"));
            builder.withValue(QuoteColumns.PE, jsonObject.getString("PERatio"));
            builder.withValue(QuoteColumns.ONE_YR_TARGET, jsonObject.getString("OneyrTargetPrice"));
            builder.withValue(QuoteColumns.ISCURRENT, 1);
            if (change.charAt(0) == '-') {
                builder.withValue(QuoteColumns.ISUP, 0);
            } else {
                builder.withValue(QuoteColumns.ISUP, 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    public static Boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean connected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        setTickerStatus(context, connected ? StockTaskService.TICKER_STATUS_OK : StockTaskService.TICKER_STATUS_UNKNOWN);
        return connected;
    }

    /**
     * Sets the ticker status into shared preference.  This function should not be called from
     * the UI thread because it uses commit to write to the shared preferences.
     *
     * @param context      Context to get the PreferenceManager from.
     * @param tickerStatus The IntDef value to set
     */
    public static void setTickerStatus(Context context, @StockTaskService.TickerStatus int tickerStatus) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spEditor = pref.edit();
        spEditor.putInt(context.getString(R.string.pref_ticker_status_key), tickerStatus);
        spEditor.commit();
    }

    /**
     * Get the ticker status from the shared preference.
     *
     * @param context used to get the SharedPreference object
     * @return the ticker status integer type
     */
    public static int getTickerStatus(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(context.getString(R.string.pref_ticker_status_key), StockTaskService.TICKER_STATUS_UNKNOWN);
    }
}
