package com.liefery.android.gallery;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.TextViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.android.flexbox.FlexboxLayout;

import java.io.File;
import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.google.android.flexbox.FlexboxLayout.*;

public class Gallery extends LinearLayout implements OnClickListener {
    private static final String TAG = Gallery.class.getCanonicalName();

    public static final String ACTION = TAG + ".action";

    public static final int EVENT_SUCCESS = 0;

    public static final int EVENT_CANCEL = 1;

    public static final int EVENT_ERROR = 2;

    public static final int EVENT_DELETE = 3;

    public static Intent createIntent( int event ) {
        return new Intent().setAction( ACTION ).putExtra( "event", event );
    }

    private static int dpToPx( int dp ) {
        return (int) ( dp * Resources.getSystem().getDisplayMetrics().density );
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            int event = intent.getIntExtra( "event", -1 );

            String path;
            File file;

            switch ( event ) {
                case EVENT_SUCCESS:
                    path = intent.getStringExtra( "file" );
                    file = new File( path );
                    addPhoto( file, true );
                break;
                case EVENT_CANCEL:
                break;
                case EVENT_ERROR:
                break;
                case EVENT_DELETE:
                    path = intent.getStringExtra( "file" );
                    file = new File( path );
                    removePhoto( file );
                break;
                default:
                    Log.w( TAG, "Received unknown event code " + event );
                break;
            }
        }
    };

    private int thumbnailBackgroundColor;

    private int thumbnailWidth;

    private int thumbnailHeight;

    private FlexboxLayout images = new FlexboxLayout( getContext() );

    private Button button = new Button( getContext() );

    public Gallery( Context context ) {
        super( context );

        TypedArray styles = context
                        .obtainStyledAttributes( R.styleable.Gallery );
        initialize( styles );
        styles.recycle();
    }

    public Gallery( Context context, AttributeSet attrs ) {
        super( context, attrs );

        TypedArray styles = context.obtainStyledAttributes(
            attrs,
            R.styleable.Gallery );
        initialize( styles );
        styles.recycle();
    }

    public Gallery( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );

        TypedArray styles = context.obtainStyledAttributes(
            attrs,
            R.styleable.Gallery,
            defStyleAttr,
            0 );
        initialize( styles );
        styles.recycle();
    }

    @TargetApi( 21 )
    public Gallery(
        Context context,
        AttributeSet attrs,
        int defStyleAttr,
        int defStyleRes ) {
        super( context, attrs, defStyleAttr, defStyleRes );

        TypedArray styles = context.obtainStyledAttributes(
            attrs,
            R.styleable.Gallery,
            defStyleAttr,
            defStyleRes );
        initialize( styles );
        styles.recycle();
    }

    private void initialize( @NonNull TypedArray styles ) {
        setClipChildren( false );
        setClipToPadding( false );
        setOrientation( VERTICAL );

        images.setVisibility( GONE );
        images.setAlignContent( ALIGN_CONTENT_FLEX_START );
        images.setAlignItems( ALIGN_ITEMS_FLEX_START );
        images.setFlexWrap( FLEX_WRAP_WRAP );
        Drawable divider = ResourcesCompat.getDrawable(
            getResources(),
            R.drawable.gallery_divider,
            null );
        images.setShowDividerHorizontal( SHOW_DIVIDER_MIDDLE );
        images.setDividerDrawableHorizontal( divider );
        images.setShowDividerVertical( SHOW_DIVIDER_MIDDLE );
        images.setDividerDrawableVertical( divider );

        button.setText( R.string.gallery_add_photo );
        button.setOnClickListener( this );
        VectorDrawableCompat icon = VectorDrawableCompat.create(
            getContext().getResources(),
            R.drawable.gallery_ic_add_photo,
            getContext().getTheme() );
        button.setCompoundDrawablePadding( dpToPx( 8 ) );
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
            button,
            icon,
            null,
            null,
            null );

        LayoutParams params = new LayoutParams( WRAP_CONTENT, WRAP_CONTENT );
        params.bottomMargin = dpToPx( 16 );
        addView( images, params );

        addView( button, WRAP_CONTENT, WRAP_CONTENT );

        int thumbnailBackgroundColor = styles.getColor(
            R.styleable.Gallery_gallery_thumbnailBackgroundColor,
            Color.GRAY );
        setThumbnailBackgroundColor( thumbnailBackgroundColor );

        int thumbnailDefaultSize = dpToPx( 80 );

        int thumbnailWidth = styles.getDimensionPixelSize(
            R.styleable.Gallery_gallery_thumbnailWidth,
            thumbnailDefaultSize );
        setThumbnailWidth( thumbnailWidth );

        int thumbnailHeight = styles.getDimensionPixelSize(
            R.styleable.Gallery_gallery_thumbnailHeight,
            thumbnailDefaultSize );
        setThumbnailHeight( thumbnailHeight );
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        IntentFilter filter = new IntentFilter( ACTION );
        getContext().registerReceiver( receiver, filter );
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        getContext().unregisterReceiver( receiver );
    }

    public void setThumbnailBackgroundColor( @ColorInt int color ) {
        this.thumbnailBackgroundColor = color;

        for ( Thumbnail thumbnail : getThumbnails() ) {
            thumbnail.setBackgroundColor( color );
        }
    }

    public int getThumbnailBackgroundColor() {
        return thumbnailBackgroundColor;
    }

    public void setThumbnailWidth( int width ) {
        this.thumbnailWidth = width;

        for ( Thumbnail thumbnail : getThumbnails() ) {
            thumbnail.getLayoutParams().width = width;
        }
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailHeight( int height ) {
        this.thumbnailHeight = height;

        for ( Thumbnail thumbnail : getThumbnails() ) {
            thumbnail.getLayoutParams().height = height;
        }
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    @Override
    public void onClick( View view ) {
        Intent intent = new Intent( getContext(), Action.class );
        getContext().startActivity( intent );
    }

    public ArrayList<File> getImages() {
        int count = images.getChildCount();
        ArrayList<File> files = new ArrayList<>();

        for ( int i = 0; i < count; i++ ) {
            File file = ( (Thumbnail) images.getChildAt( i ) ).getFile();

            if ( file != null ) {
                files.add( file );
            }
        }

        return files;
    }

    public ArrayList<String> getPaths() {
        int count = images.getChildCount();
        ArrayList<String> paths = new ArrayList<>();

        for ( int i = 0; i < count; i++ ) {
            File file = ( (Thumbnail) images.getChildAt( i ) ).getFile();

            if ( file != null ) {
                paths.add( file.getAbsolutePath() );
            }
        }

        return paths;
    }

    private ArrayList<Thumbnail> getThumbnails() {
        int count = images.getChildCount();
        ArrayList<Thumbnail> thumbnails = new ArrayList<>( count );

        for ( int i = 0; i < count; i++ ) {
            thumbnails.add( (Thumbnail) images.getChildAt( i ) );
        }

        return thumbnails;
    }

    private void addPhoto( @NonNull File file, boolean animated ) {
        if ( images.getChildCount() == 0 ) {
            images.setVisibility( VISIBLE );
        }

        Thumbnail thumbnail = addPlaceholder();

        if ( animated ) {
            ViewCompat.setScaleX( thumbnail, 0 );
            ViewCompat.setScaleY( thumbnail, 0 );
            ViewCompat.setAlpha( thumbnail, 0 );

            ViewCompat.animate( thumbnail ).scaleX( 1 ).scaleY( 1 ).alpha( 1 )
                            .setStartDelay( 250 ).setDuration( 350 ).start();
        }

        thumbnail.load( file );
    }

    private boolean removePhoto( @NonNull File file ) {
        ArrayList<Thumbnail> thumbnails = getThumbnails();
        Thumbnail selection = null;

        for ( Thumbnail thumbnail : thumbnails ) {
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
            final Thumbnail finalSelection = selection;

            ViewCompat.animate( selection ).scaleX( 0 ).scaleY( 0 ).alpha( 0 )
                            .setStartDelay( 250 ).setDuration( 250 )
                            .withEndAction( new Runnable() {
                                @Override
                                public void run() {
                                    images.removeView( finalSelection );

                                    if ( images.getChildCount() == 0 ) {
                                        images.setVisibility( GONE );
                                    }
                                }
                            } ).start();

            return true;
        }
    }

    private Thumbnail addPlaceholder() {
        Thumbnail thumbnail = new Thumbnail( getContext() );
        thumbnail.setBackgroundColor( getThumbnailBackgroundColor() );

        images.addView( thumbnail, getThumbnailWidth(), getThumbnailHeight() );

        return thumbnail;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle( 7 );
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
    protected void onRestoreInstanceState( Parcelable state ) {
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
}