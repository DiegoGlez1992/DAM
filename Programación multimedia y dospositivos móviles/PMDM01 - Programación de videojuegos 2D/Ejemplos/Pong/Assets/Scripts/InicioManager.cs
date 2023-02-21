using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class InicioManager : MonoBehaviour
{
    public void OnClick()
    {
        UnityEngine.SceneManagement.SceneManager.LoadScene("Pong");
    }
}
