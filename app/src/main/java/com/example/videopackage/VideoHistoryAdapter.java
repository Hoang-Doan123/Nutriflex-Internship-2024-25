package com.example.videopackage;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;

import java.util.List;

public class VideoHistoryAdapter extends RecyclerView.Adapter<VideoHistoryAdapter.ViewHolder> {
    private final List<String> historyList;
    private final VideoSearchActivity videoSearchActivity;

    public VideoHistoryAdapter(List<String> historyList, VideoSearchActivity videoSearchActivity) {
        this.historyList = historyList;
        this.videoSearchActivity = videoSearchActivity;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateHistory(List<String> newHistory) {
        Log.d("VideoHistoryAdapter", "New history list: " + newHistory.toString());
        historyList.clear();
        historyList.addAll(newHistory);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String historyItem = historyList.get(position);
        holder.historyText.setText(historyItem);

        // Process the delete history event
        holder.removeHistory.setOnClickListener(v -> {
            videoSearchActivity.removeSearchQuery(historyItem);
        });

        // Tap on a keyword in the history list to search
        holder.historyText.setOnClickListener(v -> {
            videoSearchActivity.searchText.setText(historyItem); // Update searchbar
            Log.d("VideoHistoryAdapter", "Clicked history item: " + historyItem);

            holder.historyText.postDelayed(() -> {
                videoSearchActivity.saveToHistory(historyItem); // Save the keyword to history
                videoSearchActivity.loadHistory();
                videoSearchActivity.filterVideos(historyItem);
            }, 300); // Wait 300ms to make sure that data is updated
            Toast.makeText(v.getContext(), "Search: " + historyItem, Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView historyText;
        ImageView removeHistory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            historyText = itemView.findViewById(R.id.history_text);
            removeHistory = itemView.findViewById(R.id.remove_history);
        }
    }
}