/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PSP04_Tarea3_Servidor;

/**
 * <strong>Clase para el objeto usuario.</strong><br>
 *
 * @author Diego González García
 */
public class User {

    private String user;
    private String pass;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public User(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "User{" + "user=" + user + ", pass=" + pass + '}';
    }
}
