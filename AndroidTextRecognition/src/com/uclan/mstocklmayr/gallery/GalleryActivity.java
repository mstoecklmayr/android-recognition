/*
 * Copyright 2015 Michael Stöcklmayr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uclan.mstocklmayr.gallery;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.uclan.mstocklmayr.CaptureActivity;
import com.uclan.mstocklmayr.PreferencesActivity;
import com.uclan.mstocklmayr.R;
import com.uclan.mstocklmayr.map.MapActivity;
import com.uclan.mstocklmayr.map.MapLocation;
import com.uclan.mstocklmayr.utils.JSONHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
    public static final String imagePath = Environment.getExternalStorageDirectory().toString() + "/Recognitions";

    private static final String TAG = GalleryActivity.class.getSimpleName();

    private int currentImageIndex = 0;
    private ViewPager pager;
    private ImagesPagerAdapter adapter;
    public static List<MapLocation> mapLocations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to have a full screen with an action bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionItemTarget target = new ActionItemTarget(this, R.id.action_send_my_card);

        new ShowcaseView.Builder(this)
                .setTarget(target)
                .setContentTitle("Send YOUR business card to one of your contacts! \n\nClick on the dots next to the icon to configure it!")
                .singleShot(300)
                .build();

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayUseLogoEnabled(false);

        setContentView(R.layout.gallery_viewpager);
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
        inflater.inflate(R.menu.gallery_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (adapter.imagePathList.size() != 0) {
            switch (item.getItemId()) {
                case R.id.action_discard:
                    deleteImage();
                    return true;
                case R.id.action_share:
                    shareRecognition();
                    return true;
                case R.id.action_location:
                    showLocationOnMap();
                    return true;
                case R.id.action_reprocess:
                    reprocessImage();
                    return true;
                case R.id.action_all_location:
                    showAllLocationOnMap();
                    return true;
                case R.id.action_send_my_card:
                    sendMyBusinessCard();
                    return true;
                case R.id.action_configure_card:
                    configureMyCard();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }else if(item.getItemId() == R.id.action_configure_card){
            configureMyCard();
            return true;
        }
        Toast.makeText(this, "No image available!", Toast.LENGTH_SHORT).show();
        return false;
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

    private void sendMyBusinessCard() {
        String fileName = adapter.imagePathList.get(this.currentImageIndex);
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);

        String email = JSONHandler.getProperty(this, fileName, JSONHandler.EMAIL);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String imagePath = settings.getString(PreferencesActivity.KEY_MY_BUSINESS_CARD, "null");
        Uri uriToImage = Uri.fromFile(new File(imagePath));

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        if (email != null)
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "My business card");
        intent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void deleteImage() {
        String path = adapter.imagePathList.get(this.currentImageIndex);

        //delete json entry for this image
        JSONHandler.removeJSONObject(this, path.substring(path.lastIndexOf("/") + 1));

        if (path != null) {
            File file = new File(path);
            file.delete();
            adapter.imagePathList.remove(this.currentImageIndex);

            adapter.notifyDataSetChanged();
            pager.invalidate();
        } else {
            Toast.makeText(this, "Error! No image path set!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Error! No image path set!");
        }
    }

    private void showLocationOnMap() {
        String path = adapter.imagePathList.get(this.currentImageIndex);
        Location location = JSONHandler.getLocation(this, path.substring(path.lastIndexOf("/") + 1));
        if (location != null) {
            Log.d(TAG, "Long: " + location.getLongitude() + " Lat: " + location.getLatitude());
            double latitude = Double.valueOf(location.getLatitude());
            double longitude = Double.valueOf(location.getLongitude());
            String label = location.getProvider();

            MapLocation loc = new MapLocation(latitude, longitude, label);
            if (this.mapLocations != null) {
                this.mapLocations.clear();
                this.mapLocations.add(loc);
            } else {
                this.mapLocations = new ArrayList<MapLocation>();
                this.mapLocations.add(loc);
            }
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);

        }
    }

    private void showAllLocationOnMap() {
        if (this.mapLocations != null) {
            this.mapLocations.clear();
        } else {
            this.mapLocations = new ArrayList<MapLocation>();
        }
        for (String locationString : adapter.imagePathList) {
            Location location = JSONHandler.getLocation(this, locationString.substring(locationString.lastIndexOf("/") + 1));
            if (location != null) {
                this.mapLocations.add(new MapLocation(location.getLatitude(), location.getLongitude(), location.getProvider()));
            }
        }
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private void configureMyCard() {
        Intent intent = new Intent().setClass(this, PreferencesActivity.class);
        startActivity(intent);
    }

    private void reprocessImage() {
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
