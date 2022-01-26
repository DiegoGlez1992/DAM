/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog05.ejerc1;

import java.time.LocalDate;
import java.time.Month;
import java.util.Scanner;
import prog05.ejerc1.util.Validar;

/**
 *
 * @author
 */
public class Principal {

    static Scanner sca = new Scanner(System.in);

    /*
    Muestra el menú en pantalla.
     */
    public static int mostrarMenu() {

        System.out.println("GESTIÓN DE VEHÍCULO");

        System.out.println("1.Nuevo Vehículo");
        System.out.println("2.Ver Matrícula");
        System.out.println("3.Ver Kilómetros");
        System.out.println("4.Actualizar Kilómetros");
        System.out.println("5.Ver años de antigüedad");
        System.out.println("6.Mostrar Propietario");
        System.out.println("7.Mostrar Descripción");
        System.out.println("8.Mostrar Precio");
        System.out.println("9. Salir");

        int opt = sca.nextInt();
        sca.nextLine(); //consumimos el salto de línea sino al leer el siguiente error tenemos error.
        return opt;
    }

    public static void main(String args[]) {
        int opt;

        String marca, matricula, descripcion, propietario, dni_propietario;
        int numkms, precio;
        int dia_mat, mes_mat, anio_mat;
        LocalDate fecha_mat;
        boolean allOK = true;

        //En principio la referencia al Vehículo apuntará a null.
        Vehiculo objVehiculo = null;
        do {
            opt = mostrarMenu();

            switch (opt) {

                //Crear nuevo vehículo. Si ya existe alguno, desaparecerá su referencia al crear el nuevo.
                case 1:
                    System.out.println("Introduce la marca del vehículo");
                    marca = sca.nextLine();
                    System.out.println("Introduce la matrícula del vehículo");
                    matricula = sca.nextLine();
                    System.out.println("Introduce la descripción del vehículo");
                    descripcion = sca.nextLine();
                    System.out.println("Introduce el número de kilómetros del vehículo");
                    numkms = sca.nextInt();
                    sca.nextLine();
                    System.out.println("Introduce el precio del vehículo");
                    precio = sca.nextInt();
                    sca.nextLine();

                    System.out.println("Introduce el propietario del vehículo");
                    propietario = sca.nextLine();
                    System.out.println("Introduce el dni propietario del vehículo");
                    dni_propietario = sca.nextLine();

                    System.out.println("Introduce el dia de matriculacion");
                    dia_mat = sca.nextInt();
                    sca.nextLine();

                    System.out.println("Introduce el mes de matriculacion");
                    mes_mat = sca.nextInt();
                    sca.nextLine();

                    System.out.println("Introduce el año de matriculacion");
                    anio_mat = sca.nextInt();
                    sca.nextLine();

                    fecha_mat = LocalDate.of(anio_mat, mes_mat, dia_mat);

                    //Validación de la fecha.
                    if (!Validar.comparaFecha(fecha_mat)) {
                        allOK = false;
                        System.out.println("Los datos de la fecha de matriculación son incorrectos o la fecha no es anterior a la actual");
                    }

                    //validación número de kilómetros.
                    if (numkms <= 0) {
                        allOK = false;
                        System.out.println("El número de kilómetros es incorrecto");
                    }

                    //control del DNI con excepciones.
                    try {
                        Validar.validaDNI(dni_propietario);
                    } catch (Exception e) {
                        allOK = false;
                        System.out.println("El formato del DNI no es correcto");
                    }
                    //Si las validaciones son correctas, instanciamos el vehículo.
                    if (allOK) {
                        objVehiculo = new Vehiculo(marca, matricula, numkms, fecha_mat, descripcion, precio, propietario, dni_propietario);
                        System.out.println ("El vehículo ha sido creado");
                    }

                    break;
                case 2:
                    if (objVehiculo != null) {
                        System.out.println("La matrícula del Vehículo es: " + objVehiculo.getMatricula());
                    } else {
                        System.out.println("No existe Vehículo.");
                    }
                    break;

                case 3:
                    if (objVehiculo != null) {
                        System.out.println("El número de kilómtros del Vehículo es: " + objVehiculo.getNum_kms());
                    } else {
                        System.out.println("No existe Vehículo.");
                    }

                    break;
                case 4:
                    if (objVehiculo != null) {

                        System.out.println("Introduce el nuevo número de kilómetros");
                        int nuevos_kms = sca.nextInt();
                        if (nuevos_kms > 0) {
                            objVehiculo.act_kms(nuevos_kms);
                        }
                    } else {
                        System.out.println("No existe Vehículo.");
                    }

                    break;

                case 5:
                    if (objVehiculo != null) {
                        System.out.println("El Vehículo tiene es: " + objVehiculo.get_Anios() + " años");
                    } else {
                        System.out.println("No existe Vehículo.");
                    }
                case 6:
                    if (objVehiculo != null) {
                        System.out.println("El propietario del Vehículo es: " + objVehiculo.getPropietario() + ", con DNI " + objVehiculo.getDni_propietario());
                    } else {
                        System.out.println("No existe Vehículo.");
                    }
                case 7:
                    if (objVehiculo != null) {
                        System.out.println("La descripción del Vehículo es: " + objVehiculo.getDescripcion() + ", con DNI " + objVehiculo.getDni_propietario());
                    } else {
                        System.out.println("No existe Vehículo.");
                    }
                case 8:
                    if (objVehiculo != null) {
                        System.out.println("El precio del Vehículo es: " + objVehiculo.getPrecio() + "€");
                    } else {
                        System.out.println("No existe Vehículo.");
                    }
                case 9: {
                    System.out.println("Eso es todo. BYE");
                    break;
                }
            }
        } while (opt != 9);
    }

}
