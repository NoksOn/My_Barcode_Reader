package com.example.my_barcode_reader;

import android.Manifest;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Camera;
import android.graphics.Point;
import android.support.annotation.RequiresPermission;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.gms.common.images.Size;
import java.io.IOException;
import com.google.android.gms.vision.CameraSource;

public class CameraPreview extends ViewGroup {
    private String TAG = "CameraPreview";
    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private Context mContext;
    private boolean CameraReady;
    private boolean SurfaceReady;
    private GraphicOverlay mOverlay;

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        CameraReady = false;
        SurfaceReady = false;

        surfaceView = new SurfaceView(context);
        surfaceView.getHolder().addCallback(new SurfaceCallback());
        Log.d("Camera","Start Preview Constructor");
        addView(surfaceView);
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(CameraSource cameraSource) throws IOException, SecurityException {
      if(cameraSource != null){
         this.cameraSource = cameraSource;
         CameraReady = true;
          Log.d("Camera","Start Camera Preview");
         StartIfReady();
      }
      else {
          stop();
      }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(CameraSource cameraSource,GraphicOverlay graphicOverlay) throws IOException, SecurityException {
      this.mOverlay = graphicOverlay;
      start(cameraSource);
    }

    public void stop(){
       if(cameraSource != null){
           cameraSource.stop();
       }
    }

    public void realease(){
        if(cameraSource != null){
            cameraSource.release();
            cameraSource = null;
        }
    }
    @RequiresPermission(Manifest.permission.CAMERA)
    public void StartIfReady()throws IOException, SecurityException{
        Log.d("Camera","StartIfReady CameraIsReady "+ CameraReady+" SurfaceIsReady "+SurfaceReady);
      if(CameraReady&&SurfaceReady){
          Log.d("Camera","Start CameraPreview");
        this.cameraSource.start(surfaceView.getHolder());
          if (mOverlay != null) {
              Size size = cameraSource.getPreviewSize();
              int min = Math.min(size.getWidth(), size.getHeight());
              int max = Math.max(size.getWidth(), size.getHeight());
              if (isPortraitMode()) {
                  // Swap width and height sizes when in portrait, since it will be rotated by
                  // 90 degrees
                  mOverlay.setCameraInfo(min, max, cameraSource.getCameraFacing());
              } else {
                  mOverlay.setCameraInfo(max, min, cameraSource.getCameraFacing());
              }
              mOverlay.clear();
          }
        CameraReady = false;
      }
    }

private class SurfaceCallback implements SurfaceHolder.Callback{

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        SurfaceReady = true;
        try {
            Log.d("Camera","Surfeced Created");
            StartIfReady();
        } catch (SecurityException se) {
            Log.e(TAG,"Do not have permission to start the camera", se);
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        SurfaceReady = false;
        Log.d("Camera","Surface Destroyed");
    }
}

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //TODO after you can get errors and bugs but it s will after)
        final int layoutWidth = r - l;
        final int layoutHeight = b - t;


        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).layout(0, 0, layoutWidth, layoutHeight);
        }

        try {
            StartIfReady();
        } catch (SecurityException se) {
            Log.e(TAG,"Do not have permission to start the camera", se);
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    private boolean isPortraitMode() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }

}
