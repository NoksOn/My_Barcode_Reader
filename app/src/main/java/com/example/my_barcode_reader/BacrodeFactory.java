package com.example.my_barcode_reader;

import android.content.Context;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

public class BacrodeFactory implements MultiProcessor.Factory<Barcode> {
    private GraphicOverlay<BarcodeGraphic> graphicOverlay;
    private Context mContext;

    public BacrodeFactory(GraphicOverlay<BarcodeGraphic> graphicOverlay,
                                 Context mContext) {
        this.graphicOverlay = graphicOverlay;
        this.mContext = mContext;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(graphicOverlay);
        return new BarcodeGraphicTracker(mContext,graphicOverlay,graphic);
    }
}
