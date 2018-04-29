package com.logo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

        init();
        variablesInit();

    }

    public void variablesInit() {

        categoryMap = new HashMap<>();
        categoryMap.put("Category 1",1);
        categoryMap.put("Category 2",2);
        categoryMap.put("Category 3",3);
        categoryMap.put("Category 4",4);
        categoryMap.put("Category 5",5);

        subCategoryMap =  new HashMap<>();
        subCategoryMap.put("Sub Category 1",1);
        subCategoryMap.put("Sub Category 2",2);
        subCategoryMap.put("Sub Category 3",3);
        subCategoryMap.put("Sub Category 4",4);
        subCategoryMap.put("Sub Category 5",5);

        //create a list of items for the spinner.
        items = new String[]{"Select","Category 1","Category 2","Category 3","Category 4","Category 5"};

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        mainSpinner.setAdapter(adapter);

        //create a list of items for the spinner.
        subItems = new String[]{"Select","Sub Category 1","Sub Category 2","Sub Category 3","Sub Category 4","Sub Category 5"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subItems);
        //set the spinners adapter to the previously created one.
        secondarySpinner.setAdapter(subAdapter);

        user = userManager.getUser();
        if (user != null) {
            tvUsername.setText(user.getFirstName()+" "+user.getLastName());
            tvEmail.setText(user.getEmail());
        }

        new FetchMyAccountProcess().execute();

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
            startActivity(new Intent(context,ContentActivity.class));
            finish();
        }
    };

    View.OnClickListener bottomProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context,ProfileActivity.class));
            finish();
        }
    };

    View.OnClickListener bottomHomeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
                myAccountJSON.put("mainCourseId",mainSpinner.getItemIdAtPosition(position));
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
                    myAccountJSON = jsonObject;
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
}
