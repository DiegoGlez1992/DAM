package tarea2_navegador_g;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;


/** 
 * Clase para ver el contenido de la URL tecleada por el usuario en la caja de texto.
 * En función del contenido enviado por el servidor (Content-type):
 *      - Si es de tipo "application/pdf", abre cuadro de dialogo para guardar fichero.
 *      - Si es de tipo "text/html", muestra el contenido en el JTextArea.
 *      - Para cualguier otro tipo, advierte que no puede procesarlo. 
 * 
 * Cuando se pulsa el botón GO, se crea un nuevo hilo. Se hereda de la clase Thread.
 * 
 * @author Gema
 * @version Enero 2023
 */

class HiloBoton extends Thread {
   
    //Variables locales
    private final String cadenaURL;
    private final JTextArea jTexto;
   
   /**   
   * Constructor recibe la URL pedida y el JTextArea donde escribir resultado
   * @param cadenaURL 
   * @param txtTexto
  */

    public HiloBoton (String cadenaURL, JTextArea txtTexto){
        this.cadenaURL = cadenaURL;
        this.jTexto = txtTexto;  
    }
    
   /**
   * Método principal que ejecuta el navegador, es el encargado de inspeccionar
   * el tipo de contenido y actuar mostrando contenido, guardando o informando.
   */
    
    @Override
    public void run(){
        
        int contentLength;  //Tamaño del contenido
        int leido;
        char[] bufChar;     //Buffer intermedio para la lectura de html
        byte[] bufByte;     //Buffer intermedio para la descarga de pdf
       
        //Texto que se va a mostrar en el JTextArea, se va añadiendo texto
        //a medida que se ejecuta el programa.
        String texto=this.jTexto.getText(); 
        
        try {
            //Se crea un objeto URL
            URL url = new URL (cadenaURL);
            //Obtiene una conexión al recurso URL
            URLConnection conexion = url.openConnection();
            //Se conecta pudiendo interactuar con parámetros.
            conexion.connect();
            
            //Una vez que se ha producido la conexión se obtiene el tipo de 
            //contenido con el fin de descargarlo / mostrarlo /
            String contentType = conexion.getContentType();
            texto+="\nURL: "+cadenaURL;
            texto += "\n\nEncabezados destacados:\n* Tipo de contenido:"+ contentType;
            
            //Obtiene el tamaño del contenido
            contentLength = conexion.getContentLength();
            texto+="\n* Tamaño: " + contentLength;
            
            //Obtiene la fecha de la  última modificación
            Date fecha = new Date( conexion.getLastModified() );
            texto += "\n* Fecha de la última modificación: "+ fecha;
           
            //Clasificamos por tipo de contenido
           
            //Si es un pdf
            if(contentType.equals("application/pdf")){
                //Muestra un cuadro de diálogo para general el fichero destino
                File archivoElegido = ventanaGuardarComo();
                if (archivoElegido != null) {
          
                    //Flujo de descarga desde la url
                    InputStream reader = url.openStream();

                    //Flujo de escritura en el fichero
                    FileOutputStream writer =new FileOutputStream(archivoElegido.getPath());

                    //Buffer intermedio ajustado al Content-Length enviado por el Servidor
                    bufByte = new byte[contentLength];

                    texto+= "\n\nDescargando pdf en el directorio elegido ...";
                    
                    //Mientras quedan bytes por leer en el buffer intermedio
                    while ((leido = reader.read(bufByte)) > 0) {
                        writer.write(bufByte, 0, leido);
                    }
                    //Cierra el flujo de escritura
                    writer.close();
                    texto +="\n\nEl pdf ha sido descargado correctamente";
                }
            }
            //Si se trata de texto o contenido html
            else if (contentType.startsWith("text/html")) {
                //Flujo para descargar el cuerpo del texto o página html
                InputStream imputString = conexion.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(imputString));
                //Buffer intermedio de tamaño medio
                bufChar = new char[512];
        
                texto += "\n\nEscribiendo el cuerpo de texto en la salida:\n\n";
              
                
                //Mientras quedan caracteres por leer
                while ((leido = bufferedReader.read(bufChar)) > 0) {
                //los escribe en la Salida
                    texto += "Cuerpo: " + new String(bufChar, 0, leido);
                }
       
                texto += "\n\n ****** Cuerpo de texto obtenido correctamente ******";
            } //en cualquier otro caso **********************************************
            else {
                texto += "\n\nLO SIENTO. No sé que hacer con el tipo de contenido indicado";
            }
        
        } catch (MalformedURLException e) {
            texto += "URL incorrecta o incompleta.";
        } catch (IOException e) {
            texto += "Error de lectura/escritura";
        } finally {
            this.jTexto.setText(texto + "\n");
        }
    }
    
    private File ventanaGuardarComo() {
        //Cuadro de diálogo 'guardar como' de Java...
        JFileChooser fc = new JFileChooser();
        
        //...posicionado en el archivo de nombre tomado de la url
        //lastIndexOf --> devuelve la posición de la última / ya que a continuación
        //viene el nombre del fichero que hay que descargar
        //
        fc.setSelectedFile(new File(cadenaURL.substring(cadenaURL.lastIndexOf("/"))
                + (cadenaURL.endsWith(".pdf") ? "" : ".pdf")));
        
        //Muestra el cuadro de diálogo en pantalla
        int showSaveDialog = fc.showSaveDialog(null);
        //Si se pulsa 'Aceptar'
        if (showSaveDialog == JFileChooser.APPROVE_OPTION) {
          //Devuelve el archivo indicado por el usuario
          return fc.getSelectedFile();
        }
        //Devuelve nulo
        return null;
    }
}
