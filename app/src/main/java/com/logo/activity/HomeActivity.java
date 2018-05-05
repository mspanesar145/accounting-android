package com.logo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.logo.R;
import com.logo.application.LogoApplication;
import com.logo.coremanager.CoreManager;
import com.logo.database.manager.UserManager;
import com.logo.services.manager.AlertManager;
import com.logo.services.manager.ApiManager;
import com.logo.services.manager.DeviceManager;
import com.logo.services.manager.InternetManager;
import com.logo.util.LogoUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by mandeep on 15/4/18.
 */

public class HomeActivity extends LogoActivity  {

    LogoApplication logoApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    InternetManager internetManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    Context context;

    ListView lvImageSection,lvVideosVertical;
    HorizontalScrollView horizontalScrollView;
    LinearLayout linearLayout,llVideoSection;
    LinearLayout llBottomProfile,llBottomMyAccount,llBottomContent;
    ImageSectionAdapter imageSectionAdapter;
    VideoSectionAdapter  videoSectionAdapter;
    ImageView ivHomeBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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

        ivHomeBanner = (ImageView) findViewById(R.id.iv_home_banner);
        String img = "http://159.203.95.8:8181/assets/uploads/static/rectangular_banner.jpeg";
        Glide.with(context).load(img).into(ivHomeBanner);

        //lvImageSection = (ListView) findViewById(R.id.lv_image_section);
        lvVideosVertical = (ListView) findViewById(R.id.lv_videos_vertical);
        lvVideosVertical.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.hsv_image_section);
        linearLayout = (LinearLayout) findViewById(R.id.ll_image_section);
        llVideoSection = (LinearLayout) findViewById(R.id.ll_video_section);

        llBottomMyAccount = (LinearLayout) findViewById(R.id.ll_my_settings);
        llBottomProfile = (LinearLayout) findViewById(R.id.ll_bottom_profile);
        llBottomContent = (LinearLayout) findViewById(R.id.ll_bottom_content);

        llBottomProfile.setOnClickListener(bottomProfileListener);
        llBottomMyAccount.setOnClickListener(bottomMySettingListener);
        llBottomContent.setOnClickListener(bottomContentListener);

        new UserDocumentsProcess().execute();
    }

    View.OnClickListener bottomProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context,ProfileActivity.class));
            finish();
        }
    };

    View.OnClickListener bottomMySettingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(HomeActivity.this,MyAccountActivity.class));
            finish();
        }
    };

    View.OnClickListener bottomContentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(HomeActivity.this,ContentActivity.class));
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
                convertView = this.inflter.inflate(R.layout.adapter_horizontal_image_section,null);
                holder.imageView = (ImageView)convertView.findViewById(R.id.iv_image);
                holder.title = (TextView)convertView.findViewById(R.id.tv_title);
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
                convertView = this.inflter.inflate(R.layout.adapter_verticlel_video_section,null);
                holder.imageView = (ImageView)convertView.findViewById(R.id.iv_video_img);
                holder.title = (TextView)convertView.findViewById(R.id.tv_video_title);
                holder.description = (TextView)convertView.findViewById(R.id.tv_video_desc);

                convertView.setTag(holder);
            } else {
                holder = (VideoSectionHolder) convertView.getTag();
            }

            try {
               final  JSONObject jsonObject = videoSectionContent.getJSONObject(position);
                holder.title.setText(jsonObject.getString("title"));
                holder.description.setText(jsonObject.getString("content"));
                Glide.with(context).load(jsonObject.getString("coverImageUrl")).into(holder.imageView);

                holder.description.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(myActivity, ContentActivity.class);
                        try {
                            intent.putExtra("createdById", jsonObject.getLong("createdById"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
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
        }
    }

    class UserDocumentsProcess extends AsyncTask<JSONObject, JSONArray, JSONArray> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);

            //progressDialog.show();

        }

        @Override
        protected JSONArray doInBackground(JSONObject... objects) {
            return apiManager.findAllUserDocuments();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            if(progressDialog!=null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                if (jsonArray != null) {
                    System.out.print(jsonArray);
                    populateImageScrollSection(jsonArray);
                    populateVideoScrollSection(jsonArray);
                    populateContentScrollSection(jsonArray);
                } else {
                    alertManager.alert("Something wrong", "Server error", context, null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void populateVideoScrollSection(JSONArray jsonArray) {
        boolean videoLinksPresent = false;
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
                            System.out.println("Videooo : "+jsonObject.getString("videoLink"));


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
            textView.setTextSize(LogoUtils.convertDpToPixel(12,getApplicationContext()));
            textView.setText("No Links Uploaded Yet");
            llVideoSection.setGravity(View.TEXT_ALIGNMENT_CENTER);
            llVideoSection.addView(textView);
        }
    }

    public void populateImageScrollSection(JSONArray jsonArray) {
        boolean videoLinksPresent = true;
        for (int i=0; i<jsonArray.length(); i++) {
            try {
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                System.out.println("----------- "+jsonObject.get("contentLinkUrl"));
                if (LogoUtils.isEmpty(jsonObject.getString("contentLinkUrl"))) {
                    continue;
                }
                videoLinksPresent = false;

                LinearLayout linearLayoutImageSection = new LinearLayout(HomeActivity.this);
                linearLayoutImageSection.setLayoutParams(new LinearLayout.LayoutParams(LogoUtils.convertDpToPixel(85,getApplicationContext()), LogoUtils.convertDpToPixel(90,getApplicationContext())));
                linearLayoutImageSection.setOrientation(LinearLayout.VERTICAL);

                ImageView imageView = new ImageView(HomeActivity.this);
                imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(LogoUtils.convertDpToPixel(80,getApplicationContext()), LogoUtils.convertDpToPixel(80,getApplicationContext())));
                Glide.with(HomeActivity.this).load(jsonObject.getString("coverImageUrl")).into(imageView);

                TextView textView = new TextView(this);
                textView.setTextSize(8);
                textView.setText(jsonObject.getString("title"));

                linearLayoutImageSection.addView(imageView);
                linearLayoutImageSection.addView(textView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            String url = jsonObject.getString("contentLinkUrl");
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
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
            textView.setTextSize(LogoUtils.convertDpToPixel(12,getApplicationContext()));
            textView.setText("No Documents Uploaded Yet");
            linearLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
            linearLayout.addView(textView);
        }

    }

    public void populateContentScrollSection(JSONArray jsonArray) {
        JSONArray contentSectionArray = new JSONArray();

        boolean conetDescriptionExists = false;
        for (int i=0; i<jsonArray.length(); i++) {
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

            videoSectionAdapter = new VideoSectionAdapter(HomeActivity.this,contentSectionArray);
            lvVideosVertical.setAdapter(videoSectionAdapter);
        } else {
            findViewById(R.id.no_content_desc).setVisibility(View.VISIBLE);
            lvVideosVertical.setVisibility(View.GONE);
        }




    }
}
