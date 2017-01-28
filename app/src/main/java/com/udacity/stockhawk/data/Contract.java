package com.udacity.stockhawk.data;


import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.collect.ImmutableList;

public final class Contract {

    static final String AUTHORITY = "com.udacity.stockhawk";
    static final String PATH_QUOTE = "quote";
    static final String PATH_QUOTE_WITH_SYMBOL = "quote/*";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract() {
    }

    @SuppressWarnings("unused")
    public static final class Quote implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_QUOTE).build();
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_ABSOLUTE_CHANGE = "absolute_change";
        public static final String COLUMN_PERCENTAGE_CHANGE = "percentage_change";
        public static final String COLUMN_HISTORY = "history";
        public static final String COLUMN_OPEN = "open";
        public static final String COLUMN_PREVIOUS_CLOSE = "previous_close";
        public static final String COLUMN_AVG_DAILY_VOLUME = "average_daily_volume";
        public static final String COLUMN_VOLUME = "volume";
        public static final String COLUMN_DAYS_HIGH = "days_high";
        public static final String COLUMN_DAYS_LOW = "days_low";
        public static final String COLUMN_YEAR_HIGH = "year_high";
        public static final String COLUMN_YEAR_LOW = "year_low";
        public static final String COLUMN_CREATED = "created";

        public static final int POSITION_SYMBOL = 1;
        public static final int POSITION_PRICE = 2;
        public static final int POSITION_ABSOLUTE_CHANGE = 3;
        public static final int POSITION_PERCENTAGE_CHANGE = 4;
        public static final int POSITION_HISTORY = 5;
        public static final int POSITION_OPEN = 6;
        public static final int POSITION_PREVIOUS_CLOSE = 7;
        public static final int POSITION_AVG_DAILY_VOLUME = 8;
        public static final int POSITION_VOLUME = 9;
        public static final int POSITION_DAYS_HIGH = 10;
        public static final int POSITION_DAYS_LOW = 11;
        public static final int POSITION_YEAR_HIGH = 12;
        public static final int POSITION_YEAR_LOW = 13;
        public static final int POSITION_CREATED = 14;
        public static final int POSITION_MARKET_CAP = 15;
        public static final int POSITION_DIVIDEND_YIELD = 16;
        public static final int POSITION_EPS = 17;
        public static final int POSITION_PE_RATIO = 18;
        public static final int POSITION_ONE_YEAR_TARGET = 19;

        public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_SYMBOL,
                COLUMN_PRICE,
                COLUMN_ABSOLUTE_CHANGE,
                COLUMN_PERCENTAGE_CHANGE,
                COLUMN_HISTORY,
                COLUMN_OPEN,
                COLUMN_PREVIOUS_CLOSE,
                COLUMN_AVG_DAILY_VOLUME,
                COLUMN_VOLUME,
                COLUMN_DAYS_HIGH,
                COLUMN_DAYS_LOW,
                COLUMN_YEAR_HIGH,
                COLUMN_YEAR_LOW,
                COLUMN_CREATED
        );

        public static final ImmutableList<String> WIDGET_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_SYMBOL,
                COLUMN_PRICE,
                COLUMN_ABSOLUTE_CHANGE,
                COLUMN_PERCENTAGE_CHANGE
        );

        public static final String TABLE_NAME = "quotes";

        public static Uri makeUriForStock(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        static String getStockFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }

}
