package com.dedigo.pmdm05_periodicos.periodicos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dedigo.pmdm05_periodicos.R;
import com.dedigo.pmdm05_periodicos.base_datos.DBInterface;

/**
 * Clase para la actividad de añadir un periodico.
 *
 * Importante añadirla en el "AndroidManifest.xml"
 */
public class ActivityAnadirPeriodico extends AppCompatActivity {

    private EditText txtNombre;
    private String tematica;
    private Button btnAgregar;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private DBInterface dbInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_periodico);

        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(view -> {
            clickBotonAgregar();
        });  //Evento para el botón agregar
    }

    /**
     * Método que se lanza con el evento click en el botón de agregar
     */
    private void clickBotonAgregar() {
        Periodico periodico = new Periodico();
        dbInterface = new DBInterface(this);

        txtNombre = findViewById(R.id.txtNombre);
        radioGroup = findViewById(R.id.grpTematica);

        if (txtNombre.getText().toString().isEmpty()) { //Si está vacio el editText
            Toast.makeText(this, "Está en blanco el periódico", Toast.LENGTH_SHORT).show(); //Mensaje
        } else if (radioGroup.getCheckedRadioButtonId() == -1) {    //Si no hay temática seleccionada
            Toast.makeText(this, "No hay ninguna temática seleccionada", Toast.LENGTH_SHORT).show(); //Mensaje
        } else {
            radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId()); //Busca la opción seleccionada
            periodico.setNombre(txtNombre.getText().toString());    //Obtiene el texto del textEdit
            periodico.setTematica(radioButton.getText().toString());    //Obtiene el texto del radio button que estaba seleccionado

            if (dbInterface.insertarPeriodico(periodico) != -1) {    //Inserta el periódico
                Toast.makeText(this, "Periodico guardado con éxito", Toast.LENGTH_SHORT).show();
                this.onBackPressed();   //LLama al método para volver atras
            } else {
                Toast.makeText(this, "Error guardando el periodico", Toast.LENGTH_LONG).show();    //Mensaje de error
            }
        }
    }
}
