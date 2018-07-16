package com.example.pmitev.chatterbox.adapters;

import com.example.pmitev.chatterbox.R;
import com.example.pmitev.chatterbox.model.Messages;
import com.example.pmitev.chatterbox.services.GetTimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends  RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;

    private DatabaseReference mUserDatabase;
    private  DatabaseReference mMessagesDatabase;

    private FirebaseAuth mAuth;
    private  String mCurrent_user_id;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;

    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);


        return  new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {




       final Messages c = mMessageList.get(position);
        String fromUser = c.getFrom();
        final String messageType = c.getType();
        long messageTime = c.getTime();



        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUser);
        mMessagesDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(fromUser).child(String.valueOf(messageTime));



        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();





                holder.displayName.setText(name);

                Picasso.with(holder.profileImage.getContext()).load(image).placeholder(R.drawable.harlequine).into(holder.profileImage);





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMessagesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {




             //   String messageTime = String.valueOf(dataSnapshot.child("time").getValue().toString());





                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(c.getTime()))));

                holder.displayTime.setText(String.valueOf(dateString));





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(messageType.equals("text")){

            holder.messageText.setText(c.getMessage());
            holder.messageImage.setVisibility(View.INVISIBLE);


        } else {

            holder.messageText.setVisibility(View.INVISIBLE);
            Picasso.with(holder.profileImage.getContext()).load(c.getMessage()).placeholder(R.drawable.harlequine).into(holder.messageImage);


        }


        holder.messageText.setText(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView profileImage;
        public TextView messageText;
        public CircleImageView customBarProfileImage;
        public TextView displayName;
        public  TextView displayTime;
        public ImageView  messageImage;

        public MessageViewHolder(View itemView) {
            super(itemView);

            displayTime = itemView.findViewById(R.id.time_display);
            messageText = itemView.findViewById(R.id.message_bubble);
            profileImage = itemView.findViewById(R.id.message_profile_pic);
            messageImage =  itemView.findViewById(R.id.image_message);
            displayName = itemView.findViewById(R.id.message_name_layout);
            customBarProfileImage = itemView.findViewById(R.id.custom_bar_image);

        }


    }


}
