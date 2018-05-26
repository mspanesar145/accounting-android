package com.logo.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.logo.R;
import com.logo.application.LogoApplication;
import com.logo.bo.User;
import com.logo.coremanager.CoreManager;
import com.logo.database.manager.UserManager;
import com.logo.services.manager.AlertManager;
import com.logo.services.manager.ApiManager;
import com.logo.services.manager.DeviceManager;
import com.logo.services.manager.InternetManager;
import com.logo.util.ImageUtils;
import com.logo.views.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mandeep on 15/4/18.
 */

public class ProfileActivity extends LogoActivity {

    private static int CAMERA_PIC_REQUEST = 100, PICK_IMAGE = 101, PICK_CONTENT = 102;

    LogoApplication logoApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    InternetManager internetManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    Context context;

    JSONObject userDocumentObject = null;
    Spinner categorySpinner, subCategorySpinner;
    RadioGroup rgContainsVideo;
    EditText etTitle, etVideoLink, etDescription;
    RelativeLayout rlUploadCoverImage, rlUploadContentFileChooser;
    LinearLayout llBottomMyAccount, llBottomContent, llBottomHome;
    Button btSubmit;
    ImageView ivUploadCi;
    TextView tvUploadContentFilename;
    TextView homeTxt,listTxt,profile,settings,logout,tvUsernmae;
    RoundedImageView riv_imageView;

    Boolean containsVideoLink = false;
    String uploadType = "";
    File imageCoverFile = null, contentFileToUpload = null;

    Map<String, Integer> categoryMap, subCategoryMap;
    private User user;
    private ArrayAdapter<String> categoryAdapter, subCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        NavigtionCreate();
        init();
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
       /* categoryMap = new HashMap<>();
        categoryMap.put("CPT",1);
        categoryMap.put("IPCC",2);
        categoryMap.put("Final",3);
        categoryMap.put("Professionals",4);
        categoryMap.put("Others",5);*/

        userDocumentObject = new JSONObject();

        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        subCategorySpinner = (Spinner) findViewById(R.id.sub_category_spinner);
        rgContainsVideo = (RadioGroup) findViewById(R.id.rg_contains_video);
        etTitle = (EditText) findViewById(R.id.et_title);
        etVideoLink = (EditText) findViewById(R.id.et_video_link);
        etDescription = (EditText) findViewById(R.id.et_description);
        btSubmit = (Button) findViewById(R.id.bt_submit);
        rlUploadCoverImage = (RelativeLayout) findViewById(R.id.rl_upload_ci);
        rlUploadContentFileChooser = (RelativeLayout) findViewById(R.id.rl_upload_content_file_chooser);
        ivUploadCi = (ImageView) findViewById(R.id.iv_upload_ci);
        tvUploadContentFilename = (TextView) findViewById(R.id.tv_upload_content_filename);

        llBottomContent = (LinearLayout) findViewById(R.id.ll_bottom_content);
        llBottomMyAccount = (LinearLayout) findViewById(R.id.ll_my_settings);
        llBottomHome = (LinearLayout) findViewById(R.id.ll_bottom_home);

        llBottomMyAccount.setOnClickListener(bottomMySettingListener);
        llBottomContent.setOnClickListener(bottomContentListener);
        llBottomHome.setOnClickListener(bottomHomeListener);

