package com.ismt.journeyjournal.user;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/* This is a DAOs which is responsible for specifying the database access methods.*/
@Dao
public interface UserDao {
    @Query("select * from user")
    List<User> getAll();

    /*@Query("")
    List<User> loadAllByIds(int[] userIds);*/

    @Query("select * from user")
    LiveData<List<User>>getUsersLiveDate();

    /*@Query("")
    User findByName(String first, String last);*/

    @Insert
    void insertAll(User... users);

    /*To insert User*/
    @Insert
    long insertUser(User user);

    /* For delete user*/
    @Delete
    void delete(User user);

  /* For login authentication*/
    @Query("Select * from user where email=:email and password=:password")
    User checkAuth(String email, String password);

}
