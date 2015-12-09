package com.codepath.googleimagesearch.data;

import android.support.v7.widget.RecyclerView;

import com.codepath.googleimagesearch.ImageSearchApplication;
import com.codepath.googleimagesearch.activity.ImageSearchActivity;
import com.codepath.googleimagesearch.adapter.SearchAdapter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by vvenkatraman on 12/2/15.
 */
public class JacksonHttpSearchClient extends BaseJsonHttpResponseHandler<List<SearchImage>> {
    WeakReference<ImageSearchActivity> activityWeakReference;
    int offset;
    boolean refresh;

    JacksonHttpSearchClient(WeakReference<ImageSearchActivity> activityWeakReference, int offset, boolean refresh) {
        this.activityWeakReference = activityWeakReference;
        this.offset = offset;
        this.refresh = refresh;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, List<SearchImage> response) {
        // update UI here
        if (activityWeakReference.get() == null) {
            // Can't do much here. Just return
            return;
        }

        ImageSearchActivity activity = activityWeakReference.get();
        RecyclerView recyclerView = activity.getRecyclerView();
        SearchAdapter adapter = (SearchAdapter) recyclerView.getAdapter();
        if (recyclerView.getAdapter() == null || refresh) {
            if (recyclerView.getAdapter() == null) {
                adapter = new SearchAdapter(response, activity);
                recyclerView.setAdapter(adapter);
            } else {
                ((SearchAdapter) recyclerView.getAdapter()).setImageList(response);
            }
            adapter.notifyDataSetChanged();
            ((ImageSearchApplication)activity.getApplication()).clearImageList();
            ((ImageSearchApplication)activity.getApplication()).setOrAppendImageList(response);
        } else {
            int oldSize = adapter.getItemCount();
            adapter.getImageList().addAll(response);
            adapter.notifyItemRangeInserted(oldSize + 1, response.size());
            ((ImageSearchApplication) activity.getApplication()).setOrAppendImageList(response);
        }
        activity.getSwipeContainer().setRefreshing(false);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, List<SearchImage> errorResponse) {
        AsyncHttpClient.log.w("JsonHttpRH", "onFailure(int, Header[], " +
                "Throwable, JSONObject) was not overriden, but callback was received", throwable);
    }

    @Override
    protected List<SearchImage> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
        List<SearchImage> results = null;
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(SearchImage.class, new SearchImageDeserializer());
        objectMapper.registerModule(module);
        try {
            List<LinkedHashMap> valueMap = new ArrayList<>();
            JsonNode responseNode = objectMapper.readValue(rawJsonData, JsonNode.class);
            JsonNode resultNode = responseNode.get("items");
            //valueMap = objectMapper.treeToValue(resultNode, valueMap.getClass());
            results = objectMapper.convertValue(resultNode, new TypeReference<List<SearchImage>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public class SearchImageDeserializer extends JsonDeserializer<SearchImage> {

        @Override
        public SearchImage deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            JsonNode root = jp.getCodec().readTree(jp);
            JsonNode pagemapNode = root.get("pagemap");
            String title = root.get("title").asText();
            JsonNode imageNode = pagemapNode.get("cse_image");
            String fullImageURL = null;
            if (imageNode.isArray()) {
                fullImageURL = imageNode.get(0).get("src").asText();
            }
            JsonNode thumbNailsNode = pagemapNode.get("cse_thumbnail");
            String thumbnailWidth = null, thumbnailHeight = null, thumbnailImageURL = null;
            if (thumbNailsNode.isArray()) {
                JsonNode thumbNailNode = thumbNailsNode.get(0);
                thumbnailWidth = thumbNailNode.get("width").asText();
                thumbnailHeight = thumbNailNode.get("height").asText();
                thumbnailImageURL = thumbNailNode.get("src").asText();
            }

            return new SearchImage(fullImageURL, thumbnailImageURL,
                     title, thumbnailHeight, thumbnailWidth);
        }
    }

}