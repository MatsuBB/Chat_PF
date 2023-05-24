package com.example.chat_pf.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OneShotPreDrawListener;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chat_pf.R;
import com.example.chat_pf.databinding.ActivityChatBinding;
import com.example.chat_pf.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding bind;
    String TAG="DEBUGGING";

    public static float PxToDp(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseOperations mDBO = new DatabaseOperations();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // needed stuff
        bind = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        // getting the user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // displays the messages
        mDBO.loadMessages(user);

        // submits the input to the realtime database
        EditText input = bind.input;
        Button enter = bind.enterButton;

        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    Log.d(TAG, "entered on focus");
                    bind.scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            bind.scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDBO.sendMessage(user, String.valueOf(input.getText()));
                input.setText("");
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

        private void adminAuth(){
            // TODO make a user for the DBO with moderator privileges to make the rules more secure
        }

        public void sendMessage(FirebaseUser user, String message){
            if(user != null){
                try{
                    String name = user.getDisplayName();
                    String id = user.getUid();

                    Long currentTime = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");

                    DatabaseReference newRef = mDatabase.child("messages").child(
                            sdf.format(new Date(currentTime)));
                    newRef.setValue(new Message(name, message, currentTime, id));
                } catch(Exception e){Log.e(TAG, "error sending message");}
            } else{Log.e(TAG, "null user can not send message");}
        }

        private void loadMessages(FirebaseUser currentUser){
            // Change the path from the next line of code if you change de realtime database structure
            mDatabase.child("messages").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String previousChildName) {
                    Message message = new Message(
                            ds.child("name").getValue(String.class),
                            ds.child("text").getValue(String.class),
                            ds.child("date").getValue(Long.class),
                            ds.child("id").getValue(String.class));
                    Log.d(TAG, String.valueOf(message));
                    displayMessage(bind.linearLayout, message, currentUser);
                    bind.scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            bind.scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
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

    private void displayMessage(LinearLayout layout, Message m, FirebaseUser user){
        try {
            // Setting the properties for all messages
            TextView tv_name = new TextView(ChatActivity.this);
            TextView tv_message = new TextView(ChatActivity.this);

            LinearLayout.LayoutParams m_params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv_message.setText(m.text);
            tv_message.setTextSize(22);

            LinearLayout.LayoutParams n_params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv_name.setText(m.name);
            tv_name.setTextSize(22);
            tv_name.setTypeface(Typeface.DEFAULT_BOLD);

            if(user.getUid().equals(m.id)) {
                // Setting some properties for messages that are from the current user
                tv_message.setGravity(Gravity.RIGHT);
                tv_name.setGravity(Gravity.RIGHT);
                n_params.setMargins(0, 0, 5, 0);
                m_params.setMargins(250, 0, 55, 0);
            } else{
                // Setting some properties for messages that are NOT from the current user
                n_params.setMargins(5, 0, 0, 0);
                m_params.setMargins(55, 0, 250, 0);
            }

            tv_name.setLayoutParams(n_params);
            tv_message.setLayoutParams(m_params);

            layout.addView(tv_name);
            layout.addView(tv_message);
        } catch(Exception e){Log.e(TAG, String.valueOf(e));}
    }
}