package com.example;

public interface SelectListener {
    default void onItemClicked(int position){
    }
    void onLongItemClick(int position);
}
