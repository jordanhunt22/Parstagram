package com.example.parstagram.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.parstagram.Post;
import com.example.parstagram.PostDetailActivity;
import com.example.parstagram.R;
import com.example.parstagram.TimeFormat.TimeFormatter;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    public static final String TAG = "Adapter";

    private Context context;
    private List<Post> posts;

    public ProfileAdapter(Context context, List<Post> posts) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.profile_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView ivPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPost = itemView.findViewById(R.id.ivPost);
            itemView.setOnClickListener(new View.OnClickListener() {
                // Shows PostDetail Activity when user clicks on a row
                @Override
                public void onClick(View v) {
                    // Gets item position
                    int position = getAdapterPosition();
                    // Make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the movie at the position, this won't work if the class is static
                        Post post = posts.get(position);
                        // Create intent for the new activity
                        Intent intent = new Intent(context, PostDetailActivity.class);
                        // Serialize the movie using parceler, use its short name as a key
                        intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                        // Show the activity
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .transform(new CenterCrop())
                        .into(ivPost);
            } else {
                //ivImage.setImageResource(android.R.color.transparent);
                Glide.with(context)
                        .load(R.drawable.ic_launcher_background)
                        .transform(new CenterCrop())
                        .into(ivPost);
            }
        }
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