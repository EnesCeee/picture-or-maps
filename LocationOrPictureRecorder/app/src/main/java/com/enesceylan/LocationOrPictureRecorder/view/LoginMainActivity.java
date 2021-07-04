package com.enesceylan.LocationOrPictureRecorder.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.enesceylan.LocationOrPictureRecorder.R;

public class LoginMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
    }
    public void goToArtMain(View view){
        Intent intent=new Intent(LoginMainActivity.this,ArtMainActivity.class);
        startActivity(intent);


    }
    public void goToMapsMain(View view){
        Intent intent=new Intent(LoginMainActivity.this,MapsMainActivity.class);
        startActivity(intent);
    }
}