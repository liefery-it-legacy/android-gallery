package com.liefery.android.gallery;

import android.content.Intent;
import android.support.annotation.Nullable;
import java.io.File;

import static com.liefery.android.gallery.GalleryView.EVENT_CANCEL;
import static com.liefery.android.gallery.GalleryView.EVENT_ERROR;
import static com.liefery.android.gallery.GalleryView.EVENT_SUCCESS;

public class ActionActivityResultHandler {

    final private Intent data;

    public ActionActivityResultHandler( @Nullable Intent data ) {
        this.data = data;
    }

    public int getEvent() {
        return data.getIntExtra( "event", -1 );
    }

    @Nullable
    public Throwable getError() {
        return (Throwable) data.getSerializableExtra( "error" );
    }

    @Nullable
    public File getFile() {
        String path = data.getStringExtra( "file" );

        return path == null ? null : new File( path );
    }

    public boolean isSuccess() {
        return ( getEvent() == EVENT_SUCCESS );
    }

    public boolean isCanceled() {
        return ( getEvent() == EVENT_CANCEL );
    }

    public boolean isError() {
        return ( getEvent() == EVENT_ERROR );
    }

}
