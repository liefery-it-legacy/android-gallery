package com.liefery.android.gallery;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

class ThumbnailView extends FrameLayout {
    public ThumbnailView( Context context ) {
        super( context );
        initialize();
    }

    public ThumbnailView( Context context, AttributeSet attrs ) {
        super( context, attrs );
        initialize();
    }

    public ThumbnailView( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        initialize();
    }

    @TargetApi( 21 )
    public ThumbnailView(
        Context context,
        AttributeSet attrs,
        int defStyleAttr,
        int defStyleRes ) {
        super( context, attrs, defStyleAttr, defStyleRes );
        initialize();
    }

    private ImageView image = new ImageView( getContext() );

    private void initialize() {
        // Summon ripple / highlight effect and apply it to the foreground
        int[] array = new int[] { R.attr.selectableItemBackground };
        TypedArray attributes = getContext().obtainStyledAttributes( array );
        Drawable highlight = attributes.getDrawable( 0 );
        setForeground( highlight );
        attributes.recycle();

        addView( image, MATCH_PARENT, MATCH_PARENT );
    }

    public void load( @NonNull final File file ) {
        setTag( file );

        ViewGroup.LayoutParams params = getLayoutParams();

        setClickable( true );

        Glide
                        .with( getContext() )
                        .load( file )
                        .apply(
                            RequestOptions.overrideOf(
                                params.width,
                                params.height ).centerCrop() ).into( image );
    }

    public void clear() {
        setTag( null );
        setClickable( false );
        image.setImageDrawable( null );
    }

    @Nullable
    public File getFile() {
        return (File) getTag();
    }

    public ImageView getImageView() {
        return image;
    }
}