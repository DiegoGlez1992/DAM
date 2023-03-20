package Tarea1_servidor_http_g;

/**
 * ****************************************************************************
 * clase no instanciable donde se definen algunos valores finales
 *
 * @author IMCG
 */
public class Paginas_G {

  public static final String primeraCabecera =
          "Content-Type:text/html;charset=ISO8859-1";
  
    //contenido index
  public static final String html_index = "<html>"
          + "<head><title>index</title></head>"
          + "<body>"
          + "<h1>�Enhorabuena!</h1>"
          + "<p>Tu servidor HTTP m�nimo funciona correctamente</p>"
          + "</body>"
          + "</html>";
  //contenido quijote
  public static final String html_quijote = "<html>"
          + "<head><title>quijote</title></head>"
          + "<body>"
          + "<h1>As� comienza el Quijote</h1>"
          + "<p>En un lugar de la Mancha, de cuyo nombre no quiero "
          + "acordarme, no ha mucho tiempo que viv�a un hidalgo de los "
          + "de lanza en astillero, adarga antigua, roc�n flaco y galgo "
          + "corredor. Una olla de algo m�s vaca que carnero, salpic�n "
          + "las m�s noches, duelos y quebrantos (huevos con tocino) los "
          + "s�bados, lentejas los viernes, alg�n palomino de a�adidura "
          + "los domingos, consum�an las tres partes de su hacienda. El "
          + "resto della conclu�an sayo de velarte (traje de pa�o fino), "
          + "calzas de velludo (terciopelo) para las fiestas con sus "
          + "pantuflos de lo mismo, y los d�as de entresemana se honraba "
          + "con su vellor� (pardo de pa�o) de lo m�s fino. Ten�a en su "
          + "casa una ama que pasaba de los cuarenta, y una sobrina que "
          + "no llegaba a los veinte, y un mozo de campo y plaza, que "
          + "as� ensillaba el roc�n como tomaba la podadera...</p>"
          + "</body>"
          + "</html>";
  //contenido noEncontrado
  public static final String html_noEncontrado = "<html>"
          + "<head><title>noEncontrado</title></head>"
          + "<body>"
          + "<h1>�ERROR! P�gina no encontrada</h1>"
          + "<p>La p�gina que solicitaste no existe en nuestro servidor</p>"
          + "</body>"
          + "</html>";
}