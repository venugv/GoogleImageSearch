package com.codepath.googleimagesearch.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by vvenkatraman on 12/1/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchImage {

    private String fullImageURL;
    private String thumbnailImageURL;
    private String title;
    private String thumbnailHeight;
    private String thumbnailWidth;

    public SearchImage (String fullImageURL, String thumbnailImageURL,
                        String title, String thumbnailHeight, String thumbnailWidth) {
        this.fullImageURL = fullImageURL;
        this.thumbnailImageURL = thumbnailImageURL;
        this.title = title;
        this.thumbnailHeight = thumbnailHeight;
        this.thumbnailWidth = thumbnailWidth;
    }
    @Override
    public String toString() {
        return "SearchImage{" +
                "fullImageURL='" + fullImageURL + '\'' +
                ", thumbnailImageURL='" + thumbnailImageURL + '\'' +
                ", title='" + title + '\'' +
                ", thumbnailHeight='" + thumbnailHeight + '\'' +
                ", thumbnailWidth='" + thumbnailWidth + '\'' +
                '}';
    }

    public String getFullImageURL() {
        return fullImageURL;
    }

    public void setFullImageURL(String fullImageURL) {
        this.fullImageURL = fullImageURL;
    }

    public String getThumbnailImageURL() {
        return thumbnailImageURL;
    }

    public void setThumbnailImageURL(String thumbnailImageURL) {
        this.thumbnailImageURL = thumbnailImageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(String thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public String getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(String thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }
}