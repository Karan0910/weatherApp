package com.example.weatherapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetData {
    @GET("weather?&units=metric&appid=8118ed6ee68db2debfaaa5a44c832918")
    Call<WeatherPojo> getWeather(@Query("q") String q);

}
