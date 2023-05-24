package com.example.chat_pf.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chat_pf.R;
import com.example.chat_pf.databinding.ActivityChatBinding;
import com.example.chat_pf.databinding.ActivityChatSelectBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    }
}