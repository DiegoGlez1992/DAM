package com.dedigo.pmdm04_tarea;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button aceptar;
    CheckBox vista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vista = (CheckBox) findViewById(R.id.vista);
        aceptar = (Button) findViewById(R.id.aceptar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vista.isChecked() == true) {    //Si está activado el checkbox
                    Toast.makeText(MainActivity.this, "Excelente eres un auténtico seriófilo", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Imperdonable, debes ver esta serie", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
}