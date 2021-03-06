package com.example.fieldnotes.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.fieldnotes.java.Picture;

import java.util.List;

/**
 * Data access object for database interaction with <code>Picture</code> objects. Used to map
 * SQL queries to Java methods for easier access within other packages.
 *
 * @author Tyler Seidel (2019)
 */

@Dao
public interface PictureDao {

    @Insert()
    void insert(Picture picture);

    @Query("DELETE FROM pictures_table WHERE parent_stop_id = :parent_unix_time")
    void deleteAllByStop(long parent_unix_time);

    @Query("DELETE FROM pictures_table WHERE picture_id = :unix_time")
    void delete(long unix_time);

    @Query("SELECT * FROM pictures_table WHERE parent_stop_id = :parent_unix_time")
    LiveData<List<Picture>> getPicturesByParentUnixTime(long parent_unix_time);

    @Query("SELECT * FROM pictures_table WHERE parent_stop_id = :parent_unix_time")
    List<Picture> getPicturesByParentUnixTimeSynchronously(long parent_unix_time);

    @Query("SELECT * FROM pictures_table")
    LiveData<List<Picture>> getAllPictures();

    @Query("UPDATE pictures_table SET parent_stop_id=:parent_unix_time, file_path=:filepath, caption=:caption WHERE picture_id=:unix_time")
    void updatePicture(long unix_time, long parent_unix_time, String filepath, String caption);

}
