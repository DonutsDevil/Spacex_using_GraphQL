package com.example.leanix.Repositry;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.leanix.Model.Launch;
import com.example.leanix.Repositry.Room.AppDatabase;
import com.example.leanix.Repositry.Room.BookmarkDAO;
import com.example.leanix.Utils.Utils;


import java.util.List;

public class Storage{
    private static final String TAG = "Storage";
    private static Storage mInstance;
    private static AppDatabase appDatabase;
    private Storage(){}

    public static Storage getInstance(Application application) {
        if (mInstance == null) {
            mInstance = new Storage();
            appDatabase = AppDatabase.getInstance(application.getApplicationContext());
        }
        return mInstance;
    }

    public LiveData<List<Launch>> getLaunchData() {
        LiveData<List<Launch>> launchList = Server.getInstance().fetchLaunchData();
        return launchList;
    }

    public LiveData<List<Launch>> getBookmarkData() {
        LiveData<List<Launch>> bookmarkList;
        // Returns null if no result found matching the query
        bookmarkList = appDatabase
                .bookmarkDAO()
                .loadAllBookMarks(Utils.BOOKMARK_KEY);
        return bookmarkList;
    }

    public void insertBookMark(Launch...launch) {
       new InsertAsyncTask(appDatabase.bookmarkDAO()).execute(launch);
    }

    public void deleteBookMark(Launch launch) {
        new DeleteAsyncTask(appDatabase.bookmarkDAO()).execute(launch);
    }

    private static class InsertAsyncTask extends AsyncTask<Launch, Void, Boolean> {
        private final BookmarkDAO bookmarkDAO;

        private InsertAsyncTask(BookmarkDAO bookmarkDAO) {
            this.bookmarkDAO = bookmarkDAO;
        }

        @Override
        protected Boolean doInBackground(Launch... launches) {
            bookmarkDAO.insert(launches[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Launch, Void, Void> {
        private final BookmarkDAO bookmarkDAO;

        private DeleteAsyncTask(BookmarkDAO bookmarkDAO) {
            this.bookmarkDAO = bookmarkDAO;
        }

        @Override
        protected Void doInBackground(Launch... launches) {
            bookmarkDAO.delete(launches[0]);
            return null;
        }
    }

}
