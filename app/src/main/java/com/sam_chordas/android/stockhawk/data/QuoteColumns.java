package com.sam_chordas.android.stockhawk.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by sam_chordas on 10/5/15.
 */
public class QuoteColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String SYMBOL = "symbol";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String PERCENT_CHANGE = "percent_change";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String CHANGE = "change";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String BIDPRICE = "bid_price";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String OPEN = "Open";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String CLOSE = "PreviousClose";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String AVG_DAILY_VOL = "AverageDailyVolume";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String VOLUME = "Volume";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String DAY_HIGH = "DaysHigh";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String DAY_LOW = "DaysLow";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String YEAR_HIGH = "YearHigh";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String YEAR_LOW = "YearLow";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String MARKET_CAP = "MarketCapitalization";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String DIV_YIELD = "DividendYield";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String EPS = "EarningsShare";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String PE = "PERatio";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String ONE_YR_TARGET = "OneyrTargetPrice";

    @DataType(DataType.Type.TEXT)
    public static final String CREATED = "created";
    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String ISUP = "is_up";
    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String ISCURRENT = "is_current";
}
