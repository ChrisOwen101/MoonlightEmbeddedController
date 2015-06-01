package com.marche.moonlightembeddedcontroller.RESTAPI;

import com.marche.moonlightembeddedcontroller.POJO.Container;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public interface GamesAPIService {

    @GET("/search?api_key=2625851d1a8f443018012f69e58db1474c62bb1d&format=json&query={query}&resources=game&limit=5")
    void searchForGame(@Path("query") String query, Callback<Container> cb);

}
