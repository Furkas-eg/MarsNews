package com.example.android.marsnews;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Related to requesting and receiving data that is related to Mars.
 */
public final class QueryUtils {

    // Tag for the log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Mars Articles dataset from Guardian and return a list of {@link MarsNews} objects.
     */
    public static List<MarsNews> fetchMarsNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTPS request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTPS request.", e);
        }

        List<MarsNews> marsNews = extractFeatureFromJson(jsonResponse);
        return marsNews;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the marsNews JSON results. :(", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException could be thrown.

                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link MarsNews} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<MarsNews> extractFeatureFromJson(String marsNewsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(marsNewsJSON)) {
            return null;
        }

        // Create an empty ArrayList so that we can start adding articles to.
        List<MarsNews> marsNews = new ArrayList<>();

        // Try to parse the JSON response string.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(marsNewsJSON);

            // Represents a list that belongs to "features".
            JSONObject marsNewsArray = baseJsonResponse.getJSONObject("response");
            JSONArray results = marsNewsArray.getJSONArray("results");

            // For each article in the marsNewsArray, this creates an {@link MarsNews} object
            for (int i = 0; i < results.length(); i++) {
                // Item names are in this folder.
                JSONObject currentMarsNews = results.getJSONObject(i);
                // Extract the value for the key called "sectionId"
                String sectionId = currentMarsNews.getString("sectionId");
                String articleName = currentMarsNews.getString("webTitle");
                String date = currentMarsNews.getString("webPublicationDate");
                String url = currentMarsNews.getString("webUrl");
                JSONArray tags = currentMarsNews.getJSONArray("tags");

                // IF we FOUND an author
                if (tags.length() != 0) {

                    JSONObject tagsIndex = tags.getJSONObject(0);
                    /** This is the author name **/
                    String authorName = tagsIndex.getString("webTitle");

                    // Removes the "-" in the original string text. Makes it look nice.
                    String shorterDate = date.substring(0, 4);
                    String month = date.substring(5, 7);
                    String newLine = " \n  ";

                    // Create a new {@link marsNews1} object with the sectionId, name of article, date of article, and URL. Order matters
                    MarsNews marsNews1 = new MarsNews(sectionId, articleName, shorterDate + newLine + month, url, authorName);
                    // Add the new {@link marsNews1} to the list of articles.
                    marsNews.add(marsNews1);

                } else {
                    //IF there is NO AUTHOR detected from Guardian API, display this
                    String authorName = "No Author detected";
                    // Removes the "-" in the original string text of date. Makes it look nice.
                    String shorterDate = date.substring(0, 4);
                    String month = date.substring(5, 7);
                    String newLine = " \n  ";
                    // Create a new {@link marsNews1} object with the sectionId, name of article, date of article.
                    MarsNews marsNews1 = new MarsNews(sectionId, articleName, shorterDate + newLine + month, url, authorName);
                    marsNews.add(marsNews1);
                }
            }

        } catch (JSONException e) {

            Log.e("QueryUtils", "Problem parsing the marsNews JSON results :(", e);
        }

        return marsNews;
    }
}