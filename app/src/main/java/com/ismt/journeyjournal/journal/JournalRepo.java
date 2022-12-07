package com.ismt.journeyjournal.journal;

import android.content.Context;

import com.ismt.journeyjournal.JounrneyDatabase;

import java.util.List;
/* JournalRepo acts as an abstraction over a particular data source.*/

public class JournalRepo {
    private final JounrneyDatabase db;
    private final JournalDao journalDao;

    public JournalRepo(Context context){
        this.db= JounrneyDatabase.getInstance(context);
        journalDao = db.journalDao();
    }
    void insertJournal(Journal journal){
        journalDao.insertJournal(journal);
    }
/*    List<Journal> getAllNotes(){
        return journalDao.getAllNotes();
    }*/

/*    LiveData<List<Journal>>retriveJournals(){
        return journalDao.getAllNotes();
    }*/
    
}
