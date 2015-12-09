package com.codepath.googleimagesearch.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.googleimagesearch.ImageSearchApplication;
import com.codepath.googleimagesearch.R;
import com.codepath.googleimagesearch.data.SearchImage;
import com.codepath.googleimagesearch.view.TouchImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by vvenkatraman on 12/7/15.
 */
public class ImageViewActivity extends AppCompatActivity {
    public static final String CLICK_POSITION = "clickPosition";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private TouchImageView ivTouchImageView;
    private int position;
    private TextView tvTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getIntent().getIntExtra(ImageViewActivity.CLICK_POSITION, -1);
        SearchImage searchImage = ((ImageSearchApplication) getApplication()).getImage(position);
        if (searchImage == null) {
            this.finish();
        }
        setContentView(R.layout.activity_image_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition fadeTransform = TransitionInflater.from(this).
                    inflateTransition(android.R.transition.fade);
            fadeTransform.setStartDelay(0);
            fadeTransform.setDuration(500);
            getWindow().setEnterTransition(fadeTransform);
            getWindow().setExitTransition(fadeTransform);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ivTouchImageView = (TouchImageView) findViewById(R.id.ivFullImage);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        if (position > -1) {
            Glide.with(this).load(searchImage.getFullImageURL())
                    .asBitmap()
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivTouchImageView);
        }
        tvTitle.setText(searchImage.getTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_share) {
            // Get access to the URI for the bitmap
            verifyStoragePermissions();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    final Uri bmpUri = getLocalBitmapUri(ivTouchImageView);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (bmpUri != null) {
                                // Construct a ShareIntent with link to image
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                shareIntent.setType("image/*");
                                // Launch sharing dialog for image
                                startActivity(Intent.createChooser(shareIntent, "Share Image"));
                            } else {
                                Snackbar.make(ivTouchImageView, "Unable to share this image", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            };
            new Thread(r).start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_view, menu);
        return true;
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
