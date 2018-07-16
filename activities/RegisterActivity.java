package com.example.pmitev.chatterbox.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pmitev.chatterbox.MainActivity;
import com.example.pmitev.chatterbox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private TextInputEditText mDisplayName;
    private TextInputEditText mEmail;
    private TextInputEditText mPass;
    private Button regButton;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private DatabaseReference mDataBase;
    private  ProgressDialog mRegProgress;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mDisplayName = findViewById(R.id.reg_display_name_text);
        mEmail = findViewById(R.id.reg_email1_text);
        mPass = findViewById(R.id.reg_pass_text);
        regButton = findViewById(R.id.reg_create_button);
        mToolbar = findViewById(R.id.register_app_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRegProgress = new ProgressDialog(this);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String display_name = mDisplayName.getEditableText().toString();
                String email = mEmail.getEditableText().toString();
                String pass = mPass.getEditableText().toString();

                if (!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)) {
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Registering user,please be patient!");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    registerUser(display_name, email, pass);
                }
            }
        });


    }

    private void  registerUser(final String display_name, String email, String pass){

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String  uid =  currentUser.getUid();
                            String currenUserId = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            mDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);



                            mUserDatabase.child(currenUserId).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    HashMap<String, String> mapUsers = new HashMap<>();
                                    mapUsers.put("name", display_name);
                                    mapUsers.put("status", "Hi there I'm using ChatterBox");
                                    mapUsers.put("image", "default");
                                    mapUsers.put("thumb_image", "default");


                                    mDataBase.setValue(mapUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                mRegProgress.dismiss();

                                                String currenUserId = mAuth.getCurrentUser().getUid();
                                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                                mUserDatabase.child(currenUserId).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        Intent registerIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                                        registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                                        startActivity(registerIntent);
                                                        finish();

                                                    }
                                                });


                                            } else {
                                                // If sign in fails, display a message to the user.
                                                mRegProgress.hide();
                                                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();

                                            }

                                            // ...
                                        }
                                    });

                                }
                            });
                        };
                    }
                });
    }

}
