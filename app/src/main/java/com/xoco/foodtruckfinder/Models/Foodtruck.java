package com.xoco.foodtruckfinder.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by uliseschino on 11/26/15.
 */
public class FoodTruck {

    public int id;

    public String name;

    public float rating;

    @SerializedName("food_type")
    public String foodType;

    @SerializedName("location_object")
    public Location location;

    public class Location {

        public Double lat;

        @SerializedName("long")
        public Double lng;

        public String toString(){
            return "(" + this.lat + "," + this.lng + ")";
        }
    }




}
