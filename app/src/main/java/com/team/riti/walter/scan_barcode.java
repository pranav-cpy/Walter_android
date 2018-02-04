package com.team.riti.walter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Created by pranav on 16/03/2017 AD.
 */

public class scan_barcode extends Activity {
    SurfaceView cameraprev;

    @Override
    protected void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        setContentView(R.layout.activity_scan_barcode);
        cameraprev = (SurfaceView) findViewById(R.id.camera_preview);
        createcamerasource();
    }

    private void createcamerasource() {
        BarcodeDetector barcodedetector = new BarcodeDetector.Builder(this).build();
        final CameraSource camera = new CameraSource.Builder(this, barcodedetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .build();
        cameraprev.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(scan_barcode.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                try {

                     camera.start(cameraprev.getHolder());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                 camera.stop();
            }
        });
        barcodedetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
    final SparseArray<Barcode> barcodes=detections.getDetectedItems();
                if(barcodes.size()>0)
                {
                    Intent intent=new Intent();
                    intent.putExtra("barcode",barcodes.valueAt(0));
                    setResult(CommonStatusCodes.SUCCESS,intent);
                    finish();
                }
            }
        });
    }


}
