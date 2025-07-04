package com.example.findit;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList = new ArrayList<>();

    public void setPostList(List<Post> posts) {
        this.postList = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvTitle.setText(post.title);
        holder.tvDescription.setText(post.description);
        holder.tvType.setText(post.type);
        holder.tvDateTime.setText(post.date + " " + post.time);

        if (post.imageUri != null) {
            holder.ivPostImage.setImageURI(Uri.parse(post.imageUri));
        } else {
            holder.ivPostImage.setImageResource(android.R.color.darker_gray);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPostImage;
        TextView tvTitle, tvDescription, tvType, tvDateTime;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvTitle = itemView.findViewById(R.id.tvPostTitle);
            tvDescription = itemView.findViewById(R.id.tvPostDescription);
            tvType = itemView.findViewById(R.id.tvPostType);
            tvDateTime = itemView.findViewById(R.id.tvPostDateTime);
        }
    }
}
