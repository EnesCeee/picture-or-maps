package com.enesceylan.LocationOrPictureRecorder.view;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.enesceylan.LocationOrPictureRecorder.R;
import com.enesceylan.LocationOrPictureRecorder.model.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    SQLiteDatabase database;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToMain=new Intent(this, MapsMainActivity.class);
        startActivity(intentToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        Intent intent=getIntent();
        String info=intent.getStringExtra("info");

        if(info.matches("new")){

            locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener= new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    SharedPreferences sharedPreferences=MapsActivity.this.getSharedPreferences("com.enesceylan.travelbook",MODE_PRIVATE);
                    boolean trackBoolean=sharedPreferences.getBoolean("trackBoolean",false);

                    if(!trackBoolean){//trackBoolean==false
                        LatLng userLocation=new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                        sharedPreferences.edit().putBoolean("trackBoolean",true).apply();
                    }

                }
            };

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                //permissons
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

            } else {
                //location
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location lastLocation=locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                if(lastLocation!=null){
                    LatLng userLastLocation=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation,15));
                }
            }

        }else{
            //sqlite data && intent data
            mMap.clear();
            Place place=(Place) intent.getSerializableExtra("place");
            LatLng latLng=new LatLng(place.latitude,place.longitude);
            String placeName=place.name;

            mMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults.length>0){
            if(requestCode==1){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                    Intent intent=getIntent();
                    String info=intent.getStringExtra("info");
                    if(info.matches("new")){
                        Location lastLocation=locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                        if(lastLocation!=null){
                            LatLng userLastLocation=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation,15));
                        }

                    }else{
                        //Sqlite data
                        mMap.clear();
                        Place place=(Place) intent.getSerializableExtra("place");
                        LatLng latLng=new LatLng(place.latitude,place.longitude);
                        String placeName=place.name;

                        mMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    }
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {


            Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());

            String address="";
            try {
                List<Address> addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                if(addressList!=null&&addressList.size()>0){
                    if(addressList.get(0).getThoroughfare()!=null){
                        address+=addressList.get(0).getThoroughfare();

                        if(addressList.get(0).getSubThoroughfare()!=null){
                            address+=addressList.get(0).getSubThoroughfare();
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            mMap.clear();


            double latitude=latLng.latitude;
            double longitude=latLng.latitude;

            final Place place=new Place(address,latitude,longitude);

            AlertDialog.Builder alertDialog=new AlertDialog.Builder(MapsActivity.this);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Are you sure");
            alertDialog.setMessage(place.name);
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        database=MapsActivity.this.openOrCreateDatabase("Travel",MODE_PRIVATE,null);
                        database.execSQL("CREATE TABLE IF NOT EXISTS travel(id INTEGER  PRIMARY KEY,name VARCHAR,latitude VARCHAR,longitude VARCHAR)");
                        String sqlString="INSERT INTO travel(name,latitude,longitude)VALUES(?,?,?)";
                        SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
                        sqLiteStatement.bindString(1,place.name);
                        sqLiteStatement.bindString(2,String.valueOf(place.latitude));
                        sqLiteStatement.bindString(3,String.valueOf(place.longitude));
                        sqLiteStatement.execute();
                        Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();

                    }catch (Exception e){

                    }

                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(),"Canceled!",Toast.LENGTH_LONG).show();
                }
            });
            alertDialog.show();


    }
}