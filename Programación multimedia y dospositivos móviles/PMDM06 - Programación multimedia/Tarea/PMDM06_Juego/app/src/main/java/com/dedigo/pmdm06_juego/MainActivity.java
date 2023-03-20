package com.dedigo.pmdm06_juego;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Para recibir el record
    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int result = activityResult.getResultCode();
                            Intent data = activityResult.getData();

                            if (result == RESULT_OK) {
                                record = data.getIntExtra("record", 0);
                                //txtRecord = findViewById(R.id.txtRecord);
                                txtRecord.setText("" + record);
                            }
                        }
                    }
            );

    Button btJugar;
    TextView txtRecord;
    Toast toast;
    int record = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Enlaza su vista



        txtRecord = findViewById(R.id.txtRecord);   //Enlaza el visor de record
        txtRecord.setText("" + record); //Muestra el record

        btJugar = findViewById(R.id.btJugar);   //Enlaza con el botón jugar
        btJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast = Toast.makeText(getApplicationContext(), "A jugar!", Toast.LENGTH_SHORT);
                toast.show();   //Muestra el mensaje
                jugar();
            }
        });
    }

    /**
     * Método para lanzar el juego
     */
    public void jugar() {
        Intent gameActivity = new Intent(this, GameActivity.class); //Crea un intent para el juego
        gameActivity.putExtra("record", record);
        //startActivity(gameActivity);   //Lanza el juego
        activityResultLauncher.launch(gameActivity);
    }


}