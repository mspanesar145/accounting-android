package com.logo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.logo.R;
import com.logo.application.LogoApplication;
import com.logo.bo.User;
import com.logo.coremanager.CoreManager;
import com.logo.database.manager.UserManager;
import com.logo.services.manager.AlertManager;
import com.logo.services.manager.ApiManager;
import com.logo.services.manager.DeviceManager;
import com.logo.services.manager.InternetManager;
import com.logo.util.AppUtil;
import com.logo.util.LogoUtils;
import com.logo.views.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mandeep on 15/4/18.
 */

public class HomeActivity extends LogoActivity {
    LogoApplication logoApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    InternetManager internetManager;
    DeviceManager deviceManager;
    ApiManager apiManager;
    Context context;
    ListView lvImageSection, lvVideosVertical;
    ScrollView scrollHome;
    HorizontalScrollView horizontalScrollView;
    LinearLayout linearLayout, llVideoSection;
    LinearLayout llBottomProfile, llBottomMyAccount, llBottomContent, llBottomBookmark;
    ImageSectionAdapter imageSectionAdapter;
    VideoSectionAdapter videoSectionAdapter;
    ImageView ivHomeBanner, ivSearch;
    TextView homeTxt,listTxt,profile,settings,logout,tvUsernmae;
    RoundedImageView riv_imageView;
    TextView tvViewAllVideo, tvViewAllImage, txtFeedback, txtBookmark;
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        NavigtionCreate();
        init();

    }

    @Override
    protected void onStart() {
        super.onStart();

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

        ivHomeBanner = (ImageView) findViewById(R.id.iv_home_banner);
        String img = "http://159.203.95.8:8181/assets/uploads/static/rectangular_banner.jpeg";
        Glide.with(context).load(img).into(ivHomeBanner);

        //lvImageSection = (ListView) findViewById(R.id.lv_image_section);
        lvVideosVertical = (ListView) findViewById(R.id.lv_videos_vertical);

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.hsv_image_section);
        linearLayout = (LinearLayout) findViewById(R.id.ll_image_section);
        llVideoSection = (LinearLayout) findViewById(R.id.ll_video_section);

        llBottomMyAccount = (LinearLayout) findViewById(R.id.ll_my_settings);
        llBottomProfile = (LinearLayout) findViewById(R.id.ll_bottom_profile);
        llBottomContent = (LinearLayout) findViewById(R.id.ll_bottom_content);
        llBottomBookmark = (LinearLayout) findViewById(R.id.ll_bottom_bookmark);
        scrollHome = (ScrollView) findViewById(R.id.scroll_home);
        tvViewAllVideo = (TextView) findViewById(R.id.view_all_video);
        tvViewAllImage = (TextView) findViewById(R.id.view_all_image);
        etSearch = (EditText) findViewById(R.id.et_search);
        txtFeedback = (TextView) findViewById(R.id.feedback);
        txtBookmark = (TextView) findViewById(R.id.bookmark_txt);

        llBottomProfile.setOnClickListener(bottomProfileListener);
        llBottomMyAccount.setOnClickListener(bottomMySettingListener);
        llBottomContent.setOnClickListener(bottomContentListener);
        llBottomBookmark.setOnClickListener(bottomBookmarkListener);

        listTxt = (TextView) findViewById(R.id.list_txt);
        ivSearch = (ImageView) findViewById(R.id.iv_search);

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etSearch.getVisibility() == View.VISIBLE){
                    etSearch.setVisibility(View.GONE);
                } else {
                    etSearch.setVisibility(View.VISIBLE);
                }
            }
        });

        listTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ContentActivity.class));
                finish();
            }
        });

        homeTxt = (TextView) findViewById(R.id.home_txt);
        homeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,HomeActivity.class));
                finish();
            }
        });
        profile = (TextView) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
                finish();
            }
        });
        settings = (TextView) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,MyAccountActivity.class));
                finish();
            }
        });

        txtFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                startActivity(new Intent(HomeActivity.this,FeedbackActivity.class));
            }
        });
        txtBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                startActivity(new Intent(HomeActivity.this,BookmarkActivity.class));
                finish();
            }
        });

        logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userManager.deleteUser();
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                finish();
            }
        });

        tvUsernmae = (TextView)findViewById(R.id.tv_usernmae);
        riv_imageView = (RoundedImageView) findViewById(R.id.riv_imageView);
        User user = userManager.getUser();
        tvUsernmae.setText(user.getFirstName());
        Glide.with(context).load(user.getPicture()).into(riv_imageView);
        // Get user data
        new UserDocumentsProcess().execute(user.getUserId());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(etSearch.getText().toString().trim())) {
                    new SearchDocumentsProcess().execute(userManager.getUser().getUserId()
                            , etSearch.getText().toString().trim());
                } else {
                    new SearchDocumentsProcess().execute(userManager.getUser().getUserId());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    View.OnClickListener bottomProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, ProfileActivity.class));
            finish();
        }
    };

    View.OnClickListener bottomMySettingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(HomeActivity.this, MyAccountActivity.class));
            finish();
        }
    };

    View.OnClickListener bottomBookmarkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(HomeActivity.this, BookmarkActivity.class));
            finish();
        }
    };

    View.OnClickListener bottomContentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserManager userManager = getLogoApplication().getCoreManager().getUserManager();
            Intent intent = new Intent(HomeActivity.this, ContentActivity.class);
            intent.putExtra("createdById", userManager.getUser().getUserId());
            startActivity(intent);
            finish();
        }
    };

    public class ImageSectionAdapter extends BaseAdapter {

        LayoutInflater inflter;
        ImageSectionHolder holder;
        private HomeActivity myActivity;
        private JSONArray imageSectionContent;

        public ImageSectionAdapter(HomeActivity context, JSONArray imageSectionContent) {
            this.myActivity = context;
            this.imageSectionContent = imageSectionContent;
            this.inflter = (LayoutInflater.from(getApplicationContext()));
        }

        @Override
        public int getCount() {
            return imageSectionContent.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return imageSectionContent.get(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                holder = new ImageSectionHolder();
                convertView = this.inflter.inflate(R.layout.adapter_horizontal_image_section, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_image);
                holder.title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            } else {
                holder = (ImageSectionHolder) convertView.getTag();
            }

            try {
                JSONObject jsonObject = imageSectionContent.getJSONObject(position);
                holder.title.setText(jsonObject.getString("title"));
                Glide.with(context).load(jsonObject.getString("image")).into(holder.imageView);
            } catch (Exception e) {

            }
            return convertView;
        }

        class ImageSectionHolder {
            ImageView imageView;
            TextView title;
        }
    }

    public class VideoSectionAdapter extends BaseAdapter {

        LayoutInflater inflter;
        VideoSectionHolder holder;
        private HomeActivity myActivity;
        private JSONArray videoSectionContent;

        public VideoSectionAdapter(HomeActivity context, JSONArray imageSectionContent) {
            this.myActivity = context;
            this.videoSectionContent = imageSectionContent;
            this.inflter = (LayoutInflater.from(getApplicationContext()));
        }

        @Override
        public int getCount() {
            return videoSectionContent.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return videoSectionContent.get(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                holder = new VideoSectionHolder();
                convertView = this.inflter.inflate(R.layout.adapter_verticlel_video_section, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_video_img);
                holder.title = (TextView) convertView.findViewById(R.id.tv_video_title);
                holder.description = (TextView) convertView.findViewById(R.id.tv_video_desc);
                holder.layoutContent = (LinearLayout) convertView.findViewById(R.id.ll_video_horizontal_row);

                convertView.setTag(holder);
            } else {
                holder = (VideoSectionHolder) convertView.getTag();
            }

            try {
                final JSONObject jsonObject = videoSectionContent.getJSONObject(position);
                holder.title.setText(jsonObject.getString("title"));
                holder.description.setText(jsonObject.getString("content"));
                Glide.with(context).load(jsonObject.getString("coverImageUrl")).into(holder.imageView);

                holder.layoutContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            if (jsonObject != null) {
                                Intent intent = new Intent(HomeActivity.this, ContentActivity.class);
                                intent.putExtra(AppUtil.CATEGORY_ID, jsonObject.optInt(AppUtil.CATEGORY_ID));
                                intent.putExtra(AppUtil.SUB_CATEGORY_ID, jsonObject.optInt(AppUtil.SUB_CATEGORY_ID));
                                startActivity(intent);
                                finish();
                            }

//                        Intent intent = new Intent(myActivity, ContentActivity.class);
//                        try {
//                            intent.putExtra("createdById", jsonObject.getLong("createdById"));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        startActivity(intent);
                    }
                });
            } catch (Exception e) {

            }
            return convertView;
        }

        class VideoSectionHolder {
            ImageView imageView;
            TextView title;
            TextView description;
            LinearLayout layoutContent;
        }
    }

    class UserDocumentsProcess extends AsyncTask<Object, JSONObject, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);

            //progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            if (objects.length == 2) {
                return apiManager.findTopTenDocuments((Integer) objects[0], String.valueOf(objects[1]));
            } else {
                return apiManager.findTopTenDocuments((Integer) objects[0], null);
            }
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
//                    System.out.print(jsonArray);
                    populateImageScrollSection(jsonObject.optJSONArray("image"));
                    populateVideoScrollSection(jsonObject.optJSONArray("video"));
                    populateContentScrollSection(jsonObject.optJSONArray("content"));
                } else {
                    alertManager.alert("Something wrong", "Server error", context, null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class SearchDocumentsProcess extends AsyncTask<Object, JSONObject, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            if (objects.length == 2) {
                return apiManager.findTopTenDocuments((Integer) objects[0], String.valueOf(objects[1]));
            } else {
                return apiManager.findTopTenDocuments((Integer) objects[0], null);
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {
                if (jsonObject != null) {
//                    System.out.print(jsonArray);
                    populateImageScrollSection(jsonObject.optJSONArray("image"));
                    populateVideoScrollSection(jsonObject.optJSONArray("video"));
                    populateContentScrollSection(jsonObject.optJSONArray("content"));
                }
//                else {
//                    alertManager.alert("Something wrong", "Server error", context, null);
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void populateVideoScrollSection(final JSONArray jsonArray) {
        boolean videoLinksPresent = false;
        llVideoSection.removeAllViews();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.get("containsVideo") != null && !jsonObject.getBoolean("containsVideo")) {
                    continue;
                }
                videoLinksPresent = true;
                LinearLayout linearLayoutImageSection = new LinearLayout(HomeActivity.this);
                linearLayoutImageSection.setLayoutParams(new LinearLayout.LayoutParams(LogoUtils.convertDpToPixel(110, getApplicationContext()), LogoUtils.convertDpToPixel(110, getApplicationContext())));
                linearLayoutImageSection.setOrientation(LinearLayout.VERTICAL);

                RelativeLayout imageRelativeLayout = new RelativeLayout(HomeActivity.this);
                imageRelativeLayout.setLayoutParams(new LinearLayout.LayoutParams(LogoUtils.convertDpToPixel(100, getApplicationContext()), LogoUtils.convertDpToPixel(100, getApplicationContext())));

                ImageView imageView = new ImageView(HomeActivity.this);
                imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(LogoUtils.convertDpToPixel(100, getApplicationContext()), LogoUtils.convertDpToPixel(100, getApplicationContext())));
                Glide.with(HomeActivity.this).load(jsonObject.getString("coverImageUrl")).into(imageView);

                ImageView playIcon = new ImageView(HomeActivity.this);
                playIcon.setImageResource(R.drawable.video_play_icon);
                RelativeLayout.LayoutParams playIconLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                playIconLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                playIcon.setLayoutParams(playIconLayoutParams);

                TextView textView = new TextView(this);
                textView.setTextSize(8);
                textView.setText(jsonObject.getString("title"));

                imageRelativeLayout.addView(imageView);
                imageRelativeLayout.addView(playIcon);
                linearLayoutImageSection.addView(imageRelativeLayout);
                linearLayoutImageSection.addView(textView);

                linearLayoutImageSection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
//                            System.out.println("Videooo : " + jsonObject.getString("videoLink"));


                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("videoLink")));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setPackage("com.google.android.youtube");
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                llVideoSection.addView(linearLayoutImageSection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!videoLinksPresent) {

            RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            TextView textView = new TextView(this);
            textView.setLayoutParams(textViewLayoutParams);
            textView.setTextSize(LogoUtils.convertDpToPixel(12, getApplicationContext()));
            textView.setText("No Links Uploaded Yet");
            llVideoSection.setGravity(View.TEXT_ALIGNMENT_CENTER);
            llVideoSection.addView(textView);
        } else {
//            if (jsonArray.length() > 10) {
                tvViewAllVideo.setVisibility(View.VISIBLE);
                tvViewAllVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (jsonArray.length() != 0) {
                            JSONObject object = jsonArray.optJSONObject(0);
                            if (object != null) {
                                Intent intent = new Intent(HomeActivity.this, ContentActivity.class);
                                intent.putExtra(AppUtil.CATEGORY_ID, object.optInt(AppUtil.CATEGORY_ID));
                                intent.putExtra(AppUtil.SUB_CATEGORY_ID, object.optInt(AppUtil.SUB_CATEGORY_ID));
                                intent.putExtra(AppUtil.CONTAINS_VIDEO, object.optBoolean(AppUtil.CONTAINS_VIDEO));
                                startActivity(intent);
                                finish();
                            }
                        }

                    }
                });
