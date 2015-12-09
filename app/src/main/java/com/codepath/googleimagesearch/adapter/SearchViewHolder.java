package com.codepath.googleimagesearch.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.codepath.googleimagesearch.R;
import com.codepath.googleimagesearch.activity.ImageViewActivity;
import com.codepath.googleimagesearch.view.DynamicHeightImageView;

/**
 * Created by vvenkatraman on 11/30/15.
 */
public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    DynamicHeightImageView thumbnail;
    TextView title;

    public SearchViewHolder(View itemView) {
        super(itemView);

        thumbnail = (DynamicHeightImageView)itemView.findViewById(R.id.thumbnail);
        title = (TextView) itemView.findViewById(R.id.tvTitle);
    }

    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        Intent viewImageIntent = new Intent(itemView.getContext(), ImageViewActivity.class);
        viewImageIntent.putExtra(ImageViewActivity.CLICK_POSITION, position);
        itemView.getContext().startActivity(viewImageIntent);
    }
}
