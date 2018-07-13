package com.liefery.android.gallery;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import static com.liefery.android.gallery.GalleryFragment.ACTION;

public class Util {
    public static int dpToPx( int dp ) {
        return (int) ( dp * Resources.getSystem().getDisplayMetrics().density );
    }

    @NonNull
    public static Intent createIntent( int event ) {
        return new Intent().setAction( ACTION ).putExtra( "event", event );
    }
}
