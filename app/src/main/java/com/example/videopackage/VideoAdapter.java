package com.example.videopackage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.R;

import java.util.Collections;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final Context context;
    private List<Video> videoList;
    private final String baseUrl;
    private final OnVideoClickListener onVideoClickListener;

    public VideoAdapter(Context context, List<Video> videoList, String baseUrl, OnVideoClickListener onVideoClickListener) {
        this.context = context;
        this.videoList = videoList;
        this.baseUrl = baseUrl;
        this.onVideoClickListener = onVideoClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Video> list) {
        this.videoList = list;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void shuffleVideos() {
        Collections.shuffle(videoList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);

        // Load thumbnail
        Glide.with(context).load(baseUrl + video.getThumbnailImageFetchableUrl()).into(holder.videoThumbnail);

        // Set title
        holder.videoTitle.setText(video.getTitle());

        // Handle click
        holder.itemView.setOnClickListener(v -> onVideoClickListener.onVideoClick(video));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoTitle;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoTitle = itemView.findViewById(R.id.videoTitle);
        }
    }

    public interface OnVideoClickListener {
        void onVideoClick(Video video);
    }
}

