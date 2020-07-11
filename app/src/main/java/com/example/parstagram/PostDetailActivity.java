package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.parstagram.TimeFormat.TimeFormatter;
import com.example.parstagram.databinding.ActivityPostDetailBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailActivity";

    // The post to display
    Post post;

    // The view objects
    private int numLikes;
    private TextView tvUsername;
    private TextView tvBody;
    private TextView tvCreatedAt;
    private TextView tvLikesCounter;
    private ImageView ivImage;
    private ImageButton btnLike;
    private ImageButton btnComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Implement ViewBinding
        ActivityPostDetailBinding binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Resolve the view objects
        tvUsername = binding.tvUsername;
        tvBody = binding.tvBody;
        tvCreatedAt = binding.tvCreatedAt;
        ivImage = binding.ivImage;
        btnLike = binding.btnLike;
        tvLikesCounter = binding.tvLikesCounter;

        // Unwrap the post
        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        // Set the Text/Image Views
        tvUsername.setText(post.getUser().getUsername());
        tvBody.setText(post.getDescription());
        tvCreatedAt
                .setText(TimeFormatter.getTimeStamp(post.getCreatedAt().toString()));
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this)
                    .load(image.getUrl())
                    .transform(new CenterCrop())
                    .into(ivImage);
        } else {
            //ivImage.setImageResource(android.R.color.transparent);
            Glide.with(this)
                    .load(R.drawable.ic_launcher_background)
                    .transform(new CenterCrop())
                    .into(ivImage);
        }

        if (isPostLiked(post)){
            Glide.with(this)
                    .load(R.drawable.heart_active)
                    .transform(new CenterCrop())
                    .into(btnLike);
        }
        else{
            Glide.with(this)
                    .load(R.drawable.heart)
                    .transform(new CenterCrop())
                    .into(btnLike);
        }

        setPostLikes(post);

        // Sets OnClickListener for the Like button
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser user = ParseUser.getCurrentUser();
                ParseRelation<ParseObject> relation = user.getRelation("likes");
                if (isPostLiked(post)) {
                    Glide.with(getBaseContext())
                            .load(R.drawable.heart)
                            .transform(new CenterCrop())
                            .into(btnLike);

                    // Changes the value of the likes counter
                    numLikes--;
                    tvLikesCounter.setText(String.valueOf(numLikes));
                    Post.postsLikedByCurrentuser.removeAll(Collections.singleton(post.getObjectId()));
                    relation.remove(post);
                    Log.i(TAG, "unlike");
                }
                else{
                    Glide.with(getBaseContext())
                            .load(R.drawable.heart_active)
                            .transform(new CenterCrop())
                            .into(btnLike);

                    // Changes the value of the likes counter
                    numLikes++;
                    tvLikesCounter.setText(String.valueOf(numLikes));
                    relation.add(post);
                    Post.postsLikedByCurrentuser.add(post.getObjectId());
                    Log.i(TAG, "like");
                }
                user.saveInBackground();
            }
        });
    }

    private void setPostLikes(final Post post) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("likes", post);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, "There was an error returning the number of likes", e);
                }
                else{
                    numLikes = objects.size();
                    tvLikesCounter.setText(String.valueOf(numLikes));
                }
            }
        });
    }

    public boolean isPostLiked(Post post){
        if (Post.postsLikedByCurrentuser.contains(post.getObjectId())){
            return true;
        }
        else { return false; }
    }
}