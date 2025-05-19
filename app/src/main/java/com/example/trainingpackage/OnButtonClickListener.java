package com.example.trainingpackage;

public interface OnButtonClickListener {
    void onCommentClick(int position, int videoId);
    void onLikeClick(int position, int videoId);
    void onBookmarkClick(int position, int videoId);
}
