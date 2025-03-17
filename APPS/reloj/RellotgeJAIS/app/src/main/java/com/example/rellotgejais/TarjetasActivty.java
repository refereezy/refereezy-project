package com.example.rellotgejais;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TarjetasActivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tarjetas_activty);
        ImageButton tRoja = findViewById(R.id.redCard);
        ImageButton tAmarilla = findViewById(R.id.yellowCard);

        tRoja.setOnClickListener(v -> {
            Intent intent = new Intent(this,TeamSelect.class);
            startActivity(intent);
        });

        tAmarilla.setOnClickListener(v -> {
            Intent intent = new Intent(this,TeamSelect.class);
            startActivity(intent);
        });

    }
}