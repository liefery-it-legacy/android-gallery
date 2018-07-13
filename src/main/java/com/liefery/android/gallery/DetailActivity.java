package com.liefery.android.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.io.File;

import static com.liefery.android.gallery.GalleryFragment.EVENT_DELETE;
import static com.liefery.android.gallery.Util.createIntent;

public class DetailActivity extends AppCompatActivity implements RequestListener<Drawable> {
    private PhotoView image;

    private File file;

    public static Intent newInstance( Context context, File file ) {
        Intent intent = new Intent( context, DetailActivity.class );
        intent.putExtra( "file", file.getAbsolutePath() );
        return intent;
    }

    @Override
    public void onCreate( @Nullable Bundle state ) {
        super.onCreate( state );

        setContentView( R.layout.gallery_detail );

        String path = getIntent().getStringExtra( "file" );
        file = new File( path );

        Toolbar toolbar = (Toolbar) findViewById( R.id.gallery_detail_toolbar );
        image = (PhotoView) findViewById( R.id.gallery_detail_image );

        setSupportActionBar( toolbar );
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled( false );

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize( size );

        Glide
                        .with( this )
                        .load( file )
                        .apply(
                            RequestOptions.overrideOf( size.x, size.y )
                                            .centerInside() ).into( image );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.gallery_detail, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        int id = item.getItemId();

        if ( id == R.id.gallery_menu_detail_delete ) {
            if ( !file.delete() ) {
                Log.e(
                    getClass().getCanonicalName(),
                    "File could not be delete: " + file.getAbsolutePath() );
            }

            Intent intent = createIntent( EVENT_DELETE ).putExtra(
                "file",
                this.file.getAbsolutePath() );
            setResult( RESULT_OK, intent );
            finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public boolean onLoadFailed(
        @Nullable GlideException e,
        Object model,
        Target<Drawable> target,
        boolean isFirstResource ) {
        return false;
    }

    @Override
    public boolean onResourceReady(
        Drawable resource,
        Object model,
        Target<Drawable> target,
        DataSource dataSource,
        boolean isFirstResource ) {
        new PhotoViewAttacher( image );
        return true;
    }
}