package com.pim.streamingapp;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    //private static final String BASE_URL = "http://10.0.2.2:5127/";
    private static final String BASE_URL = "http://192.168.1.11:5127/";


    private static Retrofit retrofit = null;

    public static ApiService getApiService(Context context) {
        SessionManager session = new SessionManager(context);
        String token = session.getToken();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);
        if (token != null) {
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .method(original.method(), original.body());
                android.util.Log.d("JWT_TOKEN", "Enviando token apartir do interceptor: " + token);
                return chain.proceed(requestBuilder.build());
            });
        }
        Gson gson = new GsonBuilder()

                .serializeNulls()
                .create();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiService.class);
    }
}


