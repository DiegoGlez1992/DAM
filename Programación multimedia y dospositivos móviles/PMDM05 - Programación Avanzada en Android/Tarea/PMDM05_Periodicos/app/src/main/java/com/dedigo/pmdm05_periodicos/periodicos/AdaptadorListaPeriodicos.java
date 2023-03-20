package com.dedigo.pmdm05_periodicos.periodicos;

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
 * Clase para el adaptador de la lista de periodicos
 */
public class AdaptadorListaPeriodicos extends ArrayAdapter<Periodico> {

    private ArrayList<Periodico> listaPeriodicos = new ArrayList<>();

    public AdaptadorListaPeriodicos(@NonNull Context context, @NonNull ArrayList<Periodico> periodicos) {
        super(context, R.layout.linea_periodico, periodicos);
        this.listaPeriodicos = periodicos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        //Define un View y lo infla con el Layout
        View viewItem = inflater.inflate(R.layout.linea_periodico, null);

        //Inicia los componentes
        TextView txtNombre = viewItem.findViewById(R.id.txtNombre);
        TextView txtTematica = viewItem.findViewById(R.id.txtTematica);

        //Pasa los datos
        txtNombre.setText(listaPeriodicos.get(position).getNombre());
        txtTematica.setText(listaPeriodicos.get(position).getTematica());

        //Devuelve la View
        return viewItem;
    }
}
