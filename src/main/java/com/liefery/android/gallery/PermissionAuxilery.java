package com.liefery.android.gallery;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PermissionAuxilery extends Fragment {
    public static final String TAG = PermissionAuxilery.class
                    .getCanonicalName();

    private GalleryView galleryView;

    public static PermissionAuxilery newInstance( GalleryView galleryView ) {
        PermissionAuxilery auxilery = new PermissionAuxilery();
        auxilery.setGalleryView( galleryView );
        return auxilery;
    }

    public void setGalleryView( GalleryView galleryView ) {
        this.galleryView = galleryView;
    }

    @Override
    public void onCreate( @Nullable Bundle state ) {
        super.onCreate( state );

        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(
        int request,
        @NonNull String[] permissions,
        @NonNull int[] results ) {
        super.onRequestPermissionsResult( request, permissions, results );

        if ( request != 420 ) {
            Log.w( TAG, "Unexpected request code " + request );
            return;
        }

        if ( results.length != 1 ) {
            Log.w( TAG, "Unexpected permission results" );
            return;
        }

        if ( results[0] == PERMISSION_DENIED ) {
            Toast.makeText(
                getActivity(),
                "Accept permission to take a photo",
                Toast.LENGTH_LONG ).show();
        } else {
            galleryView.takePhoto();
        }

        destroy();
    }

    static boolean hasPermissions( Context context ) {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PERMISSION_GRANTED;
    }

    private void checkPermissions() {
        if ( !hasPermissions( getActivity() ) ) {
            requestPermissions(
                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                420 );
        } else {
            galleryView.takePhoto();
            destroy();
        }
    }

    private void destroy() {
        getActivity().getSupportFragmentManager().beginTransaction()
                        .remove( this ).commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        galleryView = null;
    }
}
