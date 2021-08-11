package com.example.test.retrofit;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BaseRetrofitActivity extends AppCompatActivity {
    public static RetrofitInterface retrofitInterface;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public static Context getAppContext() {
        return BaseRetrofitActivity.mContext;
    }
    public static void retrofitURL() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + "ai-rebornsoft.asuscomm.com:21601")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }
}
