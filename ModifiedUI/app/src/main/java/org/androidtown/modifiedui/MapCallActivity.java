package org.androidtown.modifiedui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapCallActivity extends AppCompatActivity {

    private static final String TAG2 = "MapCallActivity";
    SupportMapFragment mapFragment;
    GoogleMap map;
    TextView locationText;
    MarkerOptions myLocationMarker;

    private static final String TAG = "";
    private long btnPressTime = 0;
    private Toast toast;
    private String tel = "01000000000";

    //boolean isGrantStorage = grantExternalStoragePermission();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_call);



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
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(tel));
                        startActivity(intent);
                    }
                //}
            }
        });
    }

}