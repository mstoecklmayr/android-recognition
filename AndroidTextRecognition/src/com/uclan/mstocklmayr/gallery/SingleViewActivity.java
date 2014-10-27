package com.uclan.mstocklmayr.gallery;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import com.uclan.mstocklmayr.R;

public class SingleViewActivity extends FragmentActivity {
    public static final String EXTRA_ID = "id";

    public static final String imagePath = Environment.getExternalStorageDirectory().toString() + "/Recognitions";

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
    }
}
