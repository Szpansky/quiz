package com.apps.szpansky.quiz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.szpansky.quiz.DialogsFragments.Information;
import com.apps.szpansky.quiz.Fragments.UserProfileFragment;
import com.apps.szpansky.quiz.SimpleData.QuestionData;
import com.apps.szpansky.quiz.SimpleData.UserData;
import com.apps.szpansky.quiz.Tasks.GetQuestion;
import com.apps.szpansky.quiz.Tasks.GetTopTen;
import com.bumptech.glide.Glide;

/**
 * That class show fragment for user content, implement toolbar and drawer
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    GetQuestion getQuestion;

    UserData userData;
    QuestionData questionData;

    Toolbar toolbar;
    ImageView thumbNail;
    DrawerLayout drawer;
    TextView textViewNavEmail;

    UserProfileFragment userProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        questionData = new QuestionData();
        setViews();
        setToolbar();
        getBundle();
        setUserData();

        userProfileFragment = UserProfileFragment.newInstance(userData);

        getSupportFragmentManager().beginTransaction().add(R.id.content_main,  userProfileFragment).commit();
    }


    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userData = (UserData) bundle.getSerializable("userData");
        } else {
            finish();
        }
    }


    private void setViews() {
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navView = navigationView.getHeaderView(0);
        thumbNail = navView.findViewById(R.id.thumbNail);
        textViewNavEmail = navView.findViewById(R.id.textViewNavEmail);
    }


    private void setToolbar() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }


    private void setUserData() {
        Glide.with(this).load(userData.getUserAvatar()).into(thumbNail);
        textViewNavEmail.setText(userData.getEmail());
    }


    private void startQuestion() {
        questionData = new QuestionData();
        getQuestion = new GetQuestion(getString(R.string.site_address), userData, questionData, getSupportFragmentManager(),getBaseContext());
        getQuestion.execute((Void) null);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            Information information = Information.newInstance("Informacje o aplikacji");
            getSupportFragmentManager().beginTransaction().add(information, "Information").commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.nav_logout: {
                finish();
            }
            break;
            case R.id.nav_ranks:{
                GetTopTen getTopTen = new GetTopTen(getString(R.string.site_address),getSupportFragmentManager());
                getTopTen.execute();
            }
            break;
            case R.id.nav_quest: {
                startQuestion();
            }
            break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
