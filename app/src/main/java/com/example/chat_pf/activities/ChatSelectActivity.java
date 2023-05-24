package com.example.chat_pf.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.chat_pf.R;
import com.example.chat_pf.databinding.ActivityChatBinding;
import com.example.chat_pf.databinding.ActivityChatSelectBinding;
import com.example.chat_pf.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatSelectActivity extends AppCompatActivity {

    ActivityChatSelectBinding bind;
    String TAG = "DEBUGGING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_select);

        bind = ActivityChatSelectBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        bind.usernameText.setText(user.getDisplayName());

        bind.addChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatSelectActivity.this, ChatCreateActivity.class);
                startActivity(intent);
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

        public void getLastMessageSentGeneral(){
            mDatabase.child("messages").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String previousChildName) {
                    Message message = new Message(
                            ds.child("name").getValue(String.class),
                            ds.child("text").getValue(String.class),
                            ds.child("date").getValue(Long.class),
                            ds.child("id").getValue(String.class));
                    Log.d(TAG, String.valueOf(message));

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

        public void getLastMessage(FirebaseUser user, String otherUser){
            String id = user.getUid();
            mDatabase.child("users").child(id).child(otherUser).
                    addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String previousChildName) {
                    Message message = new Message(
                            ds.child("name").getValue(String.class),
                            ds.child("text").getValue(String.class),
                            ds.child("date").getValue(Long.class),
                            ds.child("id").getValue(String.class));
                    Log.d(TAG, String.valueOf(message));

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

    public void generateChatView(LinearLayout layout){

    }
}