//            }
        }
    }

    public void populateImageScrollSection(final JSONArray jsonArray) {
        boolean videoLinksPresent = true;
        linearLayout.removeAllViews();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
//                System.out.println("----------- " + jsonObject.get("contentLinkUrl"));
                if (LogoUtils.isEmpty(jsonObject.getString("contentLinkUrl"))) {
                    continue;
                }
                videoLinksPresent = false;

                View linearLayoutImageSection = LayoutInflater.from(this).inflate(R.layout.item_images, null);

                ImageView imageView = linearLayoutImageSection.findViewById(R.id.iv_images);
                final ProgressBar mProgressBar = linearLayoutImageSection.findViewById(R.id.progress_bar);

                Glide.with(HomeActivity.this)
                        .load(jsonObject.getString("coverImageUrl"))
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                if (null != mProgressBar) {
                                    mProgressBar.setVisibility(View.GONE);
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                if (null != mProgressBar) {
                                    mProgressBar.setVisibility(View.GONE);
                                }
                                return false;
                            }
                        })
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);

                TextView textView = linearLayoutImageSection.findViewById(R.id.tv_title);
                textView.setText(jsonObject.getString("title"));

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            String url = jsonObject.getString("contentLinkUrl");
                            //Intent i = new Intent(Intent.ACTION_VIEW);
                            //i.setData(Uri.parse(url));
                            //startActivity(i);
                            /*webView = (WebView) findViewById(R.id.webview);
                            webView.setVisibility(View.VISIBLE);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.loadUrl(url);*/
                            if (jsonObject != null) {
                                Intent intent = new Intent(HomeActivity.this, ContentActivity.class);
                                intent.putExtra(AppUtil.CATEGORY_ID, jsonObject.optInt(AppUtil.CATEGORY_ID));
                                intent.putExtra(AppUtil.SUB_CATEGORY_ID, jsonObject.optInt(AppUtil.SUB_CATEGORY_ID));
                                startActivity(intent);
                                finish();
                            }
