package com.codepath.googleimagesearch;

import android.app.Application;

import com.codepath.googleimagesearch.data.SearchImage;

import java.util.List;

/**
 * Created by vvenkatraman on 12/7/15.
 */
public class ImageSearchApplication extends Application {
    List<SearchImage> imageList;

    public void setOrAppendImageList(List<SearchImage> imageList) {
        if (this.imageList == null) {
            this.imageList = imageList;
        } else {
            this.imageList.addAll(imageList);
        }
    }

    public List<SearchImage> getImageList() {
        return this.imageList;
    }

    public SearchImage getImage(int position) {
        if (imageList != null && position > -1 && position < imageList.size()) {
            return imageList.get(position);
        }

        return null;
    }

    public void clearImageList() {
        if (imageList != null) {
            imageList.clear();
        }
    }
}
