package com.liefery.android.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImage.ImageSource;

import java.io.*;

import static android.media.ExifInterface.ORIENTATION_ROTATE_180;
import static android.media.ExifInterface.ORIENTATION_ROTATE_270;
import static android.media.ExifInterface.ORIENTATION_ROTATE_90;
import static com.liefery.android.gallery.Gallery.*;

public class Action extends Activity implements EasyImage.Callbacks {
    public static final String TAG = Action.class.getCanonicalName();

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
    public void onImagePicked( File file, ImageSource source, int type ) {
        // Samsung treatment <3
        // https://github.com/jkwiecien/EasyImage/issues/43
        if ( source == ImageSource.CAMERA ) {
            rotateImageIfNecessary( file );
        }

        Intent intent = createIntent( EVENT_SUCCESS ).putExtra(
            "file",
            file.getAbsolutePath() );
        setResult( RESULT_OK, intent );
        finish();
    }

    private void rotateImageIfNecessary( File file ) {
        FileInputStream input = null;
        FileOutputStream output = null;
        int orientation;
        int rotation = 0;

        try {
            input = new FileInputStream( file );
            ImageHeaderParser parser = new ImageHeaderParser( input );
            orientation = parser.getOrientation();
        } catch ( FileNotFoundException exception ) {
            Log.w( TAG, "Tried to open image file, but was not found" );
            return;
        } catch ( IOException e ) {
            Log.w( TAG, "Failed to parse EXIF data from image file" );
            return;
        } finally {
            if ( input != null ) {
                try {
                    input.close();
                } catch ( IOException e ) {
                }
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
            Bitmap source = BitmapFactory.decodeFile( file.getPath() );

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
            } catch ( FileNotFoundException exception ) {
                Log.w( TAG, "Tried to open image file, but was not found" );
                return;
            } finally {
                result.recycle();

                if ( output != null ) {
                    try {
                        output.close();
                    } catch ( IOException e ) {
                    }
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