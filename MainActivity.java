package com.example.pmitev.chatterbox;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pmitev.chatterbox.activities.SettingsActivity;
import com.example.pmitev.chatterbox.activities.StartActivity;
import com.example.pmitev.chatterbox.activities.UsersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolBar;
    private ViewPager mViewPager;
    private  SectionsPageAdapter mPageAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mUserRef;
    private   FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();




        if(currentUser != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }





        mToolBar  = findViewById(R.id.main_app_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("ChatterBox");


        // Tabs
        mViewPager = findViewById(R.id.main_tab_pager);
        mPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        

    }


    @Override
    public void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.


        if (currentUser == null){

           sendToStart();
        } else  {


            mUserRef.child("online").setValue("true");

        }
    }

    @Override
    protected void onPause(){
        super.onPause();



        if (currentUser != null ) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);


        }
    }


    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

  getMenuInflater().inflate(R.menu.main_manu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       super.onOptionsItemSelected(item);

       if (item.getItemId() == R.id.main_logout_button){


           FirebaseAuth.getInstance().signOut();
           sendToStart();

       }

       if(item.getItemId() == R.id.main_settings_button){

           Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
           startActivity(settingsIntent);



       }

        if(item.getItemId() == R.id.all_users_button){

            Intent usersIntent = new Intent(MainActivity.this,UsersActivity.class);
            startActivity(usersIntent);



        }

        return true;
    }
}
