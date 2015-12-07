package com.xoco.foodtruckfinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xoco.foodtruckfinder.R;
import com.xoco.foodtruckfinder.fragments.FavoritesFragment;
import com.xoco.foodtruckfinder.fragments.MapFragment;
import com.xoco.foodtruckfinder.models.FoodTruck;
import com.xoco.foodtruckfinder.restful.ApiClient;
import com.xoco.foodtruckfinder.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, NavigationView.OnNavigationItemSelectedListener {


    //Saves food trucks info from server
    private ArrayList<FoodTruck> mFoodTrucks = new ArrayList<FoodTruck>();

    //Maps FoodTruck Object with Google Map Pins by Marker ID, a String
    private HashMap<String, FoodTruck> mHashMap = new HashMap<String, FoodTruck>();

    //To save current object in case "this" reserved word is used inside nested functions or callbacks
    private MainActivity self = this;

    //For the menu
    private DrawerLayout drawerLayout;

//    private View mainContent;
    private NavigationView navigationView;
    private Toolbar toolbar;

    //For the content

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get food trucks list
        ApiClient.getService().getAllFoodTrucks(new Callback<ArrayList<FoodTruck>>() {
            @Override
            public void success(ArrayList<FoodTruck> foodTrucks, Response response) {
                Log.d("Custom:MainActivity", "Request was successful");
                mFoodTrucks = foodTrucks;

                //Get map support and call in async way
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(self);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Custom:MainActivity", "Retrofit Error");
                Log.d("Custom:MainActivity", error.toString());
            }
        });

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Navigation Drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mainContent = findViewById(R.id.main_content);

        // Find our drawer view and set listener
        navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        navigationView.setNavigationItemSelectedListener(self);


    }

    public void onMapReady(GoogleMap googleMap) {


        String currentMarkerId;

        //Get map and sets location service
        googleMap.setMyLocationEnabled(true);

        //Set on information windows listener
        googleMap.setOnInfoWindowClickListener(this);

        //TODO Hard coded map center, it should be changed to User's position
        LatLng myCenter  = new LatLng(19.434372, -99.1397591);

        //Add food truck to map and saves in map table
        for (FoodTruck foodTruck : mFoodTrucks) {

            Log.d("Custom:MainActivity", foodTruck.name);
            Log.d("Custom:MainActivity", foodTruck.location.toString());

            currentMarkerId = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(foodTruck.location.lat, foodTruck.location.lng))
                            .title(foodTruck.name)
                            .snippet(foodTruck.foodType)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.fast_food_24))
            ).getId();

            Log.d("Custom:MainActivity",currentMarkerId);
            Log.d("Custom:MainActivity",foodTruck.name);

            mHashMap.put(currentMarkerId, foodTruck);

        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCenter, 15));

    }

    //Handles info window clicking
    public void onInfoWindowClick(Marker marker){

        FoodTruck selectedFoodTruck = mHashMap.get(marker.getId());
        Intent toDetailsIntent = new Intent(this, FoodtruckDetailsActivity.class);
        Bundle foodTruckInfo = new Bundle();

        //Inserts info in Bundle object before sending
        foodTruckInfo.putInt(Constants.ID, selectedFoodTruck.id);
        foodTruckInfo.putString(Constants.NAME, selectedFoodTruck.name);
        foodTruckInfo.putString(Constants.TYPE, selectedFoodTruck.foodType);
        foodTruckInfo.putFloat(Constants.RATING, selectedFoodTruck.rating);
        toDetailsIntent.putExtra(Constants.FOOD_TRUCK_INFO, foodTruckInfo);

        //Go to a more detailed view of the food truck
        startActivity(toDetailsIntent);

    }

    //TODO: Add settings functionality

    //Handles Drawer Menu navigation
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (menuItem.getItemId()) {
            case R.id.item_menu_map:
                Toast.makeText(this, "Map pressed", Toast.LENGTH_SHORT).show();
                fragment = new MapFragment();
                break;
            case R.id.item_menu_favs:
                Toast.makeText(this, "Favorites pressed", Toast.LENGTH_SHORT).show();
                fragment = new FavoritesFragment();
                break;
            case R.id.item_menu_settings:
                Toast.makeText(this, "Settings pressed", Toast.LENGTH_SHORT).show();
              fragment = new FavoritesFragment();
//                break;
        }


        fragmentManager.beginTransaction().replace(R.id.main_content_fragment, fragment).commit();

        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        return true;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


}
