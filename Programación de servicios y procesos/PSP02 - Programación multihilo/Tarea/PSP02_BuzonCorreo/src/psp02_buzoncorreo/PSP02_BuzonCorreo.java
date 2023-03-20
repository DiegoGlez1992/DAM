/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package psp02_buzoncorreo;

/**
 * <strong>Clase principal</strong><br>
 * Se instancia un objeto BuzonCorreo y varios LectorHilo y EscritorHilo,
 * respectivamente.<br>
 * Se arrancan los hilos y se espera que terminen.<br>
 * Al final, el programa principal deberá imprimir la frase "FIN DE LA
 * EJECUCIÓN". Este mensaje es muy importante que salga el último.
 *
 * @author Diego González García
 */
public class PSP02_BuzonCorreo {

    /**
     * <strong>Método main</strong>
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BuzonCorreo buzonCorreo = new BuzonCorreo(); //Se instancia el buzón de correo

        //Se Instancian 2 lectores
        LectorHilo lectorHilo1 = new LectorHilo("Diego", buzonCorreo);
        LectorHilo lectorHilo2 = new LectorHilo("Virginia", buzonCorreo);

        //Se instancian 2 escritores
        EscritorHilo escritorHilo1 = new EscritorHilo("Nicolás", buzonCorreo);
        EscritorHilo escritorHilo2 = new EscritorHilo("Balú", buzonCorreo);

        //Se inician los hilos
        lectorHilo1.start();
        lectorHilo2.start();
        escritorHilo1.start();
        escritorHilo2.start();

        //Bloqueamos al hilo llamante hasta que todos los hilos llamados terminen
        try {
            lectorHilo1.join();
            lectorHilo2.join();
            escritorHilo1.join();
            escritorHilo2.join();
        } catch (InterruptedException ex) {
            System.err.println(ex.toString());
        }
        System.out.println("FIN DE LA EJECUCIÓN");
    }
}
