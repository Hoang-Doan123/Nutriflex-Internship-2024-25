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

public class VideoSearchAdapter extends RecyclerView.Adapter<VideoSearchAdapter.VideoViewHolder> {
    private Context context;
    private List<Video> videoSearchList;
    private final String baseUrl;
    private final OnVideoClickListener onVideoClickListener;

    public VideoSearchAdapter(Context context, List<Video> videoSearchList, String baseUrl, OnVideoClickListener onVideoClickListener) {
        this.context = context;
        this.videoSearchList = videoSearchList;
        this.baseUrl = baseUrl;
        this.onVideoClickListener = onVideoClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Video> searchList) {
        this.videoSearchList.clear();
        this.videoSearchList.addAll(searchList);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void shuffleVideos() {
        Collections.shuffle(videoSearchList);
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
        Video video = videoSearchList.get(position);

        // Load thumbnail
        Glide.with(context).load(baseUrl + video.getThumbnailImageFetchableUrl()).into(holder.videoThumbnail);

        // Set title
        holder.videoTitle.setText(video.getTitle());

        // Handle click
        holder.itemView.setOnClickListener(v -> onVideoClickListener.onVideoClick(video));
    }

    @Override
    public int getItemCount() {
        return videoSearchList.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
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
