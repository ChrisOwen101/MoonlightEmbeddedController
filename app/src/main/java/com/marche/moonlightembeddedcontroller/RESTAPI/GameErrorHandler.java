package com.marche.moonlightembeddedcontroller.RESTAPI;

import android.util.Log;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
class GameErrorHandler implements ErrorHandler {

    @Override
    public Throwable handleError(RetrofitError cause) {
        Response r = cause.getResponse();

        if(r != null){
            if (r.getStatus() == 101) {
                Log.e("REST API ERROR", "INVALID API KEY");

            } else if(r.getStatus() == 101){
                Log.e("REST API ERROR", "OBJECT NOT FOUND");

            } else if(r.getStatus() == 102){
                Log.e("REST API ERROR", "ERROR IN URL FORMAT");

            } else if(r.getStatus() == 103){
                Log.e("REST API ERROR", "JSONP FORMAT REQUIRE JSON_CALLBACK ARGUMENT");

            } else if(r.getStatus() == 104){
                Log.e("REST API ERROR", "FILTER ERROR");

            } else if(r.getStatus() == 105){
                Log.e("REST API ERROR", "SUBSCRIBER ONLY VIDEO IS FOR SUBSCRIBERS ONLY");

            }
        }


        return cause;
    }
}

