/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Beans/Bean.java to edit this template
 */
package misControles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EventListener;
import java.util.EventObject;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * <strong>Reloj digital.</strong><br>
 * Componente java que crea un reloj digital al que se puede configurar las siguientes propiedades:
 * <ul>
 * <li>Formato de hora (12h / 24h)</li>
 * <li>Alarma (On / Off)</li>
 * <li>Hora de la alarma</li>
 * <li>Minuto de la alarma</li>
 * <li>Mensaje a mostrar cuando salta la alarma</li>
 * </ul>
 *
 * @author Diego González García
 */
public class RelojDigital extends JLabel implements ActionListener, Serializable {

    private boolean formato24h;
    private boolean alarmaOn;
    private int alarmaHora;
    private int alarmaMinuto;
    private String alarmaMensaje;

    private final Timer timer;
    private Calendar horaActual;
    private final SimpleDateFormat formato24 = new SimpleDateFormat("HH:mm:ss");      //Formato para 24h
    private final SimpleDateFormat formato12 = new SimpleDateFormat("hh:mm:ss a");    //Formato para 12h
    private AlarmaSuenaListener alarmaSuena;

    /**
     * Get the value of alarmaMensaje
     *
     * @return the value of alarmaMensaje
     */
    public String getAlarmaMensaje() {
        return alarmaMensaje;
    }

    /**
     * Set the value of alarmaMensaje
     *
     * @param alarmaMensaje new value of alarmaMensaje
     */
    public void setAlarmaMensaje(String alarmaMensaje) {
        this.alarmaMensaje = alarmaMensaje;
    }

    /**
     * Get the value of alarmaMinuto
     *
     * @return the value of alarmaMinuto
     */
    public int getAlarmaMinuto() {
        return alarmaMinuto;
    }

    /**
     * Set the value of alarmaMinuto
     *
     * @param alarmaMinuto new value of alarmaMinuto
     */
    public void setAlarmaMinuto(int alarmaMinuto) {
        this.alarmaMinuto = alarmaMinuto;
    }

    /**
     * Get the value of alarmaHora
     *
     * @return the value of alarmaHora
     */
    public int getAlarmaHora() {
        return alarmaHora;
    }

    /**
     * Set the value of alarmaHora
     *
     * @param alarmaHora new value of alarmaHora
     */
    public void setAlarmaHora(int alarmaHora) {
        this.alarmaHora = alarmaHora;
    }

    /**
     * Get the value of alarmaOn
     *
     * @return the value of alarmaOn
     */
    public boolean isAlarmaOn() {
        return alarmaOn;
    }

    /**
     * Set the value of alarmaOn
     *
     * @param alarmaOn new value of alarmaOn
     */
    public void setAlarmaOn(boolean alarmaOn) {
        this.alarmaOn = alarmaOn;
    }

    /**
     * Get the value of formato24h
     *
     * @return the value of formato24h
     */
    public boolean isFormato24h() {
        return formato24h;
    }

    /**
     * Set the value of formato24h
     *
     * @param formato24h new value of formato24h
     */
    public void setFormato24h(boolean formato24h) {
        this.formato24h = formato24h;
    }

    /**
     * Constructor of RelojDigital
     */
    public RelojDigital() {
        timer = new Timer(1000, this);
        setActivo(true);    //Activa el temporizador
    }

    /**
     * Evento que se ejecuta cada vez que "salta" el temporizador
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        horaActual = Calendar.getInstance();    //Tomamos la hora actual
        if (formato24h) {   //Si el formato definido es 24h
            setText(formato24.format(Calendar.getInstance().getTime()));    //Mostramos hora actual con formato 24h
        } else {
            setText(formato12.format(Calendar.getInstance().getTime()));    //Mostramos hora actual con formato 12h
        }
        if (alarmaOn) { //Si la alarma está activada
            if (formato24h) {   //Si el formato definido es 24h, comprovamos si es tiempo de alarma en formato 24h
                if (horaActual.get(Calendar.HOUR_OF_DAY) == alarmaHora
                        && horaActual.get(Calendar.MINUTE) == alarmaMinuto
                        && horaActual.get(Calendar.SECOND) == 0) {
                    JOptionPane.showMessageDialog(null, alarmaMensaje, "Alarma", JOptionPane.INFORMATION_MESSAGE);  //Mostramos el mensaje configurado
                    //alarmaSuena.capturarAlarmaSuena(new AlarmaSuenaEvent(this));
                }
            } else {    //Si el formato definido es 12h, comprovamos si es tiempo de alarma en formato 12h
                if (horaActual.get(Calendar.HOUR) == alarmaHora
                        && horaActual.get(Calendar.MINUTE) == alarmaMinuto
                        && horaActual.get(Calendar.SECOND) == 0) {
                    JOptionPane.showMessageDialog(null, alarmaMensaje, "Alarma", JOptionPane.INFORMATION_MESSAGE);  //Mostramos el mensaje configurado
                    //alarmaSuena.capturarAlarmaSuena(new AlarmaSuenaEvent(this));
                }
            }
        }
    }

    /**
     * Código necesario para gestionar si nuestro temporizador, objeto Timer,
     * está funcionando o no.
     *
     * @param valor True/False
     */
    public final void setActivo(boolean valor) {
        if (valor == true) {
            timer.start();
        } else {
            timer.stop();
        }
    }

    /**
     * Código necesario para que nos devuelva si nuestro temporizador, objeto
     * Timer, está funcionando o no.
     *
     * @return True/False
     */
    public boolean getActivo() {
        return timer.isRunning();
    }

    /**
     * Añade oyentes
     *
     * @param alarmaSuena
     */
    public void addAlarmaSuenaListener(AlarmaSuenaListener alarmaSuena) {
        this.alarmaSuena = alarmaSuena;
    }

    /**
     * Elimina oyentes
     *
     * @param alarmaSuena
     */
    public void removeAlarmaSuenaListener(AlarmaSuenaListener alarmaSuena) {
        this.alarmaSuena = null;
    }

    /**
     * Clase para implementar los eventos.
     */
    public class AlarmaSuenaEvent extends EventObject {

        public AlarmaSuenaEvent(Object source) {
            super(source);
        }
    }

    /**
     * Interfaz que define los métodos a usar cuando se genere el evento.
     */
    public interface AlarmaSuenaListener extends EventListener {

        public void capturarAlarmaSuena(AlarmaSuenaEvent ev);
    }

}
