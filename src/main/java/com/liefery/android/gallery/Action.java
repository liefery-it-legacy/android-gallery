package com.liefery.android.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImage.ImageSource;

import java.io.File;

import static com.liefery.android.gallery.Gallery.*;

public class Action extends Activity implements EasyImage.Callbacks {
    private String action;

    @Override
    public void onCreate( @Nullable Bundle state ) {
        super.onCreate( state );

        action = getIntent().getStringExtra( "action" );

        if ( state == null ) {
            EasyImage.openCamera( this, 0 );
        }
    }

    @Override
    public void onActivityResult( int request, int result, Intent data ) {
        super.onActivityResult( request, result, data );

        EasyImage.handleActivityResult( request, result, data, this, this );
    }

    @Override
    public void onImagePicked( File file, ImageSource source, int type ) {
        Intent intent = createIntent( action, EVENT_SUCCESS ).putExtra(
            "file",
            file.getAbsolutePath() );
        // sendBroadcast( intent );
        setResult( RESULT_OK, intent );
        finish();
    }

    @Override
    public void onImagePickerError(
        Exception exception,
        ImageSource source,
        int type ) {
        // endBroadcast( createIntent( action, EVENT_ERROR ) );
        Intent intent = createIntent( action, EVENT_ERROR );
        setResult( RESULT_OK, intent );
        finish();
    }

    @Override
    public void onCanceled( ImageSource source, int type ) {
        // sendBroadcast( createIntent( action, EVENT_CANCEL ) );
        Intent intent = createIntent( action, EVENT_CANCEL );
        setResult( RESULT_OK, intent );
        finish();
    }
}