package com.liefery.android.gallery;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

class Thumbnail extends FrameLayout {
    public Thumbnail( Context context ) {
        super( context );
        initialize();
    }

    public Thumbnail( Context context, AttributeSet attrs ) {
        super( context, attrs );
        initialize();
    }

    public Thumbnail( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        initialize();
    }

    @TargetApi( 21 )
    public Thumbnail(
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
        setOnClickListener( new OnClickListener() {
            @Override
            public void onClick( View view ) {
                Intent intent = new Intent( getContext(), Detail.class )
                                .putExtra( "file", file.getAbsolutePath() );

                getContext().startActivity( intent );
            }
        } );

        Picasso.with( getContext() ).load( file )
                        .resize( params.width, params.height ).centerCrop()
                        .into( image );
    }

    public void clear() {
        setTag( null );
        setClickable( false );
        setOnClickListener( null );
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