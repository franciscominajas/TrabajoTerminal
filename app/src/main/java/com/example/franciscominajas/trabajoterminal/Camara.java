package com.example.franciscominajas.trabajoterminal;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by FRANCISCOMINAJAS on 03/01/2016.
 */
public class Camara implements SurfaceHolder.Callback, Camera.PreviewCallback
{
    //variables
    private Camera camara = null;
    private ImageView previzualizacionCamara = null;
    private Bitmap bitmap = null;
    Imagen image = null;
    //private int[] pixeles = null;
    //private byte[] informacionFrames = null;
    private int formatoImagen;
    private int ancho;
    private int alto;
    private boolean procesando = false;

    //inicializar el manipulador de procesos
    Handler handler = new Handler(Looper.getMainLooper());

    public Camara(int Ancho, int Alto, ImageView previsualizacion)
    {
        ancho = Ancho;
        alto = Alto;
        previzualizacionCamara = previsualizacion;
        //Bitmap.Config.ARGB_8888 Cada pixel almacena 4 bytes.
        bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888);
        //creamos un arreglo donde almacenaremos todos los pixeles.
        image.setPixeles(new int[alto*ancho]);
    }

    public void onPreviewFrame(byte[] arg0, Camera arg1)
    {
        //en modo de previsualizacion, cada frame sera colocado aqui
        //ImageFormat.NV21 YCrCb formato utilizado para las imágenes, que utiliza el formato de codificación NV21.
        if(formatoImagen == ImageFormat.NV21)
        {
            //solamente aceptamos el formato NV21(YUV420).
            if(!procesando)
            {
                image.setDatos(arg0);
                handler.post(DoImageProcessing);
            }
        }
    }

    public void onPause()
    {
        camara.stopPreview();
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
    {
        Parameters parametros;
        //inicializamos el tamaño de previsualizacion
        parametros=camara.getParameters();
        parametros.setPreviewSize(ancho, alto);
        formatoImagen=parametros.getPreviewFormat();
        camara.setParameters(parametros);
        camara.startPreview();
    }

    public void surfaceCreated(SurfaceHolder arg0)
    {
        camara = Camera.open();
        try
        {
            //si no colocamos el SurfaceHolder, la area de previsualizacion sera negra.
            camara.setPreviewDisplay(arg0);
            camara.setPreviewCallback(this);
        }
        catch(IOException e)
        {
            camara.release();
            camara = null;
        }
    }

    public void surfaceDestroyed(SurfaceHolder arg0)
    {
        camara.setPreviewCallback(null);
        camara.stopPreview();
        camara.release();
        camara = null;
    }

    public boolean procesamientoImagen(int Ancho, int Alto, Imagen imagene)
    {
        //zona de procesamiento
        return true;
    }

    //hilo
    public Runnable DoImageProcessing = new Runnable()
    {
        @Override
        public void run()
        {
            Log.i("Prototipo_1","Procesando");
            procesando=true;
            procesamientoImagen(ancho, alto, image);

            bitmap.setPixels(image.getPixeles(), 0, ancho, 0, 0, ancho, alto);

            previzualizacionCamara.setImageBitmap(bitmap);
            procesando = true;
        }
    };

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
