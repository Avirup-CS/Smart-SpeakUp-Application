package com.example.registration_login_module;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import java.util.List;

public interface DictionaryApi {

    // Note: Instead of suspend, use Call for Retrofit in this case
    @GET("en/{word}")
    Call<List<WordResult>> getMeaning(@Path("word") String word);
}
