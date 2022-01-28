/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PROG05_Ejerc1;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import PROG05_Ejerc1_util.Validacion;

/**
 * En el presente programa se desarrolla una aplicación Java que permite 
 * gestionar un vehículo mediante un menú que aparece en pantalla, a través del 
 * cual se podrán realizar diferentes operaciones como:
 * 1. Nuevo vehículo
 *      - Marca
 *      - Matricula
 *      - Número de kilómetros
 *      - Fecha de matriculación
 *      - Descripción
 *      - Precio
 *      - Nombre del propietario
 *      - DNI del propietario
 * 2. Ver matrícula
 * 3. Ver número de kilómetros
 * 4. Actualizar kilómetros
 * 5. Ver años de antigüedad
 * 6. Mostrar propietario
 * 7. Mostrar descripción
 * 8. Mostrar precio
 * 9. Salir
 * 
 * @author diego
 * @version 1.0
 */
public class Principal {
    
    /**
     * Método main
     * 
     * @param arg the command line arguments
     */
    public static void main (String[] arg) {
        Scanner teclado = new Scanner(System.in); //Clase Scanner para la introducción de datos por teclado
        Vehiculo vehiculo=new Vehiculo();   //Creamos el objeto vehiculo
        
        boolean vehiculoCreado=false;   //Variable para controlar si hay algún vehículo creado
        int tecla;  //Variable auxiliar para recoger datos del teclado
        
        //Variables para el manejo de datos del vehiculo
        String marca, matricula, descripcion, nombrePropietario, dniPropietario;
        long numKilometros;
        double precio;
        LocalDate fechaMatriculacion;
        
        while(true){    //Bucle infinito
            ////////////////////////////////
            //Mostramos el menú del programa
            System.out.println("1. Nuevo vehículo.");
            System.out.println("2. Ver matrícula.");
            System.out.println("3. Ver número de kilómetros.");
            System.out.println("4. Actualizar kilómetros.");
            System.out.println("5. Ver años de antigüedad.");
            System.out.println("6. Mostrar propietario.");
            System.out.println("7. Mostrar descripción.");
            System.out.println("8. Mostrar precio.");
            System.out.println("9. Salir.");
            
            tecla=teclado.nextInt();
            teclado.nextLine();
            switch(tecla){
                case 1: //Nuevo vehículo
                    //Solicitamos marca del vehiculo
                    System.out.print("\tMarca del vehículo: ");
                    marca=teclado.nextLine();
                    
                    //Solicitamos matrícula del vehiculo
                    System.out.print("\tMatrícula del vehículo: ");
                    matricula=teclado.nextLine();
                    
                    //Solicitamos kilometros del vehiculo
                    while (true) {
                        System.out.print("\tKilómetros del vehículo: ");
                        numKilometros=teclado.nextLong();
                        teclado.nextLine();
                        if (numKilometros > 0)  //Si el numero de kilometros es > 0, damos el dato por bueno
                            break;
                        else    //Si no, mostramos mensaje de error y volvemos a solicitar
                            System.out.println("\t\tError: El número de kilometros tiene que ser superior a 0.");
                    }
                    
                    //Solicitamos fecha de matriculacion
                    while(true) {
                        try {
                            System.out.print("\tFecha de matriculación (dd/MM/YYY): ");
                            fechaMatriculacion=LocalDate.parse(teclado.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            if (Validacion.fechaMatriculacionValida(fechaMatriculacion))    //Si la validación de la fecha de matriculación es correcta, damos el dato por bueno
                                break;
                            else    //Si no, mostramos mensaje de error y volvemos a solicitar 
                                System.out.println("\t\tError: La fecha de matriculación no puede ser posterior al día de hoy.");
                        } catch (Exception error) { //Si capturamos una excepción, la tratamos mostrando el mensaje de error y volviendo a solicitar un dato
                            System.out.println("\t\tError: El formato de fecha no es el correcto.");
                        }
                    }
                    
                    //Solicitamos la descripción del vehiculo
                    System.out.print("\tDescripción del vehículo: ");
                    descripcion=teclado.nextLine();
                    
                    //Solicitamos el precio del vehículo
                    System.out.print("\tPrecio del vehículo: ");
                    precio=teclado.nextDouble();
                    teclado.nextLine();
                    
                    //Solicitamos el nombre del propietario
                    System.out.print("\tNombre del propietario: ");
                    nombrePropietario=teclado.nextLine();
                    
                    //Solicitamos el DNI del propietario
                    while (true) {
                        try {
                            System.out.print("\tDNI del propietario: ");
                            dniPropietario=teclado.nextLine();
                            if (Validacion.validarNIF(dniPropietario))  //Si el DNI es validado, damos el dato por bueno
                                break;
                        } catch (Exception error) { //Si capturamos una excepción, la tratamos mostrando el mensaje de error y volviendo a solicitar un dato
                            System.out.println("\t\tError: "+error.getMessage());
                        }
                    }
                    
                    ////////////////////////////////////////////////
                    //Actualizamos los atributos del objeto vehiculo
                    vehiculo.setMarca(marca);
                    vehiculo.setMatricula(matricula);
                    vehiculo.setDescripcion(descripcion);
                    vehiculo.setNombrePropietario(nombrePropietario);
                    vehiculo.setDniPropietario(dniPropietario);
                    vehiculo.setNumKilometros(numKilometros);
                    vehiculo.setPrecio(precio);
                    vehiculo.setFechaMatriculacion(fechaMatriculacion);
                    
                    vehiculoCreado=true;    //Marcamos el flag de que ya hay un vehiculo creado
                    System.out.println("\t\tVehículo creado con éxito. ");
                    break;
                    
                case 2: //Ver matricula
                    if (!vehiculoCreado)    //Si no hay un vehiculo creado, mostramos error
                        System.out.println("\tError: Primero debe añadir un vehículo.");
                    else {  //Si hay un objeto vehiculo creado, mostramos la información solicitada
                        System.out.println("\tLa matrícula del vehículo es: "+vehiculo.getMatricula());
                    }
                    break;
                    
                case 3: //Ver número de kilómetros
                    if (!vehiculoCreado)    //Si no hay un vehiculo creado, mostramos error
                        System.out.println("\tError: Primero debe añadir un vehículo.");
                    else {  //Si hay un objeto vehiculo creado, mostramos la información solicitada
                        System.out.println("\tEl vehículo tiene: "+vehiculo.getNumKilometros()+" kilómetros");
                    }
                    break;
                    
                case 4: //Actualizar kilómetros
                    if (!vehiculoCreado)    //Si no hay un vehiculo creado, mostramos error
                        System.out.println("\tError: Primero debe añadir un vehículo.");
                    else {  //Si hay un objeto vehiculo creado, realizamos la modificación solicitada
                        while(true) {
                            System.out.print("\tIndica el nuevo número de kilometros: ");
                            numKilometros=teclado.nextLong();
                            teclado.nextLine();
                            if (Validacion.numKilometrosValida(numKilometros, vehiculo.getNumKilometros())) //Si los kilometros indicados son superiores a los actuales, damos el dato por válido
                                break;
                            else    //Si no, mostramos mensaje de error y volvemos a solicitar 
                                System.out.println("\t\tError: El número de kilometros no puede ser inferior o igual al actual.");
                        }
                        vehiculo.setNumKilometros(numKilometros);   //Actualizamos el valor en el objeto
                        System.out.println("\t\tKilómetros actualizados con éxito. ");
                    }
                    break;
                    
                case 5: //Ver años de antiugüedad
                    if (!vehiculoCreado)    //Si no hay un vehiculo creado, mostramos error
                        System.out.println("\tError: Primero debe añadir un vehículo.");
                    else {  //Si hay un objeto vehiculo creado, mostramos la información solicitada
                        System.out.println("\tEl vehículo tiene: "+vehiculo.get_Anios()+" años");
                    }
                    break;
                    
                case 6: //Mostrar propietario
                    if (!vehiculoCreado)    //Si no hay un vehiculo creado, mostramos error
                        System.out.println("\tError: Primero debe añadir un vehículo.");
                    else {  //Si hay un objeto vehiculo creado, mostramos la información solicitada
                        System.out.println("\tEl propietario del vehículo es: "+vehiculo.getNombrePropietario()+" con DNI "+vehiculo.getDniPropietario());
                    }
                    break;
                    
                case 7: //Mostras descripción
                    if (!vehiculoCreado)    //Si no hay un vehiculo creado, mostramos error
                        System.out.println("\tError: Primero debe añadir un vehículo.");
                    else {  //Si hay un objeto vehiculo creado, mostramos la información solicitada
                        System.out.println("\tDescripción del vehículo con matrícula "+vehiculo.getMatricula()+" y "+vehiculo.getNumKilometros()+" Kilómetros: "+vehiculo.getDescripcion());
                    }
                    break;
                    
                case 8: // Mostrar precio
                    if (!vehiculoCreado)    //Si no hay un vehiculo creado, mostramos error
                        System.out.println("\tError: Primero debe añadir un vehículo.");
                    else {  //Si hay un objeto vehiculo creado, mostramos la información solicitada
                        System.out.println("\tEl precio del vehículo es: "+vehiculo.getPrecio()+" €");
                    }
                    break;
                    
                case 9: //Salir
                    return; //Termina el programa
            }
        } 
    }
    
}
