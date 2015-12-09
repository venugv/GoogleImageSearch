package com.codepath.googleimagesearch.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vvenkatraman on 12/1/15.
 */
public class SearchFilter implements Parcelable{
    String imageSize;
    String imageColor;
    String imageType;
    String imageLocation;

    static final Parcelable.Creator<SearchFilter> CREATOR
            = new Parcelable.Creator<SearchFilter>() {

        public SearchFilter createFromParcel(Parcel in) {
            return new SearchFilter(in);
        }

        public SearchFilter[] newArray(int size) {
            return new SearchFilter[size];
        }
    };

    public SearchFilter(Parcel in) {
        this.imageSize = in.readString();
        this.imageColor = in.readString();
        this.imageType = in.readString();
        this.imageLocation = in.readString();
    }

    public SearchFilter(String imageSize, String imageColor, String imageType, String imageLocation) {
        this.imageSize = imageSize;
        this.imageColor = imageColor;
        this.imageType = imageType;
        this.imageLocation = imageLocation;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public String getImageColor() {
        return imageColor;
    }

    public void setImageColor(String imageColor) {
        this.imageColor = imageColor;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageSize);
        dest.writeString(imageColor);
        dest.writeString(imageType);
        dest.writeString(imageLocation);
    }
}
