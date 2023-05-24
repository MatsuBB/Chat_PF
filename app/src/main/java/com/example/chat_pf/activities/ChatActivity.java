package com.example.chat_pf.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OneShotPreDrawListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

    public boolean isKeyboardShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseOperations mDBO = new DatabaseOperations();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // needed stuff
        FirebaseAuth auth = FirebaseAuth.getInstance();
        bind = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        // getting the user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // displays the messages
        mDBO.loadMessages(user);

        // submits the input to the realtime database
        EditText input = bind.input;
        ImageButton enter = bind.enterButton;

        bind.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(ChatActivity.this, LoginActivity.class));
            }
        });

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bind.scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        bind.scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });

        bind.rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        bind.rootView.getWindowVisibleDisplayFrame(r);
                        int screenHeight = bind.rootView.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        Log.d(TAG, "keypadHeight = " + keypadHeight);

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            if (!isKeyboardShowing) {
                                bind.scrollView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        bind.scrollView.fullScroll(View.FOCUS_DOWN);
                                    }
                                });
                                isKeyboardShowing = true;
                            }
                        }
                        else {
                            // keyboard is closed
                            if (isKeyboardShowing) {
                                isKeyboardShowing = false;
                            }
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
                    if(!message.equals("")) {
                        String name = user.getDisplayName();
                        String id = user.getUid();

                        Long currentTime = System.currentTimeMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");

                        DatabaseReference newRef = mDatabase.child("messages").child(
                                sdf.format(new Date(currentTime)));
                        newRef.setValue(new Message(name, message, currentTime, id));
                    }
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
            RelativeLayout surroundsMessage = new RelativeLayout(ChatActivity.this);
            //ImageView iv_message = new ImageView(ChatActivity.this);
            //iv_message.setImageDrawable(getDrawable(R.drawable.chat_bubble));

            RelativeLayout.LayoutParams m_params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            tv_message.setText(m.text);
            tv_message.setTextSize(22);

            LinearLayout.LayoutParams n_params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv_name.setText(m.name);
            tv_name.setTextSize(22);
            tv_name.setTypeface(Typeface.DEFAULT_BOLD);

            RelativeLayout.LayoutParams s_params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            tv_message.setPadding(50, 10, 50, 10);

            if(user.getUid().equals(m.id)) {
                // Setting some properties for messages that are from the current user
                surroundsMessage.setHorizontalGravity(Gravity.RIGHT);
                tv_name.setGravity(Gravity.RIGHT);
                n_params.setMargins(0, 0, 10, 0);
                m_params.setMargins(250, 0, 55, 20);
                tv_message.setBackground(getDrawable(R.drawable.chat_bubble_right));
            } else{
                // Setting some properties for messages that are NOT from the current user
                n_params.setMargins(10, 0, 0, 0);
                m_params.setMargins(55, 0, 250, 20);
                tv_message.setBackground(getDrawable(R.drawable.chat_bubble_left));
            }

            //surroundsMessage.setLayoutParams(s_params);

            tv_name.setLayoutParams(n_params);
            tv_message.setLayoutParams(m_params);

            layout.addView(tv_name);
            //layout.addView(tv_message);
            surroundsMessage.addView(tv_message);
            layout.addView(surroundsMessage);
        } catch(Exception e){Log.e(TAG, String.valueOf(e));}
    }
}