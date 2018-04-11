package com.logo.database.manager;

import com.logo.bo.User;

public interface UserManager {

    public void addUser(User user);
    public void deleteUser();
    public User getUser();
    public boolean isUserExist();
}
