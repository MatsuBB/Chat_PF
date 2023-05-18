package com.example.chat_pf.database;

import android.util.Log;

import com.example.chat_pf.models.Message;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseOperations {
    /**
     * All operations that require access to the firebase realtime database should be generated as
     * a method in this class.
     */
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    final String TAG = "DBO";

    private void adminAuth(){
        // TODO make a user for the DBO with moderator privileges to make the rules more secure
    }

    public void sendMessage(FirebaseUser user, String message){
        if(user != null){
            try{
                //String name = user.getDisplayName();
                String name = user.getEmail();  // eventually change this to name
                DatabaseReference newRef = mDatabase.child("messages").push();
                newRef.setValue(new Message(name, message));
            } catch(Exception e){Log.e(TAG, "error sending message");}
        } else{Log.e(TAG, "null user can not send message");}
    }
}

