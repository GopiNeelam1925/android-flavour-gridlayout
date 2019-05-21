package com.trien.dnflavor;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;

/* custom Application class that helps initialize fresco library*/
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // this is important as it helps load large images (downsizing) to avoid memory running out
        // only enable it if handling large images in the app
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build();

        // initialise fresco
        Fresco.initialize(this, config);
    }
}