package com.liefery.android.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImage.ImageSource;

import java.io.*;
import java.util.List;

import static android.media.ExifInterface.ORIENTATION_ROTATE_180;
import static android.media.ExifInterface.ORIENTATION_ROTATE_270;
import static android.media.ExifInterface.ORIENTATION_ROTATE_90;
import static com.liefery.android.gallery.GalleryView.*;

public class ActionActivity extends Activity implements EasyImage.Callbacks {
    public static final String TAG = ActionActivity.class.getCanonicalName();

    private static final BitmapFactory.Options options = new BitmapFactory.Options();

    static {
        options.inSampleSize = 2;
    }

    @Override
    public void onCreate( @Nullable Bundle state ) {
        super.onCreate( state );

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
    public void onImagesPicked(
        @NonNull List<File> files,
        ImageSource source,
        int type ) {

        if ( files.size() > 0 ) {

            File file = files.get( 0 );

            IOException rotationException = null;
            Intent intent;

            // Samsung treatment <3
            //  - Images are not rotated properly
            //    https://github.com/jkwiecien/EasyImage/issues/43
            //  - Rotating a full resolution image may cause out of memory exceptions
            if ( source == ImageSource.CAMERA ) {
                try {
                    rotateImageIfNecessary( file );
                } catch ( IOException exception ) {
                    rotationException = exception;
                }
            }

            if ( rotationException != null ) {
                intent = createIntent( EVENT_ERROR ).putExtra(
                    "error",
                    (Serializable) rotationException );
            } else {
                intent = createIntent( EVENT_SUCCESS ).putExtra(
                    "file",
                    file.getAbsolutePath() );
            }

            setResult( RESULT_OK, intent );

        } else {
            setResult( RESULT_CANCELED );
        }

        finish();
    }

    private void rotateImageIfNecessary( File file ) throws IOException {
        FileInputStream input = null;
        FileOutputStream output = null;
        int orientation;
        int rotation = 0;

        try {
            input = new FileInputStream( file );
            ImageHeaderParser parser = new ImageHeaderParser( input );
            orientation = parser.getOrientation();
        } finally {
            if ( input != null ) {
                input.close();
            }
        }

        switch ( orientation ) {
            case ORIENTATION_ROTATE_90:
                rotation = 90;
            break;
            case ORIENTATION_ROTATE_180:
                rotation = 180;
            break;
            case ORIENTATION_ROTATE_270:
                rotation = 270;
            break;
        }

        if ( rotation != 0 ) {
            Bitmap source = BitmapFactory.decodeFile( file.getPath(), options );

            if ( source == null ) {
                Log.w(
                    TAG,
                    "Tried to rotate image file, but failed to decode to Bitmap" );
                return;
            }

            Matrix matrix = new Matrix();
            matrix.postRotate( rotation );

            Bitmap result = Bitmap.createBitmap(
                source,
                0,
                0,
                source.getWidth(),
                source.getHeight(),
                matrix,
                true );

            source.recycle();

            try {
                output = new FileOutputStream( file );
                result.compress( Bitmap.CompressFormat.JPEG, 100, output );
            } finally {
                result.recycle();

                if ( output != null ) {
                    output.close();
                }
            }
        }
    }

    @Override
    public void onImagePickerError(
        Exception exception,
        ImageSource source,
        int type ) {
        Intent intent = createIntent( EVENT_ERROR );
        intent.putExtra( "error", (Serializable) exception );
        setResult( RESULT_OK, intent );
        finish();
    }

    @Override
    public void onCanceled( ImageSource source, int type ) {
        Intent intent = createIntent( EVENT_CANCEL );
        setResult( RESULT_OK, intent );
        finish();
    }
}