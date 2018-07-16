package com.example.pmitev.chatterbox.activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pmitev.chatterbox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class StatusActivity extends AppCompatActivity {

    private DatabaseReference mStatusDataBaseReference;
    private FirebaseUser mStatusCurrentUser;

    private TextInputLayout mStatus;
    private Button mSaveBtn;
    private Toolbar mToolbar;
    private ProgressDialog mStatusProgress;
    private  FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mToolbar = findViewById(R.id.status_app_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }

        mStatus = findViewById(R.id.input_status);

        mSaveBtn = findViewById(R.id.save_status_button);
        mStatusCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mStatusCurrentUser.getUid();
        mStatusDataBaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        String statusValue = getIntent().getStringExtra("status_value");
        mStatus.getEditText().setText(statusValue);
        mStatusProgress = new ProgressDialog(StatusActivity.this);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mStatusProgress.setTitle("Saving Changes");
                mStatusProgress.setMessage("Please wait while updating your status!");
                mStatusProgress.setCanceledOnTouchOutside(false);
                mStatusProgress.show();
                String status = mStatus.getEditText().getText().toString();
                mStatusDataBaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            mStatusProgress.dismiss();
                        } else {

                            Toast.makeText(StatusActivity.this, "Saving changes failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });



   }


}