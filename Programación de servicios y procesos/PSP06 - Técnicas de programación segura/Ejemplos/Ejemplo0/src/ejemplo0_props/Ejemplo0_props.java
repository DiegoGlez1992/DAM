/*
 * Vamos a ver algunas de las propiedades que obtenemos con el método
 * System.getProperty().
 * Ponemos todas las propiedades en un array.
 */
package ejemplo0_props;

/**
 *
 * @author jfranco
 */
public class Ejemplo0_props {

    /**
     * @param args the command line arguments
     */
    //  System.setProperty("java.security.policy", "./ejemplo0.policy");
      //  System.setSecurityManager(new SecurityManager());
    public static void main(String[] args) {
       /* System.setProperty("java.security.policy", 
                             "./ejemplo0.policy");
        System.setSecurityManager(new SecurityManager());*/
      
        //propiedades del sistema
        String propiedades[] = {
            "java.class.path", "java.home", "java.vendor", 
            "java.version", "os.name", "os.version", 
            "user.dir", "user.home",  "user.name"
        };
//vemos las propiedades
        for (int i = 0; i < propiedades.length; i++) {
            try {
                String prop = System.
                        getProperty(propiedades[i]);
                System.out.println("\n* " + propiedades[i] + " = " 
                        + prop);
            } catch (Exception e) {
                System.err.println("Caught exception " +
                        e.toString());
            }
        }
    }

}
