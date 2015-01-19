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
import com.uclan.mstocklmayr.CaptureActivity;
import com.uclan.mstocklmayr.PreferencesActivity;
import com.uclan.mstocklmayr.R;
import com.uclan.mstocklmayr.map.MapActivity;
import com.uclan.mstocklmayr.map.MapLocation;
import com.uclan.mstocklmayr.utils.JSONHandler;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
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

        ActionBar ab = getActionBar();
        //ab.show();
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
                default:
                    return super.onOptionsItemSelected(item);
            }
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
            intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "My business card");
        intent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void deleteImage() {
        String path = adapter.imagePathList.get(this.currentImageIndex);
        //Toast.makeText(this, path + "deleted ", Toast.LENGTH_LONG).show();

        //delete json entry for this image
        JSONHandler.removeJSONObject(this, path.substring(path.lastIndexOf("/") + 1));

        if (path != null) {
            File file = new File(path);
            file.delete();
            int before = adapter.getCount();
            adapter.imagePathList.remove(this.currentImageIndex);
            int after = adapter.getCount();

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
            Toast.makeText(this, "Long: " + location.getLongitude() + " Lat: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
            double latitude = Double.valueOf(location.getLatitude());
            double longitude = Double.valueOf(location.getLongitude());
            String label = location.getProvider();
//            String uriBegin = "geo:" + latitude + "," + longitude;
//            String query = latitude + "," + longitude + "(" + label + ")";
//            String encodedQuery = Uri.encode(query);
//            String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
//            Uri uri = Uri.parse(uriString);
//            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
//            startActivity(intent);
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
