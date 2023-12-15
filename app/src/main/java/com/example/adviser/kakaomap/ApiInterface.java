package com.example.adviser.kakaomap;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiInterface {


    // 장소이름으로 검색
    @GET("v2/local/search/keyword.json")
    Call<CategoryResult> getSearchLocation(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("category_group_code") String categoryGroupCode,
            @Query("size") int size,            // 1 ~ 15
            @Query("page") int page,            // 1 ~ 45
            @Query("sort") String sort,         // distance 또는 accuracy (기본값: accuracy)
            @Query("x") double x,
            @Query("y") double y
    );

    // 카테고리로 검색
    @GET("v2/local/search/category.json")
    Call<CategoryResult> getSearchCategory(
            @Header("Authorization") String token,
            @Query("category_group_code") String category_group_code,
            @Query("x") double longitude,
            @Query("y") double latitude,
            @Query("radius") int radius
    );

    // 장소이름으로 특정위치기준으로 검색
    @GET("v2/local/search/keyword.json")
    Call<CategoryResult> getSearchLocationDetail(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("x") String x,
            @Query("y") String y,
            @Query("size") int size
    );

    //주소로 검색 (아직안쓰는중)
    @GET("v2/local/search/address.json")
    Call<CategoryResult> getSearchAddress(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("size") int size
    );
}
