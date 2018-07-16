package com.liefery.android.gallery;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

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

        ActionActivityResultHandler resultHandler = new ActionActivityResultHandler(
            data );

        int event = resultHandler.getEvent();

        switch ( event ) {
            case EVENT_SUCCESS:
                galleryView.addPhoto( resultHandler.getFile(), true );
            break;
            case EVENT_CANCEL:
            break;
            case EVENT_ERROR:
                OnPhotoErrorListener listener = galleryView
                                .getOnPhotoErrorListener();
                if ( listener != null )
                    listener.onPhotoError( resultHandler.getError() );
            break;
            case EVENT_DELETE:
                galleryView.removePhoto( resultHandler.getFile() );
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

    //    FROM PERMISSION AUXILLARY:

}
