package com.logo.services.manager;

import com.logo.bo.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public interface ApiManager {

    JSONObject signInApi(User user);
    JSONObject signUpApi(User user);
    JSONObject saveUserDocument(JSONObject jsonObject);
    JSONArray findAllUserDocuments();
    JSONObject findTopTenDocuments(int userId, String title);
    JSONObject saveCoverImage(int userId, File file);
    JSONObject saveMyAccount(JSONObject jsonObject);
    JSONArray findAllUserDocumentsByCategoryIdAndSubCategoryIdAndNullContentLink(String queryStr);
    JSONArray findDocumentById(String queryStr);
    JSONObject findMyAccountByCreatedById(String queryStr);
    JSONObject saveDocumentRating(JSONObject postData);
    JSONObject saveDocumentComment(JSONObject postData);
    JSONArray findMainCategories();
    JSONArray findSubCatgories();
    JSONObject facebookSignInApi(JSONObject postData);
    JSONObject googleSignInApi(JSONObject postData);
    JSONArray findBannersForLogin(String screenName);
    JSONArray findCommentsById(String documentId);
    JSONObject updateDocumentViewCount(JSONObject object);

}