        listTxt = (TextView) findViewById(R.id.list_txt);
        listTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,ContentActivity.class));
                finish();
            }
        });

        homeTxt = (TextView) findViewById(R.id.home_txt);
        homeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,HomeActivity.class));
                finish();
            }
        });
        profile = (TextView) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,ProfileActivity.class));
                finish();
            }
        });
        settings = (TextView) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,MyAccountActivity.class));
                finish();
            }
        });

        logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userManager.deleteUser();
                startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
                finish();
            }
        });

        tvUsernmae = (TextView)findViewById(R.id.tv_usernmae);
        riv_imageView = (RoundedImageView) findViewById(R.id.riv_imageView);
        user = userManager.getUser();
        tvUsernmae.setText(user.getFirstName());
        Glide.with(context).load(user.getPicture()).into(riv_imageView);

        //Listeners
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    userDocumentObject.put("categoryId", categoryMap.get(categoryAdapter.getItem(position)));
                    new SubCatgoriesTask().execute(userDocumentObject.getInt("categoryId"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    userDocumentObject.put("subCategoryId", subCategoryMap.get(subCategoryAdapter.getItem(position)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rgContainsVideo.setOnCheckedChangeListener(containsVideoRadioGroup);
        rlUploadCoverImage.setOnClickListener(coverImageUploadListener);
        rlUploadContentFileChooser.setOnClickListener(coverContentUploadChooserListener);
        btSubmit.setOnClickListener(submitClickListener);
        //create a list of items for the spinner.
        //String[] items = new String[]{"Select","Category 1","Category 2","Category 3","Category 4","Category 5"};


        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        //categorySpinner.setAdapter(adapter);

        //create a list of items for the spinner.

        new CategoriesTask().execute();

    }

    RadioGroup.OnCheckedChangeListener containsVideoRadioGroup = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton radioButton = (RadioButton) findViewById(checkedId);
            try {
                System.out.println(radioButton.getText());
                if (radioButton.getText().toString().equals("Yes")) {
                    userDocumentObject.put("containsVideo", true);

                    findViewById(R.id.ll_video_link).setVisibility(View.VISIBLE);
                    findViewById(R.id.ll_upload_content).setVisibility(View.GONE);

                    rlUploadContentFileChooser.setVisibility(View.GONE);
                    tvUploadContentFilename.setVisibility(View.GONE);
                    tvUploadContentFilename.setText("");
                    contentFileToUpload = null;

                    containsVideoLink = true;
                } else {
                    containsVideoLink = false;

                    userDocumentObject.put("containsVideo", false);

                    findViewById(R.id.ll_video_link).setVisibility(View.GONE);
                    findViewById(R.id.ll_upload_content).setVisibility(View.VISIBLE);

                    rlUploadContentFileChooser.setVisibility(View.VISIBLE);
                    tvUploadContentFilename.setVisibility(View.GONE);
                    tvUploadContentFilename.setText("");
                    etVideoLink.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    View.OnClickListener coverImageUploadListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
            //startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            uploadType = "image";

            try {
                if (ContextCompat.checkSelfPermission(getLogoApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                } else {
                    Intent cameraIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //cameraIntent.setType("image/*");
                    //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
                    startActivityForResult(cameraIntent, PICK_IMAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    View.OnClickListener coverContentUploadChooserListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
            //startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            uploadType = "content";
            try {
                if (ContextCompat.checkSelfPermission(getLogoApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                } else {
                    Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //cameraIntent.setType("image/*");
                    //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
                    startActivityForResult(cameraIntent, PICK_CONTENT);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if ("image".equals(uploadType)) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(cameraIntent, PICK_IMAGE);
            } else if ("content".equals(uploadType)) {
                Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(cameraIntent, PICK_CONTENT);
            }
        }
    }

    View.OnClickListener submitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!validateDocumentForm()) {
                return;
            }

            User user = userManager.getUser();

            try {
                userDocumentObject.put("createdById", user.getUserId());
                userDocumentObject.put("title", etTitle.getText().toString());
                userDocumentObject.put("content", etDescription.getText().toString());
                if (!etVideoLink.getText().toString().equals("")) {
                    userDocumentObject.put("videoLink", etVideoLink.getText().toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.print(userDocumentObject);
            if (imageCoverFile != null) {
                new UploadCoverImageTask().execute(user.getUserId());
            } else {
                new UserDocumentProcess().execute(userDocumentObject);
            }

        }
    };

    View.OnClickListener bottomContentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, ContentActivity.class));
            finish();
        }
    };

    View.OnClickListener bottomMySettingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, MyAccountActivity.class));
            finish();
        }
    };

    View.OnClickListener bottomHomeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, HomeActivity.class));
            finish();
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data && null != data.getData()) {

            if (requestCode == PICK_IMAGE) {
                //Bitmap image = (Bitmap) data.getExtras().get("data");
                Uri imageUri = data.getData();

                try {
                    String filePath = ImageUtils.getPath(getApplicationContext(), data.getData());
                    imageCoverFile = new File(filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Bitmap bitmap =null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    FileOutputStream out = new FileOutputStream(imageCoverFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (null!= bitmap) {
                    ivUploadCi.setImageBitmap(bitmap);
                }
            /*String[] projection = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filepath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(filepath);
            ivUploadCi.setImageBitmap(bitmap);
            try {
                String filePath = ImageUtils.getPath(getApplicationContext(),data.getData());
                imageCoverFile = new File(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
                //Drawable drawable = new BitmapDrawable(bitmap);
                //imageView.setBackground(drawable);
            } else if (requestCode == PICK_CONTENT) {
                //Bitmap image = (Bitmap) data.getExtras().get("data");
                Uri imageUri = data.getData();

                System.out.println(ImageUtils.getPath(context, imageUri));
                String[] projection = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String filepath = cursor.getString(columnIndex);
                cursor.close();
                Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                //ivUploadCi.setImageBitmap(bitmap);
                System.out.println(imageUri.getLastPathSegment());
                rlUploadContentFileChooser.setVisibility(View.GONE);
                tvUploadContentFilename.setVisibility(View.VISIBLE);
                tvUploadContentFilename.setText(imageUri.getLastPathSegment());
                try {
                    String filePath = ImageUtils.getPath(getApplicationContext(), data.getData());
                    contentFileToUpload = new File(filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Boolean validateDocumentForm() {
        if (etTitle.getText().toString().trim().equals("")) {
            alertManager.alert("Please enter Title", "Info", context, null);
            return false;
        }

        if (containsVideoLink && etVideoLink.getText().toString().trim().equals("")) {
            alertManager.alert("Please enter Video Link", "Info", context, null);
            return false;
        }
        try {
            if (userDocumentObject.has("categoryId") && userDocumentObject.getInt("categoryId") == 0) {
                alertManager.alert("Please select Category", "Info", context, null);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (userDocumentObject.has("subCategoryId") && userDocumentObject.getInt("subCategoryId") == 0) {
                alertManager.alert("Please select Sub Category", "Info", context, null);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!userDocumentObject.has("containsVideo")) {
            alertManager.alert("Please tell if contains video?", "Info", context, null);
            return false;
        }

        if (userDocumentObject.has("containsVideo") && imageCoverFile == null) {
            alertManager.alert("Please Upload Cover Image ?", "Info", context, null);
            return false;
        }

        if ((userDocumentObject.has("containsVideo") && !containsVideoLink) && (contentFileToUpload == null && etDescription.getText().toString().equals(""))) {
            alertManager.alert("Please Upload Content or Enter Description.", "Info", context, null);
            return false;
        }

        return true;
    }

    class UserDocumentProcess extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);

            //progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(JSONObject... objects) {
            JSONObject jsonObject = objects[0];
            return apiManager.saveUserDocument(jsonObject);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                if (jsonObject != null) {
                    System.out.print(jsonObject);

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("myAccountExists", true);

                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    finish();
                } else {
                    alertManager.alert("Something wrong", "Server error", context, null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class UploadCoverImageTask extends AsyncTask<Integer, JSONObject, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);

            //progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(Integer... integers) {
            return apiManager.saveCoverImage(integers[0], imageCoverFile);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                if (jsonObject != null) {
                    System.out.print(jsonObject);
                    userDocumentObject.put("coverImageUrl", jsonObject.getString("coverImageUrl"));

                    if (contentFileToUpload != null && null != user) {
                        new UploadContentTask().execute(user.getUserId());
                    } else {
                        new UserDocumentProcess().execute(userDocumentObject);
                    }
                } else {
                    alertManager.alert("Something wrong", "Server error", context, null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    class UploadContentTask extends AsyncTask<Integer, JSONObject, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Uploading Content. Please wait...", true);

            //progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(Integer... integers) {
            return apiManager.saveCoverImage(integers[0],contentFileToUpload);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                if (jsonObject != null) {
                    System.out.print(jsonObject);
                    userDocumentObject.put("contentLinkUrl", jsonObject.getString("coverImageUrl"));

                    new UserDocumentProcess().execute(userDocumentObject);
                } else {
                    alertManager.alert("Something wrong", "Server error", context, null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

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
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                if (jsonArray != null && jsonArray.length() > 0) {
                    System.out.print(jsonArray);
                    try {
                        categoryMap = new HashMap<>();

                        if (jsonArray != null && jsonArray.length() > 0) {
                            System.out.print(jsonArray);

                            String[] items = new String[jsonArray.length() + 1];
                            items[0] = "Select Category";
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject subCategiryObj = jsonArray.getJSONObject(i);
                                try {
                                    categoryMap.put(subCategiryObj.getString("name"), subCategiryObj.getInt("profileCategoryId"));
                                    items[i + 1] = subCategiryObj.getString("name");
                                    //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                                    //There are multiple variations of this, but this is the basic variant.
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                categoryAdapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                                //set the spinners adapter to the previously created one.
                                categorySpinner.setAdapter(categoryAdapter);
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
            Integer parentCategoryId = longs[0];
            String queryStr = "?parentCategoryId=" + parentCategoryId;
            return apiManager.findSubCatgories();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                subCategoryMap = new HashMap<>();

                if (jsonArray != null && jsonArray.length() > 0) {
                    System.out.print(jsonArray);

                    String[] subItems = new String[jsonArray.length() + 1];
                    subItems[0] = "Select SubCategory";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject subCategiryObj = jsonArray.getJSONObject(i);
                        try {
                            subCategoryMap.put(subCategiryObj.getString("name"), subCategiryObj.getInt("profileCategoryId"));
                            subItems[i + 1] = subCategiryObj.getString("name");
                            //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                            //There are multiple variations of this, but this is the basic variant.
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        subCategoryAdapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, subItems);
                        //set the spinners adapter to the previously created one.
                        subCategorySpinner.setAdapter(subCategoryAdapter);
                    }
                } else {
                    //alertManager.alert("Something wrong", "Server error", context, null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public File convertBitmapToFile(Bitmap bitmapImage) {
        //create a file to write bitmap data
        File file = new File(context.getCacheDir(), "coverimage");
        try {
            file.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}