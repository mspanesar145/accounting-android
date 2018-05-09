package com.logo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.logo.R;
import com.logo.application.LogoApplication;
import com.logo.bo.User;
import com.logo.coremanager.CoreManager;
import com.logo.database.manager.UserManager;
import com.logo.services.manager.AlertManager;
import com.logo.services.manager.ApiManager;
import com.logo.services.manager.DeviceManager;
import com.logo.services.manager.InternetManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mandeep on 23/4/18.
 */

public class MyAccountActivity extends LogoActivity {

    LogoApplication logoApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    InternetManager internetManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    Context context;

    ImageView ivUserImage;
    TextView tvUsername,tvEmail;
    Spinner mainSpinner,secondarySpinner;
    Switch swSubScribe,swNotifications;
    Button btnSubmit;
    LinearLayout llBottomProfile,llBottomContent,llBottomHome;


    public Map<String,Integer> categoryMap,subCategoryMap;
    public JSONObject myAccountJSON;
    public User user;
    public String[] items = null,subItems = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        NavigtionCreate();
        init();
        variablesInit();

    }

    public void variablesInit() {

        myAccountJSON = new JSONObject();
        user = userManager.getUser();
        if (user != null) {
            tvUsername.setText(user.getFirstName()+" "+user.getLastName());
            tvEmail.setText(user.getEmail());
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
        if (!pref.getBoolean("myAccountExists",false))  {
            new CategoriesTask().execute();
            new SubCatgoriesTask().execute();
        } else {
            new FetchMyAccountProcess().execute();
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
        //ivUserImage = (ImageView) findViewById(R.id.iv_user_image);
        tvUsername = (TextView) findViewById(R.id.tv_usernmae);
        tvEmail = (TextView) findViewById(R.id.tv_email);


        mainSpinner = (Spinner) findViewById(R.id.main_spinner);
        secondarySpinner = (Spinner) findViewById(R.id.secondary_spinner);

        swSubScribe = (Switch) findViewById(R.id.sw_sub_newsletter);
        swNotifications = (Switch) findViewById(R.id.sw_receive_notification);

        btnSubmit = (Button) findViewById(R.id.bt_submit);

        llBottomContent = (LinearLayout) findViewById(R.id.ll_bottom_content);
        llBottomProfile = (LinearLayout) findViewById(R.id.ll_bottom_profile);
        llBottomHome = (LinearLayout) findViewById(R.id.ll_bottom_home);

        llBottomProfile.setOnClickListener(bottomProfileListener);
        llBottomContent.setOnClickListener(bottomContentListener);
        llBottomHome.setOnClickListener(bottomHomeListener);

        mainSpinner.setOnItemSelectedListener(mainSpinnerListener);
        secondarySpinner.setOnItemSelectedListener(secondarySpinnerListener);
        btnSubmit.setOnClickListener(btnSubmitClickListener);
        swSubScribe.setOnCheckedChangeListener(swSubScribeListener);
        swNotifications.setOnCheckedChangeListener(swNotificationsListener);
    }

    View.OnClickListener bottomContentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
            boolean prefrenceExits = pref.getBoolean("myAccountExists",false);
            if (!prefrenceExits)  {
                Toast.makeText(getApplicationContext(),"Please save your data first",Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(new Intent(context,ContentActivity.class));
            finish();
        }
    };

    View.OnClickListener bottomProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
            if (!pref.getBoolean("myAccountExists",false))  {
                Toast.makeText(getApplicationContext(),"Please save your data first",Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(new Intent(context,ProfileActivity.class));
            finish();
        }
    };

    View.OnClickListener bottomHomeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
            if (!pref.getBoolean("myAccountExists",false))  {
                Toast.makeText(getApplicationContext(),"Please save your data first",Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(new Intent(context,HomeActivity.class));
            finish();
        }
    };

    public Boolean validateMyAccount() {
        try {
            if (!myAccountJSON.has("mainCourseId")) {
                alertManager.alert("Please select main course","Info", context,null);
                return false;
            }
            if (!myAccountJSON.has("secondryCourseId")) {
                alertManager.alert("Please select secondary course","Info", context,null);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    View.OnClickListener btnSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validateMyAccount()) {
                User user = userManager.getUser();
                try {
                    myAccountJSON.put("createdById",user.getUserId());
                    new MyAccountProcess().execute(myAccountJSON);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };

    CompoundButton.OnCheckedChangeListener swSubScribeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            System.out.print(isChecked);
            try {
                myAccountJSON.put("isNotificationOn",isChecked);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    CompoundButton.OnCheckedChangeListener swNotificationsListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            System.out.print(isChecked);
            try {
                myAccountJSON.put("newsLetterSubscribed",isChecked);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    AdapterView.OnItemSelectedListener mainSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            System.out.println(position);
            try {
                if (myAccountJSON != null) {
                    myAccountJSON.put("mainCourseId",mainSpinner.getItemIdAtPosition(position));
                    /*new SubCatgoriesTask().execute(myAccountJSON.getInt("mainCourseId"));*/
                    populateSubCategories();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener secondarySpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            System.out.println(position);
            try {
                myAccountJSON.put("secondryCourseId",secondarySpinner.getItemIdAtPosition(position));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    class MyAccountProcess extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
        }

        @Override
        protected JSONObject doInBackground(JSONObject... objects) {
            JSONObject jsonObject = objects[0];
            return apiManager.saveMyAccount(jsonObject);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if(progressDialog!=null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                if (jsonObject != null) {
                    Toast.makeText(MyAccountActivity.this,"My Account Saved.!",Toast.LENGTH_SHORT).show();

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("myAccountExists", true);           // Saving boolean - true/false
                    editor.commit();

                    startActivity(new Intent(MyAccountActivity.this,HomeActivity.class));
                    finish();
                } else {
                    alertManager.alert("Something wrong", "Server error", context, null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class FetchMyAccountProcess extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
        }

        @Override
        protected JSONObject doInBackground(JSONObject... objects) {
            String queryStr = "?createdById="+user.getUserId();
            return apiManager.findMyAccountByCreatedById(queryStr);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if(progressDialog!=null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                if (jsonObject != null) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("myAccountExists", true);           // Saving boolean - true/false
                    editor.commit(); // commit changes

                    myAccountJSON = jsonObject;


                    if (categoryMap == null) {
                        categoryMap = new HashMap<>();
                        categoryMap.put("Select Category",0);
                    }
                    JSONArray categoriesArray = new JSONArray(pref.getString("categories", ""));           // Saving boolean - true/false

                    items = new String[categoriesArray.length()+1];
                    items[0] = "Select Category";
                    for (int i=0; i<categoriesArray.length(); i++) {
                        JSONObject categoryObj = categoriesArray.getJSONObject(i);
                        try {
                            categoryMap.put(categoryObj.getString("name"),categoryObj.getInt("profileCategoryId"));
                            items[i+1] = categoryObj.getString("name");
                            //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                            //There are multiple variations of this, but this is the basic variant.
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ArrayAdapter<String> subAdapter = new ArrayAdapter<>(MyAccountActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                        //set the spinners adapter to the previously created one.
                        mainSpinner.setAdapter(subAdapter);
                    }

                    populateSubCategories();

                    populateMyAccountData(myAccountJSON);

                } else {
                    myAccountJSON = new JSONObject();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void populateMyAccountData(JSONObject myAccountJSON) {

        for (Map.Entry categoryEntryMap : categoryMap.entrySet()) {
            try {
                if (categoryEntryMap.getValue() == (Integer)myAccountJSON.getInt("mainCourseId")) {
                    for (int position=0; position < items.length; position++) {
                        if (categoryEntryMap.getKey().equals(items[position])) {
                            mainSpinner.setSelection(position);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry subCategoryEntryMap : subCategoryMap.entrySet()) {
            try {
                Integer courseId = (Integer)myAccountJSON.getInt("secondryCourseId");
                if (subCategoryEntryMap.getValue() == (Integer)myAccountJSON.getInt("secondryCourseId")) {
                    for (int position=0; position < subItems.length; position++) {
                        if (subCategoryEntryMap.getKey().equals(subItems[position])) {
                            secondarySpinner.setSelection(position);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            swSubScribe.setChecked(myAccountJSON.getBoolean("newsLetterSubscribed"));
            swNotifications.setChecked(myAccountJSON.getBoolean("isNotificationOn"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class CategoriesTask extends AsyncTask<JSONObject, JSONArray, JSONArray> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Please wait...", true);

            //progressDialog.show();

        }
        @Override
        protected JSONArray doInBackground(JSONObject... jsonObjects) {
            return apiManager.findMainCategories();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            if(progressDialog!=null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                if (jsonArray != null && jsonArray.length() > 0) {
                    System.out.print(jsonArray);
                    try {
                        categoryMap =  new HashMap<>();

                        if (jsonArray != null && jsonArray.length() > 0) {
                            System.out.print(jsonArray);

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("categories", jsonArray.toString());           // Saving boolean - true/false
                            editor.commit();

                            String[] items = new String[jsonArray.length()+1];
                            items[0] = "Select Category";
                            for (int i=0; i<jsonArray.length(); i++) {
                                JSONObject subCategiryObj = jsonArray.getJSONObject(i);
                                try {
                                    categoryMap.put(subCategiryObj.getString("name"),subCategiryObj.getInt("profileCategoryId"));
                                    items[i+1] = subCategiryObj.getString("name");
                                    //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                                    //There are multiple variations of this, but this is the basic variant.
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ArrayAdapter<String> subAdapter = new ArrayAdapter<>(MyAccountActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                                //set the spinners adapter to the previously created one.
                                mainSpinner.setAdapter(subAdapter);
                            }
                        } else {
                            //alertManager.alert("Something wrong", "Server error", context, null);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //alertManager.alert("Something wrong", "Server error", context, null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class SubCatgoriesTask extends AsyncTask<Integer, JSONArray, JSONArray> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Please wait...", true);

            //progressDialog.show();

        }
        @Override
        protected JSONArray doInBackground(Integer... longs) {
            //Integer parentCategoryId = longs[0];
            return apiManager.findSubCatgories();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            if(progressDialog!=null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                subCategoryMap =  new HashMap<>();

                if (jsonArray != null && jsonArray.length() > 0) {
                    System.out.print(jsonArray);

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("subCategories", jsonArray.toString());           // Saving boolean - true/false
                    editor.commit();

                } else {
                    //alertManager.alert("Something wrong", "Server error", context, null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void populateSubCategories() {
        try {
            if (subCategoryMap == null) {
                subCategoryMap = new HashMap<>();
                subCategoryMap.put("Select SubCategory",0);
            }
            SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
            JSONArray subCategoriesArray = new JSONArray(pref.getString("subCategories",""));
            if (subCategoriesArray != null) {
                JSONArray selectedCategoriesArray = new JSONArray();
                for (int i=0; i< subCategoriesArray.length(); i++) {
                    JSONObject subCategoryObj = subCategoriesArray.getJSONObject(i);
                    if (subCategoryObj.getInt("parentCategoryId") == myAccountJSON.getInt("mainCourseId")) {
                        selectedCategoriesArray.put(subCategoryObj);
                    }
                }
                subItems = new String[selectedCategoriesArray.length()+1];
                subItems[0] = "Select SubCategory";
                for (int i=0; i<selectedCategoriesArray.length(); i++) {
                    JSONObject subCategiryObj = selectedCategoriesArray.getJSONObject(i);
                    try {
                        subCategoryMap.put(subCategiryObj.getString("name"),subCategiryObj.getInt("profileCategoryId"));
                        subItems[i+1] = subCategiryObj.getString("name");
                        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                        //There are multiple variations of this, but this is the basic variant.
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ArrayAdapter<String> subAdapter = new ArrayAdapter<>(MyAccountActivity.this, android.R.layout.simple_spinner_dropdown_item, subItems);
                    //set the spinners adapter to the previously created one.
                    secondarySpinner.setAdapter(subAdapter);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
