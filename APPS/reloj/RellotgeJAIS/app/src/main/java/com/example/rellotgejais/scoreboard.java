package com.example.rellotgejais;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class scoreboard extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TextView marcador;
    private ImageView escudoIzquierdo, escudoDerecho;

    public scoreboard() {
        // Required empty public constructor
    }

    public static scoreboard newInstance(String param1, String param2) {
        scoreboard fragment = new scoreboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout del fragment
        return inflater.inflate(R.layout.fragment_scoreboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar el TextView y los ImageView
        marcador = view.findViewById(R.id.socreboardtxt);
        escudoIzquierdo = view.findViewById(R.id.escudoIzquierdo);
        escudoDerecho = view.findViewById(R.id.escudoDerecho);

        // Agregar OnLongClickListener al TextView
        marcador.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Crear un Intent para iniciar incidenciasActivity
                Intent intent = new Intent(getActivity(), IncidenciasActivity.class);
                startActivity(intent);
                return true; // Indica que el evento ha sido consumido
            }
        });

        // Agregar OnLongClickListener al ImageView izquierdo
        escudoIzquierdo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Crear un Intent para iniciar incidenciasActivity
                Intent intent = new Intent(getActivity(), IncidenciasActivity.class);
                startActivity(intent);
                return true; // Indica que el evento ha sido consumido
            }
        });

        // Agregar OnLongClickListener al ImageView derecho
        escudoDerecho.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Crear un Intent para iniciar incidenciasActivity
                Intent intent = new Intent(getActivity(), IncidenciasActivity.class);
                startActivity(intent);
                return true; // Indica que el evento ha sido consumido
            }
        });
    }

    public void actualitzaGol() {
        // Aquí puedes actualizar el TextView
        if (marcador != null) {
            marcador.setText("Nuevo gol!");
        }
    }
}