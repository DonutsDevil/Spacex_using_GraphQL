package com.example.leanix.Model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;
import java.util.Objects;

@Entity
public class Launch{
    // Launch List Cell VARS
    private  String smallMissionPatchImageURL;
    private  String shortMissionName;

    // Launch Details VARS
    private  String description;
    private  boolean wasLaunchSuccessful;
    private  List<String> flickerImageURLs;

    private  String siteName;
    private  String rocketName;
    private  String longSiteName;
    private int isBookmark;

    // Used in both Launch List cell and Launch Details
    private  String launchDate;
    @PrimaryKey
    private  int launchId;

    @Ignore
    private String errorMessageWhileFetchingAPI;

    public Launch(String smallMissionPatchImageURL, String shortMissionName,
                  String description, boolean wasLaunchSuccessful,
                  List<String> flickerImageURLs, String siteName,
                  String rocketName, String launchDate,int launchId,
                  String longSiteName)
    {
        this.smallMissionPatchImageURL = smallMissionPatchImageURL;
        this.shortMissionName = shortMissionName;
        this.description = description;
        this.wasLaunchSuccessful = wasLaunchSuccessful;
        this.flickerImageURLs = flickerImageURLs;
        this.siteName = siteName;
        this.rocketName = rocketName;
        this.launchDate = launchDate;
        this.launchId = launchId;
        this.longSiteName = longSiteName;
    }

    public Launch(String errorMessage) {
        errorMessageWhileFetchingAPI = errorMessage;
    }

    public Launch() {
        // Require Empty Constructor for Room Database
    }

    // GETTERS
    public String getSmallMissionPatchImageURL() {
        return smallMissionPatchImageURL;
    }

    public String getLongSiteName() {
        return longSiteName;
    }

    public String getShortMissionName() {
        return shortMissionName;
    }

    public String getDescription() {
        return description;
    }

    public boolean getWasLaunchSuccessful() {
        return wasLaunchSuccessful;
    }

    public List<String> getFlickerImageURLs() {
        return flickerImageURLs;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getRocketName() {
        return rocketName;
    }

    public String getLaunchDate() {
        return launchDate;
    }

    public int getLaunchId() {
        return launchId;
    }

    public int getIsBookmark() {
        return isBookmark;
    }

    public String getErrorMessageWhileFetchingAPI() {
        return errorMessageWhileFetchingAPI;
    }

    // SETTERS
    public void setIsBookmark(int value) {
        this.isBookmark = value;
    }

    public void setSmallMissionPatchImageURL(String smallMissionPatchImageURL) {
        this.smallMissionPatchImageURL = smallMissionPatchImageURL;
    }

    public void setShortMissionName(String shortMissionName) {
        this.shortMissionName = shortMissionName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWasLaunchSuccessful(boolean wasLaunchSuccessful) {
        this.wasLaunchSuccessful = wasLaunchSuccessful;
    }

    public void setFlickerImageURLs(List<String> flickerImageURLs) {
        this.flickerImageURLs = flickerImageURLs;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setRocketName(String rocketName) {
        this.rocketName = rocketName;
    }

    public void setLongSiteName(String longSiteName) {
        this.longSiteName = longSiteName;
    }

    public void setLaunchDate(String launchDate) {
        this.launchDate = launchDate;
    }

    public void setLaunchId(int launchId) {
        this.launchId = launchId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Launch launch = (Launch) o;
        return launchId == launch.launchId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(launchId);
    }
}
