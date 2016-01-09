package com.example.franciscominajas.trabajoterminal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.NClob;
import java.util.Map;

/**
 * Created by FRANCISCOMINAJAS on 03/01/2016.
 */
public class Camara implements SurfaceHolder.Callback, Camera.PreviewCallback
{
    //variables
    private Camera camara = null;
    private ImageView previzualizacionCamara = null;
    private Bitmap bitmap = null;
    private int[] pixeles = null;
    private byte[] informacionFrames = null;
    private int formatoImagen;
    private int ancho;
    private int alto;
    private boolean procesando = false;
    //variables de opencv
    Mat mRgba = null;

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
        pixeles = new int[alto*ancho];
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
                informacionFrames=arg0;
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

    public boolean procesamientoImagen(int Ancho, int Alto, byte[] NV21FrameData, int[] pixels)
    {
        return true;
    }

    //hilo
    public Runnable DoImageProcessing = new Runnable()
    {
        @Override
        public void run()
        {
            //Toast.makeText(null, "Procesando", Toast.LENGTH_LONG).show();
            procesando=true;
            //procesamientoImagen(ancho, alto, informacionFrames, pixeles);

            //bitmap=convertirAGrises(bitmap);
            //bitmap=negroBlanco(bitmap);
            bitmap.setPixels(pixeles, 0, ancho, 0, 0, ancho, alto);

            previzualizacionCamara.setImageBitmap(bitmap);
            procesando = false;
        }
    };

    public Bitmap convertirAGrises(Bitmap bitmap)
    {
        //zona de procesamiento de la imagen
        mRgba = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mRgba);
        Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(mRgba, bitmap);
        //fin de la zona de procesamiento de la imagen
        //Toast.makeText(getApplicationContext(), "COLORES IGUAlES: "+rojo+" "+verde+" "+azul, Toast.LENGTH_LONG).show();
        return bitmap;
    }

    public Bitmap negroBlanco(Bitmap bitmap)
    {
        //zona de procesamiento de la imagen
        mRgba = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mRgba);
        Mat C = mRgba.clone();
        Size sizeA = mRgba.size();
        double suma=0, promedio=0;
        for (int i = 0; i < sizeA.height; i++)
            for (int j = 0; j < sizeA.width; j++) {
                double[] data = mRgba.get(i, j);
                data[0] = data[0];
                data[1] = data[1];
                data[2] = data[2];
                suma+=data[2];
                C.put(i, j, data);
            }
        promedio=suma/(sizeA.height*sizeA.width);
        //Toast.makeText(getApplicationContext(), "COLORES IGUAlES: "+promedio, Toast.LENGTH_LONG).show();
        for (int i = 0; i < sizeA.height; i++)
            for (int j = 0; j < sizeA.width; j++) {
                double[] data = mRgba.get(i, j);
                if(data[0]<promedio)
                {
                    data[0] = 0;
                    data[1] = 0;
                    data[2] = 0;
                }
                else
                {
                    data[0] = 255;
                    data[1] = 255;
                    data[2] = 255;
                }
                C.put(i, j, data);
            }
        Utils.matToBitmap(C, bitmap);
        return bitmap;
    }

    public Bitmap convertirBlur(Bitmap bitmap)
    {
        //zona de procesamiento de la imagen
        mRgba = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mRgba);
        Imgproc.medianBlur(mRgba, mRgba, 51);
        Utils.matToBitmap(mRgba, bitmap);
        //fin de la zona de procesamiento de la imagen
        return bitmap;
    }
}
