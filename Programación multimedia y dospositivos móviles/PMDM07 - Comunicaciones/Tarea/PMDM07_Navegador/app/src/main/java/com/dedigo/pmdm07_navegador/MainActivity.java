package com.dedigo.pmdm07_navegador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private EditText editTextWeb;
    private Button buttonCargar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextWeb = (EditText) findViewById(R.id.editTextWeb);    //Instanciamos el objeto
        buttonCargar = findViewById(R.id.buttonCargar); //Instanciamos el objeto
        buttonCargar.setOnClickListener(view -> {
            clickBotonCargar();
        });  //Evento para el botón cargar


        ////////////////////////////////////////////////////////////////////////////////////////////
        //  IMPORTANTE                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////
        //  Indicar los permisos en "AndroidManifest.xml"                                         //
        //      <uses-permission android:name="android.permission.INTERNET" />                    //
        //      <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>         //
        //      android:usesCleartextTraffic="true"                                               //
        ////////////////////////////////////////////////////////////////////////////////////////////
        //  Indicar las dependencias en "build.grandle"                                           //
        //      implementation 'com.github.bumptech.glide:glide:4.11.0'                           //
        //      annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'                   //
        ////////////////////////////////////////////////////////////////////////////////////////////
        //Toma la imagen de internet y la añade al objeto
        ImageView imageView = findViewById(R.id.imgLogo);
        Glide.with(this).load("https://bit.ly/2uUlAzw").into(imageView);
    }

    private void clickBotonCargar() {
        Intent intent = new Intent(this,WebActivity.class);
        intent.putExtra("nombreSitio", editTextWeb.getText().toString());
        startActivity(intent);
    }

}