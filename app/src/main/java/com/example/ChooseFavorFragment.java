package com.example;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import com.example.newspackage.NewsFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseFavorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseFavorFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final ArrayList<View> videoSelectedBrand = new ArrayList<View>();
    private final ArrayList<View> newsSelectedBrand = new ArrayList<View>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChooseFavorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChooseFavorFragment newInstance(String param1, String param2) {
        ChooseFavorFragment fragment = new ChooseFavorFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_favor, container, false);

        Button backButton = (Button) view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetNicknameFragment setNicknameFragment = new SetNicknameFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, setNicknameFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // Assuming brandContainer is your GridLayout and contains 6 ImageViews as children
        this.setChildrenClickEventListener((GridLayout) view.findViewById(R.id.brand_container_video), videoSelectedBrand);
        this.setChildrenClickEventListener(view.findViewById(R.id.brand_container_news), newsSelectedBrand);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Button nextButton = (Button) view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getContext(), NewsFragment.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Log.i("Activity", "Activity is null");
                }
            }
        });
    }

    /**
     * Set the click event to the gridlayout's children.
     * */
    private void setChildrenClickEventListener(GridLayout gridLayout, ArrayList<View> tempStorage) {
        int childCount = gridLayout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View childView = gridLayout.getChildAt(i);

            childView.getContentDescription();

            // Set an initial tag to keep track of toggle state
            childView.setTag(false); // false means not selected initially

            // Set an OnClickListener to toggle background
            childView.setOnClickListener(v -> {
                // Retrieve current state from tag
                boolean isSelected = (boolean) v.getTag();

                if (!isSelected) {
                    // If not selected, apply border background
                    v.setBackgroundResource(R.drawable.button_background);

                    tempStorage.add(v);
                } else {
                    // If selected, remove background
                    v.setBackgroundResource(0);

                    if (tempStorage.contains(v)) {
                        tempStorage.remove(v);
                    }
                }

                // Toggle the state
                v.setTag(!isSelected);
            });
        }
    }

    private void setNextButtonEventListener() {

    }
}