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
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.TextViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.android.flexbox.FlexboxLayout;

import java.io.File;
import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.google.android.flexbox.FlexboxLayout.ALIGN_CONTENT_FLEX_START;
import static com.google.android.flexbox.FlexboxLayout.ALIGN_ITEMS_FLEX_START;
import static com.google.android.flexbox.FlexboxLayout.FLEX_WRAP_WRAP;
import static com.liefery.android.gallery.Action.*;

public class Gallery extends LinearLayout implements OnClickListener {
    private static int dpToPx( int dp ) {
        return (int) ( dp * Resources.getSystem().getDisplayMetrics().density );
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            context.unregisterReceiver( receiver );

            int event = intent.getIntExtra( "event", -1 );

            switch ( event ) {
                case EVENT_SUCCESS:
                    String path = intent.getStringExtra( "file" );
                    File file = new File( path );
                    addPhoto( file, true );
                break;
                case EVENT_CANCEL:
                break;
                case EVENT_ERROR:
                break;
                default:
                break;
            }
        }
    };

    private int thumbnailBackgroundColor;

    private int thumbnailWidth;

    private int thumbnailHeight;

    private Integer maxThumbnails;

    private boolean placeholders;

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

        addView( images, WRAP_CONTENT, WRAP_CONTENT );
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

        Integer maxThumbnails = null;
        int maxThumbnailValue = styles.getInt(
            R.styleable.Gallery_gallery_maxThumbnails,
            -1 );
        if ( maxThumbnailValue != -1 ) {
            maxThumbnails = maxThumbnailValue;
        }
        setMaxThumbnails( maxThumbnails );

        boolean placeholders = styles.getBoolean(
            R.styleable.Gallery_gallery_placeholders,
            false );
        setAlwaysShowPlaceholders( placeholders );
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

    public void setMaxThumbnails( @Nullable Integer count ) {
        this.maxThumbnails = count;
    }

    public Integer getMaxThumbnails() {
        return maxThumbnails;
    }

    public void setAlwaysShowPlaceholders( boolean show ) {
        this.placeholders = show;
    }

    public boolean alwaysShowPlaceholders() {
        return placeholders;
    }

    /**
     * Amount of visible thumbnails (including empty placeholders)
     */
    public int getPlaceholderCount() {
        return images.getChildCount();
    }

    @Override
    public void onClick( View view ) {
        IntentFilter filter = new IntentFilter( ACTION_IMAGE_CAPTURE );
        getContext().registerReceiver( receiver, filter );

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

    private ArrayList<Thumbnail> getThumbnails() {
        int count = images.getChildCount();
        ArrayList<Thumbnail> thumbnails = new ArrayList<>( count );

        for ( int i = 0; i < count; i++ ) {
            thumbnails.add( (Thumbnail) images.getChildAt( i ) );
        }

        return thumbnails;
    }

    @Nullable
    private Thumbnail getNextThumbnail() {
        if ( getMaxThumbnails() == null ) {
            return addPlaceholder();
        } else {
            for ( Thumbnail thumbnail : getThumbnails() ) {
                if ( thumbnail.getFile() == null ) {
                    return thumbnail;
                }
            }

            return null;
        }
    }

    private boolean addPhoto( @NonNull File file, boolean animated ) {
        Thumbnail thumbnail = getNextThumbnail();

        if ( thumbnail != null ) {
            if ( animated ) {
                ViewCompat.setScaleX( thumbnail, 0 );
                ViewCompat.setScaleY( thumbnail, 0 );

                ViewCompat.animate( thumbnail ).scaleX( 1 ).scaleY( 1 )
                                .setStartDelay( 250 ).setDuration( 350 )
                                .start();
            }

            thumbnail.load( file );

            return true;
        } else {
            return false;
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
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState( Parcelable state ) {
        super.onRestoreInstanceState( state );
    }
}