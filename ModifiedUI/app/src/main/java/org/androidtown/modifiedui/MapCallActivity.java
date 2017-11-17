package org.androidtown.modifiedui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

public class MapCallActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap map;

    private static final String TAG = "";
    private long btnPressTime = 0;
    private Toast toast;
    private String tel = "tel:";

    String locationX;
    String locationY;
    double mLat;
    double mLng;

//    public static MapCallActivity newInstance() {
//        MapCallActivity f = new MapCallActivity();
//        return f;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_call);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String carer = intent.getStringExtra("carer");
        tel = tel + getPhoneNumberText(carer);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "GoogleMap is ready.");
                map = googleMap;
            }
        });
        try{
            MapsInitializer.initialize(this);
        }catch (Exception e){
            e.printStackTrace();
        }

        //음성합성 : 보호자에게 전화를 걸려면 화면 하단을 두번 터치해주세요.
        Button callButton = (Button) findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                //if (isGrantStorage) {
                    if (System.currentTimeMillis() > btnPressTime + 1000) {
                        btnPressTime = System.currentTimeMillis();
                        return;
                    }
                    if (System.currentTimeMillis() <= btnPressTime + 1000) {
                        Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse(tel));
                        startActivity(intent2);
                    }
                //}
            }
        });

        startLocationService();
        requestMyLocation();

//        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }

    public String getPhoneNumberText(String number) {
        String phoneText = "";

        number = number.replace("-", "");

        int length = number.length();

        if (number.length() >= 10) {
            phoneText = number.substring(0, 3) + "-"
                    + number.substring(3, length-4) + "-"
                    + number.substring(length-4, length);
        }
        return phoneText;
    }

    private void startLocationService(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        MapCallActivity.GPSListener gpsListener = new MapCallActivity.GPSListener();
        long minTime = 2000;
        float minDistance = 0;

        try{
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime, minDistance, gpsListener);

            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime, minDistance, gpsListener);

            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastLocation !=null){
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();

                locationX = String.valueOf(latitude);
                locationY = String.valueOf(longitude);
                mLat = Double.parseDouble(locationX);
                mLng = Double.parseDouble(locationY);


            }
        }catch (SecurityException ex){
            ex.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "위치확인이 시작되었습니다. 로그를 확인하세요.", Toast.LENGTH_SHORT).show();
    }

    private void showCurrentLocation(Location location){  //마커 표시
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint,18));
        //showMyLocationMarker(location);
    }

    private void requestMyLocation(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try{
            long minTime = 2000;
            float minDistance =0;
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    minTime, minDistance, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            showCurrentLocation(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastLocation != null){
                showCurrentLocation(lastLocation);
            }
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    minTime, minDistance, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            showCurrentLocation(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
        }catch (SecurityException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        //지도타입 - 일반
        this.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //기본위치 설정
        LatLng position = new LatLng(mLat,mLng);
        //화면중앙의 위치와 카메라 줌 비율
        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(position,20));

    }

    //마커, 원 추가
    public void onAddMarker(){
        LatLng position = new LatLng(mLat,mLng);

        //나의 위치 마커
        MarkerOptions mymarker = new MarkerOptions()
                .position(position);   //마커위치

        // 반경
        CircleOptions circle1KM = new CircleOptions().center(position) //원점
                .radius(5)      //반지름 단위 : m
                .strokeWidth(0f)  //선너비 0f : 선없음
                .fillColor(Color.parseColor("#880000ff")); //배경색

        //마커추가
        this.map.addMarker(mymarker);

        //원추가
        this.map.addCircle(circle1KM);

    }

    private class GPSListener implements LocationListener { //위치 리스너

        @Override
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

//            Toast toast = Toast.makeText(getApplicationContext(),"Latitude : " + latitude + " , longitude : " + longitude , Toast.LENGTH_LONG);
//            toast.show();
            String msg = "Latitude : "+latitude+ "\nLongitude : "+longitude;
            Log.i("GPSListener",msg);

            locationX = String.valueOf(latitude);
            locationY = String.valueOf(longitude);
            mLat = Double.parseDouble(locationX);
            mLng = Double.parseDouble(locationY);

            onAddMarker();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
//    /**
//     * 주어진 정보를 기반으로 맛집 정보를 조회하고 지도에 표시한다.
//     * @param memberSeq 사용자 시퀀스
//     * @param latLng 위도, 경도 객체
//     * @param distance 거리
//     * @param userLatLng 사용자 현재 위도, 경도 객체
//     */
//    Response.Listener<String> responseListener = new Response.Listener<String>() {
//        @Override
//        public void onResponse(String response) {
//            try{
//                JSONObject jsonResponse = new JSONObject(response);
//                boolean success = jsonResponse.getBoolean("success");
//                if(success){
//                    String locationX = jsonResponse.getString("locationX");
//                    String locationY = jsonResponse.getString("locationY");
//                    String type = jsonResponse.getString("type");
//
////                    Intent intent = new Intent(LoginActivity.this, MapCallActivity.class);
////                    intent.putExtra("userID", userID);
////                    intent.putExtra("carer", carer);
////                    LoginActivity.this.startActivity(intent);
//
//                }else{
//                    AlertDialog.Builder builder = new AlertDialog.Builder(MapCallActivity.this);
//                    builder.setMessage("로그인에 실패하였습니다.")
//                            .setNegativeButton("다시 시도", null)
//                            .create()
//                            .show();
//                }
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    };

}