package com.logo.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
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
import com.logo.util.AppUtil;
import com.logo.views.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mandeep on 23/4/18.
 */

public class ContentActivity extends LogoActivity {

    LogoApplication logoApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    InternetManager internetManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    Context context;

    ListView lvContentItems;
    TextView tvNoContent;
    TextView homeTxt,listTxt,profile,settings,logout,tvUsernmae;
    RoundedImageView riv_imageView;
    LinearLayout llBottomProfile,llBottomMyAccount,llBottomHome;
    LinearLayout llContentSingleListItem;
    ContentSectionAdapter contentSectionAdapter;
    JSONArray contentJSONArray;
    Integer rating = 0;
    String comment;
    Long selectedDocumentId;
    Integer selectedDocumentOverallRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
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

        lvContentItems = (ListView) findViewById(R.id.lv_content_list_items);
        tvNoContent = (TextView) findViewById(R.id.tv_no_content);

        llBottomMyAccount = (LinearLayout) findViewById(R.id.ll_my_settings);
        llBottomProfile = (LinearLayout) findViewById(R.id.ll_bottom_profile);
        llBottomHome = (LinearLayout) findViewById(R.id.ll_bottom_home);

        llBottomProfile.setOnClickListener(bottomProfileListener);
        llBottomMyAccount.setOnClickListener(bottomMySettingListener);
        llBottomHome.setOnClickListener(bottomHomeListener);

