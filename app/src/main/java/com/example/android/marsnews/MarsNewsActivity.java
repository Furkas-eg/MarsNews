package com.example.android.marsnews;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.net.NetworkInfo;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MarsNewsActivity extends AppCompatActivity
        implements LoaderCallbacks<List<MarsNews>> {

    // URL for the data page.
    private static final String MARS_REPORT_REQUEST_URL =
            "https://content.guardianapis.com/search";

    //Constant value for the marsNews loader ID. We can choose any integer.
    private static final int MARSNEWS_LOADER_ID = 1;

    //Adapter for the list of app
    private MarsNewsAdapter mAdapter;

    //TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    /** Activity and info that we show **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mars_news);

        //New ListView
        ListView marsNewsListView = (ListView) findViewById(R.id.list);
        //Give empty view a value
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        marsNewsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter
        mAdapter = new MarsNewsAdapter(this, new ArrayList<MarsNews>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        marsNewsListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected marsNews item.
        marsNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Find the current article that was clicked on
                MarsNews currentMarsNews = mAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri marsNewsUri = Uri.parse(currentMarsNews.getUrlId());
                // Create a new intent to view the marsNews URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, marsNewsUri);
                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MARSNEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

    }

    @Override
    public Loader<List<MarsNews>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String maxArticles = sharedPrefs.getString(
                // max_articles
                getString(R.string.settings_max_articles_key),
                //10 max articles shown by default
                getString(R.string.settings_max_articles_default));

        //Number of articles to display
        String orderArticlesBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(MARS_REPORT_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Parameters and key values for URL
        uriBuilder.appendQueryParameter("q", "mars");
        uriBuilder.appendQueryParameter("tag", "science/science");
        uriBuilder.appendQueryParameter("page-size", maxArticles);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("section", "science");
        uriBuilder.appendQueryParameter("order-by", orderArticlesBy);
        uriBuilder.appendQueryParameter("api-key", "b81b1b57-3499-488b-983d-f9169b824868");


        // Return the completed uri (URL of the server)
        return new MarsNewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<MarsNews>> loader, List<MarsNews> marsNews) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display "No stories found. Check internet?"
        mEmptyStateTextView.setText(R.string.no_mars_news_activity);
        // Clear the adapter of previous app data
        mAdapter.clear();

        // data set. This will trigger the ListView to update.
        if (marsNews != null && !marsNews.isEmpty()) {
            mAdapter.addAll(marsNews);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MarsNews>> loader) {
        // Clears out our existing data.
        mAdapter.clear();
    }

    //For settings menu
    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Sets up the specific action that occurs when any of the items in the Options Menu are selected.
    // In other words, makes the magic happen!
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
