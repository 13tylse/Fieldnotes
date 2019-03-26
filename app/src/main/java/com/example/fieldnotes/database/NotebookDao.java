package com.example.fieldnotes.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.fieldnotes.java.Notebook;

import java.util.List;

/**
 * Data access object for database interaction with <code>Notebook</code> objects. Used to map
 * SQL queries to Java methods for easier access within other packages.
 *
 * @author Tyler Seidel (2019)
 */
@Dao
public interface NotebookDao {

    @Insert
    void insert(Notebook notebook);

    @Query("DELETE FROM notebooks_table")
    void deleteAll();

    @Query("DELETE FROM notebooks_table WHERE notebook_id = :notebook_unix_time")
    void deleteNotebook(long notebook_unix_time);

    @Query("SELECT * FROM notebooks_table WHERE notebook_id = :notebook_unix_time")
    Notebook selectNotebook(long notebook_unix_time);

    @Query("SELECT * FROM notebooks_table")
    LiveData<List<Notebook>> getAllNotebooks();

    @Query("UPDATE notebooks_table SET notebook_name = :name WHERE notebook_id = :unix_time")
    void updateNotebook(String name, long unix_time);
}
