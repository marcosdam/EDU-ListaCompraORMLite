package com.marcosledesma.edu_listacompraormlite.configuracion;

import java.text.NumberFormat;

public class Params {

    public static final String DATABASE_NAME = "lista_compras.bd";
    public static final int DATABASE_VERSION = 2;
    public static final NumberFormat nf;

    static {
        nf = NumberFormat.getCurrencyInstance();
    }
}
