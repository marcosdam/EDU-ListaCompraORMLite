package com.marcosledesma.edu_listacompraormlite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.marcosledesma.edu_listacompraormlite.adapters.ProductosAdapter;
import com.marcosledesma.edu_listacompraormlite.configuracion.Params;
import com.marcosledesma.edu_listacompraormlite.databinding.ActivityMainBinding;
import com.marcosledesma.edu_listacompraormlite.helperbd.Ejercicio06Helper;
import com.marcosledesma.edu_listacompraormlite.modelos.Producto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Binding
    private ActivityMainBinding binding;

    // RecyclerView
    private ProductosAdapter adapter;
    private ArrayList<Producto> productos;
    private int resource = R.layout.producto_card;
    private RecyclerView.LayoutManager lm;

    // Base de Datos
    private Ejercicio06Helper helper;
    private Dao<Producto, Integer> daoProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activar binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. ArrayList (helper después)
        productos = new ArrayList<>();
        helper = OpenHelperManager.getHelper(this, Ejercicio06Helper.class);

        try {
            daoProductos = helper.getDaoProductos();
            productos.addAll(daoProductos.queryForAll());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // 2. LayoutManager, Adapter y Recycler
        lm = new LinearLayoutManager(this);
        adapter = new ProductosAdapter(productos, resource, this, daoProductos);
        binding.contenedor.recyclerView.setHasFixedSize(true);
        binding.contenedor.recyclerView.setAdapter(adapter);
        binding.contenedor.recyclerView.setLayoutManager(lm);

        calculaImporteCarrito();


        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lanzar Alert Dialog para crear producto
                crearProducto().show();
            }
        });
    }

    private AlertDialog crearProducto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View cuerpoDialog = getLayoutInflater().inflate(R.layout.producto_dialog, null);
        // txts para enlazar con producto_dialog.xml
        EditText txtNombre = cuerpoDialog.findViewById(R.id.txtNombreDialog);
        EditText txtCantidad = cuerpoDialog.findViewById(R.id.txtCantidadDialog);
        EditText txtPrecio = cuerpoDialog.findViewById(R.id.txtPrecioDialog);
        TextView txtResumenProducto = cuerpoDialog.findViewById(R.id.txtResumenDialog);

        // Para el addTextChangedListener
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Recalcular importeTotal
                try{
                    int cantidad = Integer.parseInt(txtCantidad.getText().toString());
                    float precio = Float.parseFloat(txtPrecio.getText().toString());
                    txtResumenProducto.setText(Params.nf.format(cantidad*precio));
                }catch (NumberFormatException ex){}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        //
        txtCantidad.addTextChangedListener(tw);
        txtPrecio.addTextChangedListener(tw);

        builder.setView(cuerpoDialog);
        builder.setTitle("Añadir Producto");
        builder.setCancelable(false);
        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("AÑADIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!txtNombre.getText().toString().isEmpty()
                        && !txtCantidad.getText().toString().isEmpty()
                        && !txtPrecio.getText().toString().isEmpty())
                {
                    Producto p = new Producto(txtNombre.getText().toString(),
                            Integer.parseInt(txtCantidad.getText().toString()),
                            Float.parseFloat(txtPrecio.getText().toString()));
                    // Si no hay ningún producto, no hay ID, la BD puede fallar
                    try {
                        daoProductos.create(p);
                        p.setId(daoProductos.extractId(p));
                        productos.add(p);
                        adapter.notifyDataSetChanged();
                        calculaImporteCarrito();
                    } catch (SQLException throwables) {
                        Toast.makeText(MainActivity.this, "Error de Base de Datos", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR_BD", throwables.getSQLState());
                    }
                }else {
                    Toast.makeText(MainActivity.this, "ERROR, Datos incompletos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return builder.create();
    }

    public void calculaImporteCarrito() {
        float total = 0;
        for (Producto p : productos) {
            total += p.getImporteTotal();
        }
        binding.txtResumenMain.setText(Params.nf.format(total));    // Mostrar en txt
    }
}