package com.marcosledesma.edu_listacompraormlite.modelos;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// MAPEO para ORM Lite
@DatabaseTable(tableName = "productos")
public class Producto {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private String nombre;
    @DatabaseField(canBeNull = false)
    private int cantidad;
    @DatabaseField(canBeNull = false)
    private float precio;
    @DatabaseField(columnName = "importe_total", canBeNull = false)
    private float importeTotal;

    // Vacío para la BD
    public Producto() {
    }

    // Sin id ni importe total como parámetro
    public Producto(String nombre, int cantidad, float precio) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.importeTotal = cantidad * precio;
    }

    // Cálculo automático al modificar cantidad o precio del producto
    public void recalculaImporteTotal(){
        this.importeTotal = cantidad + importeTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public float getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(float importeTotal) {
        this.importeTotal = importeTotal;
    }
}
