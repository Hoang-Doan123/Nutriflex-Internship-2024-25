package com.example.savepackage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaveNewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaveNewsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USER_ID = "user_id";

    // TODO: Rename and change types of parameters
    private int userId;
    private String mParam2;

    public SaveNewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId user's id
     * @return A new instance of fragment SaveNewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaveNewsFragment newInstance(int userId) {
        SaveNewsFragment fragment = new SaveNewsFragment();
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
        return inflater.inflate(R.layout.fragment_save_news, container, false);
    }
}