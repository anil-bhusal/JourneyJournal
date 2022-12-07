package com.ismt.journeyjournal;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ismt.journeyjournal.journal.Journal;
import com.ismt.journeyjournal.journal.JournalDao;
import com.ismt.journeyjournal.user.User;
import com.ismt.journeyjournal.user.UserDao;

/*An abstract class is a class that is declared abstract—it may or may not include abstract methods.*/
/*A database is a systematic collection of data*/
/*This is a JourneyDatabase. The name of database is Journey_db*/
@Database(entities = {User.class, Journal.class}, exportSchema = false, version=1)
public abstract class  JounrneyDatabase extends RoomDatabase {
    private static final String DB_Name = "Journey_db";
    private static JounrneyDatabase instance;

    public static synchronized  JounrneyDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), JounrneyDatabase.class,
                    DB_Name).fallbackToDestructiveMigration().allowMainThreadQueries().build();

        }
        return instance;
    }

    public abstract JournalDao journalDao();

    public abstract UserDao userDao();
}
