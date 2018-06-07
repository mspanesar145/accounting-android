package com.logo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.logo.R;
import com.logo.application.LogoApplication;
import com.logo.bo.MainCategoryListener;
import com.logo.bo.User;
import com.logo.coremanager.CoreManager;
import com.logo.database.manager.UserManager;
import com.logo.services.manager.AlertManager;
import com.logo.services.manager.ApiManager;
import com.logo.services.manager.DeviceManager;
import com.logo.services.manager.InternetManager;
import com.logo.views.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 23/4/18.
 */

public class MyAccountActivity extends LogoActivity implements MainCategoryListener {

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
    EditText etMobile,etEmail,etCity;
    Switch swSubScribe,swNotifications;
    Button btnSubmit;
    LinearLayout llBottomProfile,llBottomContent,llBottomHome;
    TextView homeTxt,listTxt,profile,settings,logout,tvUsernmae;
    RoundedImageView riv_imageView;
    TextView tvMainCategory, tvSubCategory;
    private List<Integer> selectedMainCourses, selectedSubCourses;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 11;


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
//            tvEmail.setText(user.getEmail());
            etMobile.setText(user.getPhone());
            etMobile.setSelection(etMobile.getText().length());
            etEmail.setText(user.getEmail());
            if (!TextUtils.isEmpty(user.getCity())) {
                etCity.setText(user.getCity());
            }
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
//        tvEmail = (TextView) findViewById(R.id.tv_email);
        etMobile = (EditText) findViewById(R.id.et_mobile);
        etEmail = (EditText) findViewById(R.id.et_email);
        etCity = (EditText) findViewById(R.id.et_city);

        swSubScribe = (Switch) findViewById(R.id.sw_sub_newsletter);
        swNotifications = (Switch) findViewById(R.id.sw_receive_notification);

        btnSubmit = (Button) findViewById(R.id.bt_submit);

        llBottomContent = (LinearLayout) findViewById(R.id.ll_bottom_content);
        llBottomProfile = (LinearLayout) findViewById(R.id.ll_bottom_profile);
        llBottomHome = (LinearLayout) findViewById(R.id.ll_bottom_home);
        tvMainCategory = (TextView) findViewById(R.id.tv_main_category);
        tvSubCategory = (TextView) findViewById(R.id.tv_sub_category);

        llBottomProfile.setOnClickListener(bottomProfileListener);
        llBottomContent.setOnClickListener(bottomContentListener);
        llBottomHome.setOnClickListener(bottomHomeListener);
        tvMainCategory.setOnClickListener(mainCategoryListener);
        tvSubCategory.setOnClickListener(subCategoryListener);
        etCity.setOnClickListener(citySearchListener);

        btnSubmit.setOnClickListener(btnSubmitClickListener);
        swSubScribe.setOnCheckedChangeListener(swSubScribeListener);
        swNotifications.setOnCheckedChangeListener(swNotificationsListener);

