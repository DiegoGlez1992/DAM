package com.dedigo.pmdm05_periodicos.periodicos;

/**
 * Clase para los objetos Periodico
 */
public class Periodico {
    private int id;
    private String nombre;
    private String tematica;

    public Periodico() {
    }

    public Periodico(int id, String nombre, String tematica) {
        this.id = id;
        this.nombre = nombre;
        this.tematica = tematica;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTematica() {
        return tematica;
    }

    public void setTematica(String tematica) {
        this.tematica = tematica;
    }
}
