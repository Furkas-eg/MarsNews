package com.example.android.marsnews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of articles by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class MarsNewsLoader extends AsyncTaskLoader<List<MarsNews>> {

    //Query URL
    private String mUrl;

    /**********************************************
     * Constructs a new {@link MarsNewsLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     **********************************************/
    public MarsNewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    //This is on a background thread.
    @Override
    public List<MarsNews> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of news.
        List<MarsNews> marsNews = QueryUtils.fetchMarsNewsData(mUrl);
        return marsNews;
    }
}
