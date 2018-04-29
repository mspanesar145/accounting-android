package com.logo.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
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

    private static int CAMERA_PIC_REQUEST = 100,PICK_IMAGE = 101,PICK_CONTENT = 102;

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
    RelativeLayout rlUploadCoverImage,rlUploadContentFileChooser;
    LinearLayout llBottomMyAccount,llBottomContent,llBottomHome;
    Button btSubmit;
    ImageView ivUploadCi;
    TextView tvUploadContentFilename;
    Boolean containsVideoLink = false;
    String uploadType = "";
    File imageCoverFile = null,contentFileToUpload = null;

    Map<String,Integer> categoryMap,subCategoryMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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

        //Listeners
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    userDocumentObject.put("categoryId", categorySpinner.getItemIdAtPosition(position));
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
                    userDocumentObject.put("subCategoryId", subCategorySpinner.getItemIdAtPosition(position));
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
        String[] items = new String[]{"Select","Category 1","Category 2","Category 3","Category 4","Category 5"};


        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        categorySpinner.setAdapter(adapter);

        //create a list of items for the spinner.
        String[] subItems = new String[]{"Select","Sub Category 1","Sub Category 2","Sub Category 3","Sub Category 4","Sub Category 5"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        subCategorySpinner.setAdapter(adapter);
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
            if (ContextCompat.checkSelfPermission(getLogoApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            } else {
                    Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    cameraIntent.setType("image/*");
                    //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
                    startActivityForResult(cameraIntent, PICK_IMAGE);
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
            if (ContextCompat.checkSelfPermission(getLogoApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            } else {
                Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT);
                cameraIntent.setType("image/*");
                //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
                startActivityForResult(cameraIntent, PICK_CONTENT);
            }


        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri data = Uri.fromFile(Environment.getExternalStorageDirectory());
            String type = "image/*";
            cameraIntent.setDataAndType(data, type);
            if ("image".equals(uploadType)) {
                startActivityForResult(cameraIntent, PICK_IMAGE);
            } else if ("content".equals(uploadType)) {
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
                userDocumentObject.put("createdById",user.getUserId());
                userDocumentObject.put("title",etTitle.getText().toString());
                userDocumentObject.put("content",etDescription.getText().toString());
                if (!etVideoLink.getText().toString().equals("")) {
                    userDocumentObject.put("videoLink",etVideoLink.getText().toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.print(userDocumentObject);
            if (imageCoverFile != null) {
                new UploadCoverImageTask().execute();
            } else {
                new UserDocumentProcess().execute(userDocumentObject);
            }

        }
    };

    View.OnClickListener bottomContentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
            if (pref.getBoolean("myAccountExists",false))  {
                Toast.makeText(getApplicationContext(),"Please save your data first",Toast.LENGTH_LONG).show();
                return;
            }

            startActivity(new Intent(context,ContentActivity.class));
            finish();
        }
    };

    View.OnClickListener bottomMySettingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
            if (pref.getBoolean("myAccountExists",false))  {
                Toast.makeText(getApplicationContext(),"Please save your data first",Toast.LENGTH_LONG).show();
                return;
            }

            startActivity(new Intent(context,MyAccountActivity.class));
            finish();
        }
    };

    View.OnClickListener bottomHomeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
            if (pref.getBoolean("myAccountExists",false))  {
                Toast.makeText(getApplicationContext(),"Please save your data first",Toast.LENGTH_LONG).show();
                return;
            }

            startActivity(new Intent(context,HomeActivity.class));
            finish();
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            //Bitmap image = (Bitmap) data.getExtras().get("data");
            Uri imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ivUploadCi.setImageBitmap(bitmap);

            try {
                String filePath = ImageUtils.getPath(getApplicationContext(),data.getData());
                imageCoverFile = new File(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_CONTENT) {
            //Bitmap image = (Bitmap) data.getExtras().get("data");
            Uri imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //ivUploadCi.setImageBitmap(bitmap);
            System.out.println(imageUri.getLastPathSegment());
            rlUploadContentFileChooser.setVisibility(View.GONE);
            tvUploadContentFilename.setVisibility(View.VISIBLE);
            tvUploadContentFilename.setText(imageUri.getLastPathSegment());
            try {
                String filePath = ImageUtils.getPath(getApplicationContext(),data.getData());
                contentFileToUpload = new File(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean validateDocumentForm() {
        if (etTitle.getText().toString().trim().equals("")) {
            alertManager.alert("Please enter Title","Info", context,null);
            return false;
        }

        if (containsVideoLink && etVideoLink.getText().toString().trim().equals("")) {
            alertManager.alert("Please enter Video Link", "Info", context, null);
            return false;
        }
        try {
            if (userDocumentObject.has("categoryId") && userDocumentObject.getInt("categoryId") == 0) {
                alertManager.alert("Please select Category","Info", context,null);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (userDocumentObject.has("subCategoryId") && userDocumentObject.getInt("subCategoryId") == 0) {
                alertManager.alert("Please select Sub Category","Info", context,null);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!userDocumentObject.has("containsVideo")) {
            alertManager.alert("Please tell if contains video?","Info", context,null);
            return false;
        }

        if (userDocumentObject.has("containsVideo") && !containsVideoLink && imageCoverFile == null) {
            alertManager.alert("Please Upload Cover Image ?","Info", context,null);
            return false;
        }

        if ((userDocumentObject.has("containsVideo") && !containsVideoLink) && (contentFileToUpload == null && etDescription.getText().toString().equals(""))) {
            alertManager.alert("Please Upload Content or Enter Description.","Info", context,null);
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
            if(progressDialog!=null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                if (jsonObject != null) {
                    System.out.print(jsonObject);

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("myAccountExists", true);

                    startActivity(new Intent(ProfileActivity.this,HomeActivity.class));
                    finish();
                } else {
                    alertManager.alert("Something wrong", "Server error", context, null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class UploadCoverImageTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);

            //progressDialog.show();

        }
        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {
            return apiManager.saveCoverImage(imageCoverFile);
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
                    System.out.print(jsonObject);
                    userDocumentObject.put("coverImageUrl",jsonObject.getString("coverImageUrl"));
                    new UserDocumentProcess().execute(userDocumentObject);
                } else {
                    alertManager.alert("Something wrong", "Server error", context, null);
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