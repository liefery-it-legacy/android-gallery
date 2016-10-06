package com.liefery.android.gallery.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class Activity extends android.app.Activity {
    @Override
    protected void onCreate( @Nullable Bundle state ) {
        super.onCreate( state );

        setContentView( R.layout.main );
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(
        int request,
        @NonNull String[] permissions,
        @NonNull int[] results ) {
        super.onRequestPermissionsResult( request, permissions, results );

        if ( request == 420 ) {
            List<int[]> list = Arrays.asList( results );

            if ( results.length == 0 || list.contains( PERMISSION_DENIED ) ) {
                checkPermissions();
            }
        }
    }

    private void checkPermissions() {
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
        }
    }
}