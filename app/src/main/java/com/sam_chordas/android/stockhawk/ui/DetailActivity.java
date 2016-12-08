package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.service.StockIntentService;

import static android.R.attr.fragment;

/**
 * Created by jim on 11/4/16.
 */

public class DetailActivity extends AppCompatActivity {

    private Intent mServiceIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());
            arguments.putBoolean(DetailFragment.DETAIL_TRANSITION_ANIMATION, true);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            mServiceIntent = new Intent(this, StockIntentService.class);
            // Run the initialize task service so that some stocks appear upon an empty database
            mServiceIntent.putExtra("tag", "history");
            String symbol = QuoteProvider.Quotes.getSymbolFromUri((Uri)arguments.getParcelable(DetailFragment.DETAIL_URI));
            mServiceIntent.putExtra("symbol", symbol);
            if (Utils.isConnected(this)) {
                startService(mServiceIntent);
            }


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.stock_detail_container, fragment)
                    .commit();

            // Being here means we are in animation mode
            supportPostponeEnterTransition();
        }
    }

}
