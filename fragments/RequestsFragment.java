package com.example.pmitev.chatterbox.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pmitev.chatterbox.R;
import com.example.pmitev.chatterbox.activities.ChatActivity;
import com.example.pmitev.chatterbox.activities.ProfileActivity;
import com.example.pmitev.chatterbox.activities.SettingsActivity;
import com.example.pmitev.chatterbox.model.Conversation;
import com.example.pmitev.chatterbox.model.Requests;
import com.example.pmitev.chatterbox.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match


    private View mView;
    private RecyclerView mRequestList;
    private DatabaseReference mFriendsRequestReferenceCurrentUserId;
    private  DatabaseReference mUsersDatabase;
    private  DatabaseReference mFriendsDataBase;
    private  DatabaseReference mFriendRequest;
    private  DatabaseReference mRootRef;



    private FirebaseAuth mAuth;
    private  String mCurrentUserId;
    private  LinearLayoutManager  mLayoutManager;







    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_requests, container, false);
        mRequestList = mView.findViewById(R.id.requests_list);

      mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();






        mFriendsRequestReferenceCurrentUserId = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrentUserId);
        mFriendsRequestReferenceCurrentUserId.keepSynced(true);


        mFriendsDataBase = FirebaseDatabase.getInstance().getReference().child("Friends");


        mFriendRequest = FirebaseDatabase.getInstance().getReference().child("Friend_req");



            mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");






        mRequestList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRequestList.setLayoutManager(mLayoutManager);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Requests> options =
                new FirebaseRecyclerOptions.Builder<Requests>()
                        .setQuery(mFriendsRequestReferenceCurrentUserId, Requests.class)
                        .build();

        FirebaseRecyclerAdapter<Requests,RequestsViewHolder> requestsAdapter = new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(options) {


            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_user_request_layout, parent, false);

                return new RequestsViewHolder(view);


            }


            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Requests model) {


                final String listUsersId = getRef(position).getKey();

                final DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();


                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {


                            final String requestType = dataSnapshot.getValue().toString();


                            if (requestType.equals("sent")){

                              holder.mAcceptButton.setText("Request Sent");
                               holder.mDeclineButton.setVisibility(View.INVISIBLE);



                           mUsersDatabase.child(listUsersId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        final String userStatus = dataSnapshot.child("status").getValue().toString();
                                        final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                                        holder.setName(userName);
                                        holder.setStatus(userStatus);
                                        holder.setUserImage(userThumb, getContext());


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });






                            } else   if  (requestType.equals("received")) {

                                mUsersDatabase.child(listUsersId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        final String userStatus = dataSnapshot.child("status").getValue().toString();
                                        final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                                        holder.setName(userName);
                                        holder.setStatus(userStatus);
                                        holder.setUserImage(userThumb, getContext());



                                        holder.mAcceptButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {



                                                final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                                                Map friendsMap = new HashMap();
                                                friendsMap.put("Friends/" + mCurrentUserId + "/" + listUsersId + "/date", currentDate);
                                                friendsMap.put("Friends/" + listUsersId + "/" + mCurrentUserId + "/date", currentDate);


                                                friendsMap.put("Friend_req/" + mCurrentUserId + "/" + listUsersId, null);
                                                friendsMap.put("Friend_req/" + listUsersId + "/" + mCurrentUserId, null);



                                                mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                                        if (databaseError == null) {

                                                            Toast.makeText(getContext(), "Friend Request Accepted Successfully", Toast.LENGTH_SHORT).show();

                                                        } else {

                                                            String error = databaseError.getMessage();

                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();


                                                        }

                                                    }
                                                });


                                            }
                                        });



                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                holder.mDeclineButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        Map cancelMap = new HashMap();
                                        cancelMap.put("Friend_req/" + mCurrentUserId + "/" + listUsersId, null);
                                        cancelMap.put("Friend_req/" + listUsersId + "/" + mCurrentUserId, null);

                                        mRootRef.updateChildren(cancelMap, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                                if (databaseError == null) {

                                                    Toast.makeText(getContext(),"Cancel Friendship Successfully", Toast.LENGTH_SHORT).show();

                                                } else {

                                                    String error = databaseError.getMessage();

                                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();


                                                }



                                            }
                                        });
                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });






            }



        };

        mRequestList.setAdapter(requestsAdapter);
        requestsAdapter.startListening();
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        private   Button mAcceptButton;
        private  Button mDeclineButton;
        View mView;

        public RequestsViewHolder(View itemView) {
            super(itemView);

            mAcceptButton = itemView.findViewById(R.id.accept_button);
            mDeclineButton = itemView.findViewById(R.id.decline_button);
            mView = itemView;

        }

        public void setStatus(String status){
            TextView userStatusView = mView.findViewById(R.id.single_user_request_status);
            userStatusView.setText(status);

        }

        public void  setName(String name){

            TextView userName = mView.findViewById(R.id.single_user_request_name);
            userName.setText(name);

        }


        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = mView.findViewById(R.id.single_user_request_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.harlequine).into(userImageView);

        }

    }

}
