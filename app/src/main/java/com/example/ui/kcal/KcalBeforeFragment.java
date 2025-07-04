package com.example.ui.kcal;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import com.example.R;
import com.example.network.KcalRequest;
import android.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KcalBeforeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KcalBeforeFragment extends Fragment {
    private MapView mapView;
    private Button btnStart;
    private Button btnStop;
    private TextView tvResult;
    private KcalViewModel viewModel;
    private final List<GeoPoint> routePoints = new ArrayList<>();
    private boolean isMeasuring = false;
    private long startTime = 0L;
    private LocationManager locationManager;
    private int lastElapsedMinutes = 0;
    private int lastElapsedSeconds = 0;
    private FusedLocationProviderClient fusedLocationClient;
    private GeoPoint lastKnownGeoPoint = null;
    private boolean pendingStart = false;
    private KcalSharedViewModel sharedViewModel;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (isMeasuring) {
                GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
                routePoints.add(point);
                drawRouteOrMarker();
            }
        }
    };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public KcalBeforeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment KcalBeforeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KcalBeforeFragment newInstance(String param1, String param2) {
        KcalBeforeFragment fragment = new KcalBeforeFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kcal_before, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapView);
        btnStart = view.findViewById(R.id.btnStart);
        btnStop = view.findViewById(R.id.btnStop);
        tvResult = view.findViewById(R.id.tvResult);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(18.0);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        viewModel = new ViewModelProvider(this).get(KcalViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(KcalSharedViewModel.class);

        // Take real location when open fragment
        getCurrentLocation(geoPoint -> {
            lastKnownGeoPoint = geoPoint;
            mapView.getController().setCenter(geoPoint);
        });

        btnStart.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                pendingStart = true;
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
                android.widget.Toast.makeText(requireContext(), "Please grant location permission to start measuring.", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            startMeasuring();
        });

        btnStop.setOnClickListener(v -> {
            if (isMeasuring) {
                isMeasuring = false;
                locationManager.removeUpdates(locationListener);
                long elapsedMillis = System.currentTimeMillis() - startTime;
                int minutes = (int) (elapsedMillis / 60000);
                int seconds = (int) ((elapsedMillis % 60000) / 1000);
                int duration = (minutes == 0 && seconds > 0) ? 1 : minutes;
                double distance = calculateDistance(routePoints);
                StringBuilder routeStr = new StringBuilder();
                for (GeoPoint p : routePoints) {
                    routeStr.append(p.getLongitude()).append(" ").append(p.getLatitude()).append(",");
                }
                // If user doesn't move, add current location if not exist
                if (routePoints.isEmpty() && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location lastLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastLoc != null) {
                        GeoPoint point = new GeoPoint(lastLoc.getLatitude(), lastLoc.getLongitude());
                        routePoints.add(point);
                    }
                }
                drawRouteOrMarker();
                KcalRequest request = new KcalRequest(1, distance, duration, 70.0, routeStr.toString());
                viewModel.measureAndSave(request);
                // Save real time to display m/s
                lastElapsedMinutes = minutes;
                lastElapsedSeconds = seconds;

                // Sau khi đo xong, chuyển tab sang After Cardio
                viewModel.getMeasureResult().observe(getViewLifecycleOwner(), result -> {
                    if (result != null) {
                        int caloriesBurned = (int) result.getKcal();
                        long userId = result.getUserId();
                        Log.d("KcalDebug", "Set workout: userId=" + userId + ", caloriesBurned=" + caloriesBurned);
                        sharedViewModel.setWorkoutResult(userId, caloriesBurned);
                        sharedViewModel.requestSwitchToAfterTab();
                    }
                });
            }
        });

        viewModel.getMeasureResult().observe(getViewLifecycleOwner(), result -> {
            double distance = result != null ? result.getDistance() : 0.0;
            int duration = result != null ? result.getDuration() : 0;
            String pace = (distance > 0 && duration > 0) ? String.format("%.2f", duration / distance) : "--";
            int elevation = 0; // Not yet measured the real elevation
            String heartBeat = "--"; // Not yet connect to watch
            String timeStr = String.format("%d min %02d sec", lastElapsedMinutes, lastElapsedSeconds);
            String resultText = String.format(
                    "Distance: %.2f km\nKcal: %d\nPace: %s min/km\nElevation: %d m\nHeart beat: %s\nTime: %s",
                    distance,
                    result != null ? (int) result.getKcal() : 0,
                    pace,
                    (int) elevation,
                    heartBeat,
                    timeStr
            );
            if (lastElapsedMinutes < 10) {
                resultText += "\nGreat start! Try to do at least 10 minutes of cardio for better health.";
            }
            tvResult.setText(resultText);
        });
    }

    private void drawRouteOrMarker() {
        mapView.getOverlays().clear();
        if (routePoints.size() > 1) {
            Polyline polyline = new Polyline();
            polyline.setPoints(routePoints);
            polyline.getOutlinePaint().setColor(getResources().getColor(android.R.color.holo_blue_dark, null));
            polyline.getOutlinePaint().setStrokeWidth(8f);
            mapView.getOverlays().add(polyline);
            mapView.getController().setCenter(routePoints.get(routePoints.size() - 1));
        } else if (routePoints.size() == 1) {
            Marker marker = new Marker(mapView);
            marker.setPosition(routePoints.get(0));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle("Current Location");
            mapView.getOverlays().add(marker);
            mapView.getController().setCenter(routePoints.get(0));
        }
        mapView.invalidate();
    }

    private double calculateDistance(List<GeoPoint> points) {
        double dist = 0.0;
        for (int i = 1; i < points.size(); i++) {
            dist += points.get(i - 1).distanceToAsDouble(points.get(i));
        }
        return dist / 1000.0;
    }

    private void getCurrentLocation(LocationCallbackGeo callback) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback.onResult(new GeoPoint(21.0285, 105.8542));
            return;
        }
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMaxUpdates(1)
                .build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                Location location = result.getLastLocation();
                if (location != null) {
                    callback.onResult(new GeoPoint(location.getLatitude(), location.getLongitude()));
                } else {
                    callback.onResult(new GeoPoint(21.0285, 105.8542));
                }
                fusedLocationClient.removeLocationUpdates(this);
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    interface LocationCallbackGeo {
        void onResult(GeoPoint geoPoint);
    }

    private void startMeasuring() {
        isMeasuring = true;
        startTime = System.currentTimeMillis();
        routePoints.clear();
        tvResult.setText("Measuring...");
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1f, locationListener);
        }
        // Get the real location when start measuring
        getCurrentLocation(geoPoint -> {
            lastKnownGeoPoint = geoPoint;
            mapView.getController().setCenter(geoPoint);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingStart) {
                    pendingStart = false;
                    startMeasuring();
                }
            } else {
                android.widget.Toast.makeText(requireContext(), "Location permission denied. Cannot start measuring.", android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }
}