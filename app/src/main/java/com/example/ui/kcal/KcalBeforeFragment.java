package com.example.ui.kcal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;

import androidx.annotation.*;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.*;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.view.*;
import android.widget.*;

import com.example.R;
import com.example.model.WorkoutSession;
import com.example.model.auth.User;
import com.example.network.*;
import com.example.service.UserService;
import com.example.utils.SessionManager;
import com.google.android.gms.location.*;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.*;

import java.util.*;

import android.util.Log;

import java.time.*;
import java.time.format.DateTimeFormatter;

import retrofit2.*;

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
    private SessionManager sessionManager;
    private UserService userService;
    private Polyline polyline; // Add field for polyline
    
    // GPS accuracy filtering constants
    private static final float MIN_ACCURACY = 10.0f; // meters
    private static final float MIN_DISTANCE_BETWEEN_POINTS = 5.0f; // meters
    private static final long LOCATION_UPDATE_INTERVAL = 1000; // 1 second
    private static final long LOCATION_FASTEST_INTERVAL = 500; // 500ms

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (isMeasuring && isLocationAccurate(location)) {
                GeoPoint newPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                
                // Only add point if it's far enough from the last point
                if (routePoints.isEmpty() || isPointFarEnough(routePoints.get(routePoints.size() - 1), newPoint)) {
                    routePoints.add(newPoint);
                    drawRouteOrMarker();
                    Log.d("KcalDebug", "Added point: " + newPoint.getLatitude() + ", " + newPoint.getLongitude() + 
                          " Accuracy: " + location.getAccuracy() + "m");
                }
            }
        }
        
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        
        @Override
        public void onProviderEnabled(@NonNull String provider) {}
        
        @Override
        public void onProviderDisabled(@NonNull String provider) {}
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
        sessionManager = new SessionManager(requireContext());
        userService = new UserService(requireContext());

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
                Log.d("KcalDebug", "Stopped measuring. Total points: " + routePoints.size());
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
                        Log.d("KcalDebug", "Added stationary point: " + point.getLatitude() + ", " + point.getLongitude());
                    }
                }
                drawRouteOrMarker();
                @SuppressLint("DefaultLocale") String debugInfo = String.format("Points: %d, Distance: %.3f km, Duration: %d min %d sec",
                    routePoints.size(), distance, minutes, seconds);
                Log.d("KcalDebug", debugInfo);
                String userId = sessionManager.getUserId();
                Log.d("KcalDebug", "Fetching weight for userId: " + userId);
                userService.getUserById(userId, new UserService.UserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        double userWeight = user.getWeight() != null ? user.getWeight() : 70.0;
                        int userAge = user.getAge() != null ? user.getAge() : 25;
                        double userHeight = user.getHeight() != null ? user.getHeight() : 170.0;
                        Log.d("KcalDebug", "User from DB: weight=" + userWeight + ", age=" + userAge + ", height=" + userHeight);
                        showHeartRateInputDialog((heartRate) -> {
                            int caloriesBurned = calculateCaloriesWithHeartRate(distance, duration, userWeight, heartRate, userAge);
                            float heartRateAvg = heartRate > 0 ? heartRate : -1;
                            requireActivity().runOnUiThread(() -> {
                                showMeasurementResults(distance, minutes, seconds, routePoints.size(), caloriesBurned, userId, heartRateAvg);
                                KcalRequest request = new KcalRequest(userId, distance, duration, userWeight, routeStr.toString());
                                viewModel.measureAndSave(request);
                            });
                        });
                    }
                    @Override
                    public void onError(String error) {
                        Log.e("KcalDebug", "Failed to get user weight from database: " + error);
                        double userWeight = sessionManager.getUserWeight();
                        Log.d("KcalDebug", "Using fallback weight from session: " + userWeight + "kg");
                        showHeartRateInputDialog((heartRate) -> {
                            int caloriesBurned = calculateCaloriesWithHeartRate(distance, duration, userWeight, heartRate, sessionManager.getUserAge());
                            float heartRateAvg = heartRate > 0 ? heartRate : -1;
                            requireActivity().runOnUiThread(() -> {
                                showMeasurementResults(distance, minutes, seconds, routePoints.size(), caloriesBurned, userId, heartRateAvg);
                                sharedViewModel.requestSwitchToAfterTab();
                                KcalRequest request = new KcalRequest(userId, distance, duration, userWeight, routeStr.toString());
                                viewModel.measureAndSave(request);
                            });
                        });
                    }
                });
                lastElapsedMinutes = minutes;
                lastElapsedSeconds = seconds;
            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void drawRouteOrMarker() {
        if (mapView == null) return; // Safe when mapView is destroyed
        if (routePoints.size() > 1) {
            if (polyline == null) {
                polyline = new Polyline();
                polyline.getOutlinePaint().setColor(getResources().getColor(android.R.color.holo_blue_dark, null));
                polyline.getOutlinePaint().setStrokeWidth(8f);
                mapView.getOverlays().add(polyline);
            }
            polyline.setPoints(routePoints);
            if (!mapView.getOverlays().contains(polyline)) {
                mapView.getOverlays().add(polyline);
            }
            mapView.getController().setCenter(routePoints.get(routePoints.size() - 1));
            // Show real-time distance while measuring
            if (isMeasuring) {
                double currentDistance = calculateDistance(routePoints);
                long elapsedMillis = System.currentTimeMillis() - startTime;
                int minutes = (int) (elapsedMillis / 60000);
                int seconds = (int) ((elapsedMillis % 60000) / 1000);
                @SuppressLint("DefaultLocale") String realTimeInfo = String.format("Distance: %.2f km | Time: %02d:%02d | Points: %d",
                    currentDistance, minutes, seconds, routePoints.size());
                tvResult.setText("Measuring...\n" + realTimeInfo);
            }
        } else if (routePoints.size() == 1) {
            if (polyline != null) {
                mapView.getOverlays().remove(polyline);
                polyline = null;
            }
            // DO NOT clear overlays completely, only add markers if not already present
            boolean hasMarker = false;
            for (Overlay overlay : mapView.getOverlays()) {
                if (overlay instanceof Marker) {
                    hasMarker = true;
                    break;
                }
            }
            if (!hasMarker) {
                Marker marker = new Marker(mapView);
                marker.setPosition(routePoints.get(0));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setTitle("Current Location");
                mapView.getOverlays().add(marker);
                mapView.getController().setCenter(routePoints.get(0));
            }
        }
        mapView.invalidate();
    }

    private double calculateDistance(List<GeoPoint> points) {
        if (points.size() < 2) {
            return 0.0;
        }
        
        double totalDistance = 0.0;
        for (int i = 1; i < points.size(); i++) {
            double segmentDistance = calculateHaversineDistance(
                points.get(i - 1).getLatitude(), points.get(i - 1).getLongitude(),
                points.get(i).getLatitude(), points.get(i).getLongitude()
            );
            totalDistance += segmentDistance;
            Log.d("KcalDebug", "Segment " + i + ": " + String.format("%.2f", segmentDistance) + "m");
        }
        
        Log.d("KcalDebug", "Total distance: " + String.format("%.2f", totalDistance) + "m (" + 
              String.format("%.3f", totalDistance / 1000.0) + "km)");
        return totalDistance / 1000.0; // Convert to kilometers
    }
    
    /**
     * Calculate distance between two points using Haversine formula
     * More accurate than simple Euclidean distance for GPS coordinates
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000; // Earth's radius in meters
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
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

    @SuppressLint("SetTextI18n")
    private void startMeasuring() {
        isMeasuring = true;
        startTime = System.currentTimeMillis();
        routePoints.clear();
        tvResult.setText("Measuring...");
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        // Always create new polyline attached to current mapView
        if (polyline != null) {
            if (mapView != null) mapView.getOverlays().remove(polyline);
            polyline = null;
        }
        if (mapView != null) {
            polyline = new Polyline();
            polyline.setPoints(routePoints);
            polyline.getOutlinePaint().setColor(getResources().getColor(android.R.color.holo_blue_dark, null));
            polyline.getOutlinePaint().setStrokeWidth(8f);
            mapView.getOverlays().add(polyline);
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 
                    LOCATION_UPDATE_INTERVAL, 
                    MIN_DISTANCE_BETWEEN_POINTS, 
                    locationListener
                );
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 
                    LOCATION_UPDATE_INTERVAL, 
                    MIN_DISTANCE_BETWEEN_POINTS, 
                    locationListener
                );
            }
            Log.d("KcalDebug", "Started measuring with GPS accuracy: " + MIN_ACCURACY + "m, min distance: " + MIN_DISTANCE_BETWEEN_POINTS + "m");
        }
        getCurrentLocation(geoPoint -> {
            lastKnownGeoPoint = geoPoint;
            if (mapView != null) mapView.getController().setCenter(geoPoint);
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
    
    private boolean isLocationAccurate(Location location) {
        return location.getAccuracy() <= MIN_ACCURACY;
    }

    private boolean isPointFarEnough(GeoPoint point1, GeoPoint point2) {
        return point1.distanceToAsDouble(point2) >= MIN_DISTANCE_BETWEEN_POINTS;
    }
    
    /**
     * Calculate calories burned locally using MET formula
     * MET (Metabolic Equivalent of Task) for running/walking
     */
    private int calculateCaloriesLocally(double distanceKm, int durationMinutes, double weightKg) {
        if (distanceKm <= 0 || durationMinutes <= 0) {
            return 0;
        }
        
        // Calculate average speed in km/h
        double speedKmh = (distanceKm / durationMinutes) * 60.0;
        
        // Determine MET value based on speed
        double met;
        if (speedKmh < 4.0) {
            met = 2.5; // Walking slowly
        } else if (speedKmh < 6.0) {
            met = 4.0; // Walking briskly
        } else if (speedKmh < 8.0) {
            met = 6.0; // Jogging
        } else if (speedKmh < 10.0) {
            met = 8.0; // Running
        } else if (speedKmh < 12.0) {
            met = 10.0; // Fast running
        } else {
            met = 12.0; // Very fast running
        }
        
        // Calculate calories: Calories = MET × Weight (kg) × Duration (hours)
        double durationHours = durationMinutes / 60.0;
        int calories = (int) (met * weightKg * durationHours);
        
        Log.d("KcalDebug", "Local calculation: distance=" + distanceKm + "km, duration=" + durationMinutes + 
              "min, speed=" + String.format("%.1f", speedKmh) + "km/h, MET=" + met + ", calories=" + calories);
        
        return calories;
    }
    
    /**
     * Show measurement results immediately after stopping
     */
    @SuppressLint("DefaultLocale")
    private void showMeasurementResults(double distance, int minutes, int seconds, int totalPoints, int caloriesBurned, String userId, float heartRateAvg) {
        @SuppressLint("DefaultLocale") String timeStr = String.format("%d min %02d sec", minutes, seconds);
        
        // Calculate speed in km/h and m/s
        String speedKmh = "--";
        String speedMs = "--";
        
        if (distance > 0 && (minutes > 0 || seconds > 0)) {
            double totalTimeHours = (minutes + seconds/60.0) / 60.0; // Convert to hours
            double speedKmPerHour = distance / totalTimeHours;
            double speedMetersPerSecond = (distance * 1000) / (minutes * 60 + seconds); // Convert km to m, min+sec to seconds
            
            speedKmh = String.format("%.2f", speedKmPerHour);
            speedMs = String.format("%.2f", speedMetersPerSecond);
        }
        
        @SuppressLint("DefaultLocale") String resultText = String.format(
                "Distance: %.3f km\nTime: %s\nSpeed: %s km/h\nSpeed: %s m/s\nPoints: %d\nKcal: %d",
                distance,
                timeStr,
                speedKmh,
                speedMs,
                totalPoints,
                caloriesBurned
        );
        
        if (minutes < 10) {
            resultText += "\nGreat start! Try to do at least 10 minutes of cardio for better health.";
        }
        
        tvResult.setText(resultText);
        Log.d("KcalDebug", "Showing results with local calories: " + caloriesBurned);

        // Send WorkoutSession to backend
        sendWorkoutSessionToBackend(distance, minutes, seconds, totalPoints, caloriesBurned, userId, heartRateAvg);
    }

    private void sendWorkoutSessionToBackend(double distance, int minutes, int seconds, int totalPoints, int caloriesBurned, String userId, float heartRateAvg) {
        Log.d("KcalDebug", "userId uploaded to backend: " + userId + ", heartRateAvg uploaded: " + heartRateAvg);
        // Note: need to save the actual startTime and endTime
        String type = "Running"; // or get from UI if there are multiple types
        ZonedDateTime nowVN = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        String endTime = nowVN.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String startTime = nowVN.minusMinutes(minutes).minusSeconds(seconds).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        float calories = (float) caloriesBurned;
        float dist = (float) distance;
        int steps = totalPoints; // or recalculate if there is a step sensor

        WorkoutSession session = new com.example.model.WorkoutSession(
            userId, type, startTime, endTime, calories, dist, steps, heartRateAvg
        );
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.saveWorkoutSession(session).enqueue(new Callback<WorkoutSession>() {
            @Override
            public void onResponse(@NonNull Call<WorkoutSession> call, @NonNull Response<WorkoutSession> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String sessionId = response.body().getId() != null ? response.body().getId() : "";
                    Log.d("KcalBeforeFragment", "Passing sessionId to sharedViewModel: " + sessionId);
                    sharedViewModel.setWorkoutResult(userId, caloriesBurned, sessionId);
                    sharedViewModel.requestSwitchToAfterTab(); // <-- chuyển sang KcalAfter
                    Log.d("KcalDebug", "WorkoutSession saved to backend: " + response.body());
                } else {
                    Log.e("KcalDebug", "Failed to save WorkoutSession: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<WorkoutSession> call, @NonNull Throwable t) {
                Log.e("KcalDebug", "Error saving WorkoutSession: " + t.getMessage());
            }
        });
    }

    // Save the entered heart rate to send to backend
    private float lastHeartRateAvg = -1;

    // Display heart rate input dialog
    private void showHeartRateInputDialog(HeartRateCallback callback) {
        requireActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Insert average heart rate");
            builder.setMessage("Insert average heart rate on your sensor device (bpm):");
            final EditText input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint("Example: 135");
            builder.setView(input);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String value = input.getText().toString();
                int heartRate = value.isEmpty() ? -1 : Integer.parseInt(value);
                lastHeartRateAvg = heartRate > 0 ? heartRate : -1;
                callback.onHeartRateEntered(heartRate);
            });
            builder.setNegativeButton("Skip", (dialog, which) -> {
                lastHeartRateAvg = -1;
                callback.onHeartRateEntered(-1);
            });
            builder.setCancelable(false);
            builder.show();
        });
    }

    // Callback interface for heart rate input dialog
    private interface HeartRateCallback {
        void onHeartRateEntered(int heartRate);
    }

    // Calorie calculator with heart rate
    private int calculateCaloriesWithHeartRate(double distanceKm, int durationMinutes, double weightKg, int heartRate, int age) {
        if (distanceKm <= 0 || durationMinutes <= 0) {
            return 0;
        }
        double durationHours = durationMinutes / 60.0;
        double calories = 0;
        boolean usedHeartRateFormula = false;
        Log.d("KcalDebug", "Input for calorie calc: distance=" + distanceKm + ", duration=" + durationMinutes + ", weight=" + weightKg + ", heartRate=" + heartRate + ", age=" + age);
        if (heartRate > 0 && age > 0) {
            calories = ((age * 0.2017) - (weightKg * 0.09036) + (heartRate * 0.6309) - 55.0969) * durationMinutes / 4.184;
            usedHeartRateFormula = true;
        }
        // If calories < 1 or negative, fallback to METs
        if (calories < 1) {
            double speedKmh = (distanceKm / durationMinutes) * 60.0;
            double met;
            if (speedKmh < 4.0) {
                met = 2.5;
            } else if (speedKmh < 6.0) {
                met = 4.0;
            } else if (speedKmh < 8.0) {
                met = 6.0;
            } else if (speedKmh < 10.0) {
                met = 8.0;
            } else if (speedKmh < 12.0) {
                met = 10.0;
            } else {
                met = 12.0;
            }
            calories = met * weightKg * durationHours;
            usedHeartRateFormula = false;
        }
        Log.d("KcalDebug", "Calories: " + calories + (usedHeartRateFormula ? " (heart rate formula)" : " (MET fallback)"));
        return (int) Math.max(calories, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}