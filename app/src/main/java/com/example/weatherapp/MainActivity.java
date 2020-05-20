package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private HashMap<String,ArrayList<String>> countrytoCity =new HashMap<>();
    ArrayList<String> countryList = new ArrayList<>();
    ArrayList<String> city = new ArrayList<>();
    ProgressDialog progressDoalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();

        readCitiesList();

        Location gpsLocation = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            double latitude = gpsLocation.getLatitude();
            double longitude = gpsLocation.getLongitude();
            String result = "Latitude: " + gpsLocation.getLatitude() +
                    " Longitude: " + gpsLocation.getLongitude();
            tvAddress.setText(result);

        Utils.getCityFromLocation();

        /*Create handle for the RetrofitInstance interface*/
        GetData service = RetrofitClientInstance.getRetrofitInstance().create(GetData.class);
        Call<WeatherPojo> call = service.getWeather("dhaka,bd");
        call.enqueue(new Callback<WeatherPojo>() {
            @Override
            public void onResponse(Call<WeatherPojo> call, Response<WeatherPojo> response) {
                progressDoalog.dismiss();

                System.out.println("here"+response.body().getWind().getSpeed().toString());
            }

            @Override
            public void onFailure(Call<WeatherPojo> call, Throwable t) {
                progressDoalog.dismiss();
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                System.out.println("here"+t.getMessage());
            }
        });
    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(List<WeatherPojo> weatherPojo) {

    }

    public String loadJSONFromAsset() {
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



    public void readCitiesList() {


        String[] isoCountryCodes = Locale.getISOCountries();
        for (String countryCode : isoCountryCodes) {
            Locale locale = new Locale("", countryCode);
            countryList.add(locale.getDisplayName());
            System.out.println(locale.getDisplayName());
        }

        try {

            for (String countryName : countryList) {

                JSONObject obj = new JSONObject(loadJSONFromAsset());
                countryName= countryName.replace("&","and");
                JSONArray countryListArray=null;
                if(obj.has(countryName)) {
                 countryListArray = obj.getJSONArray(countryName);

                ArrayList<String> list =new ArrayList<>();
                for (int i = 0; i < countryListArray.length(); i++) {
                    String cityName = countryListArray.getString(i);
                    Log.d("Details-->", cityName);
                    city.add(cityName);
                    list.add(cityName);

                }
                countrytoCity.put(countryName,list);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}
