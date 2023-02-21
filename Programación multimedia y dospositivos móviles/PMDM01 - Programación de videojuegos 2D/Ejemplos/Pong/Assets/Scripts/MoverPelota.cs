using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System;


public class MoverPelota : MonoBehaviour
{
    public float speed = 30;
    public AudioClip sonidoChoque;

    // Start is called before the first frame update
    void Start()
    {
        //Impulso inicial
        GetComponent<Rigidbody2D>().velocity = Vector2.right * speed;
        sonidoChoque = (AudioClip)Resources.Load("choque");
    }

    float reboteY(Vector2 bolaPos, Vector2 raquetaPos, float alturaRaqueta)
    {
        // || 1 <- parte superior de la raqueta
        // ||
        // || 0 <- parte media de la raqueta
        // ||
        // || -1 <- parte inferior de la raqueta
        return (bolaPos.y - raquetaPos.y) / alturaRaqueta;
    }

    void OnCollisionEnter2D(Collision2D col)
    {
        //col es el objecto que recibe la colisi�n de la pelota
        if (col.gameObject.name == "RaquetaPong1")
        {
            // Calculamos la direcci�n de rebote
            float y = reboteY(
            transform.position,//posicion de la pelota
            col.transform.position, //posicion de la raqueta
            col.collider.bounds.size.y);//tama�o de la raqueta
        // Calculamos la direcci�n, lo normalizamos para que el tama�o
        //del vector sea 1al chocar con la raqueta izda
        //la direcci�n x ser� 1 (hacia la derecha)
            Vector2 dir = new Vector2(1, y).normalized;
            GetComponent<Rigidbody2D>().velocity = dir * speed;
        }
        // golpea la raqueta derecha
        if (col.gameObject.name == "RaquetaPong2")
        {
            // Calculate hit Factor
            float y = reboteY(transform.position,
            col.transform.position,
            col.collider.bounds.size.y);
            // en este caso se mueve hacia la izda (x=-1)
            Vector2 dir = new Vector2(-1, y).normalized;
            // se aplica la velocidad
            GetComponent<Rigidbody2D>().velocity = dir * speed;
        }
        //reproduzco sonido de choque
        AudioSource.PlayClipAtPoint(sonidoChoque, col.transform.position, 5);
        //col es el objeto que recibe la colisi�n de la pelota
        if (col.gameObject.name == "MuroIzquierda")
        {
            GameObject scoreJ1 = GameObject.Find("Marcador2");
            Text scoreText = scoreJ1.GetComponent<Text>();
            int newScore = Int32.Parse(scoreText.text) + 1;
            scoreText.text = newScore.ToString();
        }
        //col es el objeto que recibe la colisi�n de la pelota
        if (col.gameObject.name == "MuroDerecha")
        {
            GameObject scoreJ1 = GameObject.Find("Marcador1");
            Text scoreText = scoreJ1.GetComponent<Text>();
            int newScore = Int32.Parse(scoreText.text) + 1;
            scoreText.text = newScore.ToString();
        }
    }
    

}
