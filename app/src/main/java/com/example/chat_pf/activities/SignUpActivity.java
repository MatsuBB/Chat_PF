package com.example.chat_pf.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat_pf.R;
import com.example.chat_pf.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.text.method.PasswordTransformationMethod;

import java.util.Date;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, signupName, confirmPassword;
    private Button signupButton;
    private TextView loginRedirectText;
    private final String TAG = "DEBUGGING";

//    FirebaseDatabase database;
//    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        SignUpActivity.DatabaseOperations mDBO = new SignUpActivity.DatabaseOperations();
        auth = FirebaseAuth.getInstance();

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.confirm_password);
        signupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String name = signupName.getText().toString().trim();
                String cpass = confirmPassword.getText().toString().trim();

                if (user.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }

                if (name.isEmpty()){
                    signupName.setError("Name cannot be empty");
                }

                if (pass.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                }

                if (cpass.isEmpty()){
                    confirmPassword.setError("Password cannot be empty");
                }

                if (!pass.equals(cpass)){
                    confirmPassword.setError("Password Incorrect");
                }
                else{
                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).build();

                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // add user to database
                                                    //mDBO.addUser(user);
                                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                }
                                            }
                                        });

                            } else {
                                Toast.makeText(SignUpActivity.this, "SignUp Failed" +
                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    public class DatabaseOperations{
        /**
         * All operations that require access to the firebase realtime database should be generated as
         * a method in this class.
         */
        private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        private final String TAG = "DBO";

        public void addUser(FirebaseUser user){
            String name = user.getDisplayName();
            String id = user.getUid();

            DatabaseReference newRef = mDatabase.child("users").child(id);
            newRef.setValue(name);
            Log.d(TAG, "added: " + name);
        }

        private void addChat(FirebaseUser user, String otherUserId){
            String id = user.getUid();

            DatabaseReference newRef = mDatabase.child("chats").push();
            newRef.setValue(id);
            newRef.setValue(otherUserId);
        }

        public void createChats(FirebaseUser user){
            String id = user.getUid();
            mDatabase.child("users").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String previousChildName) {
                            if(!id.equals(ds.child(id).getValue(String.class))){
                                addChat(user, ds.child(id).getValue(String.class));
                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Log.e(TAG, "loadMessageListener:onChildChanged");
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            Log.e(TAG, "loadMessageListener:onChildRemoved");
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Log.e(TAG, "loadMessageListener:onChildMoved");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "loadMessageListener:onCancelled");
                        }
                    });
        }
    }
}
