package com.example.newspackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.R;

import java.util.List;

public class NewsHotAdapter extends RecyclerView.Adapter<NewsHotAdapter.NewsViewHolder> {
    private Context context;
    private List<SmallNews> NewsHotList;
    private final String baseUrl;
    private final OnNewsClickListener onNewsClickListener;

    public NewsHotAdapter(Context context, List<SmallNews> newsList, String baseUrl, OnNewsClickListener onNewsClickListener) {
        this.context = context;
        this.NewsHotList = newsList;
        this.baseUrl = baseUrl;
        this.onNewsClickListener = onNewsClickListener;
    }


    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        public ImageView newsImageView;
        public TextView newsTitle;
        public CardView cardView;

        public NewsViewHolder(@NonNull View itemHotView) {
            super(itemHotView);
            newsImageView = itemHotView.findViewById(R.id.ImageHotNews);
            newsTitle = itemHotView.findViewById(R.id.TitleHotNews);
            cardView = itemHotView.findViewById(R.id.NewsHotCardView);
        }

    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_hot_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        SmallNews smallNews = NewsHotList.get(position);

        Glide.with(context).load(smallNews.getUrlToImage()).into(holder.newsImageView);

        holder.newsTitle.setText(smallNews.getTitle());


        holder.itemView.setOnClickListener(v -> onNewsClickListener.onNewsClick(smallNews));
    }


    @Override
    public int getItemCount() {
        return NewsHotList.size();
    }

    public void setNewsList(List<SmallNews> list) {
        this.NewsHotList = list;
        notifyDataSetChanged();
    }

    public interface OnNewsClickListener {
        void onNewsClick(SmallNews smallNews);
    }
}
