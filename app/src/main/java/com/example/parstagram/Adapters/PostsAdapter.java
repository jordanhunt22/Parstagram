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
        private TextView tvBody;
        private TextView tvCreatedAt;
        private ImageView ivProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            ivProfile = itemView.findViewById(R.id.ivProfile);
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