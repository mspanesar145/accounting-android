package com.logo.services.manager;

import com.logo.bo.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public interface ApiManager {

    public JSONObject signInApi(User user);
    public JSONObject signUpApi(User user);
    public JSONObject saveUserDocument(JSONObject jsonObject);
    public JSONArray findAllUserDocuments();
    public JSONObject findTopTenDocuments(int userId);
    public JSONObject saveCoverImage(int userId, File file);
    public JSONObject saveMyAccount(JSONObject jsonObject);
    public JSONArray findAllUserDocumentsByCategoryIdAndSubCategoryIdAndNullContentLink(String queryStr);
    public JSONObject findMyAccountByCreatedById(String queryStr);
    public JSONObject saveDocumentRating(JSONObject postData);
    public JSONArray findMainCategories();
    public JSONArray findSubCatgories();
    public JSONObject facebookSignInApi(JSONObject postData);
    public JSONObject googleSignInApi(JSONObject postData);

}
