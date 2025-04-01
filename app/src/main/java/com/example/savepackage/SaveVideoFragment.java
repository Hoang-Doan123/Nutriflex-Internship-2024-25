package com.example.savepackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.R;
import com.example.videopackage.PlayVideoActivity;
import com.example.videopackage.Video;
import com.example.videopackage.VideoAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaveVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaveVideoFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private final List<Video> videoList = new ArrayList<>();
    private TabLayout tabLayout;

    private static final String baseUrl = "https://android-backend-tech-c52e01da23ae.herokuapp.com/";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USER_ID = "user_id";

    // TODO: Rename and change types of parameters
    private Integer userId;

    public SaveVideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId user's id.
     * @return A new instance of fragment VideoAllFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaveVideoFragment newInstance(int userId) {
        SaveVideoFragment fragment = new SaveVideoFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_save_video, container, false);

        tabLayout = requireActivity().findViewById(R.id.tab_layout_main);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        recyclerView = view.findViewById(R.id.videos);

        // Setup RecyclerView with GridLayoutManager for 2 columns
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        videoAdapter = new VideoAdapter(getContext(), videoList, baseUrl, this::playVideo);
        recyclerView.setAdapter(videoAdapter);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    tabLayout.setVisibility(View.INVISIBLE);
                } else {
                    tabLayout.setVisibility(View.VISIBLE);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        // Load videos
        fetchVideos();

        return view;
    }

    private void refreshData() {
        videoList.clear();
        fetchVideos();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void fetchVideos() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SaveAPI saveAPI = retrofit.create(SaveAPI.class);
        Call<List<Video>> call = saveAPI.getVideos(userId);
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<Video>> call,
                    @NonNull Response<List<Video>> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && !response.body().isEmpty()) {
                    videoList.addAll(response.body());

                    videoList.clear();
                    videoList.addAll(response.body());
                    videoAdapter.setData(videoList);
                    videoAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "No videos available", Toast.LENGTH_SHORT).show();
                    Log.e("SaveVideoFragment", "Fetch Video Failed: " + response);
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<Video>> call,
                    @NonNull Throwable t) {
                Toast.makeText(getContext(), "Failed to fetch videos", Toast.LENGTH_SHORT).show();
                Log.e("SaveVideoFragment", "Fetch Video Failed: " + t);
            }
        });
    }

    private void playVideo(Video video) {
        ArrayList<Integer> videoIds = new ArrayList<>();
        ArrayList<String> videoUris = new ArrayList<>();
        ArrayList<String> videoTitles = new ArrayList<>();
        ArrayList<Integer> comments = new ArrayList<>();
        ArrayList<Integer> likes = new ArrayList<>();
        ArrayList<Integer> bookmarks = new ArrayList<>();
        for (Video v: videoList) {
            videoIds.add(v.getId());
            videoUris.add(baseUrl + v.getFetchableUrl());
            videoTitles.add(v.getTitle());
            comments.add(v.getCommentNum());
            likes.add(v.getLikeNum());
            bookmarks.add(v.getBookmarkNum());
        }

        // Get the selected video's position
        int selectedPosition = videoList.indexOf(video);

        // Start PlayVideoActivity with the video list and selected position
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), PlayVideoActivity.class);
            intent.putIntegerArrayListExtra("videoIds", videoIds);
            intent.putStringArrayListExtra("videoUris", videoUris);
            intent.putStringArrayListExtra("videoTitles", videoTitles);
            intent.putIntegerArrayListExtra("comments", comments);
            intent.putIntegerArrayListExtra("likes", likes);
            intent.putIntegerArrayListExtra("bookmarks", bookmarks);
            intent.putExtra("initialPosition", selectedPosition);
            startActivity(intent);
        }
    }
}