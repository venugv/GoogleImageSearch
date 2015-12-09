package com.codepath.googleimagesearch.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.codepath.googleimagesearch.activity.ImageSearchActivity;
import com.loopj.android.http.AsyncHttpClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by vvenkatraman on 12/1/15.
 */
public class DataLoader {
    static final String CX = "000588531933921507169:8jceqx4eaqq";
    static final String KEY = "AIzaSyAPnlriAWlIYpi61k3sI7hfl-JoXvGzbLM";

    public static boolean isNetworkAvailable(final ImageSearchActivity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void search(final WeakReference<ImageSearchActivity> activityWeakReference,
                              final int offset, String searchString, SearchFilter filter, boolean refresh) {
        StringBuilder searchURL = new StringBuilder();
        searchURL.append("https://www.googleapis.com/customsearch/v1?q=");
        searchURL.append(searchString);
        if (offset > 0) {
            searchURL.append("&startPage=");
            searchURL.append(offset);
        }
        searchURL.append("&cx=").append(CX);
        searchURL.append("&key=").append(KEY);
        if (filter != null) {
            if (!filter.imageSize.equals("none") || !filter.imageSize.equals("any")) {
                searchURL.append("&imgSize=");
                searchURL.append(filter.imageSize);
            }
            if (!filter.imageColor.equals("none") || !filter.imageColor.equalsIgnoreCase("all")) {
                searchURL.append("&imgDominantColor=");
                searchURL.append(filter.imageColor);
            }
            if (!filter.imageType.equals("none") || !filter.imageType.equals("any")) {
                searchURL.append("&imgType=");
                searchURL.append(filter.imageType);
            }
            if (!TextUtils.isEmpty(filter.imageLocation)) {
                searchURL.append("&siteSearch=");
                searchURL.append(filter.imageLocation);
            }
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(searchURL.toString(), new JacksonHttpSearchClient(activityWeakReference, offset, refresh));
    }
}
