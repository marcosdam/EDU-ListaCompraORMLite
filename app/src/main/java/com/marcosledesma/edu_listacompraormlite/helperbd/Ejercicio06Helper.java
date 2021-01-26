package com.marcosledesma.edu_listacompraormlite.helperbd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.marcosledesma.edu_listacompraormlite.configuracion.Params;
import com.marcosledesma.edu_listacompraormlite.modelos.Producto;

import java.sql.SQLException;

public class Ejercicio06Helper extends OrmLiteSqliteOpenHelper {

    private Dao<Producto, Integer> daoProductos;

    public Ejercicio06Helper(Context context) {
        super(context, Params.DATABASE_NAME, null, Params.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Producto.class);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.createTable(connectionSource, Producto.class);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Dao<Producto, Integer> getDaoProductos() throws SQLException {
        if (daoProductos == null)
            daoProductos = this.getDao(Producto.class);
        return daoProductos;
    }
}
