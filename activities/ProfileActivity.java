package com.example.pmitev.chatterbox.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pmitev.chatterbox.MainActivity;
import com.example.pmitev.chatterbox.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mDeclineBtn;

    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private  DatabaseReference mUserRef;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    private DatabaseReference mRootRef;

    private FirebaseUser mCurrentUser;

    private String mCurrentState;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        mAuth = FirebaseAuth.getInstance();



            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());



        final String  userId = getIntent().getStringExtra("userId");




        mRootRef = FirebaseDatabase.getInstance().getReference();


            mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
            mUsersDatabase.keepSynced(true);

        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");


            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_user_name);
        mProfileStatus = (TextView) findViewById(R.id.profile_user_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.number_of_friends);
        mProfileSendReqBtn = (Button) findViewById(R.id.send_req_btn);
        mDeclineBtn = (Button) findViewById(R.id.decline_btn);


        mCurrentState = "not_friends";

        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String displayName = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(displayName);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.harlequine).into(mProfileImage);

                   if (mCurrentUser.getUid().equals(userId)) {

                        mDeclineBtn.setEnabled(false);
                        mDeclineBtn.setVisibility(View.INVISIBLE);

                        mProfileSendReqBtn.setEnabled(false);
                        mProfileSendReqBtn.setVisibility(View.INVISIBLE);

                    }


                //--------------- FRIENDS LIST / REQUEST FEATURE -----

                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(userId)) {

                            String reqType = dataSnapshot.child(userId).child("request_type").getValue().toString();

                            if (reqType.equals("received")) {

                                mCurrentState = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);

                                mDeclineBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        //-------Decline Friend Request-------


                                        Map cancelRequestMap = new HashMap();
                                        cancelRequestMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + userId, null);
                                        cancelRequestMap.put("Friend_req/" + userId + "/" + mCurrentUser.getUid(), null);

                                        mRootRef.updateChildren(cancelRequestMap, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                if (databaseError == null) {

                                                    mProfileSendReqBtn.setEnabled(true);
                                                    mCurrentState = "not_friends";
                                                    mProfileSendReqBtn.setText("Send Friend Request");
                                                    mDeclineBtn.setEnabled(false);
                                                    mDeclineBtn.setVisibility(View.INVISIBLE);


                                                } else {
                                                    String error = databaseError.getMessage();
                                                    Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_LONG).show();
                                                }

                                                mProfileSendReqBtn.setEnabled(true);

                                            }
                                        });


                                    }
                                });


                            } else if (reqType.equals("sent")) {

                                mCurrentState = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }

                            mProgressDialog.dismiss();


                        } else {


                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(userId)) {

                                        mCurrentState = "friends";
                                        mProfileSendReqBtn.setText("Unfriend this Person");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);

                                    }

                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mProgressDialog.dismiss();

                                }
                            });

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendReqBtn.setEnabled(false);

                // --------------- NOT FRIENDS STATE ------------

                if (mCurrentState.equals("not_friends")) {


                    DatabaseReference newNotificationref = mRootRef.child("Notifications").child(userId).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrentUser.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + userId + "/request_type", "sent");
                    requestMap.put("Friend_req/" + userId + "/" + mCurrentUser.getUid() + "/request_type", "received");
                    requestMap.put("Notifications/" + userId + "/" + newNotificationId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {

                                Toast.makeText(ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                            } else {

                                mCurrentState = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");

                            }

                            mProfileSendReqBtn.setEnabled(true);


                        }
                    });

                }


                //  -------------- CANCEL REQUEST  ------------

                if (mCurrentState.equals("req_sent")) {

                    Map cancelRequestMap = new HashMap();
                    cancelRequestMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + userId, null);
                    cancelRequestMap.put("Friend_req/" + userId + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(cancelRequestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null) {

                                mProfileSendReqBtn.setEnabled(true);
                                mCurrentState = "not_friends";
                                mProfileSendReqBtn.setText("Send Friend Request");
                                mDeclineBtn.setEnabled(false);
                                mDeclineBtn.setVisibility(View.INVISIBLE);


                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_LONG).show();
                            }

                            mProfileSendReqBtn.setEnabled(true);

                        }
                    });

                }


                // ------------ REQ RECEIVED (ACCEPTED)STATE ----------

                if (mCurrentState.equals("req_received")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + mCurrentUser.getUid() + "/" + userId + "/date", currentDate);
                    friendsMap.put("Friends/" + userId + "/" + mCurrentUser.getUid() + "/date", currentDate);


                    friendsMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + userId, null);
                    friendsMap.put("Friend_req/" + userId + "/" + mCurrentUser.getUid(), null);



                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if (databaseError == null) {

                                mProfileSendReqBtn.setEnabled(true);
                                mCurrentState = "friends";
                                mProfileSendReqBtn.setText("Unfriend this Person");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                            }

                        }
                    });

                }


                // ------------ UNFRIEND ---------

                if (mCurrentState.equals("friends")) {

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + userId, null);
                    unfriendMap.put("Friends/" + userId + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if (databaseError == null) {

                                mCurrentState = "not_friends";
                                mProfileSendReqBtn.setText("Send Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                            }

                            mProfileSendReqBtn.setEnabled(true);

                        }
                    });

                }


            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

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

    private void sendToStart() {

        Intent startIntent = new Intent(ProfileActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();

    }

}
