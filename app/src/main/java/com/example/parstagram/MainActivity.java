package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.parstagram.Adapters.PostsAdapter;
import com.example.parstagram.EndlessScroll.EndlessRecyclerViewScrollListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private RecyclerView rvPosts;
    private SwipeRefreshLayout swipeContainer;
    protected PostsAdapter adapter;
    protected List<Post> allPosts;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryPosts();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvPosts = findViewById(R.id.rvPosts);

        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(this, allPosts);

        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);

        // set the layout manager on the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvPosts.setLayoutManager(linearLayoutManager);

        // Implement ScrollListener for infinite scroll
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMoreData();
            }
        };

        // Adds the scroll listener to the RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

        // query posts from Parstagram
        queryPosts();
    }

    private void loadMoreData() {
        Log.i(TAG, "loadMoreData() called");
        ParseQuery query = ParseQuery.getQuery(Post.class);
        // limits query to items that are older than the last item in the RecyclerView
        int numPosts = allPosts.size()-1;
        Post last = allPosts.get(numPosts);
        // int lastCreated = last.getCreatedAt().toString();
        query.whereLessThan(Post.KEY_CREATED, last.getCreatedAt());
        // includes the user who created the post
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder(Post.KEY_CREATED);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // Adds the new posts to the adapter
                adapter.addAll(posts);

                // Save received posts to list and notify adapter of new data
                swipeContainer.setRefreshing(false);
                for (Post post : posts){
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
            }
        });
    }

    private void queryPosts() {
        ParseQuery query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder(Post.KEY_CREATED);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // Clears the adapter
                adapter.clear();
                adapter.addAll(posts);

                // Save received posts to list and notify adapter of new data
                swipeContainer.setRefreshing(false);
                for (Post post : posts){
                        Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
            }
        });
    }
}