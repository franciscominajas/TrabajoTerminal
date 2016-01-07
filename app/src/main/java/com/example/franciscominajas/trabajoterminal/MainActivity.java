package com.example.franciscominajas.trabajoterminal;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MainActivity extends Activity
{
    //inicializacion de variables
    private Camara prevCamara;
    private ImageView previsualizacionCam = null;
    private FrameLayout principalLayout;
    private int ancho = 640;
    private int alto = 480;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //colocamos la apk en full screen opcional
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //colocamos la apk sin titulo
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //creamos la inicializacion de la camara
        previsualizacionCam  = new ImageView(this);

        SurfaceView vistaCam = new SurfaceView(this);
        SurfaceHolder HolderCam = vistaCam.getHolder();
        prevCamara = new Camara(ancho, alto, previsualizacionCam);

        HolderCam.addCallback(prevCamara);
        HolderCam.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        principalLayout = (FrameLayout) findViewById(R.id.layout1);
        principalLayout.addView(vistaCam, new LayoutParams(ancho, alto));
        principalLayout.addView(previsualizacionCam, new LayoutParams(ancho, alto));
    }
    protected void onPause()
    {
        if(prevCamara!=null)
        {
            prevCamara.onPause();
        }
        super.onPause();
    }
}
