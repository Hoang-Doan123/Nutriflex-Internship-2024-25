package com.example.newspackage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.R;
import com.example.SQLconnection;
import com.example.SelectListener;
import com.example.SettingActivity;
import com.example.videopackage.PlayVideoActivity;
import com.example.videopackage.Video;
import com.example.videopackage.VideoAllAPI;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment implements SelectListener {

    private RecyclerView recyclerNews, recyclerHotNews;
    private NewsAdapter newsAdapter;
    private NewsHotAdapter newsHotAdapter;
    private List<SmallNews> NewsList = new ArrayList<>();
    private List<SmallNews> NewsHotList = new ArrayList<>();
    private static final String baseUrl = "https://android-backend-tech-c52e01da23ae.herokuapp.com/";


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerHotNews = view.findViewById(R.id.HotNews);
        recyclerHotNews.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        recyclerHotNews.setHasFixedSize(true);
        recyclerNews = view.findViewById(R.id.NewsList);
        recyclerNews.setHasFixedSize(true);
        recyclerNews.setLayoutManager(new GridLayoutManager(getContext(), 1));

        newsHotAdapter = new NewsHotAdapter(getContext(), NewsHotList, baseUrl, this::newsItem);
        recyclerHotNews.setAdapter(newsHotAdapter);

        newsAdapter = new NewsAdapter(getContext(), NewsList, baseUrl, this::newsItem);
        recyclerNews.setAdapter(newsAdapter);

        fetchNews();

        return view;
    }


    private void fetchNews() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsAPI NewsAPI = retrofit.create(NewsAPI.class);
        Call<List<SmallNews>> call = NewsAPI.getNews();
        call.enqueue(new Callback<List<SmallNews>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<SmallNews>> call, @NonNull Response<List<SmallNews>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (SmallNews news : response.body()) {
                        NewsHotList.add(news);
                        if (news.getId() > 5) {
                            break;
                        }
                    }
                    for (SmallNews news : response.body()){
                        NewsList.add(news);
                        if (news.getId() > 20) {
                            break;
                        }
                    }
                    newsHotAdapter.setNewsList(NewsHotList);
                    newsHotAdapter.notifyDataSetChanged();

                    newsAdapter.setNewsList(NewsList);
                    newsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "No news available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SmallNews>> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to fetch news", Toast.LENGTH_SHORT).show();
                }
                Log.e("API Error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void newsHotItem(SmallNews smallNews) {
        ArrayList<Object> NewsItem = new ArrayList<>();
        for (SmallNews news : NewsList) {
            NewsItem.add(news.getId());
            NewsItem.add(news.getUrlToImage());
            NewsItem.add(news.getTitle());
            NewsItem.add(news.getLikeNum());
            NewsItem.add(news.getCommentNum());
            NewsItem.add(news.getBookmarkNum());
        }


        int selectedPosition = NewsList.indexOf(smallNews);

        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), ShowNewsActivity.class);
            smallNews = NewsList.get(selectedPosition);
            intent.putExtra("NewsId", smallNews.getId());
            intent.putExtra("NewsImgURL", smallNews.getUrlToImage());
            intent.putExtra("NewsDescription", smallNews.getDescription());
            intent.putExtra("NewsTitle", smallNews.getTitle());
            intent.putExtra("NewsContent", smallNews.getContent());
            intent.putExtra("comments", smallNews.getCommentNum());
            intent.putExtra("likes", smallNews.getLikeNum());
            intent.putExtra("bookmarks", smallNews.getBookmarkNum());
            intent.putExtra("initialPosition", selectedPosition);
            startActivity(intent);
        }
    }

    private void newsItem(SmallNews smallNews) {
        ArrayList<Object> NewsItem = new ArrayList<>();
        for (SmallNews news : NewsHotList) {
            NewsItem.add(news.getId());
            NewsItem.add(news.getUrlToImage());
            NewsItem.add(news.getTitle());
            NewsItem.add(news.getLikeNum());
            NewsItem.add(news.getCommentNum());
            NewsItem.add(news.getBookmarkNum());
        }


        int selectedPosition = NewsHotList.indexOf(smallNews);

        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), ShowNewsActivity.class);
            smallNews = NewsHotList.get(selectedPosition);
            intent.putExtra("NewsId", smallNews.getId());
            intent.putExtra("NewsImgURL", smallNews.getUrlToImage());
            intent.putExtra("NewsDescription", smallNews.getDescription());
            intent.putExtra("NewsTitle", smallNews.getTitle());
            intent.putExtra("NewsContent", smallNews.getContent());
            intent.putExtra("comments", smallNews.getCommentNum());
            intent.putExtra("likes", smallNews.getLikeNum());
            intent.putExtra("bookmarks", smallNews.getBookmarkNum());
            intent.putExtra("initialPosition", selectedPosition);
            startActivity(intent);
        }
    }





    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(getContext(), ShowNewsActivity.class);

    }

    public void onLongItemClick(int position){
        //no usage here, just declare to not abstract
    }



}