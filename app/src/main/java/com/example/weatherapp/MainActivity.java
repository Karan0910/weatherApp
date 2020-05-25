package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;
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


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private HashMap<String,ArrayList<String>> countrytoCity =new HashMap<>();
    ArrayList<String> countryList = new ArrayList<>();
    ArrayList<String> city = new ArrayList<>();
    ProgressDialog progressDoalog;
    AppLocationService appLocationService;
    String cityW="";
    ArrayAdapter adapter;
    ListView listView;
    TextView emptyView,address,updated_at,status,temp,temp_min,temp_max,sunrise,sunset,humidity,wind,pressure;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        appLocationService = new AppLocationService(
                MainActivity.this,MainActivity.this);
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();

        readCitiesList();
        getLatLon();
        getWeatherInfo(cityW);
        populateAdapter ();
    }

    public void initView()
    {
        spinner = (Spinner) findViewById(R.id.spinner);
        address = (TextView) findViewById(R.id.address);
        address = (TextView) findViewById(R.id.address);

        address = (TextView) findViewById(R.id.address);

        address = (TextView) findViewById(R.id.address);

        address = (TextView) findViewById(R.id.address);

        address = (TextView) findViewById(R.id.address);

        address = (TextView) findViewById(R.id.address);

        address = (TextView) findViewById(R.id.address);

        address = (TextView) findViewById(R.id.address);

        address = (TextView) findViewById(R.id.address);

        address = (TextView) findViewById(R.id.address);
    }


    public void populateAdapter () {

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, city);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }




    public void getWeatherInfo (String cityW) {
        GetData service = RetrofitClientInstance.getRetrofitInstance().create(GetData.class);
        Call<WeatherPojo> call = service.getWeather(cityW);
        call.enqueue(new Callback<WeatherPojo>() {
            @Override
            public void onResponse(Call<WeatherPojo> call, Response<WeatherPojo> response) {
                progressDoalog.dismiss();


            }

            @Override
            public void onFailure(Call<WeatherPojo> call, Throwable t) {
                progressDoalog.dismiss();
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                System.out.println("here" + t.getMessage());
            }
        });
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


    public void readCitiesList () {


            String[] isoCountryCodes = Locale.getISOCountries();
            for (String countryCode : isoCountryCodes) {
                Locale locale = new Locale("", countryCode);
                countryList.add(locale.getDisplayName());
                System.out.println(locale.getDisplayName());
            }

            try {

                for (String countryName : countryList) {

                    JSONObject obj = new JSONObject(loadJSONFromAsset());
                    countryName = countryName.replace("&", "and");
                    JSONArray countryListArray = null;
                    if (obj.has(countryName)) {
                        countryListArray = obj.getJSONArray(countryName);

                        ArrayList<String> list = new ArrayList<>();
                        for (int i = 0; i < countryListArray.length(); i++) {
                            String cityName = countryListArray.getString(i);
                            Log.d("Details-->", cityName);
                            city.add(cityName);
                            list.add(cityName);

                        }
                        countrytoCity.put(countryName, list);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    public void getLatLon() {
        Location gpsLocation = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            double latitude = gpsLocation.getLatitude();
            double longitude = gpsLocation.getLongitude();

            cityW=Utils.getCityFromLocation(latitude,longitude,MainActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppLocationService.MY_PERMISSIONS_REQUEST_LOCATION : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        getLatLon();
                    }

                }
            }
                return;
            }

        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainsearch, menu);
        MenuItem mSearch = menu.findItem(R.id.appSearchBar);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        getWeatherInfo(item);
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
