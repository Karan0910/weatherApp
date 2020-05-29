package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {


    ArrayList<String> countryList = new ArrayList<>();
    ArrayList<String> city = new ArrayList<>();
    ProgressDialog progressDoalog;
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressDoalog = new ProgressDialog(SplashScreen.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();


        if(getArrayList("cityList")==null){

            System.out.println("here");
            new Thread( new Runnable() {
                @Override public void run()
                {
                    readCitiesList ();

                    saveArrayList(city,"cityList");
                    progressDoalog.dismiss();
                    Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }
            } ).start();
        } else {

            progressDoalog.dismiss();
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {

                    Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();

                }
            }, SPLASH_DISPLAY_LENGTH);

        }
        //System.out.println(getArrayList("cityList").size());









    }


    public void readCitiesList () {

        String[] isoCountryCodes = Locale.getISOCountries();
        for (String countryCode : isoCountryCodes) {
            Locale locale = new Locale("", countryCode);
            countryList.add(locale.getDisplayName());
        }

        try {

            for (String countryName : countryList) {

                JSONObject obj = new JSONObject(loadJSONFromAsset());
                countryName = countryName.replace("&", "and");
                JSONArray countryListArray = null;
                if (obj.has(countryName)) {
                    countryListArray = obj.getJSONArray(countryName);
                    for (int i = 0; i < countryListArray.length(); i++) {
                        String cityName = countryListArray.getString(i);
                        city.add(cityName);
                        System.out.println(cityName);

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

    public void saveArrayList(ArrayList<String> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();

        progressDoalog.dismiss();

    }

    public String loadJSONFromAsset () {
        String json = null;
        try {
            InputStream is = getAssets().open("cities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public  ArrayList<String> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        System.out.println("here"+gson.fromJson(json, type));
        return gson.fromJson(json, type);
    }


}
