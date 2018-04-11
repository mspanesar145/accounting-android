package com.logo.services.manager;

import com.logo.bo.User;

import org.json.JSONObject;

public interface ApiManager {

    public JSONObject signInApi(User user);
    public JSONObject signUpApi(User user);
}
