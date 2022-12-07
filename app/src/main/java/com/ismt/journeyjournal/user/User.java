package com.ismt.journeyjournal.user;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
/*This is a  POJO class which contain user information. We can called this classes as table of database, with table name user.*/
@Entity (tableName ="user")
public class User {

    private String userName;

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String email;
    private String password;
    private String conPassword;

    public User(String userName, int id, String email, String password, String conPassword) {
        this.userName = userName;
        this.id = id;
        this.email = email;
        this.password = password;
        this.conPassword = conPassword;
    }

    public String getUserName() {
        return userName;
    }


    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getConPassword() {
        return conPassword;
    }

    public String getEmail() {
        return email;
    }
}
