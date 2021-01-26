package com.marcosledesma.edu_listacompraormlite.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.j256.ormlite.dao.Dao;
import com.marcosledesma.edu_listacompraormlite.MainActivity;
import com.marcosledesma.edu_listacompraormlite.R;
import com.marcosledesma.edu_listacompraormlite.configuracion.Params;
import com.marcosledesma.edu_listacompraormlite.modelos.Producto;

import java.sql.SQLException;
import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoVH> {

    private List<Producto> objects;
    private int resource;
    private Context context;
    private Dao<Producto, Integer> daoProductos;

    public ProductosAdapter(List<Producto> objects, int resource, Context context, Dao<Producto, Integer> daoProductos) {
        this.objects = objects;
        this.resource = resource;
        this.context = context;
        this.daoProductos = daoProductos;
    }

    @NonNull
    @Override
    public ProductoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productoView = LayoutInflater.from(context).inflate(resource, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        productoView.setLayoutParams(lp);
        return new ProductoVH(productoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoVH holder, int position) {
        holder.txtNombre.setText(objects.get(position).getNombre());
        holder.txtCantidad.setText(String.valueOf(objects.get(position).getCantidad()));

        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("¿Seguro que desea eliminar?");
                alert.setNegativeButton("No", null);
                alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            // 1. Eliminar de la BD (con el dao)
                            daoProductos.delete(objects.get(position));
                            // 2. Eliminar de la lista
                            objects.remove(position);
                            // 3. Actualizar visualización e importeTotal
                            notifyDataSetChanged();
                            ((MainActivity)context).calculaImporteCarrito();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                });
                alert.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertEditar = new AlertDialog.Builder(context);
                View contenido = LayoutInflater.from(context).inflate(R.layout.producto_dialog, null);
                alertEditar.setView(contenido);

                EditText txtNombre = contenido.findViewById(R.id.txtNombreDialog);
                EditText txtCantidad = contenido.findViewById(R.id.txtCantidadDialog);
                EditText txtPrecio = contenido.findViewById(R.id.txtPrecioDialog);
                TextView txtResumenProducto = contenido.findViewById(R.id.txtResumenDialog);

                txtNombre.setVisibility(View.GONE);
                alertEditar.setTitle(objects.get(position).getNombre());
                txtCantidad.setText(String.valueOf(objects.get(position).getCantidad()));
                txtPrecio.setText(String.valueOf(objects.get(position).getPrecio()));
                txtResumenProducto.setText(Params.nf.format(objects.get(position).getCantidad()*objects.get(position).getPrecio()));

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

                alertEditar.setCancelable(true);
                alertEditar.setNegativeButton("CANCELAR", null);
                alertEditar.setPositiveButton("ACTUALIZAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!txtCantidad.getText().toString().isEmpty()
                        && !txtPrecio.getText().toString().isEmpty()){
                            try {
                                // Actualizar en la lista
                                objects.get(position).setCantidad(Integer.parseInt(txtCantidad.getText().toString()));
                                objects.get(position).setPrecio(Float.parseFloat(txtPrecio.getText().toString()));
                                objects.get(position).recalculaImporteTotal();
                                // Una vez actualizado, actualizar DAO
                                daoProductos.update(objects.get(position));
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }else {
                            Toast.makeText(context, "ERROR EN LOS DATOS", Toast.LENGTH_SHORT).show();
                        }
                        notifyDataSetChanged(); // Actualizar adapter
                        ((MainActivity)context).calculaImporteCarrito();
                    }
                });

                // Mostrarlo
                alertEditar.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public static class ProductoVH extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCantidad;
        ImageButton btnEliminar;
        public ProductoVH(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreCard);
            txtCantidad = itemView.findViewById(R.id.txtCantidadCard);
            btnEliminar = itemView.findViewById(R.id.btnEliminarCard);
        }
    }
}
