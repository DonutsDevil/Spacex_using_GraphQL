package com.example.leanix.Repositry;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.LaunchDetailsQuery;
import com.example.leanix.Model.Launch;
import com.example.leanix.Utils.Utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Server {

    private static final String TAG = "Server";
    private static final String GRAPH_QL_API_URL ="https://api.spacex.land/graphql/";
    private static Server mInstance;
    private MutableLiveData<List<Launch>> launchList;

    private Server() {
        launchList = new MutableLiveData<>();
    }

    public static Server getInstance() {
        if(mInstance == null) {
            mInstance = new Server();
        }
        return mInstance;
    }

    public LiveData<List<Launch>> fetchLaunchData() {
        final ApolloClient apolloClient = getApolloClient();
        setResponse(apolloClient);
        return launchList;
    }

    private ApolloClient getApolloClient() {
        return ApolloClient.builder()
                .serverUrl(GRAPH_QL_API_URL)
                .build();
    }

     private void setResponse(ApolloClient apolloClient) {
        apolloClient.query(new LaunchDetailsQuery())
                .enqueue(new ApolloCall.Callback<LaunchDetailsQuery.Data>()  {
                    @Override
                    public void onResponse(@NonNull Response<LaunchDetailsQuery.Data> response) {
                        List<Launch> launchList;
                        LaunchDetailsQuery.Data data = response.getData();
                        if (data != null) {
                            List<LaunchDetailsQuery.LaunchesPast> responseList
                                    = response.getData().launchesPast();

                            launchList = getLaunchList(responseList);
                            setLaunchList(launchList);
                            Log.d(TAG, "Server Response List Size: "+launchList.size());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Log.d(TAG, "Fail to fetch JSON Response from apollo server: "+e.getLocalizedMessage());
                        List<Launch> errorObjList = new ArrayList<>();
                        errorObjList.add(new Launch(Utils.ERROR_FETCHING_API));
                        setLaunchList(errorObjList);
                    }
                });

    }

    private void setLaunchList(List<Launch> launchList) {
        this.launchList.postValue(launchList);
    }

    private List<Launch> getLaunchList(List<LaunchDetailsQuery.LaunchesPast> responseList) {
        List<Launch> launchList = new ArrayList<>();
        for(LaunchDetailsQuery.LaunchesPast launchPast : responseList) {

            String missionName = launchPast.mission_name();
            BigDecimal unixTime = (BigDecimal) launchPast.launch_date_unix();
            String idStr = launchPast.id();
            String launchDateTime = getDate(unixTime);
            int id = 0;
            if(idStr != null) {
                id = Integer.parseInt(idStr);
            }
            boolean isLaunchSuccessful = false;
            if (launchPast.launch_success() != null) {
                isLaunchSuccessful = launchPast.launch_success();
            }
            String description = launchPast.details();

            LaunchDetailsQuery.Rocket rocket = launchPast.rocket();
            String rocketName = "";

            if(rocket != null ){
                rocketName = rocket.rocket_name();
            }

            LaunchDetailsQuery.Links link = launchPast.links();
            String missionPatchImageURL = "";
            List<String> flickerImageURL = null;
            if (link != null) {
                missionPatchImageURL = link.mission_patch_small();
                flickerImageURL =  link.flickr_images();
            }

            LaunchDetailsQuery.Launch_site launchSite = launchPast.launch_site();
            String shortSiteName = launchSite.site_name();
            String longSiteName = launchSite.site_name_long();

            Launch launch = new Launch(missionPatchImageURL,missionName,
                    description,isLaunchSuccessful,flickerImageURL,
                    shortSiteName,rocketName,launchDateTime,id,longSiteName);
            launchList.add(launch);
        }
        return launchList;
    }

    private String getDate(BigDecimal unixTime) {
        final String outputFormat = "dd MMM, yyyy HH:mm ";
        long unixLong = unixTime.longValue();
        Date dateTimeObj = new Date(unixLong * 1000L);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(outputFormat);
        String dateAndTime = dateFormatter.format(dateTimeObj);
        return dateAndTime;
    }
}
