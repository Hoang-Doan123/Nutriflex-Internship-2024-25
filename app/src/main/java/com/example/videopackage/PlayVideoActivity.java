package com.example.videopackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.authpackage.LoginActivity;
import com.example.R;
import com.example.RetrofitClient;
import com.example.requestpackage.VideoBookmarkRequest;
import com.example.requestpackage.VideoLikeRequest;
import com.example.responsepackage.VideoBookmarkResponse;
import com.example.responsepackage.VideoLikeResponse;
import com.example.videopackage.videoComment.VideoCommentFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayVideoActivity extends AppCompatActivity implements OnButtonClickListener {
    private ViewPager2 viewPager;
    private PlayVideoPagerAdapter adapter;
    private List<Integer> videoIds;
    private List<String> videoUris;
    private List<String> videoTitles;
    private List<Integer> comments;
    private List<Integer> likes;
    private List<Integer> bookmarks;
    private List<Boolean> isVideosLiked;
    private List<Boolean> isVideosBookmarked;
    private FloatingActionButton likeButton, commentButton, bookmarkButton;
    private ExoPlayer exoPlayer;

    private SharedPreferences sharedPreferences;
    private String token;
    private int userId;

    private final String url = "https://android-backend-tech-c52e01da23ae.herokuapp.com/";
    private VideoAPI videoApiService = RetrofitClient.getVideoApiService();

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play_video);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("auth_token", null);
        userId = sharedPreferences.getInt("userId", -1);

        // Find the close_icon ImageView
        ImageView backButton = findViewById(R.id.back_button);

        // Set a click listener to finish the activity
        backButton.setOnClickListener(v -> {
            // Close the SearchActivity and return to the previous fragment
            onBackPressed();
        });

        exoPlayer = new ExoPlayer.Builder(this).build();

        exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_OFF);
        exoPlayer.setShuffleModeEnabled(false);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                // Prevent auto-play of the next item until user swipes
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    exoPlayer.pause();
                }
            }
        });

        videoIds = getIntent().getIntegerArrayListExtra("videoIds");

        // Retrieve video list from intent
        videoUris = getIntent().getStringArrayListExtra("videoUris");

        // Retrieve video list from intent
        videoTitles = getIntent().getStringArrayListExtra("videoTitles");

        // Retrieve video comments list from intent
        comments = getIntent().getIntegerArrayListExtra("comments");

        // Retrieve video likes list from intent
        likes = getIntent().getIntegerArrayListExtra("likes");

        // Retrieve video saves list from intent
        bookmarks = getIntent().getIntegerArrayListExtra("bookmarks");

        isVideosBookmarked = new ArrayList<>();
        isVideosLiked = new ArrayList<>();
        getCurrentVideoLikeState(userId, videoIds);
        getCurrentVideoBookmarkState(userId, videoIds);

        int initialPosition = getIntent().getIntExtra("initialPosition", 0);

        viewPager = findViewById(R.id.viewPager);

        // Set up adapter
        adapter = new PlayVideoPagerAdapter(
                this,
                videoIds,
                videoUris,
                videoTitles,
                comments,
                likes,
                bookmarks,
                isVideosLiked,
                isVideosBookmarked
        );
        viewPager.setAdapter(adapter);

        // Set initial video position
        viewPager.setCurrentItem(initialPosition, false);
    }

    @Override
    public void onLikeClick(int position, int videoId) {
        if (token == null) {
            Intent intent = new Intent(PlayVideoActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (isVideosLiked.get(position)) {
                Call<Void> call = videoApiService.deleteLike(userId, videoId);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<Void> call,
                            @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(PlayVideoActivity.this, "Video Unliked", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PlayVideoActivity.this, "Video Unliking Failed", Toast.LENGTH_SHORT).show();
                            Log.e("VideoUnlikingFailed", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Void> call,
                            @NonNull Throwable t) {
                        Toast.makeText(PlayVideoActivity.this, "Video Unliking Failed", Toast.LENGTH_SHORT).show();
                        Log.i("VideoUnlikingFailed", t.toString());
                    }
                });
            } else {
                VideoLikeRequest videoLikeRequest = new VideoLikeRequest(userId, videoId);

                Call<VideoLikeResponse> call = videoApiService.addLike(videoLikeRequest);
                call.enqueue(new Callback<VideoLikeResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<VideoLikeResponse> call,
                            @NonNull Response<VideoLikeResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.i("VideoLiked", "Success");
                        } else {
                            Toast.makeText(PlayVideoActivity.this, "Video Liking Failed", Toast.LENGTH_SHORT).show();
                            Log.e("VideoLikingFailed", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<VideoLikeResponse> call,
                            @NonNull Throwable t) {
                        Toast.makeText(PlayVideoActivity.this, "Video Liking Failed", Toast.LENGTH_SHORT).show();
                        Log.e("VideoLikingFailed", t.toString());
                    }
                });
            }
        }
    }

    @Override
    public void onBookmarkClick(int position, int videoId) {
        if (token == null) {
            Intent intent = new Intent(PlayVideoActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (isVideosBookmarked.get(position)) {
                Call<Void> call = videoApiService.deleteBookmark(userId, videoId);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<Void> call,
                            @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(PlayVideoActivity.this, "Video Un-bookmarked", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PlayVideoActivity.this, "Video Un-bookmarking Failed", Toast.LENGTH_SHORT).show();
                            Log.e("VideoUnsavingFailed", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Void> call,
                            @NonNull Throwable t) {
                        Toast.makeText(PlayVideoActivity.this, "Video Un-bookmarking Failed", Toast.LENGTH_SHORT).show();
                        Log.i("VideoUnsavingFailed", t.toString());
                    }
                });
            } else {
                VideoBookmarkRequest videoBookmarkRequest = new VideoBookmarkRequest(userId, videoId);

                Call<VideoBookmarkResponse> call = videoApiService.addBookmark(videoBookmarkRequest);
                call.enqueue(new Callback<VideoBookmarkResponse>() {
                    @Override
                    public void onResponse(Call<VideoBookmarkResponse> call, Response<VideoBookmarkResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.i("VideoBookmarked", "Success");
                        } else {
                            Toast.makeText(PlayVideoActivity.this, "Video Saving Failed", Toast.LENGTH_SHORT).show();
                            Log.e("VideoBookmarkingFailed", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoBookmarkResponse> call, Throwable t) {
                        Toast.makeText(PlayVideoActivity.this, "Video Saving Failed", Toast.LENGTH_SHORT).show();
                        Log.e("VideoBookmarkingFailed", t.toString());
                    }
                });
            }
        }
    }

    @Override
    public void onCommentClick(int position, int videoId) {
        if (token == null) {
            Intent intent = new Intent(PlayVideoActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.commentVideoContainer, VideoCommentFragment.newInstance(userId, videoId));
            transaction.commit();
        }
    }

    private void getCurrentVideoLikeState(int user, List<Integer> videoIds) {
        for (Integer videoId: videoIds) {
            Call<List<VideoLikeResponse>> call = videoApiService.getLike(user, videoId);
            call.enqueue(new Callback<List<VideoLikeResponse>>() {
                @Override
                public void onResponse(
                        @NonNull Call<List<VideoLikeResponse>> call,
                        @NonNull Response<List<VideoLikeResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.i("isVideoLiked", response.body().toString());
                        isVideosLiked.add(true);
                    } else {
                        isVideosLiked.add(false);
                    }
                }

                @Override
                public void onFailure(
                        @NonNull Call<List<VideoLikeResponse>> call,
                        @NonNull Throwable t) {
                    Log.i("GetVideoLikeState Failed", t.toString());
                    isVideosLiked.add(false);
                }
            });
        }
    }

    private void getCurrentVideoBookmarkState(int user, List<Integer> videoIds) {
        for (Integer videoId: videoIds) {
            Call<List<VideoBookmarkResponse>> call = videoApiService.getBookmark(user, videoId);
            call.enqueue(new Callback<List<VideoBookmarkResponse>>() {
                @Override
                public void onResponse(
                        @NonNull Call<List<VideoBookmarkResponse>> call,
                        @NonNull Response<List<VideoBookmarkResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.i("isVideoBookmarked", response.body().toString());
                        isVideosBookmarked.add(true);
                    } else {
                        isVideosBookmarked.add(false);
                    }
                }

                @Override
                public void onFailure(
                        @NonNull Call<List<VideoBookmarkResponse>> call,
                        @NonNull Throwable t) {
                    Log.i("GetBookmarkStateFailed", t.toString());
                    isVideosBookmarked.add(false);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            exoPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }
}