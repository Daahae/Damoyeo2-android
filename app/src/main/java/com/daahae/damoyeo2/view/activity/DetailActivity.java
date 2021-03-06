package com.daahae.damoyeo2.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daahae.damoyeo2.R;
import com.daahae.damoyeo2.model.Building;
import com.daahae.damoyeo2.model.BuildingDetail;
import com.daahae.damoyeo2.model.MidInfo;
import com.daahae.damoyeo2.presenter.DetailPresenter;
import com.daahae.damoyeo2.view.Constant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DetailActivity
        extends AppCompatActivity
        implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private DetailPresenter presenter;

    private GoogleMap googleMap = null;
    private MapView mapView = null;
    private GoogleApiClient googleApiClient = null;
    private Marker currentMarker = null;

    private TextView txtBuildingName, txtBuildingAddress, txtBuildingTel, txtDescription, txtBuildingDistance;

    private ImageButton btnBack;

    private FloatingActionButton fabReset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        presenter = new DetailPresenter(this);

        initView();
        initListener();

        MapsInitializer.initialize(getApplicationContext());

        if(mapView != null)
            mapView.onCreate(savedInstanceState);

        presenter.startCallback();
    }

    private void initView(){

        mapView = findViewById(R.id.map_detail);
        mapView.getMapAsync(this);

        txtBuildingName = findViewById(R.id.txt_building_name_detail);
        txtBuildingAddress = findViewById(R.id.txt_building_address_datail);
        txtBuildingTel = findViewById(R.id.txt_building_tel_detail);
        txtDescription = findViewById(R.id.txt_building_description_detail);
        txtBuildingDistance = findViewById(R.id.txt_building_distance_detail);

        btnBack = findViewById(R.id.btn_back_building_transport);

        fabReset = findViewById(R.id.fab_reset);
    }

    private void initListener(){
        btnBack.setOnClickListener(this);
        fabReset.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();

        if ( googleApiClient != null && googleApiClient.isConnected())
            googleApiClient.disconnect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        if ( googleApiClient != null)
            googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

        if ( googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.stopAutoManage(this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

        if ( googleApiClient != null ) {
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);

            if ( googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                googleApiClient.disconnect();
            }
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if ( !checkLocationServicesStatus()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("위치 서비스 비활성화");
            builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" +
                    "위치 설정을 수정하십시오.");
            builder.setCancelable(true);
            builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent callGPSSettingIntent =
                            new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(callGPSSettingIntent, Constant.GPS_ENABLE_REQUEST_CODE);
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(Constant.UPDATE_INTERVAL_MS);
        locationRequest.setFastestInterval(Constant.FASTEST_UPDATE_INTERVAL_MS);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                LocationServices.FusedLocationApi
                        .requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        } else {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if ( cause ==  CAUSE_NETWORK_LOST )
            Log.e(Constant.TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED )
            Log.e(Constant.TAG,"onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, "위치정보 가져올 수 없음\n위치 퍼미션과 GPS활성 여부 확인", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(Constant.TAG, "onLocationChanged call..");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // 다이어로그 로그인 토큰 만료 로 인한 재 로그인 유도
            // TODO 스택에 쌓인 액티비티 전체 종료
            setResult(Constant.LOG_OUT);
            finish();
        }
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        setLocation(Constant.DEFAULT_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if ( hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constant.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                if ( googleApiClient == null)
                    buildGoogleApiClient();
            }
        } else {
            if ( googleApiClient == null)
                buildGoogleApiClient();
        }

        setBuildingLocation();
        showMid();
        showBuilding();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back_building_transport:
                finish();
                break;
            case R.id.fab_reset:
                showCurrentMarker();
                break;
        }
    }

    public void setBuildingInfo(){
        txtBuildingName.setText(Building.getInstance().getName());
        txtBuildingAddress.setText(Building.getInstance().getBuildingAddress());
        txtBuildingDistance.setText(String.format("%.2f",Building.getInstance().getDistance()));
    }

    public void setBuildingDetail(BuildingDetail buildingDetail){
        if(buildingDetail.getBuildingTel()!=null)
            txtBuildingTel.setText("tel. "+buildingDetail.getBuildingTel());
        else
            txtBuildingTel.setVisibility(View.GONE);
        txtDescription.setText(buildingDetail.getBuildingDescription());
    }

    public void setLocation(LatLng latLng) {
        CameraUpdate point = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f);
        googleMap.moveCamera(point);
    }

    public void setBuildingLocation() {
        if(googleMap != null) {
            LatLng latLng = new LatLng(Building.getInstance().getLatitude(), Building.getInstance().getLongitude());
            setLocation(latLng);
        }
    }

    public void showMid() {

        MarkerOptions markerOption = new MarkerOptions();
        // 이전페이지에서 중간지점 주변장소를 선택했다면
        markerOption.position(MidInfo.getInstance().getLatLng());
        markerOption.title(Constant.DEFAULT_MIDINFO_NAME);
        markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        googleMap.addMarker(markerOption);
    }

    // 이전페이지에서 선택한 장소를 표시
    public void showBuilding() {

        MarkerOptions markerOption = new MarkerOptions();
        LatLng latLng = new LatLng(Building.getInstance().getLatitude(), Building.getInstance().getLongitude());
        markerOption.position(latLng);
        markerOption.title(Building.getInstance().getName());
        markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentMarker = googleMap.addMarker(markerOption);
        currentMarker.showInfoWindow();
    }

    public void showCurrentMarker() {

        CameraUpdate point = CameraUpdateFactory.newLatLngZoom(Constant.DEFAULT_LOCATION, 15.0f);
        if(currentMarker != null)
            point = CameraUpdateFactory.newLatLngZoom(currentMarker.getPosition(), 15.0f);
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            googleMap.animateCamera(point);
        else
            googleMap.moveCamera(point);
    }
}
