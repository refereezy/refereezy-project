package com.example.rellotgejais;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class IncidenciasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private IncidenciaAdapter adapter;
    private List<Incidencia> listaIncidencias;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaIncidencias = new ArrayList<>();
        listaIncidencias.add(new Incidencia(R.drawable.gol, 4, "Gol", 12, 0));
        listaIncidencias.add(new Incidencia(R.drawable.t_amarilla, 10, "Tarjeta Amarilla", 14, 0));

        adapter = new IncidenciaAdapter(listaIncidencias);
        recyclerView.setAdapter(adapter);
    }
}
