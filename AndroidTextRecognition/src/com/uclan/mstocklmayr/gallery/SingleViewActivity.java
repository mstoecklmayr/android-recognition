package com.uclan.mstocklmayr.gallery;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;
import com.uclan.mstocklmayr.R;

import java.io.File;

public class SingleViewActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
    public static final String imagePath = Environment.getExternalStorageDirectory().toString() + "/Recognitions";

    private int currentImageIndex = 0;
    private ViewPager pager;
    private ImagesPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_viewpager);

        // Selected image id
        int position = 0;
        ImagesPagerAdapter adapter = new ImagesPagerAdapter(getSupportFragmentManager(), this);
        AsyncTaskLoadFiles alf = new AsyncTaskLoadFiles(this, adapter, imagePath);
        alf.execute();

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
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

    void deleteImage(){
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
