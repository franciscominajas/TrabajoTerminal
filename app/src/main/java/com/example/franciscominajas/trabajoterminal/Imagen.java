package com.example.franciscominajas.trabajoterminal;

/**
 * Created by FRANCISCOMINAJAS on 06/01/2016.
 */
public class Imagen
{
    private byte[] Data;
    private int[] pixeles;

    public void setPixeles(int pixel [])
    {
        this.pixeles=pixel;
    }

    public void setDatos(byte [] data)
    {
        this.Data=data;
    }

    public int[] getPixeles()
    {
        return pixeles;
    }

    public byte[] getDatos()
    {
        return Data;
    }
}
