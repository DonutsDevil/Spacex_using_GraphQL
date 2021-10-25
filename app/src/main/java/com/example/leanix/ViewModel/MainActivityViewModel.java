package com.example.leanix.ViewModel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.leanix.Model.Launch;
import com.example.leanix.Repositry.Storage;
import com.example.leanix.Utils.Utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private static final String TAG = "MainActivityViewModel";
    private Launch selectedLaunch;
    private MutableLiveData<List<Launch>> mLaunchesList;
    private MutableLiveData<List<Launch>> mBookmarkList;
    private final MutableLiveData<String> mListViewType = new MutableLiveData<>();
    private final Storage storage;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        storage = Storage.getInstance(application);
    }

    public LiveData<List<Launch>> getLaunchesList() {
        if (mLaunchesList == null) {
            mLaunchesList = new MutableLiveData<>();
            loadLaunchList();
        }
        return mLaunchesList;
    }

    public LiveData<List<Launch>> getBookmarkList() {
        if (mBookmarkList == null) {
            mBookmarkList = new MutableLiveData<>();
            loadBookmarkList();
        }
        return mBookmarkList;
    }

    public void insertBookmark(Launch launch){
        storage.insertBookMark(launch);
    }

    public void deleteBookmark(Launch launch) {
        storage.deleteBookMark(launch);
    }

    private void loadLaunchList() {
        storage.getLaunchData().observeForever(new Observer<List<Launch>>() {
            @Override
            public void onChanged(List<Launch> launchList) {
                mLaunchesList.setValue(launchList);
            }
        });
    }

    private void loadBookmarkList() {
        storage.getBookmarkData().observeForever(new Observer<List<Launch>>() {
            @Override
            public void onChanged(List<Launch> bookmarkList) {
                mBookmarkList.setValue(bookmarkList);
            }
        });
    }


    public void setSelectedLaunch(Launch launch) {
        selectedLaunch = launch;
    }

    public Launch getSelectedLaunch() {
        return selectedLaunch;
    }

    public void sortLaunchList(int sortID) {
        if (mLaunchesList == null || mBookmarkList == null) {
            return;
        }
        List<Launch> launchList = mLaunchesList.getValue();
        List<Launch> bookmarkList = mBookmarkList.getValue();
        if (sortID == Utils.SORT_BY_MISSION_NAME_LAUNCH_LIST) {
            if (launchList != null) {
                sortCollection(launchList,mLaunchesList, new SortByMissionName());
            }
        }
        else if(sortID == Utils.SORT_BY_LAUNCH_DATE_LAUNCH_LIST) {
            if (launchList != null) {
                sortCollection(launchList,mLaunchesList, new SortByLaunchDate());
            }
        }
        else if(sortID == Utils.SORT_BY_MISSION_NAME_BOOKMARK_LIST) {
            if (bookmarkList != null ){
                sortCollection(bookmarkList,mBookmarkList,new SortByMissionName());
            }
        }
        else if(sortID == Utils.SORT_BY_LAUNCH_DATE_BOOKMARK_LIST) {
            if (bookmarkList != null ){
                sortCollection(bookmarkList,mBookmarkList,new SortByLaunchDate());
            }
        }
    }

    public List<Launch> getSortedLaunchList(List<Launch> list, int sortID) {
        if (sortID == Utils.SORT_BY_LAUNCH_DATE_BOOKMARK_LIST) {
            Collections.sort(list,new SortByLaunchDate());
        }else if(sortID == Utils.SORT_BY_MISSION_NAME_BOOKMARK_LIST) {
            Collections.sort(list,new SortByMissionName());
        }
        return list;
    }

    private void sortCollection(List<Launch> list,MutableLiveData<List<Launch>> mutableLiveData, Comparator comparator) {
        if (comparator instanceof SortByMissionName) {
            Collections.sort(list, new SortByMissionName());
            mutableLiveData.setValue(list);
        }else if (comparator instanceof SortByLaunchDate) {
            Collections.sort(list, new SortByLaunchDate());
            mutableLiveData.setValue(list);
        }
    }

    public void setListViewType(String listViewType) {
        mListViewType.setValue(listViewType);
    }

    public LiveData<String> getListViewType() {
        return mListViewType;
    }

    private static class SortByMissionName implements Comparator<Launch> {

        @Override
        public int compare(Launch launch, Launch t1) {
            return launch.getShortMissionName().compareTo(t1.getShortMissionName());
        }
    }

    private static class SortByLaunchDate implements Comparator<Launch> {

        @Override
        public int compare(Launch launch, Launch t1) {
            Date date1 = new Date(launch.getLaunchDate());
            Date date2 = new Date(t1.getLaunchDate());
            return date1.compareTo(date2);
        }
    }
}
