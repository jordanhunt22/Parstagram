package com.example.parstagram.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
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
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.example.parstagram.Post;
import com.example.parstagram.PostDetailActivity;
import com.example.parstagram.R;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;
import java.util.Objects;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "Adapter";

    private Context context;
    private List<Post> posts;

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
        private TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
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
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
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