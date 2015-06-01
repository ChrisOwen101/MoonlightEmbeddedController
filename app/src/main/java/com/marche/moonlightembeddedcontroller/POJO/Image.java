package com.marche.moonlightembeddedcontroller.POJO;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("icon_url")
    @Expose
    private String iconUrl;
    @SerializedName("medium_url")
    @Expose
    private String mediumUrl;
    @SerializedName("screen_url")
    @Expose
    private String screenUrl;
    @SerializedName("small_url")
    @Expose
    private String smallUrl;
    @SerializedName("super_url")
    @Expose
    private String superUrl;
    @SerializedName("thumb_url")
    @Expose
    private String thumbUrl;
    @SerializedName("tiny_url")
    @Expose
    private String tinyUrl;

    /**
     * @return The iconUrl
     */
    public String getIconUrl() {
        return iconUrl;
    }

    /**
     * @param iconUrl The icon_url
     */
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    /**
     * @return The mediumUrl
     */
    public String getMediumUrl() {
        return mediumUrl;
    }

    /**
     * @param mediumUrl The medium_url
     */
    public void setMediumUrl(String mediumUrl) {
        this.mediumUrl = mediumUrl;
    }

    /**
     * @return The screenUrl
     */
    public String getScreenUrl() {
        return screenUrl;
    }

    /**
     * @param screenUrl The screen_url
     */
    public void setScreenUrl(String screenUrl) {
        this.screenUrl = screenUrl;
    }

    /**
     * @return The smallUrl
     */
    public String getSmallUrl() {
        return smallUrl;
    }

    /**
     * @param smallUrl The small_url
     */
    public void setSmallUrl(String smallUrl) {
        this.smallUrl = smallUrl;
    }

    /**
     * @return The superUrl
     */
    public String getSuperUrl() {
        return superUrl;
    }

    /**
     * @param superUrl The super_url
     */
    public void setSuperUrl(String superUrl) {
        this.superUrl = superUrl;
    }

    /**
     * @return The thumbUrl
     */
    public String getThumbUrl() {
        return thumbUrl;
    }

    /**
     * @param thumbUrl The thumb_url
     */
    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    /**
     * @return The tinyUrl
     */
    public String getTinyUrl() {
        return tinyUrl;
    }

    /**
     * @param tinyUrl The tiny_url
     */
    public void setTinyUrl(String tinyUrl) {
        this.tinyUrl = tinyUrl;
    }
}