package com.logo.services.managerimpl;

import android.util.Log;

import com.logo.application.LogoApplication;
import com.logo.bo.User;
import com.logo.services.JsonParsing;
import com.logo.services.manager.ApiManager;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ApiManagerImpl implements ApiManager {

    LogoApplication logoApplication;
    JsonParsing jsonParsing;

    public String servarUrl = "http://159.203.95.8:8181";

    public String signUpApi = "/api/users/";
    public String signInApi = "/auth/user/authenticate";
    public String saveUserDocumentApi = "/save/userDocument";
    public String findAllUserDocumentsApi = "/find/allUserDocuments";
    public String findTopTenDocumentsApi = "/find/topTenDocuments";
    public String saveCoverImage = "/save/coverimage";
    public String saveMyAccountApi = "/save/myAccount";
    public String findBlogsApi = "/find/allContentUserDocumentsForNullPdfAndCategoryIdSubCategoryId";
    public String findMyAccountByCreatedById = "/find/myAccountByCreatedById";
    public String saveDoumentRating="/save/documentRating";
    public String findMainCategories="/find/categories";
    public String findSubCategories="/find/subCategories";



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
    public JSONObject facebookSignInApi(JSONObject postData){
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(servarUrl+signUpApi,postData,null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public JSONObject googleSignInApi(JSONObject postData){
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(servarUrl+signUpApi,postData,null);
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

    public JSONObject saveUserDocument(JSONObject postData) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(servarUrl+saveUserDocumentApi,postData,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray findAllUserDocuments() {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGet(servarUrl+findAllUserDocumentsApi,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject findTopTenDocuments(int userId) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJSONObject(servarUrl+findTopTenDocumentsApi+"?"+"userId="+userId,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject saveCoverImage(int userId, File file) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPostMultipart(servarUrl+saveCoverImage+"?"+"userId="+userId,file);
    }

    public JSONObject saveMyAccount(JSONObject postData) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(servarUrl+saveMyAccountApi,postData,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray findAllUserDocumentsByCategoryIdAndSubCategoryIdAndNullContentLink(String queryStr) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String url = servarUrl+findBlogsApi+queryStr;
            return jsonParsing.httpGet(url,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject findMyAccountByCreatedById(String queryStr) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String url = servarUrl+findMyAccountByCreatedById+queryStr;
            return jsonParsing.httpGetJSONObject(url,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject saveDocumentRating(JSONObject postData) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(servarUrl+saveDoumentRating,postData,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray findMainCategories() {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String url = servarUrl+findMainCategories;
            return jsonParsing.httpGet(url,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray findSubCatgories() {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String url = servarUrl+findSubCategories;
            return jsonParsing.httpGet(url,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
