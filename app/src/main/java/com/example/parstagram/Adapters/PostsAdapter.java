package com.example.parstagram.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.parstagram.Fragments.ProfileFragment;
import com.example.parstagram.Fragments.UserFragment;
import com.example.parstagram.LoginActivity;
import com.example.parstagram.MainActivity;
import com.example.parstagram.Post;
import com.example.parstagram.PostDetailActivity;
import com.example.parstagram.R;
import com.example.parstagram.TimeFormat.TimeFormatter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.xml.sax.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "Adapter";

    private Context context;
    private List<Post> posts;
    private boolean liked;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvBody;
        private TextView tvCreatedAt;
        private ImageView ivProfile;

        private ImageButton btnComment;
        private ImageButton btnLike;
        private ImageButton btnSend;
        private Post post;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Resolves the views
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnLike = itemView.findViewById(R.id.btnLike);

            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Gets item position
                    int position = getAdapterPosition();
                    // Make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the post at the position, this won't work if the class is static
                        post = posts.get(position);
                        //Put the value
                        Fragment fragment = new UserFragment();
                        Bundle args = new Bundle();
                        args.putParcelable("user", Parcels.wrap(post.getUser()));
                        fragment.setArguments(args);

                        //Inflate the fragment
                        ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
                    }
                }
            });

            // Sets OnClickListener to go to Details Activity
            ivImage.setOnClickListener(new View.OnClickListener() {
                // Shows PostDetail Activity when user clicks on a row
                @Override
                public void onClick(View v) {
                    // Gets item position
                    int position = getAdapterPosition();
                    // Make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the movie at the position, this won't work if the class is static
                        post = posts.get(position);
                        // Create intent for the new activity
                        Intent intent = new Intent(context, PostDetailActivity.class);
                        // Serialize the Post using parceler, use its short name as a key
                        intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                        // Show the activity
                        context.startActivity(intent);
                    }
                }
            });

            btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Gets item position
                    int position = getAdapterPosition();
                    // Make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        ParseUser user = ParseUser.getCurrentUser();
                        ParseRelation<ParseObject> relation = user.getRelation("likes");
                        // Get the movie at the position, this won't work if the class is static
                        post = posts.get(position);
                        if (isPostLiked(post)) {
                            Glide.with(context)
                                    .load(R.drawable.heart)
                                    .transform(new CenterCrop())
                                    .into(btnLike);
                            Post.postsLikedByCurrentuser.removeAll(Collections.singleton(post.getObjectId()));
                            relation.remove(post);
                            Log.i(TAG, "unlike");
                        }
                        else{
                            Glide.with(context)
                                    .load(R.drawable.heart_active)
                                    .transform(new CenterCrop())
                                    .into(btnLike);
                            relation.add(post);
                            Post.postsLikedByCurrentuser.add(post.getObjectId());
                            Log.i(TAG, "like");
                        }
                        user.saveInBackground();
                    }
                }
            });
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            tvBody.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            tvCreatedAt
                    .setText(TimeFormatter.getTimeDifference(post.getCreatedAt().toString()));
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .transform(new CenterCrop())
                        .into(ivImage);
            } else {
                //ivImage.setImageResource(android.R.color.transparent);
                Glide.with(context)
                        .load(R.drawable.ic_launcher_background)
                        .transform(new CenterCrop())
                        .into(ivImage);
            }
            ParseFile profileImage = post.getUser().getParseFile("profileImage");
            if (profileImage != null) {
                Glide.with(context)
                        .load(profileImage.getUrl())
                        .transform(new CircleCrop())
                        .into(ivProfile);
            } else {
                Glide.with(context)
                        .load(R.drawable.instagram_user_filled_24)
                        .transform(new CircleCrop())
                        .into(ivProfile);
            }

            if (isPostLiked(post)){
                Glide.with(context)
                        .load(R.drawable.heart_active)
                        .transform(new CenterCrop())
                        .into(btnLike);
            }
            else{
                Glide.with(context)
                        .load(R.drawable.heart)
                        .transform(new CenterCrop())
                        .into(btnLike);
            }


        }
    }

    public boolean isPostLiked(Post post){
        if (Post.postsLikedByCurrentuser.contains(post.getObjectId())){
            return true;
        }
        else { return false; }
    }

    // Clean all elements of the recyclerview
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> allPosts) {
        posts.addAll(allPosts);
        notifyDataSetChanged();
    }
}