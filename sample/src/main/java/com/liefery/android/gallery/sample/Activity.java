package com.liefery.android.gallery.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import com.liefery.android.gallery.Gallery;
import com.liefery.android.gallery.Gallery.OnTakePhotoListener;

import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class Activity extends android.app.Activity implements OnTakePhotoListener {
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
            ArrayList<Integer> list = new ArrayList<>( results.length );

            for ( int result : results ) {
                list.add( result );
            }

            if ( results.length == 0 || list.contains( PERMISSION_DENIED ) ) {
                Toast.makeText(
                    this,
                    "Accept all permissions to take a photo",
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
        ArrayList<String> permissions = new ArrayList<>();

        if ( ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA ) == PERMISSION_DENIED ) {
            permissions.add( Manifest.permission.CAMERA );
        }

        if ( ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PERMISSION_DENIED ) {
            permissions.add( Manifest.permission.WRITE_EXTERNAL_STORAGE );
        }

        if ( !permissions.isEmpty() ) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toArray( new String[permissions.size()] ),
                420 );

            return false;
        } else {
            return true;
        }
    }
}