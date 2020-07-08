package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.parstagram.TimeFormat.TimeFormatter;
import com.example.parstagram.databinding.ActivityPostDetailBinding;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class PostDetailActivity extends AppCompatActivity {

    // The post to display
    Post post;

    // The view objects
    TextView tvUsername;
    TextView tvBody;
    TextView tvCreatedAt;
    ImageView ivImage;

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
    }
}