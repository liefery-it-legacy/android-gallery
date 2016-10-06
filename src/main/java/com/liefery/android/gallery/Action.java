package com.liefery.android.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImage.ImageSource;

import java.io.File;

public class Action extends Activity implements EasyImage.Callbacks {
    public static final String ACTION_IMAGE_CAPTURE = Action.class
                    .getCanonicalName() + ".image_capture";

    public static final int EVENT_SUCCESS = 0;

    public static final int EVENT_CANCEL = 1;

    public static final int EVENT_ERROR = 2;

    @Override
    protected void onCreate( @Nullable Bundle state ) {
        super.onCreate( state );

        if ( state == null ) {
            EasyImage.openCamera( this, 0 );
        }
    }

    @Override
    protected void onActivityResult( int request, int result, Intent data ) {
        super.onActivityResult( request, result, data );

        EasyImage.handleActivityResult( request, result, data, this, this );
    }

    @Override
    public void onImagePicked( File file, ImageSource source, int type ) {
        Intent intent = createIntent( EVENT_SUCCESS ).putExtra(
            "file",
            file.getAbsolutePath() );
        sendBroadcast( intent );
        finish();
    }

    @Override
    public void onImagePickerError(
        Exception exception,
        ImageSource source,
        int type ) {
        sendBroadcast( createIntent( EVENT_ERROR ) );
        finish();
    }

    @Override
    public void onCanceled( ImageSource source, int type ) {
        sendBroadcast( createIntent( EVENT_CANCEL ) );
        finish();
    }

    private Intent createIntent( int event ) {
        return new Intent().setAction( ACTION_IMAGE_CAPTURE ).putExtra(
            "event",
            event );
    }
}