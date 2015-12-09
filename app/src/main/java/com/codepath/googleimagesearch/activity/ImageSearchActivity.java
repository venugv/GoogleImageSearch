package com.codepath.googleimagesearch.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.googleimagesearch.ImageSearchApplication;
import com.codepath.googleimagesearch.R;
import com.codepath.googleimagesearch.adapter.EndlessRecyclerOnScrollListener;
import com.codepath.googleimagesearch.adapter.SearchAdapter;
import com.codepath.googleimagesearch.data.SearchFilter;
import com.codepath.googleimagesearch.data.SearchImage;
import com.codepath.googleimagesearch.data.DataLoader;
import com.codepath.googleimagesearch.fragment.SettingsFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ImageSearchActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String SEARCH_QUERY = "searchQuery";
    public static final String SEARCH_FILTER = "searchFilter";
    EndlessRecyclerOnScrollListener endlessScrollListener;
    String searchQuery;
    SearchFilter searchFilter;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);
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

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.columnCount),
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        endlessScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                startSearch(currentPage, false);
            }
        };
        recyclerView.addOnScrollListener(endlessScrollListener);
        recyclerView.setAdapter(null);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);
        }
        startSearch(0, true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!TextUtils.isEmpty(searchQuery)) {
            outState.putString(SEARCH_QUERY, searchQuery);
            outState.putParcelable(SEARCH_FILTER, searchFilter);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY);
            searchFilter = savedInstanceState.getParcelable(SEARCH_FILTER);
            ImageSearchApplication application = (ImageSearchApplication) getApplication();
            if (searchQuery == null && application.getImageList() == null) {
                startSearch(0, true);
            } else {
                SearchAdapter adapter = new SearchAdapter(application.getImageList(), this);
                recyclerView.setAdapter(adapter);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_search, menu);
        final MenuItem search = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                startSearch(0, true);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        if (!TextUtils.isEmpty(searchQuery)) {
            search.expandActionView();
            searchView.setQuery(searchQuery, false);
            searchView.clearFocus();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SettingsFragment fragment = new SettingsFragment();

            if (this.searchFilter != null) {
                Bundle args = new Bundle();
                args.putParcelable(SEARCH_FILTER, searchFilter);
                fragment.setArguments(args);
            }
            fragment.show(getFragmentManager(), "Settings");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        //endlessScrollListener.reset(0, true);
    }

    @Override
    public void onRefresh() {
        List<SearchImage> imageList = ((ImageSearchApplication)getApplication()).getImageList();
        if (imageList == null || imageList.size() == 0) {
            this.getSwipeContainer().setRefreshing(false);
            return;
        }
        startSearch(0, true);
    }

    public void startSearch(int currentPage, boolean refresh) {
        if (this.searchQuery == null) {
            if (recyclerView.getAdapter() == null) {
                SearchAdapter adapter = new SearchAdapter(new ArrayList<SearchImage>(), this);
                this.recyclerView.setAdapter(adapter);
                this.swipeContainer.setRefreshing(false);
            }
        } else if (!DataLoader.isNetworkAvailable(this)) {
            Snackbar.make(recyclerView, getString(R.string.offlineText), Snackbar.LENGTH_LONG).show();
        } else {
            if (!refresh) {
                currentPage ++;
            }
            DataLoader.search(new WeakReference<>(this), currentPage, this.searchQuery, this.searchFilter, refresh);
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setSearchFilter(SearchFilter searchFilter) {
        this.searchFilter = searchFilter;
        startSearch(0, true);
    }

    public SwipeRefreshLayout getSwipeContainer() {
        return this.swipeContainer;
    }
}
