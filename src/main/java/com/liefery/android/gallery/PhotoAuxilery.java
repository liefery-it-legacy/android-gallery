package com.liefery.android.gallery;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;

import static com.liefery.android.gallery.GalleryView.*;

public class PhotoAuxilery extends Fragment {
    public static final String TAG = PhotoAuxilery.class.getCanonicalName();

    private GalleryView galleryView;

    public static PhotoAuxilery newInstance( GalleryView galleryView ) {
        PhotoAuxilery auxilery = new PhotoAuxilery();
        auxilery.setGalleryView( galleryView );
        return auxilery;
    }

    public void setGalleryView( GalleryView galleryView ) {
        this.galleryView = galleryView;
    }

    @Override
    public void onActivityResult( int request, int result, Intent data ) {
        super.onActivityResult( request, result, data );

        if ( request != 421 ) {
            Log.w( TAG, "Unexpected request code " + request );
            return;
        }

        if ( result != Activity.RESULT_OK ) {
            return;
        }

        if ( data == null ) {
            Log.w( TAG, "Result data is null" );
            return;
        }

        int event = data.getIntExtra( "event", -1 );

        String path;
        File file;

        switch ( event ) {
            case EVENT_SUCCESS:
                path = data.getStringExtra( "file" );
                file = new File( path );
                galleryView.addPhoto( file, true );
            break;
            case EVENT_CANCEL:
            break;
            case EVENT_ERROR:
            break;
            case EVENT_DELETE:
                path = data.getStringExtra( "file" );
                file = new File( path );
                galleryView.removePhoto( file );
            break;
            default:
                Log.w( TAG, "Received unknown event code " + event );
            break;
        }

        destroy();
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
