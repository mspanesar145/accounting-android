package com.logo.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.logo.views.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
            new ContentProcess().execute(createdById);
        } else {
            if (user != null) {
                Long userId = Long.valueOf(user.getUserId());
                new ContentProcess().execute(userId);
            }
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

    class ContentProcess extends AsyncTask<Long, JSONArray, JSONArray> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
        }

        @Override
        protected JSONArray doInBackground(Long... longs) {
            Long createdById = longs[0];
            //User user = userManager.getUser();
            String queryStr = "?userId="+createdById;
            return apiManager.findAllUserDocumentsByCategoryIdAndSubCategoryIdAndNullContentLink(queryStr);
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
                holder.tvContentTitle = (TextView)convertView.findViewById(R.id.tv_content_title);
                holder.tvContentDesc = (TextView)convertView.findViewById(R.id.tv_content_desc);
                holder.btRate = (Button) convertView.findViewById(R.id.bt_rate);
                holder.tvContentShare = (ImageView) convertView.findViewById(R.id.iv_content_share);

                convertView.setTag(holder);
            } else {
                holder = (ContentSectionHolder) convertView.getTag();
            }

            try {
                final JSONObject jsonObject = imageSectionContent.getJSONObject(position);

                holder.tvContentTitle.setText(jsonObject.getString("title"));

                String text = jsonObject.getString("content");
                if (text.length()>50) {
                    text=text.substring(0,50);
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

                holder.btRate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RatingDialog alert = new RatingDialog(jsonObject);
                        alert.showDialog(contentActivity, "Error opening dialog");
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
            ImageView ivContentImage,tvContentShare;
            TextView tvContentTitle,tvContentDesc;
            Button btRate;


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


            TextView tvOverAllRatingText = (TextView) dialog.findViewById(R.id.tv_overall_rating_text);
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
            tvOverAllRatingText.setText("Overall Rating : "+selectedDocumentOverallRating);
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
}
