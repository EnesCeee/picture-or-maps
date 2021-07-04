package com.enesceylan.LocationOrPictureRecorder.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.enesceylan.LocationOrPictureRecorder.R;
import com.enesceylan.LocationOrPictureRecorder.adapter.CustomAdapter;
import com.enesceylan.LocationOrPictureRecorder.model.Place;

import java.util.ArrayList;

public class MapsMainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Place> placeArrayList=new ArrayList<>();
    CustomAdapter customAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=findViewById(R.id.listView);
        getData();

    }

    public void getData(){

        customAdapter=new CustomAdapter(this,placeArrayList);
        try {
            SQLiteDatabase database=this.openOrCreateDatabase("Travel",MODE_PRIVATE,null);

            Cursor cursor=database.rawQuery("SELECT*FROM travel",null);
            int nameIx=cursor.getColumnIndex("name");
            int latitudeIx=cursor.getColumnIndex("latitude");
            int longitudeIx=cursor.getColumnIndex("longitude");

            while (cursor.moveToNext()){
                String nameFromDatabase=cursor.getString(nameIx);
                String latitudeFromDatabase=cursor.getString(latitudeIx);
                String longitudeFromDatabase=cursor.getString(longitudeIx);

                Double latitude=Double.parseDouble(latitudeFromDatabase);
                Double longitude=Double.parseDouble(longitudeFromDatabase);

                Place place=new Place(nameFromDatabase,latitude,longitude);

                placeArrayList.add(place);

            }

            customAdapter.notifyDataSetChanged();

            cursor.close();


        }catch (Exception e){
            e.printStackTrace();

        }


        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MapsMainActivity.this,MapsActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("place",placeArrayList.get(position));
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.add_place,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.add_place_item){
            Intent intent=new Intent(MapsMainActivity.this,MapsActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}