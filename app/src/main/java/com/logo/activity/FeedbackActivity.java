package com.logo.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.logo.R;
import com.logo.application.LogoApplication;
import com.logo.coremanager.CoreManager;
import com.logo.database.manager.UserManager;
import com.logo.services.manager.ApiManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by deepaksingh on 11/06/18.
 */

public class FeedbackActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvToolbar;
    EditText etFeedback;
    Button btnSubmit;

    ApiManager apiManager;
    UserManager userManager;
    CoreManager coreManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvToolbar = toolbar.findViewById(R.id.tv_toolbar_title);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        etFeedback = (EditText) findViewById(R.id.et_feedback);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        init();
        setupToolbar();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etFeedback.getText().toString().trim())) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", userManager.getUser().getUserId());
                        jsonObject.put("feedback", etFeedback.getText().toString().trim());

                        new FeedbackProcess().execute(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(FeedbackActivity.this, "Please enter feedback first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        coreManager = getLogoApplication().getCoreManager();
        userManager = coreManager.getUserManager();
        apiManager = coreManager.getApiManager();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        tvToolbar.setText(getString(R.string.feedback));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public LogoApplication getLogoApplication() {
        return (LogoApplication) getApplication();
    }

    class FeedbackProcess extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(FeedbackActivity.this, "", "Loading. Please wait...", true);

        }

        @Override
        protected JSONObject doInBackground(JSONObject... objects) {
            return apiManager.sendFeedack(objects[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            Toast.makeText(FeedbackActivity.this, "Thanks for your feedback", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
