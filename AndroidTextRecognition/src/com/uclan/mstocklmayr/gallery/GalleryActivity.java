package com.uclan.mstocklmayr.gallery;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.uclan.mstocklmayr.CaptureActivity;
import com.uclan.mstocklmayr.R;

import java.io.File;

public class GalleryActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
    public static final String imagePath = Environment.getExternalStorageDirectory().toString() + "/Recognitions";

    private int currentImageIndex = 0;
    private ViewPager pager;
    private ImagesPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_viewpager);

        ActionBar ab = getActionBar();
        ab.show();

        // Selected image id
        int position = 0;
        adapter = new ImagesPagerAdapter(getSupportFragmentManager(), this);
        AsyncTaskLoadFiles alf = new AsyncTaskLoadFiles(this, adapter, imagePath);
        alf.execute();

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
        pager.setOnPageChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_discard:
                deleteImage();
                return true;
            case R.id.action_share:
                shareRecognition();
                return true;
            //TODO add reprocess feature
//            case R.id.action_reprocess:
//                reprocessImage();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteImage(){
        String path = adapter.imagePathList.get(this.currentImageIndex);
        Toast.makeText(this, path + "deleted ", Toast.LENGTH_LONG).show();
        if(path != null){
            File file = new File(path);
            file.delete();
            int before = adapter.getCount();
            adapter.imagePathList.remove(this.currentImageIndex);
            int after = adapter.getCount();

            adapter.notifyDataSetChanged();
            pager.invalidate();
        }else{
            Toast.makeText(this, "error. no image path set", Toast.LENGTH_LONG).show();
        }
    }

    private void shareRecognition() {
        String path = adapter.imagePathList.get(this.currentImageIndex);
        File file = new File(path);
        Uri uriToImage = Uri.fromFile(file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "Share image via"));
    }

    private void reprocessImage(){
        String path = adapter.imagePathList.get(this.currentImageIndex);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(CaptureActivity.FILE_PATH, path);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
            currentImageIndex = i;
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
