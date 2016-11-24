package com.liefery.android.gallery.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import com.liefery.android.gallery.Gallery;
import com.liefery.android.gallery.Gallery.OnTakePhotoListener;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class Activity extends android.app.Activity implements OnTakePhotoListener {
    public static final String TAG = Gallery.class.getCanonicalName();

    private Gallery gallery;

    @Override
    public void onCreate( @Nullable Bundle state ) {
        super.onCreate( state );

        setContentView( R.layout.main );

        gallery = (Gallery) findViewById( R.id.gallery );
        gallery.setOnTakePhotoListener( this );
    }

    @Override
    public void onRequestPermissionsResult(
        int request,
        @NonNull String[] permissions,
        @NonNull int[] results ) {
        super.onRequestPermissionsResult( request, permissions, results );

        if ( request == 420 ) {
            if ( results.length != 1 ) {
                Log.w( TAG, "Unexpected permission results" );
                return;
            }

            if ( results[0] == PERMISSION_DENIED ) {
                Toast.makeText(
                    this,
                    "Accept permission to take a photo",
                    Toast.LENGTH_LONG ).show();
            } else {
                gallery.takePhoto();
            }
        }
    }

    @Override
    public boolean onTakePhoto() {
        return checkPermissions();
    }

    private boolean checkPermissions() {
        if ( ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PERMISSION_DENIED ) {
            ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                420 );

            return false;
        } else {
            return true;
        }
    }
}