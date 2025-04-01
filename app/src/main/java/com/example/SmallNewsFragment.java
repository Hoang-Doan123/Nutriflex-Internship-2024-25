package com.example;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SmallNewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SmallNewsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "title";
    private static final String ARG_DATE = "date";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_IMAGE = "image";
    private static final String ARG_ID = "id";
    static Random random = new Random();

    // TODO: Rename and change types of parameters
    private int id;
    private String title;
    private String date;
    private String description;
    private String imageUrl;

    TextView newsTitle, newsDescription, newsDate;
    ImageView mainImage;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public SmallNewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title The News Title.
     * @param date The News Post Date.
     * @param description The Brief Description Of The News.
     * @param imageUrl The Image Of The Main Title.
     * @return A new instance of fragment SmallNewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SmallNewsFragment newInstance(
            int id,
            String title,
            String date,
            String description,
            String imageUrl
    ) {
        SmallNewsFragment fragment = new SmallNewsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DATE, date);
        args.putString(ARG_DESCRIPTION, description);
        args.putString(ARG_IMAGE, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ARG_ID);
            title = getArguments().getString(ARG_TITLE);
            date = getArguments().getString(ARG_DATE);
            description = getArguments().getString(ARG_DESCRIPTION);
            imageUrl = getArguments().getString(ARG_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_small_news, container, false);

        newsTitle = requireActivity().findViewById(R.id.newsShortTitle);
        newsDate = requireActivity().findViewById(R.id.newsDate);
        newsDescription = requireActivity().findViewById(R.id.newsDescription);
        mainImage = requireActivity().findViewById(R.id.mainImage);

        newsTitle.setText(title);
        newsDate.setText(date);
        newsDescription.setText(description);

        this.downloadImage(imageUrl, mainImage);

        return view;
    }

    private void downloadImage(String url, ImageView imageView) {
        executorService.execute(() -> {
            Bitmap bitmap = null;
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();

                try (InputStream inputStream = connection.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error Downloading Image", e.toString());
            }

            Bitmap finalBitmap = bitmap;
            handler.post(() -> {
                if (finalBitmap != null) {
                    imageView.setImageBitmap(finalBitmap);
                }
            });
        });
    }
}