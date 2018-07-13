package com.liefery.android.gallery;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import com.google.android.flexbox.FlexboxLayout;

import java.io.File;
import java.util.ArrayList;

import static com.google.android.flexbox.AlignContent.FLEX_START;
import static com.google.android.flexbox.FlexWrap.WRAP;

public class GalleryView extends FlexboxLayout {
    private int thumbnailBackgroundColor;

    private int thumbnailWidth;

    private int thumbnailHeight;

    private OnClickListener onClickThumbnailListener;

    private ImageButton takePhotoButton;

    public GalleryView( Context context ) {
        super( context );

        TypedArray styles = context
                        .obtainStyledAttributes( R.styleable.GalleryView );
        initialize( styles );
    }

    public GalleryView( Context context, AttributeSet attrs ) {
        super( context, attrs );

        TypedArray styles = context.obtainStyledAttributes(
            attrs,
            R.styleable.GalleryView );
        initialize( styles );
    }

    public GalleryView( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );

        TypedArray styles = context.obtainStyledAttributes(
            attrs,
            R.styleable.GalleryView,
            defStyleAttr,
            0 );
        initialize( styles );
    }

    private void initialize( @NonNull TypedArray styles ) {
        Context context = getContext();

        setClipChildren( false );
        setClipToPadding( false );

        setAlignContent( FLEX_START );
        setAlignItems( FLEX_START );
        setFlexWrap( WRAP );
        Drawable divider = ResourcesCompat.getDrawable(
            getResources(),
            R.drawable.gallery_divider,
            null );
        setShowDividerHorizontal( SHOW_DIVIDER_MIDDLE );
        setDividerDrawableHorizontal( divider );
        setShowDividerVertical( SHOW_DIVIDER_MIDDLE );
        setDividerDrawableVertical( divider );

        int thumbnailBackgroundColor = styles.getColor(
            R.styleable.GalleryView_gallery_thumbnailBackgroundColor,
            Color.GRAY );
        setThumbnailBackgroundColor( thumbnailBackgroundColor );

        int thumbnailDefaultSize = Util.dpToPx( 80 );

        int thumbnailWidth = styles.getDimensionPixelSize(
            R.styleable.GalleryView_gallery_thumbnailWidth,
            thumbnailDefaultSize );
        setThumbnailWidth( thumbnailWidth );

        int thumbnailHeight = styles.getDimensionPixelSize(
            R.styleable.GalleryView_gallery_thumbnailHeight,
            thumbnailDefaultSize );
        setThumbnailHeight( thumbnailHeight );

        takePhotoButton = addButton();

        styles.recycle();
    }

    public void setThumbnailBackgroundColor( @ColorInt int color ) {
        this.thumbnailBackgroundColor = color;

        for ( ThumbnailView thumbnail : getThumbnailViews() ) {
            thumbnail.setBackgroundColor( color );
        }
    }

    public int getThumbnailBackgroundColor() {
        return thumbnailBackgroundColor;
    }

    public void setThumbnailWidth( int width ) {
        this.thumbnailWidth = width;

        for ( int i = 0; i < getChildCount(); i++ ) {
            getChildAt( i ).getLayoutParams().width = width;
        }
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailHeight( int height ) {
        this.thumbnailHeight = height;

        for ( int i = 0; i < getChildCount(); i++ ) {
            getChildAt( i ).getLayoutParams().height = height;
        }
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setOnClickTakePhotoListener(
        OnClickListener onClickTakePhotoListener ) {
        if ( takePhotoButton != null )
            this.takePhotoButton.setOnClickListener( onClickTakePhotoListener );
    }

    public void setOnClickThumbnailListener(
        OnClickListener onClickThumbnailListener ) {
        this.onClickThumbnailListener = onClickThumbnailListener;

        for ( ThumbnailView view : getThumbnailViews() ) {
            view.setOnClickListener( onClickThumbnailListener );
        }
    }

    @NonNull
    private ArrayList<ThumbnailView> getThumbnailViews() {
        int count = getChildCount();
        ArrayList<ThumbnailView> thumbnails = new ArrayList<>( count );

        for ( int i = 0; i < count - 1; i++ ) {
            thumbnails.add( (ThumbnailView) getChildAt( i ) );
        }

        return thumbnails;
    }

    public void addPhoto( @NonNull File file, boolean animated ) {
        ThumbnailView thumbnail = addPlaceholder();

        if ( animated ) {
            ViewCompat.setScaleX( thumbnail, 0 );
            ViewCompat.setScaleY( thumbnail, 0 );
            ViewCompat.setAlpha( thumbnail, 0 );

            ViewCompat.animate( thumbnail ).scaleX( 1 ).scaleY( 1 ).alpha( 1 )
                            .setStartDelay( 250 ).setDuration( 350 ).start();
        }

        thumbnail.setOnClickListener( onClickThumbnailListener );
        thumbnail.load( file );
    }

    public void removePhoto( @NonNull File file ) {
        ArrayList<ThumbnailView> thumbnails = getThumbnailViews();
        ThumbnailView selection = null;

        for ( ThumbnailView thumbnail : thumbnails ) {
            File thumbnailFile = thumbnail.getFile();

            if ( thumbnailFile != null && thumbnailFile.equals( file ) ) {
                selection = thumbnail;
                break;
            }
        }

        if ( selection == null ) {
            // Log.w( TAG, "Deleted file is not present in gallery" );
            //            return false;
        } else {
            final ThumbnailView finalSelection = selection;

            ViewCompat.animate( selection ).scaleX( 0 ).scaleY( 0 ).alpha( 0 )
                            .setStartDelay( 250 ).setDuration( 250 )
                            .withEndAction( new Runnable() {
                                @Override
                                public void run() {
                                    removeView( finalSelection );
                                }
                            } ).start();
            //
            //            return true;
        }
    }

    private ImageButton addButton() {
        ImageButton button = (ImageButton) LayoutInflater.from( getContext() )
                        .inflate( R.layout.gallery_add_photo, this, false );
        addView( button, getThumbnailWidth(), getThumbnailHeight() );
        return button;
    }

    @NonNull
    private ThumbnailView addPlaceholder() {
        ThumbnailView thumbnail = new ThumbnailView( getContext() );
        thumbnail.setBackgroundColor( getThumbnailBackgroundColor() );

        LayoutParams params = new LayoutParams(
            getThumbnailWidth(),
            getThumbnailHeight() );
        addView( thumbnail, getChildCount() - 1, params );

        return thumbnail;
    }
}