package com.example.pmitev.chatterbox.activities;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pmitev.chatterbox.MainActivity;
import com.example.pmitev.chatterbox.R;
import com.example.pmitev.chatterbox.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolBar;
    private RecyclerView usersList;
    private EditText mSearchInputText;
    private ImageButton mSearchImgBtn;

    private LinearLayoutManager mLayoutManager;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private  DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        mAuth = FirebaseAuth.getInstance();

            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mToolBar = findViewById(R.id.users_app_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLayoutManager = new LinearLayoutManager(this);

        usersList = findViewById(R.id.users_list);
        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(mLayoutManager);

        mSearchInputText = findViewById(R.id.search_input_text);
        mSearchImgBtn = findViewById(R.id.search_button);

        mSearchImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchUserName = mSearchInputText.getText().toString();

                if(TextUtils.isEmpty(searchUserName)){

                    Toast.makeText(UsersActivity.this,"Please enter a Name",Toast.LENGTH_LONG).show();
                }
                searchPeople(searchUserName);
            }
        });


    }

    private void sendToStart() {

        Intent startIntent = new Intent(UsersActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();

    }


   private void searchPeople(String searchUserName) {


       Query userNameQuery = mDatabaseReference.orderByChild("name").startAt(searchUserName).endAt(searchUserName + "\uF8FF");



        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(userNameQuery, Users.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {

            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_user_layout, parent, false);

                return new UsersViewHolder(view);
            }





            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, int i, @NonNull Users users) {
                usersViewHolder.setUserName(users.getName());
                usersViewHolder.setUserStatus(users.getStatus());
                usersViewHolder.setUserImage(users.getThumbImage(),getApplicationContext());

                final String userId = getRef(i).getKey();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(userId != null) {
                            Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                            profileIntent.putExtra("userId", userId);
                            startActivity(profileIntent);

                        }
                    }
                });


            }
        };

        usersList.setAdapter(adapter);
        adapter.startListening();


    }


    @Override
    protected void onResume() {
        super.onResume();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){

            sendToStart();
        } else  {


            mUserRef.child("online").setValue("true");

        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);


        }
    }







    public class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setUserName(String name) {

            TextView userNameView = mView.findViewById(R.id.single_user_name);

            userNameView.setText(name);

        }

        public void setUserStatus(String status) {

            TextView userStatusView = mView.findViewById(R.id.single_user_status);
            userStatusView.setText(status);


        }

        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = mView.findViewById(R.id.single_user_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.harlequine).into(userImageView);

        }


    }


    public  static class ChatterBox extends Application {


        @Override
        public void onCreate() {
            super.onCreate();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);




            Picasso.Builder builder = new Picasso.Builder(this);
            builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
            Picasso built = builder.build();
            built.setIndicatorsEnabled(true);
            built.setLoggingEnabled(true);
            Picasso.setSingletonInstance(built);


        }
    }
}