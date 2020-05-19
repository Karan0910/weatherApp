package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

    ProgressDialog progressDoalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        readCitiesList();

        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();


        ArrayList<String> list=new ArrayList<String>();

        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales) {

            Locale obj = new Locale("", countryCode);

            System.out.println("Country Name = " );
            list.add(obj.getDisplayCountry());

        }

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

    public String readCitiesList() {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("formules");
            ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> m_li;

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Log.d("Details-->", jo_inside.getString("formule"));
                String formula_value = jo_inside.getString("formule");
                String url_value = jo_inside.getString("url");

                //Add your values in your `ArrayList` as below:
                m_li = new HashMap<String, String>();
                m_li.put("formule", formula_value);
                m_li.put("url", url_value);

                formList.add(m_li);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
