package com.liefery.android.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import com.google.android.flexbox.FlexboxLayout;

import java.io.File;
import java.util.ArrayList;

import static com.google.android.flexbox.AlignContent.FLEX_START;
import static com.google.android.flexbox.FlexWrap.WRAP;

public class GalleryView extends FlexboxLayout implements OnClickListener {
    public static final String TAG = GalleryView.class.getCanonicalName();

    public static final String ACTION = TAG + ".action";

    public static final int EVENT_SUCCESS = 0;

    public static final int EVENT_CANCEL = 1;

    public static final int EVENT_ERROR = 2;

    public static final int EVENT_DELETE = 3;

    @NonNull
    public static Intent createIntent( int event ) {
        return new Intent().setAction( ACTION ).putExtra( "event", event );
    }

    private static int dpToPx( int dp ) {
        return (int) ( dp * Resources.getSystem().getDisplayMetrics().density );
    }

    @NonNull
    private static FragmentManager getFragmentManager( Context context ) {
        if ( context instanceof Activity ) {
            return ( (FragmentActivity) context ).getSupportFragmentManager();
        } else {
            throw new IllegalStateException( "Not a valid host" );
        }
    }

    private int thumbnailBackgroundColor;

    private int thumbnailWidth;

    private int thumbnailHeight;

    private OnPhotoAddedListener onPhotoAddedListener;

    private OnPhotoRemovedListener onPhotoRemovedListener;

    private OnPhotoErrorListener onPhotoErrorListener;

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

        int thumbnailDefaultSize = dpToPx( 80 );

        int thumbnailWidth = styles.getDimensionPixelSize(
            R.styleable.GalleryView_gallery_thumbnailWidth,
            thumbnailDefaultSize );
        setThumbnailWidth( thumbnailWidth );

        int thumbnailHeight = styles.getDimensionPixelSize(
            R.styleable.GalleryView_gallery_thumbnailHeight,
            thumbnailDefaultSize );
        setThumbnailHeight( thumbnailHeight );

        FragmentManager fm = getFragmentManager( context );

        PermissionAuxilery permissionAuxilery = (PermissionAuxilery) fm
                        .findFragmentByTag( PermissionAuxilery.TAG );

        if ( permissionAuxilery != null ) {
            permissionAuxilery.setGalleryView( this );
        }

        PhotoAuxilery photoAuxilery = (PhotoAuxilery) fm
                        .findFragmentByTag( PhotoAuxilery.TAG );

        if ( photoAuxilery != null ) {
            photoAuxilery.setGalleryView( this );
        }

        addButton();

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

    @Override
    public void onClick( View view ) {
        takePhoto();
    }

    void takePhoto() {
        Context context = getContext();
        FragmentManager manager = getFragmentManager( context );

        if ( !PermissionAuxilery.hasPermissions( context ) ) {
            PermissionAuxilery permissionAuxilery = PermissionAuxilery
                            .newInstance( this );
            manager.beginTransaction()
                            .add( permissionAuxilery, PermissionAuxilery.TAG )
                            .commitNow();
            return;
        }

        PhotoAuxilery photoAuxiliary = PhotoAuxilery.newInstance( this );
        manager.beginTransaction().add( photoAuxiliary, PhotoAuxilery.TAG )
                        .commitNow();

        Intent intent = new Intent( context, ActionActivity.class );
        photoAuxiliary.startActivityForResult( intent, 421 );
    }

    @NonNull
    public ArrayList<File> getImages() {
        int count = getChildCount();
        ArrayList<File> files = new ArrayList<>();

        for ( int i = 0; i < count - 1; i++ ) {
            File file = ( (ThumbnailView) getChildAt( i ) ).getFile();

            if ( file != null ) {
                files.add( file );
            }
        }

        return files;
    }

    @NonNull
    public ArrayList<String> getPaths() {
        int count = getChildCount();
        ArrayList<String> paths = new ArrayList<>();

        for ( int i = 0; i < count - 1; i++ ) {
            File file = ( (ThumbnailView) getChildAt( i ) ).getFile();

            if ( file != null ) {
                paths.add( file.getAbsolutePath() );
            }
        }

        return paths;
    }

