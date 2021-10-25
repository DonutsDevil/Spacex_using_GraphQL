package com.example.leanix.Repositry.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.leanix.Model.Launch;

import java.util.List;
import java.util.Set;


@Dao
public interface BookmarkDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Launch ... launches);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Launch launch);

    @Delete
    void delete(Launch launch);

    @Query("select * from Launch where isBookmark = :bookmarkKey")
    LiveData<List<Launch>> loadAllBookMarks(int bookmarkKey);
}
