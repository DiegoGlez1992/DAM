/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Beans/Bean.java to edit this template
 */
package misControles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.EventListener;
import java.util.EventObject;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author Diego González García
 */
public class MiTemporizador extends JLabel implements ActionListener, Serializable {

    private int tiempo;
    private final Timer t;
    private FinCuentaAtrasListener receptor;

    /**
     * Get the value of tiempo
     *
     * @return the value of tiempo
     */
    public int getTiempo() {
        return tiempo;
    }

    /**
     * Set the value of tiempo
     *
     * @param tiempo new value of tiempo
     */
    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
        setText(Integer.toString(tiempo));
        repaint();
    }

    /**
     * Es necesario crear un método constructor sin argumentos
     */
    public MiTemporizador() {
        tiempo = 5; //Para que comience la cuenta atrás a los 5 segundos
        t = new Timer(1000, this); //Inicialización del objeto Timer
        setText(Integer.toString(tiempo));   //Método que modifica el texto a visualizar por la etiqueta
        setActivo(true);    //Activa el temporizador
    }

    /**
     * Evento que salta cada vez que termina el contador.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        setText(Integer.toString(tiempo));  //Asignamos el valor a mostrar en la etiqueta
        repaint();  //Repintamos la etiqueta
        tiempo--;   //Disminuimos el valos de la variable tiempo para que vaya disminuyendo hasta 0
        if (tiempo == 0) {  //Cuando alcancemos el valor 0, mostramos un aviso de Terminado
            setActivo(false);
            //JOptionPane.showMessageDialog(null, "Terminado", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            receptor.capturarFinCuentaAtras(new FinCuentaAtrasEvent(this));
        }
    }

    /**
     * Código necesario para gestionar si nuestro temporizador, objeto Timer,
     * está funcionando o no. Activo es en sí mismo una propiedad (sin atributo
     * subyacente).
     *
     * @param valor True/False
     */
    public final void setActivo(boolean valor) {
        if (valor == true) {
            t.start();
        } else {
            t.stop();
        }
    }

    /**
     * Código necesario para que nos devuelva si nuestro temporizador, objeto
     * Timer, está funcionando o no.
     *
     * @return True/False
     */
    public boolean getActivo() {
        return t.isRunning();
    }

    /**
     * Añade oyentes
     *
     * @param receptor
     */
    public void addFinCuentaAtrasListener(FinCuentaAtrasListener receptor) {
        this.receptor = receptor;
    }

    /**
     * Elimina oyentes
     *
     * @param receptor
     */
    public void removeFinCuentaAtrasListener(FinCuentaAtrasListener receptor) {
        this.receptor = null;
    }

    /**
     * Clase para implementar los eventos.
     */
    public class FinCuentaAtrasEvent extends EventObject {

        public FinCuentaAtrasEvent(Object source) {
            super(source);
        }
    }

    /**
     * Interfaz que define los métodos a usar cuando se genere el evento.
     */
    public interface FinCuentaAtrasListener extends EventListener {

        public void capturarFinCuentaAtras(FinCuentaAtrasEvent ev);
    }

}
