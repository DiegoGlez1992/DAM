package com.dedigo.pmdm05_periodicos.periodicos;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dedigo.pmdm05_periodicos.R;
import com.dedigo.pmdm05_periodicos.base_datos.DBInterface;

import java.util.ArrayList;

/**
 * Clase para el fragment que muestra la lista de los periodicos
 */
public class FragmentListaPeriodicos extends Fragment {
    private View vista; //Para inflar la vita con el Layout y poder acceder a los componentes
    private ListView listViewPeriodicos;    //Objeto para el ListView
    private ArrayList<Periodico> listadoPeriodicos = new ArrayList<>();     //ArrayList para guardar los periodicos
    private DBInterface dbInterface = null;     //Objeto para el acceso a la base de datos
    private AdaptadorListaPeriodicos adaptadorListaPeriodicos = null; //Adaptardor para el ListView


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.vista = inflater.inflate(R.layout.fragment_lista_periodicos, container, false);

        this.dbInterface = new DBInterface(getContext());   //Inicia la Base de datos
        obtenerPeriodicos();

        this.listViewPeriodicos = this.vista.findViewById(R.id.lista_periodicos);
        this.adaptadorListaPeriodicos = new AdaptadorListaPeriodicos(getContext(), listadoPeriodicos);

        this.listViewPeriodicos.setAdapter(this.adaptadorListaPeriodicos);

        registerForContextMenu(listViewPeriodicos); //Vincula el menú contextual con el ListView

        return vista;
    }

    /**
     * Método que obtiene toda la lista de Periodicos y los añade al arrayList
     */
    private void obtenerPeriodicos() {
        this.listadoPeriodicos.clear();
        Periodico periodico = null;
        Cursor cursor = this.dbInterface.obtenerPeriodicos();        //Llama a la consulta que obtiene el listado

        //Recorre el cursor y va añadiendo los periodicos a la lista
        while (cursor.moveToNext()) {
            periodico = new Periodico();
            periodico.setId(cursor.getInt(0));  //Obtiene el id del periodico en la posición 0
            periodico.setNombre(cursor.getString(1));   //Obtiene el nombre del periodico en la posición 1
            periodico.setTematica(cursor.getString(2)); //Obtiene la tematica del periodico en la posición 2
            this.listadoPeriodicos.add(periodico); //Añade el Periodico leido del cursor al ArrayList
        }
    }

    /**
     * Método que actualiza los datos del ListView
     */
    public void actualizaDatos() {
        obtenerPeriodicos();    //Lee la base de datos
        adaptadorListaPeriodicos.notifyDataSetChanged();    //Notifica al ListView
    }

    /**
     * Método para vincular el menu contextual
     */
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.lista_periodicos) {   //Si el evento es generado por el listView
            getActivity().getMenuInflater().inflate(R.menu.context_menu, menu); //Infla el menu
        }
    }

    /**
     * Método que recoge el evento de cual es la opción del menu contextual que se ha pulsado.
     *
     * @param item The context menu item that was selected.
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.itemBorrar) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            //Llama al método de borrar
            long resul = this.dbInterface.borrarPeriodico(this.listadoPeriodicos.get(info.position).getId());

            if (resul > 0) {    //Si no hay error
                Toast.makeText(getContext(), "El periodico " + this.listadoPeriodicos.get(info.position).getNombre() + " ha sido borrado", Toast.LENGTH_SHORT).show();
            }
            actualizaDatos();   //Actualiza la lista de datos y notifica al ListView
        }
        return super.onContextItemSelected(item);
    }
}
