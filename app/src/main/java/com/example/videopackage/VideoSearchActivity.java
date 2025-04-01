package com.example.videopackage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoSearchActivity extends AppCompatActivity {
    private RecyclerView videoSearchResult;
    private RecyclerView histories;
    private RelativeLayout trendsAndHistory;
    private VideoSearchAdapter videoSearchAdapter;
    private VideoHistoryAdapter videoHistoryAdapter;
    private final List<Video> videoSearchList = new ArrayList<>();
    private List<Video> originalList = new ArrayList<>();
    private ImageView searchIcon;
    EditText searchText;
    private Button newest;
    private Button hot;
    private Button oldPosts;
    private Button searchApple;
    private Button searchSamsung;
    private Button searchHuawei;
    private Button searchXiaomi;
    private Button searchMicrosoft;
    private Button searchAsus;
    private Button searchButton;
    private Button deleteHistoryButton;
    private TextView trend1;
    private TextView trend2;
    private TextView trend3;
    private TextView trend4;
    private TextView searchNoResult;

    private static final String baseUrl = "https://android-backend-tech-c52e01da23ae.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.back_button);
        ImageView clearSearchButton = findViewById(R.id.clear_search);
        videoSearchResult = findViewById(R.id.video_search_result);
        trendsAndHistory = findViewById(R.id.trends_and_history);
        searchIcon = findViewById(R.id.search_icon);
        searchText = findViewById(R.id.search_text);
        newest = findViewById(R.id.newest);
        hot = findViewById(R.id.hot);
        oldPosts = findViewById(R.id.old_post);
        searchApple = findViewById(R.id.apple_part);
        searchSamsung = findViewById(R.id.samsung_part);
        searchHuawei = findViewById(R.id.huawei_part);
        searchXiaomi = findViewById(R.id.xiaomi_part);
        searchMicrosoft = findViewById(R.id.microsoft_part);
        searchAsus = findViewById(R.id.asus_part);
        trend1 = findViewById(R.id.trend1);
        trend2 = findViewById(R.id.trend2);
        trend3 = findViewById(R.id.trend3);
        trend4 = findViewById(R.id.trend4);
        searchButton = findViewById(R.id.search_button);
        deleteHistoryButton = findViewById(R.id.delete_history);
        histories = findViewById(R.id.histories);
        searchNoResult = findViewById(R.id.video_search_no_result);

        // Back button part
        // Set a click listener to finish the activity
        backButton.setOnClickListener(v -> {
            // Close the SearchActivity and return to the previous fragment
            finish();
        });

        // Clear search button part
        // Set a click listener to clear the search text
        clearSearchButton.setOnClickListener(v -> {
            // Clear the typed text to search
            clearSearch();
        });

        // Video search result part
        videoSearchResult.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
        videoSearchAdapter = new VideoSearchAdapter(getBaseContext(), videoSearchList, baseUrl, this::playVideo);
        videoSearchResult.setAdapter(videoSearchAdapter);
        videoSearchResult.setVisibility(View.GONE); // Ensure it's hidden initially

        // Set visibility for trendsAndHistory, searchButton and deleteHistoryButton
        trendsAndHistory.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        deleteHistoryButton.setVisibility(View.VISIBLE);

        // Search icon part
        // Set an OnClickListener for the search icon
        searchIcon.setOnClickListener(v -> {
            String query = searchText.getText().toString().trim();
            if (!query.isEmpty()) {
                saveToHistory(query); // Save the keyword to history
                loadHistory();
                filterVideos(query);
                Toast.makeText(this, "Search: " + query, Toast.LENGTH_SHORT).show();
            }
        });

        // Tap in buttons containing keywords for search progress
        newest.setOnClickListener(v -> performSearch("Samsung Galaxy Note9"));
        hot.setOnClickListener(v -> performSearch("Ipad Pro"));
        oldPosts.setOnClickListener(v -> performSearch("Drone"));
        searchApple.setOnClickListener(v -> performSearch("Iphone"));
        searchSamsung.setOnClickListener(v -> performSearch("Xiaomi 12T"));
        searchHuawei.setOnClickListener(v -> performSearch("Huawei MatePad Pro"));
        searchXiaomi.setOnClickListener(v -> performSearch("Tab S10 Series"));
        searchMicrosoft.setOnClickListener(v -> performSearch("Watch"));
        searchAsus.setOnClickListener(v -> performSearch("Laptop"));

        // Search button part
        // Set an OnClickListener for the search button
        searchButton.setOnClickListener(v -> {
            String query = searchText.getText().toString().trim();
            if (!query.isEmpty()) {
                saveToHistory(query); // Save the keyword to history
                loadHistory();
                filterVideos(query);
                Toast.makeText(this, "Search: " + query, Toast.LENGTH_SHORT).show();
            }
        });

        // Trends part
        trend1.setOnClickListener(v -> performSearch("Iphone 16"));
        trend2.setOnClickListener(v -> performSearch("Huawei WATCH 3"));
        trend3.setOnClickListener(v -> performSearch("S24"));
        trend4.setOnClickListener(v -> performSearch("Ipad Pro"));

        // Remove all histories
        deleteHistoryButton.setOnClickListener(v -> {
            clearAllHistory();
        });

        // History part
        histories.setLayoutManager(new LinearLayoutManager(this));
        videoHistoryAdapter = new VideoHistoryAdapter(new ArrayList<>(), this);
        histories.setAdapter(videoHistoryAdapter);
        Log.d("VideoSearchActivity", "Histories: " + histories.toString());

        // Search no result part
        searchNoResult.setVisibility(View.GONE);

        fetchVideos(); // Load videos
        loadHistory(); // Load the search history when rerun the app
    }

    private void fetchVideos() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        VideoAllAPI videoAllAPI = retrofit.create(VideoAllAPI.class);
        Call<List<Video>> call = videoAllAPI.getVideos();
        call.enqueue(new Callback<List<Video>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Video>> call, @NonNull Response<List<Video>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Save the original list
                    originalList = response.body();
                    // Update adapter with video list
                    videoSearchAdapter.setData(originalList);
                    // Shuffle the video list
                    videoSearchAdapter.shuffleVideos();
                    // Update the adapter after adding video
                    videoSearchAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getBaseContext(), "No videos available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Video>> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), "Failed to fetch videos", Toast.LENGTH_SHORT).show();
                Log.e("API Error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void fetchSearchVideos(@NonNull Runnable onVideosLoaded) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        VideoAllAPI videoAllAPI = retrofit.create(VideoAllAPI.class);
        Call<List<Video>> call = videoAllAPI.getVideos();
        call.enqueue(new Callback<List<Video>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Video>> call, @NonNull Response<List<Video>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Save the original list
                    originalList = response.body();
                    // Update adapter with video list
                    videoSearchAdapter.setData(originalList);
                    // Shuffle the video list
                    videoSearchAdapter.shuffleVideos();
                    // Update the adapter after adding video
                    videoSearchAdapter.notifyDataSetChanged();
                    Log.d("VideoSearchActivity", "Videos loaded successfully: " + originalList.size());
                    // Function callback after loading data
                    onVideosLoaded.run();
                } else {
                    Toast.makeText(getBaseContext(), "No videos available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Video>> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), "Failed to fetch videos", Toast.LENGTH_SHORT).show();
                Log.e("API Error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void playVideo(Video video) {
        ArrayList<Integer> videoIds = new ArrayList<>(); // Create a list of video IDs
        ArrayList<String> videoUris = new ArrayList<>(); // List of video URIs
        ArrayList<String> videoTitles = new ArrayList<>(); // List of video titles
        ArrayList<Integer> comments = new ArrayList<>(); // List of video comments
        ArrayList<Integer> likes = new ArrayList<>(); // List of video likes
        ArrayList<Integer> bookmarks = new ArrayList<>(); // List of video saves

        for (Video v : videoSearchList) {
            videoIds.add(v.getId());
            videoUris.add(baseUrl + v.getFetchableUrl());
            videoTitles.add(v.getTitle());
            comments.add(v.getCommentNum());
            likes.add(v.getLikeNum());
            bookmarks.add(v.getBookmarkNum());
        }

        // Get the selected video's position
        int selectedPosition = videoSearchList.indexOf(video);

        // Start PlayVideoActivity with the video list and selected position
        Intent intent = new Intent(this, PlayVideoActivity.class);
        intent.putExtra("videoIds", videoIds);
        intent.putStringArrayListExtra("videoUris", videoUris);
        intent.putStringArrayListExtra("videoTitles", videoTitles);
        intent.putIntegerArrayListExtra("comments", comments);
        intent.putIntegerArrayListExtra("likes", likes);
        intent.putIntegerArrayListExtra("bookmarks", bookmarks);
        intent.putExtra("initialPosition", selectedPosition);
        startActivity(intent);
    }

    // Add the filterVideos method
    @SuppressLint("NotifyDataSetChanged")
    void filterVideos(String query) {
        Log.d("VideoSearchActivity", "Filtering videos with: " + query);

        // Check if query is valid
        if (query == null || query.trim().isEmpty()) {
            Log.d("VideoSearchActivity", "Query is empty, skipping filter.");
            return;
        }

        if (originalList.isEmpty()) {
            Log.d("VideoSearchActivity", "Original list is empty, waiting for data...");
            // Call fetchSearchVideos() and do filterVideos when complete
            fetchSearchVideos(() -> filterVideos(query));
            return;
        }

        List<Video> filteredList = new ArrayList<>();
        for (Video video : originalList) {
            if (video.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(video);
            }
        }

        videoSearchAdapter.setData(filteredList);
        videoSearchAdapter.notifyDataSetChanged();

        // Show search result
        videoSearchResult.setVisibility(filteredList.isEmpty() ? View.GONE : View.VISIBLE);
        searchNoResult.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
        trendsAndHistory.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        deleteHistoryButton.setVisibility(View.GONE);

        Log.d("VideoSearchActivity", "Filtered video list size: " + filteredList.size());
    }


    // Method to clear the search input and reset the video list
    private void clearSearch() {
        searchText.setText(""); // Clear the search text
        videoSearchAdapter.setData(originalList); // Reset to the original list
        videoSearchResult.setVisibility(View.GONE); // Hide the search results
        searchNoResult.setVisibility(View.GONE);
        trendsAndHistory.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        loadHistory();
    }

    // Save the keywords to history list
    void saveToHistory(String query) {
        SharedPreferences sharedPreferences = getSharedPreferences("SearchHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> historySet = new HashSet<>(sharedPreferences.getStringSet("history", new HashSet<>()));
        historySet.add(query);

        editor.putStringSet("history", historySet);
        editor.apply();
    }

    private void clearAllHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences("SearchHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("history"); // Clear all histories
        editor.apply();

        Toast.makeText(this, "All search histories are deleted", Toast.LENGTH_SHORT).show();

        loadHistory();
    }

    void loadHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences("SearchHistory", MODE_PRIVATE);
        Set<String> historySet = sharedPreferences.getStringSet("history", new HashSet<>());

        List<String> historyList = new ArrayList<>(historySet);
        Collections.reverse(historyList); // Show the newest keyword on the top

        videoHistoryAdapter.updateHistory(historyList);

        if (!historyList.isEmpty()) {
            histories.setVisibility(View.VISIBLE);
            deleteHistoryButton.setVisibility(View.VISIBLE); // Show if there are keywords in history
        } else {
            histories.setVisibility(View.GONE);
            deleteHistoryButton.setVisibility(View.GONE); // Hide if there are no keywords in history
        }
    }

    private void performSearch(String keyword) {
        searchText.setText(keyword); // Update the searchbar
        Log.d("VideoSearchActivity", "Clicked trend item: " + keyword);

        searchText.postDelayed(() -> {
            saveToHistory(keyword); // Save the keyword to history
            loadHistory();
            filterVideos(keyword);
        }, 300); // Wait 300ms to make sure that data is updated

        Toast.makeText(this, "Search: " + keyword, Toast.LENGTH_SHORT).show();
    }

    // Remove a keyword from the history list
    public void removeSearchQuery(String query) {
        SharedPreferences sharedPreferences = getSharedPreferences("SearchHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> historySet = new HashSet<>(sharedPreferences.getStringSet("history", new HashSet<>()));
        historySet.remove(query);

        editor.putStringSet("history", historySet);
        editor.apply();

        loadHistory();
    }
}