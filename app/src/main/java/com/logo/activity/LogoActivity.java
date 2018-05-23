package com.logo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.logo.R;
import com.logo.application.LogoApplication;

public class LogoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    DrawerLayout drawer;
    ImageView mImgBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

    }

    protected void NavigtionCreate() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mImgBack = (ImageView) findViewById(R.id.iv_back);
        mImgBack.setOnClickListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public LogoApplication getLogoApplication() {
        return (LogoApplication) getApplication();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (null != drawer && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onButtonClick(View view) {
        drawer.closeDrawer(GravityCompat.START);
        switch (view.getId()) {
            case R.id.home_txt:
                break;
            case R.id.list_txt:
                break;
            case R.id.bookmark_txt:
                break;
            /*case R.id.overview_txt:
                break;
            case R.id.calendar:
                break;
            case R.id.timeline:
                break;*/
            case R.id.profile:
                break;
           /* case R.id.widgets:
                break;*/
            case R.id.settings:
                break;
            case R.id.logout:
                break;
            case R.id.tv_usernmae:
                break;

        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle the camera action
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else if (id == R.id.nav_list) {
            startActivity(new Intent(this, ContentActivity.class));
            finish();
        } else if (id == R.id.nav_upload) {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        } else if (id == R.id.nav_my_acct) {
            startActivity(new Intent(this, MyAccountActivity.class));
            finish();
        } else if (id == R.id.nav_bookmarks) {

        } else if (id == R.id.logout) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                drawer.openDrawer(GravityCompat.START);
                break;
        }
    }
}
