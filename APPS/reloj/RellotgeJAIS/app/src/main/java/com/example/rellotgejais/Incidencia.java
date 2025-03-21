package com.example.rellotgejais;

import android.widget.ImageView;

public class Incidencia {
    private int imagenResId;
    private int jerseyNumber;
    private String descripcion;
    private int mins;
    private int seconds;

    // Constructor
    public Incidencia(int imagenResId, int jerseyNumber, String descripcion, int minutos, int segundos) {
        this.imagenResId = imagenResId;
        this.jerseyNumber = jerseyNumber;
        this.descripcion = descripcion;
        this.mins = minutos;
        this.seconds = segundos;
    }
    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getMins() {
        return mins;
    }

    public void setMins(int mins) {
        this.mins = mins;
    }

    public int getImagenResId() {
        return imagenResId;
    }

    public void setImagenResId(int imagenResId) {
        this.imagenResId = imagenResId;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(int jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }






}
