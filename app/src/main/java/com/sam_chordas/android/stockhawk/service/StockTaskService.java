package com.sam_chordas.android.stockhawk.service;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sam_chordas on 9/30/15.
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 */
public class StockTaskService extends GcmTaskService {
    private String LOG_TAG = StockTaskService.class.getSimpleName();

    private OkHttpClient client = new OkHttpClient();
    private String responseBody;
    private Context mContext;
    private StringBuilder mStoredSymbols = new StringBuilder();
    private boolean isUpdate;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TICKER_STATUS_OK,
            TICKER_STATUS_ADDED,
            TICKER_STATUS_EXISTS,
            TICKER_STATUS_INVALID,
            TICKER_STATUS_SERVER_DOWN,
            TICKER_STATUS_SERVER_INVALID,
            TICKER_STATUS_UNKNOWN
    })
    public @interface TickerStatus {
    }

    public static final int TICKER_STATUS_OK = 0;
    public static final int TICKER_STATUS_ADDED = 1;
    public static final int TICKER_STATUS_EXISTS = 2;
    public static final int TICKER_STATUS_INVALID = 3;
    public static final int TICKER_STATUS_SERVER_DOWN = 10;
    public static final int TICKER_STATUS_SERVER_INVALID = 11;
    public static final int TICKER_STATUS_UNKNOWN = 20;


    public static final String ACTION_FAILURE = "GcmTaskService#ACTION_FAILURE";

    public StockTaskService() {
    }

    public StockTaskService(Context context) {
        mContext = context;
    }

    @Override
    public int onRunTask(TaskParams params) {
        if (mContext == null) {
            mContext = this;
        }

        StringBuilder urlStringBuilder = getUrlStringBuilder(params);

        int result = GcmNetworkManager.RESULT_FAILURE;

        if (urlStringBuilder != null) {
            String urlString = urlStringBuilder.toString();
            result = fetchData(urlString);
            if (result == GcmNetworkManager.RESULT_SUCCESS) {
                if (params.getTag().equals("history")) {
                    Log.d(LOG_TAG, "Parse historical data");
                } else {
                    processResponse(responseBody);
                }
            }

        }

        return result;
    }

    private int fetchData(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            responseBody = response.body().string();
            Log.d(LOG_TAG, "fetchUrl:response:" + responseBody);

            if (response.code() != 200) {
                return GcmNetworkManager.RESULT_FAILURE;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "fetchUrl:error" + e.toString());
            return GcmNetworkManager.RESULT_FAILURE;
        }

        return GcmNetworkManager.RESULT_SUCCESS;
    }

    private void processResponse(String getResponse) {
        try {
            ContentValues contentValues = new ContentValues();
            // update ISCURRENT to 0 (false) so new data is current
            if (isUpdate) {
                contentValues.put(QuoteColumns.ISCURRENT, 0);
                mContext.getContentResolver().update(QuoteProvider.Quotes.CONTENT_URI, contentValues,
                        null, null);
            }

            ArrayList<ContentProviderOperation> batchOperations =
                    quoteJsonToContentVals(getResponse);

            if (batchOperations.size() > 0) {
                mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
                        batchOperations);
            }
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
            setTickerStatus(TICKER_STATUS_UNKNOWN);
        }
    }

    private StringBuilder getUrlStringBuilder(TaskParams params) {
        StringBuilder urlStringBuilder = new StringBuilder();

        // Base URL for the Yahoo query
        urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
        try {
            String dataTable;
            if (params.getTag().equals("history")) {
                dataTable = "yahoo.finance.historicaldata";
            } else {
                dataTable = "yahoo.finance.quotes";
            }
            urlStringBuilder.append(URLEncoder.encode("select * from " + dataTable + " where symbol "
                    + "in (", "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            setTickerStatus(TICKER_STATUS_SERVER_INVALID);
            e.printStackTrace();
        }

        // Check if we are getting all quotes or adding a new tickerSymbol to the list
        if (params.getTag().equals("init") || params.getTag().equals("periodic")) {
            appendAllTickerSymbols(urlStringBuilder);
        } else if (params.getTag().equals("add")) {
            isUpdate = false;
            // get symbol from params.getExtra and build query
            String stockInput = params.getExtras().getString("symbol");
            appendNewTickerSymbol(urlStringBuilder, stockInput);
        }

        if (params.getTag().equals("history")) {
            String stockInput = params.getExtras().getString("symbol");
            appendNewTickerSymbol(urlStringBuilder, stockInput);
            // Initialize the formatter
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            // initialize calendar object
            Calendar cal = Calendar.getInstance();
            String endDate = simpleDateFormat.format(cal.getTime());
            cal.add(Calendar.YEAR, -1);
            String startDate = simpleDateFormat.format(cal.getTime());

            urlStringBuilder.append("and startDate = \""+startDate.toString()+"\" and endDate = \""+endDate.toString()+"\"");
        }

        // finalize the URL for the API query.
        urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                + "org%2Falltableswithkeys&callback=&a=01&b=19&c=2010&d=01&e=19&f=2010&g=d");

        Log.d(LOG_TAG, urlStringBuilder.toString());
        return urlStringBuilder;
    }


    private void appendAllTickerSymbols(StringBuilder baseUrlStringBuilder) {
        Cursor initQueryCursor;
        isUpdate = true;
        initQueryCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{"Distinct " + QuoteColumns.SYMBOL}, null,
                null, null);
        if (initQueryCursor.getCount() == 0 || initQueryCursor == null) {
            // Init task. Populates DB with quotes for the symbols seen below
            try {
                baseUrlStringBuilder.append(
                        URLEncoder.encode("\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\")", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                setTickerStatus(TICKER_STATUS_SERVER_INVALID);
                e.printStackTrace();
            }
        } else if (initQueryCursor != null) {
            DatabaseUtils.dumpCursor(initQueryCursor);
            initQueryCursor.moveToFirst();
            for (int i = 0; i < initQueryCursor.getCount(); i++) {
                mStoredSymbols.append("\"" +
                        initQueryCursor.getString(initQueryCursor.getColumnIndex("symbol")) + "\",");
                initQueryCursor.moveToNext();
            }
            mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), ")");
            try {
                baseUrlStringBuilder.append(URLEncoder.encode(mStoredSymbols.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                setTickerStatus(TICKER_STATUS_SERVER_INVALID);
                e.printStackTrace();
            }
        }
    }

    private void appendNewTickerSymbol(StringBuilder baseUrlStringBuilder, String tickerSymbol) {
        isUpdate = false;
        try {
            baseUrlStringBuilder.append(URLEncoder.encode("\"" + tickerSymbol + "\")", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            setTickerStatus(TICKER_STATUS_SERVER_INVALID);
        }
    }

    public boolean isValidTickerSymbol(JSONObject jsonObject) {
        boolean result = true;
        try {
            String bid = jsonObject.getString("Bid");
            if (bid.equalsIgnoreCase("null")) {
                result = false;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
            setTickerStatus(TICKER_STATUS_UNKNOWN);
            result = false;
        }

        return result;
    }

    public ArrayList quoteJsonToContentVals(String JSON) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            if (jsonObject != null && jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject("query");
                int count = Integer.parseInt(jsonObject.getString("count"));
                if (count == 1) {
                    jsonObject = jsonObject.getJSONObject("results")
                            .getJSONObject("quote");
                    if (isValidTickerSymbol(jsonObject)) {
                        batchOperations.add(Utils.buildBatchOperation(jsonObject));
                        setTickerStatus(TICKER_STATUS_ADDED);
                    } else {
                        setTickerStatus(TICKER_STATUS_INVALID);
                        Log.d(LOG_TAG, "quoteJsonToContentVals - skipped it!");
                    }
                } else {
                    JSONArray resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

                    if (resultsArray != null && resultsArray.length() != 0) {
                        for (int i = 0; i < resultsArray.length(); i++) {
                            jsonObject = resultsArray.getJSONObject(i);
                            batchOperations.add(Utils.buildBatchOperation(jsonObject));
                        }
                    }
                    setTickerStatus(TICKER_STATUS_OK);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
            setTickerStatus(TICKER_STATUS_UNKNOWN);
        }
        return batchOperations;
    }

    private void setTickerStatus(@TickerStatus int tickerStatus) {
        Utils.setTickerStatus(mContext, tickerStatus);
    }

}
