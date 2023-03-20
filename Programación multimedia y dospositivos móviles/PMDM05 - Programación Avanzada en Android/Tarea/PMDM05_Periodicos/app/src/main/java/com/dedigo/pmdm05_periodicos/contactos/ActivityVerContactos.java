package com.dedigo.pmdm05_periodicos.contactos;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.dedigo.pmdm05_periodicos.R;

import java.util.ArrayList;

/**
 * Clase para la actividad de ver los contactos.
 * Importante añadirla en el "AndroidManifest.xml", habilitar los permisos y concederlos desde la app.
 * "<uses-permission android:name="android.permission.READ_CONTACTS" />"
 */
public class ActivityVerContactos extends AppCompatActivity {

    private ArrayList<Contacto> listaContactos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_contactos);

        obtenerContactosTelefono(); //Obtiene los contactos del teléfono.

        ListView listViewContactos = findViewById(R.id.listviewContactos);
        AdaptadorListaContactos adaptadorListaContactos = new AdaptadorListaContactos(this, listaContactos);
        listViewContactos.setAdapter(adaptadorListaContactos);
    }

    /**
     * Método para obtener los contactos del dispositivo.
     *
     * @return
     */
    private void obtenerContactosTelefono() {

        //Proyección de la consulta, especificando las columnas
        String[] proyeccion = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        //Selección de la consulta, especificando solo los contactos que tienen número telefónico
        String seleccion = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ?";

        //Argumentos de la selección, especificando que se quieren solo los contactos con número telefónico
        String[] seleccionArgs = {"1"};

        //Crea un cursor para recibir los datos de la consulta
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                proyeccion,
                seleccion,
                seleccionArgs,
                null);

        //Recorre el cursor y va añadiendo los contactos a la lista
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);    //Obtiene el id del contacto en la posición 0
            String nombre = cursor.getString(1);    //Obtiene el nombre del contacto en la posición 1
            String telefono = cursor.getString(2);  //Obtiene el telefono del contacto en la posición 2
            this.listaContactos.add(new Contacto(id, nombre, telefono));
        }
        cursor.close();     // Cierra el cursor
    }
}