    @Nullable
    public OnPhotoAddedListener getOnPhotoAddedListener() {
        return onPhotoAddedListener;
    }

    public void setOnPhotoAddedListener(
        @Nullable OnPhotoAddedListener onPhotoAddedListener ) {
        this.onPhotoAddedListener = onPhotoAddedListener;
    }

    @Nullable
    public OnPhotoRemovedListener getOnPhotoRemovedListener() {
        return onPhotoRemovedListener;
    }

    public void setOnPhotoRemovedListener(
        @Nullable OnPhotoRemovedListener onPhotoRemovedListener ) {
        this.onPhotoRemovedListener = onPhotoRemovedListener;
    }

    @Nullable
    public OnPhotoErrorListener getOnPhotoErrorListener() {
        return onPhotoErrorListener;
    }

    public void setOnPhotoErrorListener(
        @Nullable OnPhotoErrorListener onPhotoErrorListener ) {
        this.onPhotoErrorListener = onPhotoErrorListener;
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

    void addPhoto( @NonNull File file, boolean animated ) {
        ThumbnailView thumbnail = addPlaceholder();

        if ( animated ) {
            ViewCompat.setScaleX( thumbnail, 0 );
            ViewCompat.setScaleY( thumbnail, 0 );
            ViewCompat.setAlpha( thumbnail, 0 );

            ViewCompat.animate( thumbnail ).scaleX( 1 ).scaleY( 1 ).alpha( 1 )
                            .setStartDelay( 250 ).setDuration( 350 ).start();
        }

        final Context context = getContext();
        final String path = file.getAbsolutePath();

        OnClickListener onClick = new OnClickListener() {
            @Override
            public void onClick( View view ) {
                FragmentManager manager = getFragmentManager( context );

                PhotoAuxilery auxiliary = PhotoAuxilery
                                .newInstance( GalleryView.this );
                manager.beginTransaction().add( auxiliary, PhotoAuxilery.TAG )
                                .commitNow();

                Intent intent = new Intent( context, DetailActivity.class )
                                .putExtra( "file", path );
                auxiliary.startActivityForResult( intent, 421 );
            }
        };

        thumbnail.load( file, onClick );

        if ( onPhotoAddedListener != null )
            onPhotoAddedListener.onPhotoAdded( this, file );
    }

    boolean removePhoto( @NonNull File file ) {
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
            Log.w( TAG, "Deleted file is not present in gallery" );
            return false;
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

            if ( onPhotoRemovedListener != null )
                onPhotoRemovedListener.onPhotoRemoved( this, file );

            return true;
        }
    }

    private ImageButton addButton() {
        ImageButton button = (ImageButton) LayoutInflater.from( getContext() )
                        .inflate( R.layout.gallery_add_photo, this, false );
        button.setOnClickListener( this );
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

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle( 5 );
        bundle.putParcelable( "state", super.onSaveInstanceState() );
        bundle.putInt(
            "thumbnail-background-color",
            getThumbnailBackgroundColor() );
        bundle.putInt( "thumbnail-width", getThumbnailWidth() );
        bundle.putInt( "thumbnail-height", getThumbnailHeight() );
        bundle.putStringArrayList( "files", getPaths() );
        return bundle;
    }

    @Override
    public void onRestoreInstanceState( Parcelable state ) {
        if ( state instanceof Bundle ) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState( bundle.getParcelable( "state" ) );
            setThumbnailBackgroundColor( bundle
                            .getInt( "thumbnail-background-color" ) );
            setThumbnailWidth( bundle.getInt( "thumbnail-width" ) );
            setThumbnailHeight( bundle.getInt( "thumbnail-height" ) );
            ArrayList<String> files = bundle.getStringArrayList( "files" );
            for ( String file : files ) {
                addPhoto( new File( file ), false );
            }
        } else {
            super.onRestoreInstanceState( state );
        }
    }

    public interface OnPhotoAddedListener {
        void onPhotoAdded( GalleryView gallery, File photo );
    }

    public interface OnPhotoRemovedListener {
        void onPhotoRemoved( GalleryView galley, File photo );
    }

    public interface OnPhotoErrorListener {
        void onPhotoError( Throwable throwable );
    }
}