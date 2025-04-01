package com.example.videopackage.videoComment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.webkit.internal.ApiFeature;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.R;
import com.example.RetrofitClient;
import com.example.requestpackage.VideoCommentRequest;
import com.example.responsepackage.VideoCommentResponse;
import com.example.videopackage.VideoAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoCommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoCommentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USER_ID = "userId";
    private static final String VIDEO_ID = "videoId";

    // TODO: Rename and change types of parameters
    private int userId;
    private int videoId;

    private RecyclerView recyclerView;
    private VideoCommentAdapter videoCommentAdapter;
    private FloatingActionButton closeButton;
    private Button sendButton;
    private EditText commentInput;

    private VideoAPI videoApiService = RetrofitClient.getVideoApiService();

    public VideoCommentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId user's id.
     * @param videoId video's id.
     * @return A new instance of fragment VideoCommentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoCommentFragment newInstance(int userId, int videoId) {
        VideoCommentFragment fragment = new VideoCommentFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, userId);
        args.putInt(VIDEO_ID, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(USER_ID);
            videoId = getArguments().getInt(VIDEO_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeButton = view.findViewById(R.id.commentCloseButton);

        closeButton.setOnClickListener(l -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(this);
            fragmentTransaction.commit();
        });
        recyclerView = view.findViewById(R.id.videoCommentContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Call<List<VideoComment>> call = videoApiService.getComments(videoId);
        call.enqueue(new Callback<List<VideoComment>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<VideoComment>> call,
                    @NonNull Response<List<VideoComment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    videoCommentAdapter = new VideoCommentAdapter(response.body());
                    recyclerView.setAdapter(videoCommentAdapter);
                } else {
                    Toast.makeText(getContext(), "An error getting comments", Toast.LENGTH_SHORT).show();
                    Log.e("Video comments", response.toString());
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<VideoComment>> call,
                    @NonNull Throwable t
            ) {
                Toast.makeText(getContext(), "An error getting comments", Toast.LENGTH_SHORT).show();
                Log.e("Video comments", t.toString());
            }
        });

        sendButton = view.findViewById(R.id.sendButton);
        commentInput = view.findViewById(R.id.commentInput);

        sendButton.setOnClickListener(l -> {
            VideoCommentRequest request = new VideoCommentRequest(
                    commentInput.getText().toString(),
                    null,
                    videoId,
                    userId
            );

            Call<VideoCommentResponse> commentResponseCall = videoApiService.addComment(request);
            commentResponseCall.enqueue(new Callback<VideoCommentResponse>() {
                @Override
                public void onResponse(
                        @NonNull Call<VideoCommentResponse> call,
                        @NonNull Response<VideoCommentResponse> response
                ) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(requireContext(), "Comment Added", Toast.LENGTH_SHORT).show();
                        Log.i("Video Comments", "Comment Added");
                    } else {
                        Toast.makeText(requireContext(), "Failed To Add Comment", Toast.LENGTH_SHORT).show();
                        Log.e("Video Comments", response.toString());
                    }
                }

                @Override
                public void onFailure(
                        @NonNull Call<VideoCommentResponse> call,
                        @NonNull Throwable t
                ) {
                    Toast.makeText(requireContext(), "Failed To Add Comment", Toast.LENGTH_SHORT).show();
                    Log.e("Video Comments", t.toString());
                }
            });
        });
    }
}