package com.ismt.journeyjournal.user;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.ismt.journeyjournal.JounrneyDatabase;

import java.util.List;
/*User Repo class will be responsible for interacting with the Jounrney database.*/
public class UserRepo {

    private final JounrneyDatabase db;
    private final UserDao userDao;

    public UserRepo(Context context){
        db = JounrneyDatabase.getInstance(context);
        userDao = db.userDao();
    }

    public LiveData<List<User>>getLiveUsers(){
        return userDao.getUsersLiveDate();
    }
    public List<User>allUser(){
        return userDao.getAll();
    }

    public long insertUser(User user){
        return userDao.insertUser(user);
    }

    public User checkAuth(String username, String password){
        return userDao.checkAuth(username, password);
    }
}
