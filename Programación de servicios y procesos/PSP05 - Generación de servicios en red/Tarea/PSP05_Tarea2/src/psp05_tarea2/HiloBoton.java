/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package psp05_tarea2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;

/**
 * <strong>Clase para el botón Go.</strong><br>
 *
 * @author Diego González García
 */
public class HiloBoton extends Thread {

    /**
     * TextArea de salida del formulario
     */
    private JTextArea taSalida;
    /**
     * Url de búsquera
     */
    private String stringUrl;

    /**
     * Método constructor
     *
     * @param tfUrl
     * @param taSalida
     */
    public HiloBoton(String tfUrl, JTextArea taSalida) {
        this.taSalida = taSalida;
        this.stringUrl = tfUrl;
    }

    /**
     * Método run
     */
    @Override
    public void run() {
        InputStream inputStream;    //Flujo de lectura
        FileOutputStream fileOutputStream;  //Flujo de escritura
        BufferedReader bufferedReader;  //Buffer de lectura
        byte[] bufferByte;  //Buffer intermedio para el application/pdf
        char[] bufferChar;  //Buffer intermedio para el text/html
        int aux;

        String contentType; //Tipo de contenido
        int contentLength;  //Longitud
        Date lastModified;  //Fecha de la última modificación

        String texto = this.taSalida.getText(); //Almacena el posible texto que pueda contener el JTextArea previamente

        try {
            URL url = new URL(stringUrl);   //Instancia un objeto URL con la dirección indicada
            URLConnection urlConnection = url.openConnection(); //Abre una conexión
            urlConnection.connect();    //Se conecta

            contentType = urlConnection.getContentType();    //Obtiene el tipo de contenido
            contentLength = urlConnection.getContentLength();   //Obtiene la longitud
            lastModified = new Date(urlConnection.getLastModified());  //Obtiene la fecha de la última modificación

            //Añadimos la información obtenida
            texto += ("\nEncabezados destacados:"
                    + "\n\tTipo de contenido: " + contentType
                    + "\n\tLongitud: " + contentLength
                    + "\n\tFecha de la última modificación: " + lastModified);

            if (contentType.equals("application/pdf")) {    //Si se trata de "application/pdf"
                File archivoElegido = ficheroDestino(); //Muestra un cuadro de dialogo modal para generar el fichero de destino
                if (archivoElegido != null) {   //Si el fichero está generado correctamente
                    inputStream = url.openStream();  //Instancia un flujo de lectura desde la url
                    fileOutputStream = new FileOutputStream(archivoElegido.getPath());   //Instancia un flujo de escritura en el archivo
                    bufferByte = new byte[contentLength];   //Buffer intermedio ajustado al content-Length enviado por el servidor
                    texto += ("\nDescargando el pdf en directorio elegido...");

                    //Copia todos los bytes al archivo de salida
                    while ((aux = inputStream.read(bufferByte)) > 0) {
                        fileOutputStream.write(bufferByte, 0, aux);
                    }

                    fileOutputStream.close();   //Cierra el flujo de escritura
                    texto += ("\nEl pdf ha sido descargado correctamente.");
                }
            } else if (contentType.startsWith("text/html")) {   //Si se trata de "text/html"
                inputStream = urlConnection.getInputStream();   //Instancia un flujo de lectura desde la url
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));    //Instancia el buffer de lectura
                if (contentLength < 0) {    //Si el servidor no devuelve la longitud (-1)
                    contentLength = 512;    //Indica 512 por defecto
                }
                bufferChar = new char[contentLength];   //Buffer intermedio ajustado al content-Length enviado por el servidor 
                texto += ("\nEscribiendo el cuerpo del texto en la salida...\n\n");

                //Copia todos los bytes al texto de salida
                while ((aux = bufferedReader.read(bufferChar)) > 0) {
                    texto += (new String(bufferChar, 0, aux));
                }

                texto += ("\n\nCuerpo de texto obtenido.");
            }
        } catch (MalformedURLException mfurle) {    //Si la direccion esta mal escrita
            texto += ("\nError! Url mal formada -> " + mfurle);
        } catch (UnknownHostException uhe) {
            texto += ("\nError! Host desconocido -> " + uhe);
        }
        catch (IOException ioe) {
            Logger.getLogger(HiloBoton.class.getName()).log(Level.SEVERE, null, ioe);
        } catch (Exception e) {
            Logger.getLogger(HiloBoton.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            taSalida.setText(texto);    //Traspasa todos los mensajes al TextArea
        }
    }

    /**
     * Muestra un cuadro de dialogo modal para generar el fichero de destino en
     * la ruta especificada por el usuario
     *
     * @return
     */
    private File ficheroDestino() {
        //Cuadro de diálogo 'guardar como' de Java...
        JFileChooser fc = new JFileChooser();
        //...posicionado en el archivo de nombre tomado de la url
        fc.setSelectedFile(new File(stringUrl.substring(stringUrl.lastIndexOf("/"))
                + (stringUrl.endsWith(".pdf") ? "" : ".pdf")));
        //muestra el cuadro de diálogo en pantalla
        int showSaveDialog = fc.showSaveDialog(null);
        //si se pulsa 'Aceptar'
        if (showSaveDialog == JFileChooser.APPROVE_OPTION) {
            //devuelve el archivo indicado por el usuario
            return fc.getSelectedFile();
        }
        //devuelve nulo
        return null;
    }

}