//                            if (!TextUtils.isEmpty(url)) {
//                                if (AppUtil.isImage(url)) {
//                                    Intent webview = new Intent(HomeActivity.this, FullScreenImageActivity.class);
//                                    webview.putExtra("url", url);
//                                    startActivity(webview);
//                                } else {
//                                    Intent webview = new Intent(HomeActivity.this, WebViewActivity.class);
//                                    webview.putExtra("url", url);
//                                    startActivity(webview);
//                                }
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                linearLayout.addView(linearLayoutImageSection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (videoLinksPresent) {

            RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            TextView textView = new TextView(this);
            textView.setLayoutParams(textViewLayoutParams);
            textView.setTextSize(LogoUtils.convertDpToPixel(12, getApplicationContext()));
            textView.setText("No Documents Uploaded Yet");
            linearLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
            linearLayout.addView(textView);
        } else {
//            if (jsonArray.length() > 10) {
                tvViewAllImage.setVisibility(View.VISIBLE);
                tvViewAllImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (jsonArray.length() != 0) {
                            JSONObject object = jsonArray.optJSONObject(0);
                            if (object != null) {
                                Intent intent = new Intent(HomeActivity.this, ContentActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
//            }
        }

    }

    public void populateContentScrollSection(final JSONArray jsonArray) {
        JSONArray contentSectionArray = new JSONArray();
        videoSectionAdapter = null;
        lvVideosVertical.requestLayout();

        boolean conetDescriptionExists = false;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (!(!jsonObject.getBoolean("containsVideo") && LogoUtils.isEmpty(jsonObject.getString("contentLinkUrl")))) {
                    continue;
                }
                conetDescriptionExists = true;

                contentSectionArray.put(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (conetDescriptionExists) {
            findViewById(R.id.no_content_desc).setVisibility(View.GONE);
            lvVideosVertical.setVisibility(View.VISIBLE);

            videoSectionAdapter = new VideoSectionAdapter(HomeActivity.this, contentSectionArray);
            lvVideosVertical.setAdapter(videoSectionAdapter);
            AppUtil.updateListViewHeight(lvVideosVertical);
            scrollHome.smoothScrollTo(0, 0);
        } else {
            findViewById(R.id.no_content_desc).setVisibility(View.VISIBLE);
            lvVideosVertical.setVisibility(View.GONE);
        }


    }
}
