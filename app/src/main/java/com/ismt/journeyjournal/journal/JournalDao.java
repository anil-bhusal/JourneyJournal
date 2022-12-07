package com.ismt.journeyjournal.journal;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/* This is a JournalDAOs which is responsible for specifying the database access methods.*/
@Dao
public interface JournalDao {

    @Query("select * from journal ORDER BY id DESC")
    List<Journal> getAllJournal();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertJournal(Journal journal);

/*    @Update
   // @Query("update table journal")
    void updateJournal(Journal journal);*/

    @Delete
    void deleteJournal(Journal journal);
}
