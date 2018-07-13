package com.liefery.android.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.liefery.android.gallery.GalleryView.*;

public class GalleryFragment extends Fragment {
    private OnPhotoAddedListener onPhotoAddedListener;

    private OnPhotoRemovedListener onPhotoRemovedListener;

    private OnPhotoErrorListener onPhotoErrorListener;

    public void setOnPhotoAddedListener(
        OnPhotoAddedListener onPhotoAddedListener ) {
        this.onPhotoAddedListener = onPhotoAddedListener;
    }

    public void setOnPhotoRemovedListener(
        OnPhotoRemovedListener onPhotoRemovedListener ) {
        this.onPhotoRemovedListener = onPhotoRemovedListener;
    }

    public void setOnPhotoErrorListener(
        OnPhotoErrorListener onPhotoErrorListener ) {
        this.onPhotoErrorListener = onPhotoErrorListener;
    }

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
                checkTakePhotoPermissions();
            }
        } );
        gallery.setOnClickThumbnailListener( new OnClickListener() {
            @Override
            public void onClick( View v ) {
                File file = (File) v.getTag();
                if ( file != null )
                    openDetails( file );
            }
        } );
    }

    @Nullable
    @Override
    public GalleryView getView() {
        return (GalleryView) super.getView();
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
        File photo = resultHandler.getFile();

        switch ( event ) {
            case EVENT_SUCCESS:
                getView().addPhoto( photo, true );

                if ( onPhotoAddedListener != null )
                    onPhotoAddedListener.onPhotoAdded( photo );
            break;
            case EVENT_CANCEL:
            break;
            case EVENT_ERROR:
                if ( onPhotoErrorListener != null )
                    onPhotoErrorListener
                                    .onPhotoError( resultHandler.getError() );
            break;
            case EVENT_DELETE:
                getView().removePhoto( photo );

                if ( onPhotoRemovedListener != null )
                    onPhotoRemovedListener.onPhotoRemoved( photo );
            break;
            default:
                Log.w( TAG, "Received unknown event code " + event );
            break;
        }
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

        if ( results.length != 2 ) {
            Log.w( TAG, "Unexpected permission results" );
            return;
        }

        if ( results[0] == PERMISSION_DENIED || results[1] == PERMISSION_DENIED ) {
            Toast.makeText(
                getActivity(),
                "Accept permission to take a photo",
                Toast.LENGTH_LONG ).show();
        } else {
            takePhoto();
        }
    }

    boolean hasTakePhotoPermissions() {
        return ContextCompat.checkSelfPermission(
            getContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.CAMERA ) == PERMISSION_GRANTED;
    }

    private void checkTakePhotoPermissions() {
        if ( !hasTakePhotoPermissions() ) {
            requestPermissions( new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA }, 420 );
        } else {
            takePhoto();
        }
    }

    private void takePhoto() {
        Intent intent = new Intent( getContext(), ActionActivity.class );
        startActivityForResult( intent, 421 );
    }

    private void openDetails( File file ) {
        Intent intent = DetailActivity.newInstance( getContext(), file );
        startActivityForResult( intent, 421 );
    }

    public interface OnPhotoAddedListener {
        void onPhotoAdded( File photo );
    }

    public interface OnPhotoRemovedListener {
        void onPhotoRemoved( File photo );
    }

    public interface OnPhotoErrorListener {
        void onPhotoError( Throwable throwable );
    }
}
