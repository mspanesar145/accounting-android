package com.logo.services.managerimpl;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.logo.application.LogoApplication;
import com.logo.bo.User;
import com.logo.services.JsonParsing;
import com.logo.services.manager.ApiManager;
import com.logo.util.AppUtil;

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
    public String saveMyAccountApi = "/api/users/update";
    public String findBlogsApi = "/find/allContentUserDocumentsForNullPdfAndCategoryIdSubCategoryId";
    public String findDocumentsById = "/find/allDocumentsByCategotyIdSubCategoryIdContainsVideo";
    public String findMyAccountByCreatedById = "/find/myAccountByCreatedById";
    public String saveDoumentRating="/save/documentRating";
    public String saveDocumentComment="/save/documentComment";
    public String findMainCategories="/find/categories";
    public String findSubCategories="/find/subCategories";
    public String findByBannersForLogin = "/find/bannersByScreen";
    public String findCommentsById = "/find/documentsCommentsByDocumentId";
    public String updateContentViewCount = "/save/documentStats";
    public String sendFeedback = "/api/users/sendFeedback";



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

            if (ConnectionResult.SUCCESS == GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(logoApplication.getApplicationContext())){
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                postData.put(user.DEVICE_TOKEN, refreshedToken);
                postData.put(user.DEVICE_TYPE, AppUtil.DEVICE_TYPE);
            }

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
            if (ConnectionResult.SUCCESS == GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(logoApplication.getApplicationContext())){
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                postData.put(AppUtil.DEVICE_TOKEN, refreshedToken);
                postData.put(AppUtil.PARAM_DEVICE_TYPE, AppUtil.DEVICE_TYPE);
            }
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
            if (ConnectionResult.SUCCESS == GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(logoApplication.getApplicationContext())){
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                postData.put(AppUtil.DEVICE_TOKEN, refreshedToken);
                postData.put(AppUtil.PARAM_DEVICE_TYPE, AppUtil.DEVICE_TYPE);
            }
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(servarUrl+signUpApi,postData,null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public JSONArray findBannersForLogin(String screenName) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGet(servarUrl+findByBannersForLogin+"?screen="+screenName, null);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONArray findCommentsById(String documentId) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGet(servarUrl+findCommentsById+"?documentId="+documentId, null);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject updateDocumentViewCount(JSONObject jsonObject) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJSONObject(servarUrl+updateContentViewCount
                            +"?userDocumentId="+jsonObject.optString("userDocumentId")
                            +"&source="+jsonObject.optString("source")
                    , null);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject sendFeedack(JSONObject jsonObject) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(servarUrl+sendFeedback
                    , jsonObject, null);
        }catch (Exception e) {
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
            postData.put(user.CITY,user.getCity());
            postData.put(user.PHONE,user.getPhone());


            if (ConnectionResult.SUCCESS == GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(logoApplication.getApplicationContext())){
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                postData.put(AppUtil.DEVICE_TOKEN, refreshedToken);
                postData.put(AppUtil.PARAM_DEVICE_TYPE, AppUtil.DEVICE_TYPE);
            }

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
    public JSONObject findTopTenDocuments(int userId, String title) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String query = servarUrl+findTopTenDocumentsApi+"?"+"userId="+userId;
            if (!TextUtils.isEmpty(title)) {
                query = query + "&title="+ title;
            }
            return jsonParsing.httpGetJSONObject(query,null);
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

    @Override
    public JSONArray findDocumentById(String queryStr) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String url = servarUrl+findDocumentsById+queryStr;
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

    @Override
    public JSONObject saveDocumentComment(JSONObject postData) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(servarUrl+saveDocumentComment,postData,null);
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
