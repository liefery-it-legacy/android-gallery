package com.liefery.android.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import static com.liefery.android.gallery.GalleryView.*;

public class GalleryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState ) {
        return new GalleryView( getContext() );
    }

    @Override
    public void onViewCreated(
        @NonNull View view,
        @Nullable Bundle savedInstanceState ) {
        GalleryView gallery = (GalleryView) view;
        gallery.setOnClickTakePhotoListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                takePhoto();
            }
        } );
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
                ( (GalleryView) getView() ).addPhoto(
                    resultHandler.getFile(),
                    true );
            //                galleryView.addPhoto( resultHandler.getFile(), true );
            break;
            case EVENT_CANCEL:
            break;
            case EVENT_ERROR:
            //                OnPhotoErrorListener listener = galleryView
            //                                .getOnPhotoErrorListener();
            //                if ( listener != null )
            //                    listener.onPhotoError( resultHandler.getError() );
            break;
            case EVENT_DELETE:
            //                galleryView.removePhoto( resultHandler.getFile() );
            break;
            default:
                Log.w( TAG, "Received unknown event code " + event );
            break;
        }
    }

    private void takePhoto() {
        Intent intent = new Intent( getContext(), ActionActivity.class );
        startActivityForResult( intent, 421 );
    }
}
