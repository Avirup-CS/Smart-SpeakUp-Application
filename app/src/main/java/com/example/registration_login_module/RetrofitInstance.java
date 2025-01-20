package com.example.registration_login_module;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static final String BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/";

    // Private constructor to prevent instantiation
    private RetrofitInstance() {}

    // Get Retrofit instance
    private static Retrofit getInstance() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // Singleton DictionaryApi instance
    public static final DictionaryApi dictionaryApi = getInstance().create(DictionaryApi.class);
}
