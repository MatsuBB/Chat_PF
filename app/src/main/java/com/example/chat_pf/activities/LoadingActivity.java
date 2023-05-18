package com.example.chat_pf.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.bumptech.glide.Glide;
import com.example.chat_pf.R;
import com.example.chat_pf.databinding.ActivityLoadingBinding;

public class LoadingActivity extends AppCompatActivity {

    ActivityLoadingBinding binding;
    MyCountDownTimer myCountDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Glide.with(this).load(R.drawable.logo_image).into(binding.icImage);

        myCountDownTimer = new MyCountDownTimer(5000,1000);
        myCountDownTimer.start();

    }
    public class MyCountDownTimer extends CountDownTimer {
        int progress = 0;
        public MyCountDownTimer(long millisInFuture, long countDownInternet) {
            super(millisInFuture, countDownInternet);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            progress = progress + 20;
            binding.loadingProgressBar.setProgress(progress);
        }

        @Override
        public void onFinish() {
            Intent intent = new Intent(LoadingActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    }
}

