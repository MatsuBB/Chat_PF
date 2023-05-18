package com.example.chat_pf.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chat_pf.R;
import com.example.chat_pf.database.DatabaseOperations;
import com.example.chat_pf.databinding.ActivityChatBinding;
import com.example.chat_pf.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    String TAG="DEBUGGING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // needed stuff
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DatabaseOperations mDBO = new DatabaseOperations();

        // getting the user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        EditText input = binding.input;
        Button enter = binding.enterButton;

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDBO.sendMessage(user, String.valueOf(input.getText()));
            }
        });

        // getting the messages, prob move this to DatabaseOperations later
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("messages").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }
}