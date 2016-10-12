package com.tfm.sporting.sporting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class PlanesEntrenamientoActivity extends AppCompatActivity {

    private ImageButton ibtnVer;
    private ImageButton ibtnNuevo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planes_entrenamiento);

        // Muestra la actividad para un nuevo plan de Entrenamiento
        ibtnNuevo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent ActividadNuevoPlan = new Intent(getApplicationContext(), NuevoPlanEntrenamientoActivity.class);
                startActivity(ActividadNuevoPlan);
            }
        });

        // Muestra la actividad para ver un Plan de Entrenamiento
        ibtnVer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent ActividadVerPlanes = new Intent(getApplicationContext(), PlanesEntrenamientoActivity.class);
                startActivity(ActividadVerPlanes);
            }
        });
    }
}
