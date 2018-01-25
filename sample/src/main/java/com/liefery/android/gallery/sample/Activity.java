package com.liefery.android.gallery.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import com.liefery.android.gallery.GalleryView;

public class Activity extends FragmentActivity {
    public static final String TAG = GalleryView.class.getCanonicalName();

    private GalleryView galleryView;

    @Override
    public void onCreate( @Nullable Bundle state ) {
        super.onCreate( state );

        setContentView( R.layout.main );

        galleryView = (GalleryView) findViewById( R.id.gallery );
    }
}