/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actualizarcongresoestructurados;

/**
 *
 * @author IMCG
 */
//clase que implementa un charla. Cada charla tiene un título y está asignada
//a un ponente. CLASE ESTRUCTURADA: contiene un objeto ponente
public class charla {
  private String titulo;
  private float duracion;
  private ponente pl;
  //constructor
  public charla(String ti, float h) {
    this.titulo = ti;
    this.pl = null;
    this.duracion=h;
  }
//Método para obtener el ponente de una charla
  public ponente getPonente() {
    return pl;
  }
//Método para asignar el ponente de una charla
  public void setPonente(ponente p) {
    this.pl = p;
  }
//Método para obtener el título de una charla
  public String getTitulo() {
    return titulo;
  }
  //Método para obtener la duración de una charla
  public float getDuracion() {
    return duracion;
  }
//Método para asignar el ponente de una charla
  public void setDuracion(float h) {
    this.duracion = h;
  }
  //Método para mostrar título y ponente de una charla
  @Override
  public String toString() {
    return "Charla: " + titulo + ", " + duracion + " horas.  PONENTE: " +pl ;
  }
}
