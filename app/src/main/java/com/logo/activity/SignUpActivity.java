package com.logo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.logo.R;
import com.logo.application.LogoApplication;
import com.logo.bo.User;
import com.logo.coremanager.CoreManager;
import com.logo.database.manager.UserManager;
import com.logo.services.manager.AlertManager;
import com.logo.services.manager.ApiManager;
import com.logo.services.manager.DeviceManager;
import com.logo.services.manager.InternetManager;

import org.json.JSONObject;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends LogoActivity  {

    LogoApplication logoApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    InternetManager internetManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    ImageView imageViewBack;
    EditText editTextFirstName,editTextLastName,editTextAge,editTextMobile,editTextEmail,editTextCity,editTextPassword;

    Button buttonSignUp;

    User user;
    Context context;

    String firstName="",lastName="",age="",mobile="",gender="",email="",city="",password="";
    boolean isGenderSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
    }

    public void init(){
        logoApplication = getLogoApplication();
        coreManager = logoApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        alertManager = coreManager.getAlertManager();
        internetManager = coreManager.getInternetManager();
        deviceManager = coreManager.getDeviceManager();
        apiManager = coreManager.getApiManager();

        context = this;

        editTextFirstName = (EditText) findViewById(R.id.et_fname);
        editTextLastName = (EditText) findViewById(R.id.et_lname);
        editTextAge = (EditText) findViewById(R.id.et_age);
        editTextMobile = (EditText) findViewById(R.id.et_mobile);
        editTextEmail = (EditText) findViewById(R.id.et_email);
        editTextCity = (EditText) findViewById(R.id.et_city);
        //editTextPassword = (EditText) findViewById(R.id.et_mobile);
        imageViewBack = (ImageView) findViewById(R.id.iv_back);
        buttonSignUp = (Button) findViewById(R.id.bt_signup);

        imageViewBack.setOnClickListener(onClickListener);
        buttonSignUp.setOnClickListener(onClickListener);
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
                    isGenderSelected = true;
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_male:
                if (checked)
                    // Pirates are the best
                    gender = "male";
                    break;
            case R.id.radio_female:
                if (checked)
                    // Ninjas rule
                    gender = "female";
                    break;
        }
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.iv_back){
                startActivity(new Intent(context,LoginActivity.class));
                finish();
            }else if(v.getId() == R.id.bt_signup){
                validation();
            }
        }
    };

    public void  validation(){
         firstName = editTextFirstName.getText().toString();
         lastName = editTextLastName.getText().toString();
         age = editTextAge.getText().toString();
         mobile = editTextMobile.getText().toString();
         email = editTextEmail.getText().toString();
         city = editTextCity.getText().toString();
         password = editTextMobile.getText().toString();

         boolean isValid = true,isNextStep = true;

         if(firstName.trim().equals("") &&
                 lastName.trim().equals("") &&
                 age.trim().equals("") &&
                 mobile.trim().equals("") &&
                 email.trim().equals("") &&
                 city.trim().equals("") &&
                 password.trim().equals("")){
             isValid = false;
             isNextStep = false;
             alertManager.alert("Please Enter values","Info", context,null);
         }

         if(isNextStep && firstName.trim().equals("") ){
             isValid = false;
             isNextStep = false;
             alertManager.alert("Please Enter First Name","Info", context,null);
         }

        if(isNextStep && lastName.trim().equals("") ){
            isValid = false;
            isNextStep = false;
            alertManager.alert("Please Enter Last Name","Info", context,null);
        }

        if(isNextStep && age.trim().equals("") ){
            isValid = false;
            isNextStep = false;
            alertManager.alert("Please Enter Age","Info", context,null);
        }

        if(isNextStep && mobile.trim().equals("") ){
            isValid = false;
            isNextStep = false;
            alertManager.alert("Please Enter Mobile Number","Info", context,null);
        }

        if(isNextStep && city.trim().equals("") ){
            isValid = false;
            isNextStep = false;
            alertManager.alert("Please Enter City","Info", context,null);
        }

        if(isNextStep && password.trim().equals("") ){
            isValid = false;
            isNextStep = false;
            alertManager.alert("Please Enter Password","Info", context,null);
        }

        if(isNextStep
                && !email.trim().contains("@") ){
            isValid = false;
            isNextStep = false;
            alertManager.alert("Please enter Valid email", "Info",this,null);
        }

        if(isNextStep && !isGenderSelected){
             alertManager.alert("Please select gender","Error", context,null);
             isNextStep = false;
             isValid = false;
        }

        if(isValid){
            if(internetManager.isInternet(this)){
                user = new User();
                user.setEmail(email);
                user.setPassword(password);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                new SignUpProcess().execute();

            }else{
                alertManager.alert("Please check your Internet","Info",this,null);
            }
        }
    }


    public class SignUpProcess extends AsyncTask<Object, Object, Object> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading");
            progressDialog.show();

        }

        @Override
        protected Object doInBackground(Object... objects) {
            JSONObject jsonObject = null;

            jsonObject = apiManager.signUpApi(user);

            return jsonObject;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                JSONObject jsonObject = new JSONObject(o.toString());
                if(jsonObject != null){
                    progressDialog.dismiss();
                    Log.i("result", jsonObject.toString());
                    if(jsonObject.has("errorCode")){
                        if(jsonObject.getInt("errorCode") != 0 && jsonObject.has("errorDetail")){
                            alertManager.alert(jsonObject.getString("errorDetail"),"Error",context,null);
                        }else{
                            if(jsonObject.has(user.USERID)){
                                user.setUserId(jsonObject.getInt(user.USERID));
                            }else{
                                user.setUserId(0);
                            }

                            if(jsonObject.has(user.USERNAME)){
                                user.setUsername(jsonObject.getString(user.USERNAME));
                            }else{
                                user.setUsername("");
                            }

                            if(jsonObject.has(user.TOKEN)){
                                user.setAuthToken(jsonObject.getString(user.TOKEN));
                            }else{
                                user.setAuthToken("");
                            }

                            userManager.addUser(user);
                            startActivity(new Intent(context,MainActivity.class));
                            finish();
                        }
                    }
                }else {
                    alertManager.alert("Something wrong","Server error",context,null);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}

