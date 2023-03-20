
package servidor_g2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;



class Servidor_G2 extends Thread{
    
    //Por cada hilo se crea un Socket para el cliente
    Socket skCliente;
    
    public Servidor_G2(Socket sCliente){
        skCliente = sCliente;
    }
    
    @Override
    public void run(){
        
        try{
            // Se crean los flujos de entrada y salida
            DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(skCliente.getOutputStream());
           
            //Cuerpo del algoritmo que realiza el 
            int estado = 0;         //Estado inicial del sistema
            String usuario ="";     //Nombre de usuario que da acceso al servidor
            String password ="";    //Contraseña de usuario que da acceso al servidor
            String comando = "";    //Recoge el comando a ejecutar
            String fichero="";      //Recoge el nombre del fichero a leer
            
            //Manda un mensaje de bienvenida
            flujo_salida.writeUTF("----------------------------------");
            flujo_salida.writeUTF("BIENVENIDO AL SERVIDOR DE COMANDOS");
            flujo_salida.writeUTF("----------------------------------");
            flujo_salida.writeUTF(""); //Salto de línea
            //Dependiendo del estado en que se encuentre el sistema variarán la 
            //acciones a ejecutarse.
            
            do{
                switch(estado){
                    case 0:
                        System.out.println("Solicitando usuario.");
                        //Pide el nombre de usuario
                        flujo_salida.writeUTF("Usuario: ");
                        flujo_salida.writeUTF("EOF");
                        
                        //Se recoje el nombre de usuario
                        usuario = flujo_entrada.readUTF();
                        System.out.println("/"+usuario+"/");
                        if(usuario.equals("gema")){
                            System.out.println("Usuario correcto.");
                            estado = 1;
                        }else{
                            flujo_salida.writeUTF("      Usuario incorrecto.");
                            flujo_salida.writeUTF(""); //Salto de línea
                        }
                        
                    break; 
                    
                    case 1:
                        System.out.println("Solicitando contraseña.");
                        //Pide la contraseña
                        flujo_salida.writeUTF("Contraseña: ");
                        flujo_salida.writeUTF("EOF");
                        
                        //Se recoje el nombre de usuario
                        password = flujo_entrada.readUTF();
                        System.out.println("/"+password+"/");
                        if(password.equals("secreta")){
                            System.out.println("Contraseña correcta.");
                            estado = 2;
                        }else{
                            flujo_salida.writeUTF("      Contraseña incorrecta.");
                            flujo_salida.writeUTF(""); //Salto de línea
                        }
                        
                    break;
                     
                    case 2:
                        //Muestra un menú con las diferentes opciones
                        flujo_salida.writeUTF("");
                        flujo_salida.writeUTF("MENÚ");
                        flujo_salida.writeUTF("    * Listado del contenido del directorio: DIR");
                        flujo_salida.writeUTF("    * Mostrar el contenido de un archivo: GET");
                        flujo_salida.writeUTF("    * Salir de la aplicación: EXIT");
                        flujo_salida.writeUTF("         Introduzca DIR / GET / EXIT... ");
                        flujo_salida.writeUTF("EOF");        
                       
                        comando = flujo_entrada.readUTF();
                        
                        comando=comando.toLowerCase();
                        
                        switch (comando) {
                            case "dir":
                                estado = 3;
                                break;
                            case "get":
                                estado = 4;
                                break;  
                            case "exit":
                                estado = -1;
                                break;
                            default:
                                flujo_salida.writeUTF("      Opción incorrecta.");
                                flujo_salida.writeUTF(""); //Salto de línea
                                break;
                        }
                        break;

                    case 3:
                        System.out.println("El cliente ha seleccionado ver el contenido del directorio.");
                        String sDirectorio="./";
                        File f = new File(sDirectorio);
                        File[] ficheros = f.listFiles();
                        flujo_salida.writeUTF(""); //Salto de línea
                        flujo_salida.writeUTF("Contenido del directorio (comando dir)");
                        flujo_salida.writeUTF("--------------------------------------");
                        for (File f1 : ficheros) {
                            flujo_salida.writeUTF(f1.getName());
                        }
                        estado = 2;
                        
                        break;
                    case 4:

                        System.out.println("El cliente ha seleccionado mostrar el contenido de un archivo.");

                        flujo_salida.writeUTF(""); //Salto de línea
                        flujo_salida.writeUTF("Introduzca el nombre del archivo");
                        flujo_salida.writeUTF("EOF");
                        fichero = flujo_entrada.readUTF();
                        
                        System.out.println("Leyendo: /"+fichero+"/");
                        
                        estado = 5;
                        break;
                    
                    case 5:
                        try{
                            FileInputStream fstream = new FileInputStream(fichero);
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));

                            String strLine="";
                            flujo_salida.writeUTF("");
                            flujo_salida.writeUTF("Contenido del fichero: "+fichero);
                            flujo_salida.writeUTF("---------------------");
                            while ((strLine = br.readLine()) != null)
                              flujo_salida.writeUTF(strLine);

                            in.close();

                            System.out.println("\tFichero enviado correctamente");
                            flujo_salida.writeUTF(" ");

                        }catch (Exception e){
                                flujo_salida.writeUTF("       Error, el fichero "+fichero+" no existe");
                                System.out.println("\tError, el fichero "+fichero+" no existe");
                                flujo_salida.writeUTF(" ");
                        }	                       
                        estado = 2;
                        break;
                            
                }
            }while(estado!=-1);
              
            
            // Se cierran los flujos y la conexión
            flujo_entrada.close();
            flujo_salida.close();
            
            skCliente.close();
            
            System.out.println("Cliente desconectado");
            
        } catch (Exception ex) {
           System.out.println(ex.getMessage());
        } 
      
    }
}
           