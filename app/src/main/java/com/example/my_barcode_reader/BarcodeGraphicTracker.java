package com.example.my_barcode_reader;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

public class BarcodeGraphicTracker extends Tracker<Barcode> {

    private BarcodeUpdateListener barcodeUpdateListener;
    private GraphicOverlay<BarcodeGraphic> graphicOverlay;
    private BarcodeGraphic barcodeGraphic;


    public BarcodeGraphicTracker(Context context, GraphicOverlay<BarcodeGraphic> graphicOverlay,BarcodeGraphic barcodeGraphic) {
        this.graphicOverlay = graphicOverlay;
        this.barcodeGraphic = barcodeGraphic;
        if(context instanceof BarcodeUpdateListener) {
             this.barcodeUpdateListener = (BarcodeUpdateListener) context;
        }
        else {
            throw new RuntimeException("Hosting activity must implement BarcodeUpdateListener");
        }
    }

    @Override
    public void onNewItem(int i, Barcode barcode) {
        super.onNewItem(i, barcode);
        barcodeGraphic.setmId(i);
        barcodeUpdateListener.onBarcodeDetecdet(barcode);

    }

    @Override
    public void onUpdate(Detector.Detections<Barcode> detections, Barcode barcode) {
        super.onUpdate(detections, barcode);
       graphicOverlay.add(barcodeGraphic);
        barcodeGraphic.updateItem(barcode);

    }

    @Override
    public void onMissing(Detector.Detections<Barcode> detections) {
        super.onMissing(detections);
       graphicOverlay.remove(barcodeGraphic);
    }

    @Override
    public void onDone() {
        super.onDone();
        graphicOverlay.remove(barcodeGraphic);
    }
}
