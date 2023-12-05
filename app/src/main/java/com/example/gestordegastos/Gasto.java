package com.example.gestordegastos;

public class Gasto {
    private String fecha;
    private String cantidad;
    private String descripcion;
    private int imagen;

    public Gasto() {
        // Constructor vacío requerido para Firebase
    }

    public Gasto(String fecha, String cantidad, String descripcion, int imagen) {
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }



    public String getFecha() {
        return fecha;
    }

    public String getCantidad() {
        return cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getImagen() {
        return imagen;
    }
}

