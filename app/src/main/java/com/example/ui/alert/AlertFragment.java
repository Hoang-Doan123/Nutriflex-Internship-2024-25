package com.example.ui.alert;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.*;

import com.example.R;
import com.example.ui.alert.meal.AlertMealFragment;
import com.example.ui.alert.training.AlertTrainingFragment;
import com.google.android.material.tabs.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AlertFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlertFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertFragment newInstance(String param1, String param2) {
        AlertFragment fragment = new AlertFragment();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("AlertFragment", "onCreateView called");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("AlertFragment", "onViewCreated called");

        ViewPager2 viewPagerAlert = view.findViewById(R.id.viewPagerAlert);
        TabLayout tabLayoutAlert = view.findViewById(R.id.tabLayoutAlert);

        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Log.d("AlertFragment", "Creating fragment at position: " + position);
                switch (position) {
                    case 0:
                        Log.d("AlertFragment", "Creating AlertTrainingFragment");
                        return new AlertTrainingFragment();
                    case 1:
                        Log.d("AlertFragment", "Creating AlertMealFragment");
                        return new AlertMealFragment();
                    default:
                        throw new IllegalArgumentException();
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        };

        viewPagerAlert.setAdapter(adapter);

        new TabLayoutMediator(tabLayoutAlert, viewPagerAlert,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Training");
                            break;
                        case 1:
                            tab.setText("Meal");
                            break;
                    }
                }).attach();
        
        Log.d("AlertFragment", "ViewPager and TabLayout setup completed");
    }
}