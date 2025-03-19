package com.example.rellotgejais;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TeamSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_team_select);

        ImageButton local = findViewById(R.id.localteam);
        ImageButton visitant = findViewById(R.id.visitorteam);

        local.setOnClickListener(v -> {
            Intent intent = new Intent(this,localPlayers.class);
            startActivity(intent);
        });
        visitant.setOnClickListener(v -> {
            Intent intent = new Intent(this,vititantPlayers.class);
            startActivity(intent);
        });
    }
}