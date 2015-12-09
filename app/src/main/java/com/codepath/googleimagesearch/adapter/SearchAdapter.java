package com.codepath.googleimagesearch.adapter;

import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.googleimagesearch.R;
import com.codepath.googleimagesearch.activity.ImageSearchActivity;
import com.codepath.googleimagesearch.data.SearchImage;
import com.codepath.googleimagesearch.data.DataLoader;
import com.codepath.googleimagesearch.view.TouchImageView;

import java.util.List;

/**
 * Created by vvenkatraman on 11/30/15.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private static final int EMPTY_VIEW = 10;

    private List<SearchImage> imageList;
    private ImageSearchActivity imageSearchActivity;
    private LayoutInflater layoutInflater;

    public SearchAdapter(List<SearchImage> imageList, ImageSearchActivity imageSearchActivity) {
        this.imageList = imageList;
        this.imageSearchActivity = imageSearchActivity;
        this.layoutInflater = LayoutInflater.from(imageSearchActivity);
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_item_search, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        if (imageList == null || imageList.size() == 0) {
            // This is the empty view scenario
            if (!DataLoader.isNetworkAvailable(imageSearchActivity)) {
                holder.thumbnail.setImageDrawable(ContextCompat.getDrawable(imageSearchActivity,
                        R.drawable.ic_stat_offline));
                double positionHeight = getPositionRatio(position);
                holder.thumbnail.setHeightRatio(positionHeight);
                holder.title.setText(R.string.offlineText);
                Toast.makeText(imageSearchActivity, imageSearchActivity.getString(R.string.offlineText), Toast.LENGTH_LONG).show();
            } else {
                holder.title.setText(R.string.emptyText);
                holder.thumbnail.setImageDrawable(ContextCompat.getDrawable(imageSearchActivity,
                        R.mipmap.ic_launcher));
            }
        } else {
            SearchImage searchImage = imageList.get(position);
            holder.title.setText(Html.fromHtml(searchImage.getTitle()), TextView.BufferType.SPANNABLE);
            double positionHeight = getPositionRatio(position);
            holder.thumbnail.setHeightRatio(positionHeight);
            Glide.with(imageSearchActivity).load(searchImage.getThumbnailImageURL())
                    .thumbnail(1.0f)
                    .placeholder(R.mipmap.ic_launcher)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.thumbnail);
            holder.itemView.setOnClickListener(holder);
        }
    }

    private double getPositionRatio(final int position) {
        double thumbnailHeight;
        double thumbnailWidth;
        if (imageList != null && imageList.size() > 0) {
            SearchImage result = imageList.get(position);
            thumbnailHeight = Double.parseDouble(result.getThumbnailHeight());
            thumbnailWidth = Double.parseDouble(result.getThumbnailWidth());
        } else {
            Drawable launcherIcon = imageSearchActivity.getResources().getDrawable(R.mipmap.ic_launcher);
            thumbnailHeight = launcherIcon.getIntrinsicHeight();
            thumbnailWidth = launcherIcon.getIntrinsicWidth();
        }
        return (thumbnailHeight
                / thumbnailWidth);
    }

    @Override
    public int getItemViewType(int position) {
        if (imageList != null && imageList.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return (imageList != null && imageList.size() > 0) ? imageList.size() : 1;
    }

    public List<SearchImage> getImageList() {
        return imageList;
    }

    public void setImageList(List<SearchImage> imageList) {
        this.imageList = imageList;
    }
}
