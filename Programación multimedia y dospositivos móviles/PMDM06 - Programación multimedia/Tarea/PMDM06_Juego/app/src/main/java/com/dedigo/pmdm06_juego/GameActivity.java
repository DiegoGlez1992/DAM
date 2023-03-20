package com.dedigo.pmdm06_juego;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends Activity {

    Toast toast;

    //Variables de los componentes
    ImageButton imb00, imb01, imb02;
    ImageButton imb10, imb11, imb12;
    ImageButton imb20, imb21, imb22;
    ImageButton imb30, imb31, imb32;
    ImageButton[] tablero = new ImageButton[12];
    TextView txtRecord, txtIntentos, txtAciertos;
    int record, intentos, aciertos;

    //Variables de las imagenes
    int[] imagenes;
    int fondo;

    //Variables para los sonidos
    int[] sonidos;

    //Variables de juego
    ArrayList<Integer> arrayDesordenado;
    ImageButton primero, segundo;
    int numeroPrimero, numeroSegundo;
    boolean bloqueo = false;
    final Handler temporizador = new Handler();
    Bundle datos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game); //Enlaza su vista

        datos = getIntent().getExtras();
        record = datos.getInt("record");
        iniciar();
    }

    /**
     * Método que guarda los botones del tablero
     */
    private void cargarTablero() {
        tablero[0] = (imb00 = findViewById(R.id.imb00));
        tablero[1] = (imb01 = findViewById(R.id.imb01));
        tablero[2] = (imb02 = findViewById(R.id.imb02));
        tablero[3] = (imb10 = findViewById(R.id.imb10));
        tablero[4] = (imb11 = findViewById(R.id.imb11));
        tablero[5] = (imb12 = findViewById(R.id.imb12));
        tablero[6] = (imb20 = findViewById(R.id.imb20));
        tablero[7] = (imb21 = findViewById(R.id.imb21));
        tablero[8] = (imb22 = findViewById(R.id.imb22));
        tablero[9] = (imb30 = findViewById(R.id.imb30));
        tablero[10] = (imb31 = findViewById(R.id.imb31));
        tablero[11] = (imb32 = findViewById(R.id.imb32));
    }

    /**
     * Método que muestra los textos
     */
    private void cargarTextos() {
        intentos = 0;   //Inicializa la variable en 0
        aciertos = 0;   //Inicializa la variable en 0
        txtRecord = findViewById(R.id.txtRecord);   //Enlaza el texto de record
        txtIntentos = findViewById(R.id.txtIntentos);   //Enlaza el texto de intentos
        txtAciertos = findViewById(R.id.txtAciertos);   //Enlaza el texto de aciertos
        txtRecord.setText("" + record); //Muestra el valor
        txtIntentos.setText("" + intentos); //Muestra el valor
        txtAciertos.setText("" + aciertos); //Muestra el valor
    }

    /**
     * Método que carga las imagenes en un array
     */
    private void cargarImagenes() {
        imagenes = new int[]{
                R.drawable.caballo,
                R.drawable.gato,
                R.drawable.cerdo,
                R.drawable.pato,
                R.drawable.perro,
                R.drawable.vaca
        };
        fondo = R.drawable.cara;
    }

    /**
     * Método que carga los sonidos en un array
     */
    private void cargarSonidos() {
        sonidos = new int[]{
                R.raw.caballo,
                R.raw.gato,
                R.raw.cerdo,
                R.raw.pato,
                R.raw.perro,
                R.raw.vaca
        };
    }

    /**
     * Método que rellena el tablero y lo desordena
     *
     * @param longitud
     * @return
     */
    private ArrayList<Integer> barajar(int longitud) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < longitud * 2; i++) {
            result.add(i % longitud);
        }
        Collections.shuffle(result);
        return result;
    }

    /**
     * Método que controla la jugabilidad
     *
     * @param i
     * @param imb
     */
    private void comprobarSeleccion(int i, final ImageButton imb) {
        MediaPlayer.create(this, sonidos[arrayDesordenado.get(i)]).start(); //Reproduce el sonido

        //Animación de la imagen
        @SuppressLint("ResourceType") Animator animator = AnimatorInflater.loadAnimator(this, R.anim.rotar_y);
        animator.setTarget(imb);
        animator.start();

        if (primero == null) {
            primero = imb;
            primero.setScaleType(ImageView.ScaleType.CENTER_CROP);  //Escala la imagen y la centra
            primero.setImageResource(imagenes[arrayDesordenado.get(i)]);    //Enseña la imagen
            primero.setEnabled(false);  //Deshabilita el botón
            numeroPrimero = arrayDesordenado.get(i);    //Guarda el número de la imagen
        } else {
            bloqueo = true; //Bloquea el panel
            segundo = imb;
            segundo.setScaleType(ImageView.ScaleType.CENTER_CROP);  //Escala la imagen y la centra
            segundo.setImageResource(imagenes[arrayDesordenado.get(i)]);    //Enseña la imagen
            segundo.setEnabled(false);  //Deshabilita el botón
            numeroSegundo = arrayDesordenado.get(i);    //Guarda el número de la imagen
            intentos++; //Suma un intento
            txtIntentos.setText("" + intentos); //Actualiza el valor en la vista
            if (numeroPrimero == numeroSegundo) {   //Si hay coincidencia
                primero = null; //Borra la comprobación
                segundo = null; //Borra la comprobación
                bloqueo = false;    //Desbloquea el panel
                aciertos++; //Suma un acierto
                txtAciertos.setText("" + aciertos); //Actualiza el valor en la vista
                if (aciertos == imagenes.length) {   //Si se han acertado todas
                    toast = Toast.makeText(getApplicationContext(), "Enhorabuena!\n Has ganado.", Toast.LENGTH_LONG);
                    toast.show();   //Muestra el mensaje
                    temporizador.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent data = new Intent();
                            if ((intentos < record) || (record == 0)) {    //Si hay nuevo record o es la primera partida
                                data.putExtra("record", intentos);  //Actualiza valor del record
                                setResult(RESULT_OK, data); //Devuelve los datos
                                toast = Toast.makeText(getApplicationContext(), "¡¡¡Nuevo record!!!", Toast.LENGTH_LONG);
                                toast.show();   //Muestra el mensaje
                            } else {
                                data.putExtra("record", record);    //Mantiene el valor del record
                                setResult(RESULT_OK, data); //Devuelve los datos
                            }
                            finish();   //Sale
                        }
                    }, 2000);   //Espera 2 segundos
                }
            } else {    //Si no hay coincidencia
                temporizador.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        primero.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        primero.setImageResource(fondo);    //Esconde la imagen
                        primero.setEnabled(true);  //Habilita el botón
                        segundo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        segundo.setImageResource(fondo);    //Esconde la imagen
                        segundo.setEnabled(true);  //Habilita el botón
                        primero = null; //Borra la comprobación
                        segundo = null; //Borra la comprobación
                        bloqueo = false;    //Desbloquea el panel
                    }
                }, 500);   //Espera 0,5 segundo
            }
        }
    }

    /**
     * Método que inicia el juego
     */
    private void iniciar() {
        cargarTablero();
        cargarTextos();
        cargarImagenes();
        cargarSonidos();
        arrayDesordenado = barajar(imagenes.length);    //Desordena el tablero

        //Rellena el tablero con las caras
        for (int i = 0; i < tablero.length; i++) {
            tablero[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            //tablero[i].setImageResource(imagenes[arrayDesordenado.get(i)]);
            tablero[i].setImageResource(fondo);
        }

        //Habilita todos los botones del tablero y les crea un listener para la comprobación de selección
        for (int i = 0; i < tablero.length; i++) {
            int finalI = i;
            tablero[i].setEnabled(true);    //Habilita el botón
            tablero[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!bloqueo) {
                        comprobarSeleccion(finalI, tablero[finalI]);
                    }
                }
            });
        }
    }
}
