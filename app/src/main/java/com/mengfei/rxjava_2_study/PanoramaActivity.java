package com.mengfei.rxjava_2_study;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;

public class PanoramaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panorama);
        VrPanoramaView vv = (VrPanoramaView) findViewById(R.id.mVrPanoramaView);
        VrPanoramaView.Options options = new VrPanoramaView.Options();
        options.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;
        // OOM
        vv.loadImageFromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.panorama), options);
    }
}