        listTxt = (TextView) findViewById(R.id.list_txt);
        listTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContentActivity.this,ContentActivity.class));
                finish();
            }
        });

        homeTxt = (TextView) findViewById(R.id.home_txt);
        homeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContentActivity.this,HomeActivity.class));
                finish();
            }
        });
        profile = (TextView) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContentActivity.this,ProfileActivity.class));
                finish();
            }
        });
        settings = (TextView) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContentActivity.this,MyAccountActivity.class));
                finish();
            }
        });

        logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userManager.deleteUser();
                startActivity(new Intent(ContentActivity.this,LoginActivity.class));
                finish();
            }
        });



        User user = userManager.getUser();

        tvUsernmae = (TextView)findViewById(R.id.tv_usernmae);
        riv_imageView = (RoundedImageView) findViewById(R.id.riv_imageView);
        tvUsernmae.setText(user.getFirstName());
        Glide.with(context).load(user.getPicture()).into(riv_imageView);

        Intent receiverIntent = getIntent();
        if (receiverIntent.hasExtra("createdById")) {
            Long createdById = receiverIntent.getLongExtra("createdById", user.getUserId());
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", createdById);
            new ContentProcess().execute(map);
        } else if (receiverIntent.hasExtra(AppUtil.CATEGORY_ID)) {
            HashMap<String, Object> map =new HashMap<>();
            map.put(AppUtil.CATEGORY_ID, receiverIntent.getIntExtra(AppUtil.CATEGORY_ID, 0));
            if (receiverIntent.hasExtra(AppUtil.SUB_CATEGORY_ID)) {
                map.put(AppUtil.SUB_CATEGORY_ID, receiverIntent.getIntExtra(AppUtil.SUB_CATEGORY_ID, 0));
            }
            if (receiverIntent.hasExtra(AppUtil.CONTAINS_VIDEO)) {
                map.put(AppUtil.CONTAINS_VIDEO
                        , receiverIntent.getBooleanExtra(AppUtil.CONTAINS_VIDEO, false));
            }
            new ContentProcess().execute(map);
        } else  {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
//            if (pref.getBoolean("myAccountExists",false))  {
                new FetchMyAccountProcess().execute();
//            }
        }


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
            startActivity(new Intent(context,MyAccountActivity.class));
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

    class ContentProcess extends AsyncTask<HashMap<String, Object>, JSONArray, JSONArray> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
        }

        @Override
        protected JSONArray doInBackground(HashMap<String, Object>... objects) {
            HashMap<String, Object> map = objects[0];

            String queryStr;
            if (map.containsKey(AppUtil.CATEGORY_ID)){
                queryStr = "?" + AppUtil.CATEGORY_ID + "="+ map.get(AppUtil.CATEGORY_ID);
                if (map.containsKey(AppUtil.SUB_CATEGORY_ID)) {
                    queryStr = queryStr + "&" + AppUtil.SUB_CATEGORY_ID + "="+ map.get(AppUtil.SUB_CATEGORY_ID);
                }
                if (map.containsKey(AppUtil.CONTAINS_VIDEO)) {
                    queryStr = queryStr + "&" + AppUtil.CONTAINS_VIDEO + "=" + map.get(AppUtil.CONTAINS_VIDEO);
                }

                return apiManager.findDocumentById(queryStr);
            } else if (map.containsKey("userId")) {
                queryStr = "?userId="+map.get("userId");
                return apiManager.findAllUserDocumentsByCategoryIdAndSubCategoryIdAndNullContentLink(queryStr);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            if(progressDialog!=null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                if (jsonArray != null && jsonArray.length() > 0) {
                    System.out.print(jsonArray);
                    contentSectionAdapter = new ContentSectionAdapter(ContentActivity.this,jsonArray);
                    lvContentItems.setAdapter(contentSectionAdapter);

                    // Set an item click listener for ListView
                    lvContentItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Get the selected item text from ListView
                            //String selectedItem = (LinearLayout) parent.getItemAtPosition(position);

                            // Display the selected item text on TextView
                            //-tv.setText("Your favorite : " + selectedItem);
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(position);
                                FullScreenDialog dialog = new FullScreenDialog(jsonObject);
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                dialog.show(ft, FullScreenDialog.TAG);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    });



                } else {
                    tvNoContent.setVisibility(View.VISIBLE);
                    lvContentItems.setVisibility(View.GONE);
                    //alertManager.alert("Something wrong", "Server error", context, null);
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
            String queryStr = "?createdById="+userManager.getUser().getUserId();
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

                    HashMap<String, Object> map =new HashMap<>();
                    map.put(AppUtil.CATEGORY_ID, jsonObject.opt("mainCourseId"));
                    map.put(AppUtil.SUB_CATEGORY_ID, jsonObject.opt("secondryCourseId"));
                    // Fetch content
                    new ContentProcess().execute(map);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public class ContentSectionAdapter extends BaseAdapter {

        LayoutInflater inflter;
        private ContentActivity contentActivity;
        private JSONArray imageSectionContent;

        public ContentSectionAdapter(ContentActivity context, JSONArray imageSectionContent) {
            this.contentActivity = context;
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
            final ContentSectionHolder holder;

            if (convertView == null) {
                holder = new ContentSectionHolder();
                convertView = this.inflter.inflate(R.layout.adapter_content_screen_items,null);
                holder.ivContentImage = (ImageView)convertView.findViewById(R.id.iv_content_image);
                holder.ivContentAttachment = (ImageView)convertView.findViewById(R.id.iv_content_attachment);
                holder.tvContentTitle = (TextView)convertView.findViewById(R.id.tv_content_title);
                holder.tvContentDesc = (TextView)convertView.findViewById(R.id.tv_content_desc);
                holder.btRate = (Button) convertView.findViewById(R.id.bt_rate);
                holder.tvContentShare = (ImageView) convertView.findViewById(R.id.iv_content_share);
                holder.ratingBar = (RatingBar) convertView.findViewById(R.id.rb_rating);
                holder.btnComment = (Button) convertView.findViewById(R.id.bt_comment);
                holder.btnViewComments = (Button) convertView.findViewById(R.id.bt_view_comment);

                convertView.setTag(holder);
            } else {
                holder = (ContentSectionHolder) convertView.getTag();
            }

            try {
                final JSONObject jsonObject = imageSectionContent.getJSONObject(position);

                holder.tvContentTitle.setText(jsonObject.getString("title"));

                String text = jsonObject.getString("content");
                if (text.length()>24) {
                    text=text.substring(0,24);
                }
                text += "...";
                holder.tvContentDesc.setText(Html.fromHtml(text+"<font color='#76daff'> <u>Read More</u></font>"));
                Glide.with(context).load(jsonObject.getString("coverImageUrl")).into(holder.ivContentImage);

                holder.tvContentDesc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                                FullScreenDialog dialog = new FullScreenDialog(jsonObject);
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                dialog.show(ft, FullScreenDialog.TAG);
                    }
                });

                holder.ratingBar.setRating((float) jsonObject.getInt("overallRating"));


                holder.btRate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RatingDialog alert = new RatingDialog(jsonObject);
                        alert.showDialog(contentActivity, "Error opening dialog");
                    }
                });

                holder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommentDialog commentDialog = new CommentDialog(jsonObject);
                        commentDialog.showDialog(contentActivity, "Error opening dialog");
                    }
                });

                holder.btnViewComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CommentProccess().execute(String.valueOf(jsonObject.optInt("userDocumentId")));
                    }
                });

                holder.ivContentAttachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (jsonObject.optBoolean("containsVideo") && !TextUtils.isEmpty(jsonObject.optString("videoLink"))) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("videoLink")));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setPackage("com.google.android.youtube");
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (!TextUtils.isEmpty(jsonObject.optString("contentLinkUrl"))) {
                            try {
                                String url = jsonObject.getString("contentLinkUrl");
                                if (AppUtil.isImage(url)) {
                                    Intent webview = new Intent(ContentActivity.this, FullScreenImageActivity.class);
                                    webview.putExtra("url", url);
                                    startActivity(webview);
                                } else {
                                    Intent webview = new Intent(ContentActivity.this, WebViewActivity.class);
                                    webview.putExtra("url", url);
                                    startActivity(webview);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });

                holder.tvContentShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "http://download.com");
                        try {
                            contentActivity.startActivity(whatsappIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(contentActivity,"Whatsapp have not been installed.",Toast.LENGTH_SHORT);
                        }
                    }
                });

            } catch (Exception e) {

            }
            return convertView;
        }

        class ContentSectionHolder {
            ImageView ivContentImage,tvContentShare, ivContentAttachment;
            TextView tvContentTitle,tvContentDesc;
            Button btRate, btnComment, btnViewComments;
            RatingBar ratingBar;


        }
    }


    public class RatingDialog {

        JSONObject userDocObj;

        public RatingDialog(JSONObject jsonObject) {
            this.userDocObj =  jsonObject;
        }

        public void showDialog(final ContentActivity activity, String msg){
            rating = 0;
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_rating);

            try {
                selectedDocumentId =  this.userDocObj.getLong("userDocumentId");
                if (this.userDocObj.get("overallRating") == null || this.userDocObj.get("overallRating") == "null") {
                    selectedDocumentOverallRating = 0;
                } else {
                    selectedDocumentOverallRating = this.userDocObj.getInt("overallRating");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            final ImageView ivRating1 = (ImageView) dialog.findViewById(R.id.iv_rating_1);
            final ImageView ivRating2 = (ImageView) dialog.findViewById(R.id.iv_rating_2);
            final ImageView ivRating3 = (ImageView) dialog.findViewById(R.id.iv_rating_3);
            final ImageView ivRating4 = (ImageView) dialog.findViewById(R.id.iv_rating_4);
            final ImageView ivRating5 = (ImageView) dialog.findViewById(R.id.iv_rating_5);

            ivRating1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivRating1.setImageResource(R.drawable.star_yellow);
                    ivRating2.setImageResource(R.drawable.star_white);
                    ivRating3.setImageResource(R.drawable.star_white);
                    ivRating4.setImageResource(R.drawable.star_white);
                    ivRating5.setImageResource(R.drawable.star_white);

                    rating = 1;
                }
            });

            ivRating2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivRating1.setImageResource(R.drawable.star_yellow);
                    ivRating2.setImageResource(R.drawable.star_yellow);
                    ivRating3.setImageResource(R.drawable.star_white);
                    ivRating4.setImageResource(R.drawable.star_white);
                    ivRating5.setImageResource(R.drawable.star_white);

                    rating = 2;
                }
            });

            ivRating3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivRating1.setImageResource(R.drawable.star_yellow);
                    ivRating2.setImageResource(R.drawable.star_yellow);
                    ivRating3.setImageResource(R.drawable.star_yellow);
                    ivRating4.setImageResource(R.drawable.star_white);
                    ivRating5.setImageResource(R.drawable.star_white);

                    rating = 3;

                }
            });

            ivRating4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivRating1.setImageResource(R.drawable.star_yellow);
                    ivRating2.setImageResource(R.drawable.star_yellow);
                    ivRating3.setImageResource(R.drawable.star_yellow);
                    ivRating4.setImageResource(R.drawable.star_yellow);
                    ivRating5.setImageResource(R.drawable.star_white);

                    rating = 4;
                }
            });

            ivRating5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ivRating1.setImageResource(R.drawable.star_yellow);
                    ivRating2.setImageResource(R.drawable.star_yellow);
                    ivRating3.setImageResource(R.drawable.star_yellow);
                    ivRating4.setImageResource(R.drawable.star_yellow);
                    ivRating5.setImageResource(R.drawable.star_yellow);

                    rating = 5;

                }
            });
            Button btRatingOk = (Button) dialog.findViewById(R.id.bt_rating_ok);
            btRatingOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rating == 0) {
                        dialog.dismiss();
                    } else {
                        new RatingProcess().execute();
                        dialog.dismiss();

                    }
                }
            });

            dialog.show();

        }
    }

    class RatingProcess extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
        }

        @Override
        protected JSONObject doInBackground(JSONObject... objects) {
            JSONObject ratingObject = new JSONObject();
            User user = userManager.getUser();

            try {
                ratingObject.put("userDocumentId",selectedDocumentId);
                ratingObject.put("ratedById",user.getUserId());
                ratingObject.put("score",rating);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return apiManager.saveDocumentRating(ratingObject);
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                if (jsonObject != null) {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class CommentDialog {

        JSONObject userDocObj;

        public CommentDialog(JSONObject jsonObject) {
            this.userDocObj =  jsonObject;
        }

        public void showDialog(final ContentActivity activity, String msg){
            rating = 0;
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_content_comment);

            try {
                selectedDocumentId =  this.userDocObj.getLong("userDocumentId");
                if (this.userDocObj.get("overallRating") == null || this.userDocObj.get("overallRating") == "null") {
                    selectedDocumentOverallRating = 0;
                } else {
                    selectedDocumentOverallRating = this.userDocObj.getInt("overallRating");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            final EditText tvComment = (EditText) dialog.findViewById(R.id.tv_comment);
            Button btRatingOk = (Button) dialog.findViewById(R.id.bt_rating_ok);
            btRatingOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(tvComment.getText().toString().trim())) {
                        dialog.dismiss();
                    } else {
                        comment = tvComment.getText().toString().trim();
                        new CommentProcess().execute();
                        dialog.dismiss();

                    }
                }
            });

            dialog.show();

        }
    }

    class CommentProcess extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
        }

        @Override
        protected JSONObject doInBackground(JSONObject... objects) {
            JSONObject ratingObject = new JSONObject();
            User user = userManager.getUser();

            try {
                ratingObject.put("userDocumentId",selectedDocumentId);
                ratingObject.put("commentedById",user.getUserId());
                ratingObject.put("comment",comment);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return apiManager.saveDocumentComment(ratingObject);
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                if (jsonObject != null) {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class CommentProccess extends AsyncTask<String, JSONObject, JSONArray> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ContentActivity.this, "", "Loading. Please wait...", true);
        }

        @Override
        protected JSONArray doInBackground(String... objects) {
            return apiManager.findCommentsById(objects[0]);
        }

        @Override
        protected void onPostExecute(final JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            try {
                if (jsonArray != null && jsonArray.length() > 0) {
                    ViewCommentFragment dialog = new ViewCommentFragment();

                    Bundle bundle = new Bundle();

                    ArrayList<String> commentList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        commentList.add(jsonArray.optJSONObject(i).optString("comment"));
                    }
                    bundle.putStringArrayList("comments", commentList);
                    dialog.setArguments(bundle);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    dialog.show(ft, FullScreenDialog.TAG);
                } else {
                    Toast.makeText(ContentActivity.this, "No Comments Found", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
