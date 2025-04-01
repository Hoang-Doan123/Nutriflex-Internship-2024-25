package com.example.newspackage;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.R;
import com.example.SQLconnection;
import com.example.videopackage.VideoHistoryAdapter;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Locale;

import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class ShowNewsActivity extends AppCompatActivity {
    TextToSpeech textToSpeech;
    SharedPreferences preferences;
    TextView Content, Describe, Title;
    private int newsId;
    private String newsImgURL;
    private String newsTitle;
    private String newsDescription;
    private String newsContent;
    private int comments;
    private int likes;
    private int bookmarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_news);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SQLconnection sqlconnection = new SQLconnection();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = getIntent();
        if (intent != null){
            newsId = intent.getIntExtra("NewsId", -1);
            newsImgURL = intent.getStringExtra("NewsImgURL");
            newsTitle = intent.getStringExtra("NewsTitle");
            newsDescription = intent.getStringExtra("NewsDescription");
            newsContent = intent.getStringExtra("NewsContent");
            comments = intent.getIntExtra("comments", 0);
            likes = intent.getIntExtra("likes", 0);
            bookmarks = intent.getIntExtra("bookmarks", 0);
            int initialPosition = getIntent().getIntExtra("initialPosition", 0);
        }

        ImageButton BackButton = findViewById(R.id.BackButton);
        BackButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        ImageButton Playbutton = findViewById(R.id.Playbutton);

        Content = findViewById(R.id.NewsContent);
        Title = findViewById(R.id.Title);
        Describe = findViewById(R.id.Describe);
        ImageView imageNews = findViewById(R.id.contentImage);

        Title.setText(newsTitle);
        Describe.setText(newsDescription);
        Picasso.get()
                .load(newsImgURL).into(imageNews);

        Content.setText(newsContent);


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        Playbutton.setOnClickListener(view -> textToSpeech.speak(Content.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null));

        // Apply the font size
        String savedFontSize = preferences.getString("font_size", "Medium");
        setFontSize(savedFontSize);

        // Listener for font size change in SharedPreferences
        preferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("font_size")) {
                String fontSize = sharedPreferences.getString("font_size", "Medium");
                setFontSize(fontSize);
            }
        });
    }

    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();
    }





    // Method to apply the font size to all text views
    private void setFontSize(String fontSize) {
        float size = 16;
        switch (fontSize) {
            case "small":
                size = 12;
                break;
            case "medium":
                size = 16;
                break;
            case "large":
                size = 20;
                break;
        }

        TextView Content = findViewById(R.id.NewsContent);
        if (Content != null) {
            Content.setTextSize(size);
        }
    }
}