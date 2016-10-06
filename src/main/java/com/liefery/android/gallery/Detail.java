package com.liefery.android.gallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.File;

public class Detail extends AppCompatActivity implements Callback {
    private ImageView image;

    private File file;

    @Override
    protected void onCreate( @Nullable Bundle state ) {
        super.onCreate( state );

        setContentView( R.layout.gallery_detail );

        String path = getIntent().getStringExtra( "file" );
        file = new File( path );

        Toolbar toolbar = (Toolbar) findViewById( R.id.gallery_detail_toolbar );
        image = (ImageView) findViewById( R.id.gallery_detail_image );

        setSupportActionBar( toolbar );
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled( false );

        Picasso.with( this ).load( file ).into( image, this );
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
            finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onSuccess() {
        new PhotoViewAttacher( image );
    }

    @Override
    public void onError() {
    }
}