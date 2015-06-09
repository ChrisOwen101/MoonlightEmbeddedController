package com.marche.moonlightembeddedcontroller.RESTAPI;

import com.marche.moonlightembeddedcontroller.POJO.Container;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public interface GamesAPIService {

    @GET("/search")
    void searchForGame(@Query("api_key") String api_key, @Query("format") String json, @Query("query") String gameName, @Query("resources") String game , @Query("limit") int limit,Callback<Container> cb);

}
