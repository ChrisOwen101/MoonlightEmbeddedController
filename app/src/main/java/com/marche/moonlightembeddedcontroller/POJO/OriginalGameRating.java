package com.marche.moonlightembeddedcontroller.POJO;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OriginalGameRating {

    @SerializedName("api_detail_url")
    @Expose
    private String apiDetailUrl;
    @Expose
    private Integer id;
    @Expose
    private String name;

    /**
     * @return The apiDetailUrl
     */
    public String getApiDetailUrl() {
        return apiDetailUrl;
    }

    /**
     * @param apiDetailUrl The api_detail_url
     */
    public void setApiDetailUrl(String apiDetailUrl) {
        this.apiDetailUrl = apiDetailUrl;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }
}
