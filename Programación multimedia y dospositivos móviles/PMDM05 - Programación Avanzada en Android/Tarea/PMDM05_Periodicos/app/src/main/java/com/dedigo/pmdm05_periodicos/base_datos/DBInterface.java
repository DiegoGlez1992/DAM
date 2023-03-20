package com.dedigo.pmdm05_periodicos.base_datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dedigo.pmdm05_periodicos.periodicos.Periodico;

/**
 * Clase para el manejo de la base de datos
 */
public class DBInterface {

    private static final String CAMPO_ID = "_id";
    private static final String CAMPO_NOMBRE = "nombre";
    private static final String CAMPO_TEMATICA = "tematica";

    private static final String TAG = "DBInterface";
    private static final String DB_NAME = "DBPeriodicos";
    private static final String DB_TABLE = "periodicos";
    private static final String DB_CREATE =
            "create table " + DB_TABLE + "("
                    + CAMPO_ID + " integer primary key autoincrement, "
                    + CAMPO_NOMBRE + " text not null,"
                    + CAMPO_TEMATICA + " text not null); ";
    private DatabaseConnection conn;
    private SQLiteDatabase bd;

    /**
     * Método constructor
     *
     * @param context
     */
    public DBInterface(Context context) {
        conn = new DatabaseConnection(context, DB_NAME, null, 1);
        //borrarPeriodicos();
        //insertarPeriodico("El Diario Montanes", "Generalista");
    }

    /**
     * Método para insertar un periodico en la base de datos
     *
     * @param periodico Objeto Periodico
     * @return ID de la operación. -1 = error
     */
    public long insertarPeriodico(Periodico periodico) {
        long idResult = -1;

        if (periodico != null) {    //Si existe el periodico
            bd = conn.getWritableDatabase();    //Abre la base de datos
            ContentValues contentValues = new ContentValues();
            contentValues.put(CAMPO_NOMBRE, periodico.getNombre());
            contentValues.put(CAMPO_TEMATICA, periodico.getTematica());
            idResult = bd.insert(DB_TABLE, CAMPO_ID, contentValues);  //Inserta el periodico
            bd.close(); //Cierra la base de datos
        }
        return idResult;
    }

    /**
     * Método para insertar un periodico en la base de datos
     *
     * @param nombre   Nombre del periódico
     * @param tematica Temática del periódico
     * @return ID de la operación. -1 = error
     */
    public long insertarPeriodico(String nombre, String tematica) {
        long idResult = -1;

        if (nombre.equalsIgnoreCase("") || tematica.equalsIgnoreCase("")) {

        } else {
            bd = conn.getWritableDatabase();    //Abre la base de datos
            ContentValues contentValues = new ContentValues();
            contentValues.put(CAMPO_NOMBRE, nombre);
            contentValues.put(CAMPO_TEMATICA, tematica);
            idResult = bd.insert(DB_TABLE, CAMPO_ID, contentValues);
            bd.close(); //Cierra la base de datos
        }

        return idResult;
    }

    /**
     * Método que modifica el nombre y la temática de un periódico
     *
     * @param id       ID del periódico a modificar
     * @param nombre   Nuevo nombre
     * @param tematica Nueva temática
     * @return ID de la operación. -1 = error
     */
    public long modificarPeriodico(int id, String nombre, String tematica) {
        long idResult = -1;

        if (nombre.equalsIgnoreCase("") || tematica.equalsIgnoreCase("")) {

        } else {
            bd = conn.getWritableDatabase();    //Abre la base de datos
            ContentValues contentValues = new ContentValues();
            contentValues.put(CAMPO_NOMBRE, nombre);
            contentValues.put(CAMPO_TEMATICA, tematica);
            idResult = bd.update(DB_TABLE, contentValues, CAMPO_ID + "=" + id, null);
            bd.close(); //Cierra la base de datos
        }
        return idResult;
    }

    /**
     * Método para obtener el periodico que coincida con el ID dado de la base de datos
     *
     * @param id ID del periódico
     * @return Un cursor con los datos de la tabla
     */
    public Cursor obtenerPeriodico(int id) {
        bd = conn.getReadableDatabase();    //Abre la base de datos
        Cursor cursor = bd.query(DB_TABLE, new String[]{CAMPO_ID, CAMPO_NOMBRE, CAMPO_TEMATICA}, CAMPO_ID + "=" + id, null, null, null, null);
        return cursor;
    }

    /**
     * Método para obtener todos los periodicos de la base de datos
     *
     * @return Un cursor con los datos de la tabla
     */
    public Cursor obtenerPeriodicos() {
        bd = conn.getReadableDatabase();    //Abre la base de datos
        Cursor cursor = bd.query(DB_TABLE, new String[]{CAMPO_ID, CAMPO_NOMBRE, CAMPO_TEMATICA}, null, null, null, null, null);
        return cursor;
    }

    /**
     * Método para borrar un periodico en la base de datos
     *
     * @param id ID del periódico
     * @return ID de la operación. -1 = error
     */
    public long borrarPeriodico(int id) {
        long idResult = -1;
        bd = conn.getWritableDatabase();    //Abre la base de datos
        idResult = bd.delete(DB_TABLE, CAMPO_ID + "=" + id, null);
        bd.close(); //Cierra la base de datos
        return idResult;
    }

    /**
     * Método para borrar todos los periódicos de la base de datos
     *
     * @return ID de la operación. -1 = error
     */
    public long borrarPeriodicos() {
        long idResult = -1;
        bd = conn.getWritableDatabase();    //Abre la base de datos
        idResult = bd.delete(DB_TABLE, null, null);
        bd.close(); //Cierra la base de datos
        return idResult;
    }


    public class DatabaseConnection extends SQLiteOpenHelper {

        public DatabaseConnection(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory cursorFactory, int version) {
            super(context, DB_NAME, cursorFactory, version);
            Log.w(TAG, "Constructor de la base de datos");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                Log.w(TAG, "Creando la base de datos " + DB_CREATE);
                db.execSQL(DB_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {
            Log.w(TAG, "Actualizando la base de datos de la versión " + versionAntigua + " a la versión " + versionNueva + ". Destruirá todos los datos.");
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    }
}
