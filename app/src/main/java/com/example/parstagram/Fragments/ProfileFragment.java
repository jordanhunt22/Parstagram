package com.example.parstagram.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.parstagram.Adapters.PostsAdapter;
import com.example.parstagram.Adapters.ProfileAdapter;
import com.example.parstagram.EndlessScroll.EndlessRecyclerViewScrollListener;
import com.example.parstagram.LoginActivity;
import com.example.parstagram.MainActivity;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static final String TAG = "HomeFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 43;
    Button btnLogOut;
    Button btnProfile;
    private File photoFile;
    public String photoFileName = "profilePhoto.jpg";
    ProfileAdapter pAdapter;
    protected RecyclerView rvPosts;
    protected SwipeRefreshLayout swipeContainer;
    protected List<Post> allPosts;
    private ImageView ivProfile;

    protected EndlessRecyclerViewScrollListener scrollListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout in this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer);
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

        rvPosts = view.findViewById(R.id.rvPosts);
        ivProfile = view.findViewById(R.id.ivProfile);

        ParseFile profileImage = ParseUser.getCurrentUser().getParseFile("profileImage");
        if (profileImage != null) {
            Glide.with(getContext())
                    .load(profileImage.getUrl())
                    .transform(new CircleCrop())
                    .into(ivProfile);
        } else {
            Glide.with(getContext())
                    .load(R.drawable.instagram_user_filled_24)
                    .transform(new CircleCrop())
                    .into(ivProfile);
        }

        allPosts = new ArrayList<>();
        // Sets GridLayoutManager and changes the adapter
        pAdapter = new ProfileAdapter(getContext(), allPosts);
        // set the adapter on the recycler view
        rvPosts.setAdapter(pAdapter);

        // set the layout manager on the recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        rvPosts.setLayoutManager(gridLayoutManager);

        btnLogOut = view.findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                goLogIn();
            }
        });
        btnProfile = view.findViewById(R.id.btnProfileImage);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        // Implement ScrollListener for infinite scroll
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
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

    private void goLogIn() {
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }


    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        // if (intent.resolveActivity(getContext().getPackageManager()) != null) {
        // Start the image capture intent to take photo
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        // }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
//                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ParseUser currentUser = ParseUser.getCurrentUser();
                saveProfileImage(currentUser, photoFile);
                Toast.makeText(getContext(), "Profile photo was saved!", Toast.LENGTH_SHORT).show();
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void loadMoreData() {
        Log.i(TAG, "loadMoreData() called");
        ParseQuery query = ParseQuery.getQuery(Post.class);
        // limits query to items that are older than the last item in the RecyclerView
        int numPosts = allPosts.size()-1;
        // only includes items that have the logged in user as the author
        query.whereEqualTo("user", ParseUser.getCurrentUser());
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
                pAdapter.addAll(posts);

                // Save received posts to list and notify adapter of new data
                swipeContainer.setRefreshing(false);
                for (Post post : posts){
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
            }
        });
    }


    protected void queryPosts() {
        ParseQuery query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        // only includes items that have the logged in user as the author
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder(Post.KEY_CREATED);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // Clears the adapter
                pAdapter.clear();
                pAdapter.addAll(posts);

                // Save received posts to list and notify adapter of new data
                swipeContainer.setRefreshing(false);
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
            }
        });

    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void saveProfileImage(ParseUser currentUser, File photoFile) {
        currentUser.put("profileImage", new ParseFile(photoFile));
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving!", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful!");
                Toast.makeText(getContext(), "Post was successful!", Toast.LENGTH_SHORT).show();
            } // parse-dashboard --appId jordan-parstagram --masterKey CodePathMoveFastParse --serverURL "https://jordan-parstagram.herokuapp.com/parse"
        });
    }
}


