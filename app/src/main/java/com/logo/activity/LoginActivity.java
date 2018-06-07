package com.logo.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Handler;
import android.support.v4.view.ViewPager;

import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.logo.R;
import com.logo.adapters.LoginSliderAdapter;
import com.logo.application.LogoApplication;
import com.logo.bo.User;
import com.logo.coremanager.CoreManager;
import com.logo.database.manager.UserManager;
import com.logo.services.manager.AlertManager;
import com.logo.services.manager.ApiManager;
import com.logo.services.manager.DeviceManager;
import com.logo.services.manager.InternetManager;
import com.logo.util.LogoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.relex.circleindicator.CircleIndicator;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends LogoActivity {

    LogoApplication logoApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    InternetManager internetManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    Context context;
    EditText editTextEmail, editTextMobile;
    Button buttonLogin;
    TextView textViewCreateAccount, textViewForgotPassword;
    ImageView imageViewGoogle, imageViewFaceBook;
    CircleIndicator indicator;

    ViewPager mPager;
    private static int currentPage = 0;
    public String servarUrl = "http://159.203.95.8:8181";

    User user;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private static final int RC_SIGN_IN = 007;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        new FetchBanner().execute("login");
        //initFacebook();
        //initGoogle();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void initGoogle() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        try {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }
        } catch (Exception e) {
            e.toString();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestId().requestEmail().requestIdToken("1079274333856-ljvunn45oc3snfu2v2u0obbb3va1vi8s.apps.googleusercontent.com")
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            //Log.d("res", data.toString());
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result != null)
                SignInResult(result);
        } else
            callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void SignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String givenname = acct.getGivenName();
            String familyname = acct.getFamilyName();
            String personName = acct.getDisplayName();
            String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();
            String idToken = acct.getIdToken();
            //Toast.makeText(LoginActivity.this, "token = " + idToken + " name = " + givenname + " email= " + email + " imgurl= " + personPhotoUrl, Toast.LENGTH_LONG).show();

            JSONObject postData = new JSONObject();
            try {
                postData.put("authType", "google");
                postData.put("googleAuthToken", idToken);
                user = new User();
                new SignInProcess().execute(postData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.v("", "");
            // Signed out, show unauthenticated UI.
        }
    }

    private void getUserFbProfile(final AccessToken loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());
                        String first_name = object.optString("first_name");
                        String last_name = object.optString("last_name");
                        String id = object.optString("id");
                        String email = object.optString("email");
                        String imgUrl = "https://graph.facebook.com/" + id + "/picture?type=large";
                        Toast.makeText(LoginActivity.this, "token = " + loginResult.toString() + " name = " + first_name.toString() + " last name = " + last_name.toString() + " email= " + email + " imgurl= " + imgUrl, Toast.LENGTH_LONG).show();

                        //Toast.makeText(LoginActivity.this, "token = " + loginResult.toString() + " name = " + first_name.toString() + " last name = " + last_name.toString(), Toast.LENGTH_LONG).show();
                        //loginWithFacebook(loginResult.getToken(), first_name, last_name, email, id);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void initFacebook() {
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                Log.v("", "");
            }
        };

        accessTokenTracker.startTracking();


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                //Log.d("FB_token:",loginResult.getAccessToken().getToken());
                AccessToken token = loginResult.getAccessToken();
                String facebook_id = loginResult.getAccessToken().getUserId();
                String appid = loginResult.getAccessToken().getApplicationId();
                //getUserFbProfile(token);
                JSONObject postData = new JSONObject();
                try {
                    postData.put("authType", "facebook");
                    postData.put("facebookID", facebook_id);
                    postData.put("facebookAuthToken", token.getToken());
                    System.out.println(postData.toString());
                    user = new User();
                    new SignInProcess().execute(postData);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancel() {
                try {
                    Toast.makeText(LoginActivity.this, "Login Cancelled", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.toString();
                }

            }

            @Override
            public void onError(FacebookException error) {
                try {
                    Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    if (error instanceof FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }
                    }
                } catch (Exception e) {
                    e.toString();
                }
            }
        });
    }

    public void doFacebookLogin() {
        if (null != AccessToken.getCurrentAccessToken()){
            LoginManager.getInstance().logOut();
        }
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday"));
    }

    public void doGooglePlusLogin() {
        //Toast.makeText(SignIn.this,"in progress...",Toast.LENGTH_SHORT).show();
        try {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }
        } catch (Exception e) {
            e.toString();
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        try {
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(LoginActivity.this, "You haven't installed google+ on your device", Toast.LENGTH_SHORT).show();
        }


    }

    public void init() {
        logoApplication = getLogoApplication();
        coreManager = logoApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        alertManager = coreManager.getAlertManager();
        internetManager = coreManager.getInternetManager();
        deviceManager = coreManager.getDeviceManager();
        apiManager = coreManager.getApiManager();

        context = this;

        editTextEmail = (EditText) findViewById(R.id.et_email);
        editTextMobile = (EditText) findViewById(R.id.et_mobile);
        buttonLogin = (Button) findViewById(R.id.bt_login);
        textViewCreateAccount = (TextView) findViewById(R.id.tv_create_account);
        textViewForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        imageViewFaceBook = (ImageView) findViewById(R.id.iv_fb);
        imageViewGoogle = (ImageView) findViewById(R.id.iv_google);
        mPager = (ViewPager) findViewById(R.id.pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);

        buttonLogin.setOnClickListener(onClickListener);
        textViewCreateAccount.setOnClickListener(onClickListener);
        imageViewGoogle.setOnClickListener(onClickListener);
        imageViewFaceBook.setOnClickListener(onClickListener);
        textViewForgotPassword.setOnClickListener(onClickListener);
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.bt_login) {
                //startActivity(new Intent(context, HomeActivity.class));
                validation();
            }
            if (v.getId() == R.id.tv_create_account) {
                startActivity(new Intent(context, SignUpActivity.class));
                finish();
            }
            if (v.getId() == R.id.iv_google) {
                initGoogle();

                doGooglePlusLogin();
                //    alertManager.alert("Comming Soon", "Info", context, null);
            }
            if (v.getId() == R.id.iv_fb) {
                //    alertManager.alert("Comming Soon", "Info", context, null);
                initFacebook();

                doFacebookLogin();
            }
            if (v.getId() == R.id.tv_forgot_password) {
                alertManager.alert("Comming Soon", "Info", context, null);
            }
        }
    };

    public void validation() {
        String email = editTextEmail.getText().toString();
        String mobile = editTextMobile.getText().toString();

        deviceManager.hideKeypad(editTextEmail, this);
        boolean isValid = true;
        boolean nextStep = true;
        if (email.trim().equals("") && mobile.trim().equals("")) {
            isValid = false;
            nextStep = false;
            alertManager.alert("Please enter details", "Info", this, null);
        }


        if (nextStep && email.trim().equals("")) {
            isValid = false;
            nextStep = false;
            alertManager.alert("Please enter Email", "Info", this, null);
        }

        if (nextStep && mobile.trim().equals("")) {
            isValid = false;
            nextStep = false;
            alertManager.alert("Please enter Password", "Info", this, null);
        }

        if (nextStep && !email.trim().contains("@")) {
            isValid = false;
            nextStep = false;
            alertManager.alert("Please enter Valid email", "Info", this, null);
        }


        if (isValid) {
            if (internetManager.isInternet(this)) {
                user = new User();
                user.setEmail(email);
                user.setPassword(mobile);
                new SignInProcess().execute();
            } else {
                alertManager.alert("Please check your Internet", "Info", this, null);
            }
        }
    }

    private class FetchBanner extends AsyncTask<String, Object, Object> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (null == progressDialog) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading");
            }
            progressDialog.show();

        }

        @Override
        protected Object doInBackground(String... strings) {
            return apiManager.findBannersForLogin(strings[0]);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (null != progressDialog) {
                progressDialog.dismiss();
            }
            try {
                JSONArray jsonArray = new JSONArray(o.toString());
                if (null!= jsonArray && jsonArray.length() > 0) {
                    initSlider(jsonArray);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initSlider(JSONArray array) {
        final List<String> imageUrlList = new ArrayList<>();
        for(int i = 0; i< array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                imageUrlList.add(servarUrl + object.optString("path"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mPager.setAdapter(new LoginSliderAdapter(this, imageUrlList));
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == imageUrlList.size()) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2200, 2200);
    }


    public class SignInProcess extends AsyncTask<JSONObject, Object, Object> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (null == progressDialog) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading");
            }
            progressDialog.show();

        }

        @Override
        protected Object doInBackground(JSONObject... objects) {

            JSONObject jsonObject = null;

            if (null != objects && objects.length > 0 && objects[0] != null) {
                JSONObject postData = objects[0];

                try {
                    if (postData.has("authType") && postData.getString("authType") == "facebook") {
                        jsonObject = apiManager.facebookSignInApi(objects[0]);
                    } else {
                        jsonObject = apiManager.googleSignInApi(objects[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                jsonObject = apiManager.signInApi(user);
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (null != progressDialog) {
                progressDialog.dismiss();
            }
            try {
                JSONObject jsonObject = new JSONObject(o.toString());
                if (jsonObject != null) {
                    progressDialog.dismiss();
                    Log.i("result", jsonObject.toString());
                    if (jsonObject.has("errorCode")) {
                        if (jsonObject.getInt("errorCode") != 0 && jsonObject.has("errorDetail")) {
                            alertManager.alert(jsonObject.getString("errorDetail"), "Error", context, null);
                        } else {
                            if (jsonObject.has(user.USERID)) {
                                user.setUserId(jsonObject.getInt(user.USERID));
                            } else {
                                user.setUserId(0);
                            }

                            if (jsonObject.has(user.EMAIL)) {
                                user.setEmail(jsonObject.getString(user.EMAIL));
                            } else {
                                user.setEmail("");
                            }


                            if (jsonObject.has(user.FIRSTNAME)) {
                                user.setFirstName(jsonObject.getString(user.FIRSTNAME));
                            } else {
                                user.setFirstName("");
                            }

                            if (jsonObject.has(user.LASTNAME)) {
                                if (LogoUtils.isEmpty(jsonObject.getString(user.LASTNAME))) {
                                    user.setLastName("");
                                } else {
                                    user.setLastName(jsonObject.getString(user.LASTNAME));
                                }
                            } else {
                                user.setLastName("");
                            }

                            if (jsonObject.has(user.USERNAME)) {
                                user.setUsername(jsonObject.getString(user.USERNAME));
                            } else {
                                user.setUsername("");
                            }

                            if (jsonObject.has(user.TOKEN)) {
                                user.setAuthToken(jsonObject.getString(user.TOKEN));
                            } else {
                                user.setAuthToken("");
                            }
                            if (jsonObject.has(user.CITY)) {
                                user.setCity(jsonObject.getString(user.CITY));
                            }
                            if (jsonObject.has(user.PHONE)) {
                                user.setPhone(jsonObject.getString(user.PHONE));
                            }
                            if (jsonObject.has("myAccounts")) {
                                user.setMyAccounts(jsonObject.getJSONArray("myAccounts"));
                            }

                            userManager.addUser(user);

                            if (jsonObject.has("myAccounts") && jsonObject.getJSONArray("myAccounts").length() == 0) {
                                SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putBoolean("myAccountExists", false);           // Saving boolean - true/false
                                editor.commit(); // commit changes

                                startActivity(new Intent(context, MyAccountActivity.class));
                            } else {
                                startActivity(new Intent(context, HomeActivity.class));
                            }
                            finish();
                        }
                    }
                } else {
                    alertManager.alert("Something wrong", "Server error", context, null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

