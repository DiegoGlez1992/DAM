using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System;


public class MoverPelota : MonoBehaviour
{
    public AudioClip sonidoChoque;
    public float speed = 30;
    void Start()
    {         // Impulso inicial         
        GetComponent<Rigidbody2D>().velocity =
             Vector2.right * speed * Time.deltaTime;


    }
    // Update is called once per frame
    void Update()
    {

    }

    float reboteY(Vector2 bolaPos, Vector2 raquetaPos, float alturaRaqueta)
    {
        // ||  1 <- parte superior de la raqueta         
        // ||         
        // ||  0 <- parte media de la raqueta         
        // ||         // || -1 <- parte inferior de la raqueta 
        return (bolaPos.y - raquetaPos.y) / alturaRaqueta;
    }



    void OnCollisionEnter2D(Collision2D col)
    {
        //reproduzco sonido de choque
        AudioSource.PlayClipAtPoint(sonidoChoque,
                                    col.transform.position,
                                    5);

        //col es el objecto que recibe la colisi�n de la pelota         
        if (col.gameObject.name == "RaquetaIzda")
        {
            // Calculamos la direcci�n de rebote             
            float y = reboteY(transform.position,//posicion de la pelota                
                              col.transform.position, //posicion de la raqueta
                              col.collider.bounds.size.y);//tama�o de la raqueta 
            // Calculamos la direcci�n, lo normalizamos para que el tama�o               //del vector sea 1al chocar con la raqueta izda               //la direcci�n x ser� 1 (hacia la derecha)             
            Vector2 dir = new Vector2(1, y).normalized;

            GetComponent<Rigidbody2D>().velocity =
                dir * speed * Time.deltaTime;
        }

        // golpea la raqueta derecha         
        if (col.gameObject.name == "RaquetaDcha")
        {
            // Calculate hit Factor             
            float y = reboteY(transform.position,
                col.transform.position,
                col.collider.bounds.size.y);
            // en este caso se mueve hacia la izda (x=-1) 
            Vector2 dir = new Vector2(-1, y).normalized;
            // se aplica la velocidad             
            GetComponent<Rigidbody2D>().velocity =
                dir * speed * Time.deltaTime;
        }

        
    }
}







