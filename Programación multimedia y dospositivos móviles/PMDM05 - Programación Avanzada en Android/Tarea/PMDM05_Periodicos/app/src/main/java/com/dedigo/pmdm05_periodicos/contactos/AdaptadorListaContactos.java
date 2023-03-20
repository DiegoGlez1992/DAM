package com.dedigo.pmdm05_periodicos.contactos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dedigo.pmdm05_periodicos.R;

import java.util.ArrayList;

/**
 * Clase para el adaptador de la lista de contactos
 */
public class AdaptadorListaContactos extends ArrayAdapter<Contacto> {

    private ArrayList<Contacto> listaContactos = new ArrayList<>();

    public AdaptadorListaContactos(@NonNull Context context, @NonNull ArrayList<Contacto> lista) {
        super(context, R.layout.linea_contacto, lista);
        this.listaContactos = lista;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        //Definimos un View y lo inflamos con el Layout
        View viewItem = inflater.inflate(R.layout.linea_contacto, null);

        //Iniciamos los componentes
        TextView txtNombre = viewItem.findViewById(R.id.txtNombre);

        //Pasamos los datos
        txtNombre.setText(listaContactos.get(position).getNombre());

        //Devolvemos la View
        return viewItem;
    }

}
