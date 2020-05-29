package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener,SearchView.OnQueryTextListener,OnClickListener {

    private HashMap<String,ArrayList<String>> countrytoCity =new HashMap<>();

    ArrayList<String> city = new ArrayList<>();
    ProgressDialog progressDoalog;
    AppLocationService appLocationService;
    String cityW="";
    // Declare Variables
    ListView list;
    ListViewAdapter adapter;
    SearchView editsearch;

    TextView emptyView,address,updated_at,status,temp,temp_min,temp_max,sunrise,sunset,humidity,wind,pressure;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        appLocationService = new AppLocationService(
                MainActivity.this,MainActivity.this);
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();

        populateAdapter();
        getLatLon();
        getWeatherInfo("Montreal");

    }

    public  ArrayList<String> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        System.out.println("here"+gson.fromJson(json, type));
        return gson.fromJson(json, type);
    }


    public void initView()
    {
        updated_at = (TextView) findViewById(R.id.updated_at);
        status = (TextView) findViewById(R.id.status);

        temp = (TextView) findViewById(R.id.temp);

        temp_min = (TextView) findViewById(R.id.temp_min);

        temp_max = (TextView) findViewById(R.id.temp_max);

        sunrise = (TextView) findViewById(R.id.sunrise);

        sunset = (TextView) findViewById(R.id.sunset);

        humidity = (TextView) findViewById(R.id.humidity);

        wind = (TextView) findViewById(R.id.wind);
        pressure = (TextView) findViewById(R.id.pressure);


        address = (TextView) findViewById(R.id.address);


        // Pass results to ListViewAdapter Class

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);

    }


    public void populateAdapter () {
        city=getArrayList("cityList");
        list = (ListView) findViewById(R.id.listview);
        list.setVisibility(View.INVISIBLE);

        adapter = new ListViewAdapter(this, city,MainActivity.this);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

    }




    public void getWeatherInfo (String cityW) {

        progressDoalog.setMessage("Loading....");
        progressDoalog.show();
        GetData service = RetrofitClientInstance.getRetrofitInstance().create(GetData.class);
        Call<WeatherPojo> call = service.getWeather(cityW);
        call.enqueue(new Callback<WeatherPojo>() {
            @Override
            public void onResponse(Call<WeatherPojo> call, Response<WeatherPojo> response) {

                progressDoalog.dismiss();
                updated_at.setText(doubleToLong(response.body().getDt()));
                status.setText(response.body().getWeather().get(0).getMain());
                temp.setText(response.body().getMain().getTemp().toString()+"°C");

                temp_max.setText(response.body().getMain().getTempMax().toString()+"°C");

                temp_min.setText(response.body().getMain().getTempMin().toString()+"°C");

                sunset.setText(doubleToLongWithoutDate(response.body().getSys().getSunset()));

                sunrise.setText(doubleToLongWithoutDate(response.body().getSys().getSunrise()));


                humidity.setText(response.body().getMain().getHumidity().toString());

                wind.setText(response.body().getWind().getSpeed().toString());

                address.setText(response.body().getName()+" ,"+response.body().getSys().getCountry());
                pressure.setText(response.body().getMain().getPressure().toString());
            }

            @Override
            public void onFailure(Call<WeatherPojo> call, Throwable t) {
                progressDoalog.dismiss();
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                System.out.println("here" + t.getMessage());
            }
        });
    }


    public String doubleToLong (Double d) {
        return new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(new Double(d).longValue()*1000));
    }

    public String doubleToLongWithoutDate (Double d) {
        return new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(new Double(d).longValue()*1000));
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


    SearchView searchView;
    MenuItem  searchMenuItem;


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        //getWeatherInfo(item);
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        list.setVisibility(View.VISIBLE);
        adapter.filter(text);
        return false;
    }

    private void handelListItemClick(String city) {
        // close search view if its visible
        if (searchView.isShown()) {
            searchMenuItem.collapseActionView();
            searchView.setQuery("", false);
        }
        getWeatherInfo(city);
    }

    @Override
    public void onItemClick(String city){
        getWeatherInfo(city);
        list.setVisibility(View.INVISIBLE);
    }

}