        listTxt = (TextView) findViewById(R.id.list_txt);
        listTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManager userManager = getLogoApplication().getCoreManager().getUserManager();
                Intent intent = new Intent(MyAccountActivity.this, ContentActivity.class);
                intent.putExtra("createdById", userManager.getUser().getUserId());
                startActivity(intent);
                finish();
            }
        });

        homeTxt = (TextView) findViewById(R.id.home_txt);
        homeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccountActivity.this,HomeActivity.class));
                finish();
            }
        });
        profile = (TextView) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccountActivity.this,ProfileActivity.class));
                finish();
            }
        });
        settings = (TextView) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccountActivity.this,MyAccountActivity.class));
                finish();
            }
        });

        logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userManager.deleteUser();
                startActivity(new Intent(MyAccountActivity.this,LoginActivity.class));
                finish();
            }
        });

        tvUsernmae = (TextView)findViewById(R.id.tv_usernmae);
        riv_imageView = (RoundedImageView) findViewById(R.id.riv_imageView);
        User user = userManager.getUser();
        tvUsernmae.setText(user.getFirstName());
        Glide.with(context).load(user.getPicture()).into(riv_imageView);

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

    View.OnClickListener mainCategoryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<String> courses = new ArrayList<>();
            if (null != categoryMap && null != selectedMainCourses) {
                for (Map.Entry categoryEntryMap : categoryMap.entrySet()) {
                    for (int i = 0; i < selectedMainCourses.size(); i++) {
                        if (categoryEntryMap.getValue() == selectedMainCourses.get(i)) {
                            courses.add(String.valueOf(categoryEntryMap.getKey()));
                        }
                    }
                }
            }

            ArrayList<String> allMainCourses = new ArrayList<>();
            allMainCourses.addAll(Arrays.asList(items));

            SelectCourseDialogFragment dialog = new SelectCourseDialogFragment();
            dialog.setMainCategoryListener(MyAccountActivity.this);

            Bundle bundle = new Bundle();

            bundle.putSerializable("category", allMainCourses);
            bundle.putStringArrayList("selectedCourses", courses);
            bundle.putBoolean("mainCourses", true);
            dialog.setArguments(bundle);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            dialog.show(ft, FullScreenDialog.TAG);
        }
    };

    View.OnClickListener subCategoryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (null != selectedMainCourses && selectedMainCourses.size() > 0) {
                // Get already selected sub-courses
                ArrayList<String> subCourses = new ArrayList<>();
                if (null != subCategoryMap && null != selectedSubCourses) {
                    for (Map.Entry categoryEntryMap : subCategoryMap.entrySet()) {
                        for (int i = 0; i < selectedSubCourses.size(); i++) {
                            if (categoryEntryMap.getValue() == selectedSubCourses.get(i)) {
                                subCourses.add(String.valueOf(categoryEntryMap.getKey()));
                            }
                        }
                    }
                }
                // Get permissible sub-courses to show
                ArrayList<String> subCategories = new ArrayList<>();
                SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
                try {
                    JSONArray subCategoriesArray = new JSONArray(pref.getString("subCategories",""));
                    for (int i = 0; i < subCategoriesArray.length(); i++) {
                        JSONObject jsonObject = subCategoriesArray.optJSONObject(i);
                        for (Integer selectedCourseId : selectedMainCourses) {
                            if (jsonObject.optInt("parentCategoryId") == selectedCourseId) {
                                subCategories.add(jsonObject.optString("name"));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                SelectCourseDialogFragment dialog = new SelectCourseDialogFragment();
                dialog.setMainCategoryListener(MyAccountActivity.this);

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("category", subCategories);
                bundle.putStringArrayList("selectedCourses", subCourses);
                dialog.setArguments(bundle);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                dialog.show(ft, FullScreenDialog.TAG);

            } else {
                if (null != alertManager) {
                    alertManager.alert("Please select main course","Info", context,null);
                }
            }
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
            if (TextUtils.isEmpty(etMobile.getText().toString().trim())) {
                alertManager.alert("Mobile no. cannot be left empty","Info", context,null);
                return false;
            }
            if (TextUtils.isEmpty(etEmail.getText().toString().trim())) {
                alertManager.alert("Email Id cannot be left empty","Info", context,null);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    View.OnClickListener citySearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                        .build();

                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .setBoundsBias(new LatLngBounds(new LatLng(23.63936, 68.14712)
                                        , new LatLng(28.20453, 97.34466)))
                                .setFilter(typeFilter)
                                .build(MyAccountActivity.this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    };

    View.OnClickListener btnSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validateMyAccount()) {
                if (null != user) {
                    user.setEmail(etEmail.getText().toString().trim());
                    user.setPhone(etMobile.getText().toString().trim());
                    user.setPassword(etMobile.getText().toString().trim());
                    user.setCity(etCity.getText().toString().trim());
                    try {
                        myAccountJSON.put("createdById",user.getUserId());

                        JSONObject jsonObject = new JSONObject(new Gson().toJson(user));

                        JSONArray array = new JSONArray();
                        array.put(myAccountJSON);
                        jsonObject.put("myAccounts", array);


                        new MyAccountProcess().execute(jsonObject);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                etCity.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMainCategorySelected(List<String> categories) {
        if (null != categories && categories.size() > 0){
            if (null == selectedMainCourses) {
                selectedMainCourses = new ArrayList<>();
            } else {
                selectedMainCourses.clear();
            }
            StringBuilder builder = new StringBuilder();
            String idString = "";
            for (String category : categories) {
                selectedMainCourses.add(categoryMap.get(category));
                if (null != selectedSubCourses) {
                    selectedSubCourses.clear();
                    tvSubCategory.setText(getString(R.string.select_sub_cat));
                    myAccountJSON.remove("secondryCourseId");
                }

                if (idString.length() == 0) {
                    idString = String.valueOf(categoryMap.get(category));
                } else {
                    idString = idString + "," + String.valueOf(categoryMap.get(category));
                }

                if (builder.length() == 0) {
                    builder.append(category);
                } else {
                    builder.append(",").append(category);
                }
            }
            try {
                if (null != myAccountJSON) {
                    myAccountJSON.put("mainCourseId", idString);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tvMainCategory.setText(builder);
        } else {
            tvMainCategory.setText(getString(R.string.select_main_cat));
            tvSubCategory.setText(getString(R.string.select_sub_cat));
            myAccountJSON.remove("mainCourseId");
            myAccountJSON.remove("secondryCourseId");
            if (null != selectedMainCourses) {
                selectedMainCourses.clear();
            }
            if (null != selectedSubCourses) {
                selectedSubCourses.clear();
            }
        }
    }

    @Override
    public void onSubCategorySelected(List<String> subCategories) {
        if (null != subCategories && subCategories.size() > 0) {
            if (null == selectedSubCourses) {
                selectedSubCourses = new ArrayList<>();
            } else {
                selectedSubCourses.clear();
            }

            StringBuilder builder = new StringBuilder();
            String idString = "";
            for (String category : subCategories) {
                selectedSubCourses.add(subCategoryMap.get(category));
                if (idString.length() == 0) {
                    idString = String.valueOf(subCategoryMap.get(category));
                } else {
                    idString = idString + "," + String.valueOf(subCategoryMap.get(category));
                }

                if (builder.length() == 0) {
                    builder.append(category);
                } else {
                    builder.append(",").append(category);
                }
            }
            try {
                if (null != myAccountJSON) {
                    myAccountJSON.put("secondryCourseId", idString);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tvSubCategory.setText(builder);
        } else {
            tvSubCategory.setText(getString(R.string.select_sub_cat));
            myAccountJSON.remove("secondryCourseId");
            if (null != selectedSubCourses) {
                selectedSubCourses.clear();
            }
        }
    }

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

                    User user = new Gson().fromJson(jsonObject.optJSONObject("data").toString(), User.class);

                    userManager.deleteUser();
                    userManager.addUser(user);

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
                    }
                    if (subCategoryMap == null) {
                        subCategoryMap = new HashMap<>();
                    }

                    JSONArray categoriesArray = new JSONArray(pref.getString("categories", ""));           // Saving boolean - true/false
                    JSONArray subCategoriesArray = new JSONArray(pref.getString("subCategories", ""));           // Saving boolean - true/false

                    items = new String[categoriesArray.length()];
                    for (int i=0; i<categoriesArray.length(); i++) {
                        JSONObject categoryObj = categoriesArray.getJSONObject(i);
                        try {
                            categoryMap.put(categoryObj.getString("name"),categoryObj.getInt("profileCategoryId"));
                            items[i] = categoryObj.getString("name");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    subItems = new String[subCategoriesArray.length()];
                    for (int i=0; i<subCategoriesArray.length(); i++) {
                        JSONObject categoryObj = subCategoriesArray.getJSONObject(i);
                        try {
                            subCategoryMap.put(categoryObj.getString("name"),categoryObj.getInt("profileCategoryId"));
                            subItems[i] = categoryObj.getString("name");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
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

        StringBuilder builder = new StringBuilder();
        try {
            String courseIds = myAccountJSON.getString("mainCourseId");
            String[] selectedMainCategories = courseIds.split(",");
            for (Map.Entry categoryEntryMap : categoryMap.entrySet()) {
                    for (String selectedId : selectedMainCategories) {
                        if (selectedId.equals(String.valueOf(categoryEntryMap.getValue()))) {
                            if (null == selectedMainCourses) {
                                selectedMainCourses = new ArrayList<>();
                            }
                            selectedMainCourses.add(Integer.parseInt(selectedId));
                            if (builder.length() == 0) {
                                builder.append(categoryEntryMap.getKey());
                            } else {
                                builder.append(",").append(categoryEntryMap.getKey());
                            }
                        }
                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvMainCategory.setText(builder);

        StringBuilder secondaryBuilder = new StringBuilder();
        try {
            String secondaryCourseIds = myAccountJSON.getString("secondryCourseId");
            String[] selectedSecondayCategories = secondaryCourseIds.split(",");

            for (Map.Entry subCategoryEntryMap : subCategoryMap.entrySet()) {
                for (String selectedId : selectedSecondayCategories) {
                    if (selectedId.equals(String.valueOf(subCategoryEntryMap.getValue()))) {
                        if (null == selectedSubCourses) {
                            selectedSubCourses = new ArrayList<>();
                        }
                        selectedSubCourses.add(Integer.parseInt(selectedId));
                        if (secondaryBuilder.length() == 0) {
                            secondaryBuilder.append(subCategoryEntryMap.getKey());
                        } else {
                            secondaryBuilder.append(",").append(subCategoryEntryMap.getKey());
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvSubCategory.setText(secondaryBuilder);

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
                    try {
                        categoryMap =  new HashMap<>();

                        if (jsonArray != null && jsonArray.length() > 0) {
                            System.out.print(jsonArray);

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("categories", jsonArray.toString());           // Saving boolean - true/false
                            editor.commit();

                            items = new String[jsonArray.length()];
                            for (int i=0; i<jsonArray.length(); i++) {
                                JSONObject subCategiryObj = jsonArray.getJSONObject(i);
                                try {
                                    categoryMap.put(subCategiryObj.getString("name"),subCategiryObj.getInt("profileCategoryId"));
                                    items[i] = subCategiryObj.getString("name");
                                    //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                                    //There are multiple variations of this, but this is the basic variant.
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("subCategories", jsonArray.toString());           // Saving boolean - true/false
                    editor.commit();

                    subItems = new String[jsonArray.length()];
                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject subCategiryObj = jsonArray.getJSONObject(i);
                        try {
                            subCategoryMap.put(subCategiryObj.getString("name"),subCategiryObj.getInt("profileCategoryId"));
                            subItems[i] = subCategiryObj.getString("name");
                            //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                            //There are multiple variations of this, but this is the basic variant.
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    //alertManager.alert("Something wrong", "Server error", context, null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    public void populateSubCategories() {
//        try {
//            if (subCategoryMap == null) {
//                subCategoryMap = new HashMap<>();
//            }
//            SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
//            JSONArray subCategoriesArray = new JSONArray(pref.getString("subCategories",""));
//            if (subCategoriesArray != null) {
//                JSONArray selectedCategoriesArray = new JSONArray();
//                for (int i=0; i< subCategoriesArray.length(); i++) {
//                    JSONObject subCategoryObj = subCategoriesArray.getJSONObject(i);
//                    if (subCategoryObj.getInt("parentCategoryId") == myAccountJSON.getInt("mainCourseId")) {
//                        selectedCategoriesArray.put(subCategoryObj);
//                    }
//                }
//                subItems = new String[selectedCategoriesArray.length()];
//                for (int i=0; i<selectedCategoriesArray.length(); i++) {
//                    JSONObject subCategiryObj = selectedCategoriesArray.getJSONObject(i);
//                    try {
//                        subCategoryMap.put(subCategiryObj.getString("name"),subCategiryObj.getInt("profileCategoryId"));
//                        subItems[i] = subCategiryObj.getString("name");
//                        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
//                        //There are multiple variations of this, but this is the basic variant.
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                for (Map.Entry subCategoryEntryMap : subCategoryMap.entrySet()) {
//                    try {
//                        Integer courseId = (Integer)myAccountJSON.getInt("secondryCourseId");
//                        if (subCategoryEntryMap.getValue() == (Integer)myAccountJSON.getInt("secondryCourseId")) {
//                            for (int position=0; position < subItems.length; position++) {
//                                if (subCategoryEntryMap.getKey().equals(subItems[position])) {
//                                    break;
//                                }
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
