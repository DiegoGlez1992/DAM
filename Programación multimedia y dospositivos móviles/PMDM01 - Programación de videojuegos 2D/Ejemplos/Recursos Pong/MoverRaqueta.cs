using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MoverRaqueta : MonoBehaviour
{

    public float speed = 30;
    public bool raqueta1;

    void FixedUpdate() 
    {
        float v;
        if (raqueta1)
        {
            v = Input.GetAxisRaw("Vertical");
        }
        else
        {
            v = Input.GetAxisRaw("Vertical2");
        }
        GetComponent<Rigidbody2D>().velocity = new Vector2(0, v) * speed;

    }

}
