package com.example.test.retrofit;

import com.example.test.retrofit.data.CoordinateData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("m/gps/coordinate")
    Call<CoordinateData> coordinate(@Body CoordinateData coordinateData);
}
