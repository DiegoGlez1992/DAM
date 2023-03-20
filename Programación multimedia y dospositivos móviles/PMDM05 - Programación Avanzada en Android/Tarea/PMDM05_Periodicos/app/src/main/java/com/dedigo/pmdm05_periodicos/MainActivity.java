package com.dedigo.pmdm05_periodicos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dedigo.pmdm05_periodicos.contactos.ActivityVerContactos;
import com.dedigo.pmdm05_periodicos.periodicos.ActivityAnadirPeriodico;
import com.dedigo.pmdm05_periodicos.periodicos.FragmentListaPeriodicos;

public class MainActivity extends AppCompatActivity {

    private FragmentListaPeriodicos fragmentListaPeriodicos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Acordarse de poner en "themes.xml" "parent="Theme.MaterialComponents.DayNight.NoActionBar""
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Acordarse de indicar las dependencias en "build.grandle"
        //Toma la imagen de internet y la añade al objeto
        ImageView imageView = findViewById(R.id.imgLogo);
        Glide.with(this).load("http://bit.ly/2fVot9z").into(imageView);

        //
        this.fragmentListaPeriodicos =  (FragmentListaPeriodicos) getSupportFragmentManager().findFragmentById(R.id.fragment_lista_periodicos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.fragmentListaPeriodicos.actualizaDatos();
    }

    /**
     * Método para inflar el menu del toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Método para la acción de "Añadir Medio"
     *
     * @param menuItem
     */
    public void onAnadirMedio(MenuItem menuItem) {
        Intent intentAddMedio = new Intent(this, ActivityAnadirPeriodico.class);
        startActivity(intentAddMedio);
    }

    /**
     * Método para la acción de "Ver Contactos"
     *
     * @param menuItem
     */
    public void onVerContactos(MenuItem menuItem) {
        Intent intentVerContactos = new Intent(this, ActivityVerContactos.class);
        startActivity(intentVerContactos);
    }
}