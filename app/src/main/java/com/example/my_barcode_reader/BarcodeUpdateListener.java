package com.example.my_barcode_reader;

import android.support.annotation.UiThread;

import com.google.android.gms.vision.barcode.Barcode;

public interface BarcodeUpdateListener {

    @UiThread
    public void onBarcodeDetecdet(Barcode barcode);
}
