package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.retrofit.BaseRetrofitActivity;
import com.example.test.retrofit.RetrofitInterface;
import com.example.test.retrofit.data.CoordinateData;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.app.Service.START_STICKY;
import static com.example.test.retrofit.BaseRetrofitActivity.retrofitInterface;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button start, end;
    private TextView result;
    LocationManager lm;
    private Handler handlerUpdateLocation;
    private static double lati = 0.0;
    private static double longi = 0.0;
    private String coordType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.startBtn);
        end = findViewById(R.id.endBtn);
        result = findViewById(R.id.result);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        TimeStart timeStart = new TimeStart();

        start.setOnClickListener(v -> {
            coordType = "start";
            sendGPS();
            if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions( MainActivity.this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION }, 0 );
            }
            else {
                coordType = "ongoing";
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                String provider = location.getProvider();
                longi = location.getLongitude();
                lati = location.getLatitude();
                double altitude = location.getAltitude();
                if (location != null) {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeStart.three(), 0, gpsLocationListener);
                    result.setText("위치정보 : " + provider + "\n" + "위도 : " + longi + "\n" + "경도 : " + lati + "\n" + "고도 : " + altitude);
                }
                else {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, gpsLocationListener);
                    Log.d(TAG, "realTime " + longi + lati);
                }
            }
        });

        end.setOnClickListener(v -> {
            coordType = "finish";
            try {
                timeStart.second.cancel();
            }
            catch (Exception e) {

            }
            lm.removeUpdates(gpsLocationListener);
            sendGPS();
        });
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();
            try {
                Log.d(TAG, "onLocationChanged: " + geocoder.getFromLocation(latitude, longitude, 1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        public void onProviderEnabled(String provider) {

        }
        public void onProviderDisabled(String provider) {

        }
    };

    class TimeStart {
        TimerTask second;
        final Handler handler = new Handler();

        public long three() {
            final int[] timer_sec = {0};
            int count = 0;
            second = new TimerTask() {
                @Override
                public void run() {
                    Update();
                    timer_sec[0]++;
                }
            };
            Timer timer = new Timer();
            timer.schedule(second, 0, 5000);
            return 0;
        }

        protected void Update() {
            Runnable updater = new Runnable() {
                public void run() {
                    sendGPS();
                    Toast.makeText(MainActivity.this, "" + lati, Toast.LENGTH_SHORT).show();
                }
            };
            handler.post(updater);
        }
    }

    private void sendGPS() {
            BaseRetrofitActivity.retrofitURL();
            CoordinateData coordinateData = new CoordinateData(2, lati, longi, coordType);
            Call<CoordinateData> call = retrofitInterface.coordinate(coordinateData);
            call.enqueue(new Callback<CoordinateData>() {
                @Override
                public void onResponse(Call<CoordinateData> call, Response<CoordinateData> response) {
                    if (response.isSuccessful()) {
                        CoordinateData coordinateData = response.body();
                        Boolean status = coordinateData.getStatus();

                        if (status) {
                            Log.d(TAG, "onResponse: " + "성공");
                        }
                        else {
                            String message = coordinateData.getMessage();
                            Log.d(TAG, "onResponse: " + message);
                        }
                    }
                }

                @Override
                public void onFailure(Call<CoordinateData> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + "실패");
                }
            });
    }
}
