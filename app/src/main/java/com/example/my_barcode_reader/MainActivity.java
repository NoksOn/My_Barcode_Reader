package com.example.my_barcode_reader;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class MainActivity extends AppCompatActivity implements BarcodeUpdateListener, View.OnTouchListener {

    private CameraSource cameraSource;
    private CameraPreview cameraPreview;
    private final int RequestCameraPermissionID = 1001;
    private BarcodeDetector barcodeDetector;
    private Barcode mbarcode;
    private GestureDetector mDetector;
    private GraphicOverlay graphicOverlay;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cameraPreview = findViewById(R.id.mSurfaceView);
        graphicOverlay = findViewById(R.id.GraphicOverlay);

        mDetector = new GestureDetector(this,new mGestureListener());

        int rc = ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA);
        if(rc == PackageManager.PERMISSION_GRANTED){
            CreateCameraSource();
        }
        else {
             CheckPermission();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cameraSource!=null){
            cameraPreview.realease();
        }
    }

    public void StartCameraSource() throws SecurityException{

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(code != ConnectionResult.SUCCESS) {
         Dialog dlg =
                 GoogleApiAvailability.getInstance().getErrorDialog(this, code, RequestCameraPermissionID);
         dlg.show();
       }
      if(cameraSource!=null){
          try{

              Log.d("Camera","Start");
              cameraPreview.start(cameraSource,graphicOverlay);
           }
          catch (Exception e){
               cameraPreview.realease();
               cameraSource = null;
          }
      }
    }

    public void CreateCameraSource(){
        BacrodeFactory bacrodeFactory = new BacrodeFactory(graphicOverlay,this);
         barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
         barcodeDetector.setProcessor(new MultiProcessor.Builder<>(bacrodeFactory).build());

         cameraSource = new CameraSource.Builder(MainActivity.this,barcodeDetector)
         .setFacing(CameraSource.CAMERA_FACING_BACK)
         .setRequestedPreviewSize(1600,1024)
         .setRequestedFps(15.0f)
         .setAutoFocusEnabled(true)
         .build();

    }

    public void CheckPermission(){
        Log.w("Camera", "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RequestCameraPermissionID);
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StartCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraSource!=null){
            cameraPreview.stop();
        }
    }


    @Override
    public void onBarcodeDetecdet(Barcode barcode) {
         Log.d("Camera","Barcode is Detected");
        graphicOverlay.setOnTouchListener(this);
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
         this.mbarcode = barcode;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int[] location = new int[2];
        graphicOverlay.getLocationOnScreen(location);
        float x = (event.getX() - location[0]) / graphicOverlay.getWidthScaleFactor();
        float y = (event.getY() - location[1]) / graphicOverlay.getHeightScaleFactor();


        if (mbarcode.getBoundingBox().contains((int) x, (int) y)) {
            Log.d("Camera","Ok");
            return mDetector.onTouchEvent(event);
        }
        else {
            Log.d("Camera","missed");
            return mDetector.onTouchEvent(event);
        }

    }


    private class mGestureListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }
    }
}
