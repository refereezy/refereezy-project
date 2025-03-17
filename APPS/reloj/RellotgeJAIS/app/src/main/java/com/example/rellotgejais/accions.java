package com.example.rellotgejais;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class accions extends AppCompatActivity {
    boolean estdoCrono = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accions);
        ImageButton button2 = findViewById(R.id.PausaStart);
        ImageButton btnTarjetas = findViewById(R.id.tarjetas);
        ImageButton btnIncidencias = findViewById(R.id.inccidencias);
        ImageButton btnnGol = findViewById(R.id.gol);
        Chronometer crono = findViewById(R.id.chronometer);


        button2.setOnClickListener(v -> {
            if (estdoCrono){
                button2.setImageResource(android.R.drawable.ic_media_play);
                crono.stop();
            }else {
                button2.setImageResource(android.R.drawable.ic_media_pause);
                crono.start();
            }
            estdoCrono= !estdoCrono;
        });

        crono.setOnClickListener(v -> {
            if (!estdoCrono) {
                crono.setBase(SystemClock.elapsedRealtime());
            }
        });

        btnnGol.setOnClickListener(v -> {
            Intent intentt = new Intent(this,TeamSelect.class);
            startActivity(intentt);
        });
        btnTarjetas.setOnClickListener(v -> {
            Intent intent = new Intent(this,TarjetasActivty.class);
            startActivity(intent);
        });


    }
}