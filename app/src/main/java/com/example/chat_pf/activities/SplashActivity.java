package com.example.chat_pf.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bumptech.glide.Glide;
import com.example.chat_pf.R;

import com.example.chat_pf.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 1000; // Tiempo en milisegundos para mostrar la pantalla de inicio
    private Handler mHandler; // Manejador para postergar la tarea donde podemos enviar y procesar objetos
    ActivitySplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Glide.with(this).load(R.drawable.icono_app).into(binding.icImage);

        mHandler = new Handler(); // Crear un nuevo manejador

        // Postergar la tarea de transición a la actividad principal después de un tiempo determinado
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Iniciar la actividad principal y cerrar la actividad de presentación
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY);
    }

}