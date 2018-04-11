package com.logo.services.managerimpl;

import android.util.Log;

import com.logo.application.LogoApplication;
import com.logo.bo.User;
import com.logo.services.JsonParsing;
import com.logo.services.manager.ApiManager;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiManagerImpl implements ApiManager {

    LogoApplication logoApplication;
    JsonParsing jsonParsing;

    public String servarUrl = "http://159.203.95.8:8181";

    public String signUpApi = "/api/users/";
    public String signInApi = "/auth/user/authenticate";

    public ApiManagerImpl(LogoApplication logoApplication){
        this.logoApplication = logoApplication;
    }

    @Override
    public JSONObject signInApi(User user){
        JSONObject postData = new JSONObject();
        try {
            postData.put(user.AUTHTYPE,"email");
            postData.put(user.EMAIL,user.getEmail());
            postData.put(user.PASSWORD,user.getPassword());

            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(servarUrl+signInApi,postData,null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public JSONObject signUpApi(User user){
        JSONObject postData = new JSONObject();
        try {
            postData.put(user.AUTHTYPE,"email");
            postData.put(user.EMAIL,user.getEmail());
            postData.put(user.PASSWORD,user.getPassword());
            postData.put(user.FIRSTNAME,user.getFirstName());
            postData.put(user.LASTNAME,user.getLastName());

            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(servarUrl+signUpApi,postData,null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
