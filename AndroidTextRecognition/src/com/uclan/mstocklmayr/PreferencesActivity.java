/*
 * Copyright (C) 2008 ZXing authors
 * Copyright 2011 Robert Theis
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
package com.uclan.mstocklmayr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.widget.Toast;
import com.github.amlcurran.showcaseview.ShowcaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle preferences that are saved across sessions of the app. Shows
 * a hierarchy of preferences to the user, organized into sections. These
 * preferences are displayed in the options menu that is shown when the user
 * presses the MENU button.
 * <p/>
 * The code for this class was adapted from the ZXing project: http://code.google.com/p/zxing
 */
public class PreferencesActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {

    // Preference keys not carried over from ZXing project
    public static final String KEY_SOURCE_LANGUAGE_PREFERENCE = "sourceLanguageCodeOcrPref";
    public static final String KEY_CONTINUOUS_PREVIEW = "preference_capture_continuous";
    public static final String KEY_PAGE_SEGMENTATION_MODE = "preference_page_segmentation_mode";
    public static final String KEY_OCR_ENGINE_MODE = "preference_ocr_engine_mode";
    public static final String KEY_TOGGLE_LIGHT = "preference_toggle_light";

    // Preference keys carried over from ZXing project
    public static final String KEY_AUTO_FOCUS = "preferences_auto_focus";
    public static final String KEY_REVERSE_IMAGE = "preferences_reverse_image";
    public static final String KEY_PLAY_BEEP = "preferences_play_beep";


    public static final String KEY_MY_BUSINESS_CARD = "preferences_my_business_card";
    public static final String KEY_RESET_HINTS = "preferences_reset_hints";
    public static final String MY_BUSINESS_CARD_SELECTED = "Image selected";

    private Preference preferenceMyBusinessCard;
    private Preference preferenceResetHints;
    private ListPreference listPreferenceOcrEngineMode;

    private static SharedPreferences sharedPreferences;
    private List<Integer> singleShotIdList = new ArrayList<Integer>();

    /**
     * Set the default preference values.
     *
     * @param savedInstanceState the current Activity's state, as passed by
     *               Android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //to have a full screen with an action bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set showcase view single shot ids in order to reset them within the settings
        singleShotIdList.add(200);
        singleShotIdList.add(300);


        //TODO could be changed to PreferenceFragment
        preferenceMyBusinessCard = findPreference(KEY_MY_BUSINESS_CARD);
        preferenceMyBusinessCard.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
                return true;
            }
        });

        preferenceResetHints = findPreference(KEY_RESET_HINTS);
        preferenceResetHints.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                boolean result = ShowcaseView.resetShotState(PreferencesActivity.this,singleShotIdList);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_MY_BUSINESS_CARD, CaptureActivity.DEFAULT_MY_BUSINESS_CARD);
                editor.commit();
                if(result)
                    Toast.makeText(PreferencesActivity.this, "Hints have been reset",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(PreferencesActivity.this, "Error while resetting hints!",Toast.LENGTH_SHORT).show();
                return result;
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        listPreferenceOcrEngineMode = (ListPreference) getPreferenceScreen().findPreference(KEY_OCR_ENGINE_MODE);
        preferenceMyBusinessCard = getPreferenceScreen().findPreference(KEY_MY_BUSINESS_CARD);
    }

    /**
     * Interface definition for a callback to be invoked when a shared
     * preference is changed. Sets summary text for the app's preferences. Summary text values show the
     * current settings for the values.
     *
     * @param sharedPreferences the Android.content.SharedPreferences that received the change
     * @param key               the key of the preference that was changed, added, or removed
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        // Update preference summary values to show current preferences
        if (key.equals(KEY_PAGE_SEGMENTATION_MODE)) {
            //listPreferencePageSegmentationMode.setSummary(sharedPreferences.getString(key, CaptureActivity.DEFAULT_PAGE_SEGMENTATION_MODE));
        } else if (key.equals(KEY_OCR_ENGINE_MODE)) {
            listPreferenceOcrEngineMode.setSummary(sharedPreferences.getString(key, CaptureActivity.DEFAULT_OCR_ENGINE_MODE));
        }
    }

    /**
     * Sets up initial preference summary text
     * values and registers the OnSharedPreferenceChangeListener.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Set up the initial summary values
        //listPreferenceOcrEngineMode.setSummary(sharedPreferences.getString(KEY_OCR_ENGINE_MODE, CaptureActivity.DEFAULT_OCR_ENGINE_MODE));

        String pref = sharedPreferences.getString(KEY_MY_BUSINESS_CARD, CaptureActivity.DEFAULT_MY_BUSINESS_CARD);
        preferenceMyBusinessCard.setSummary(pref.equals(CaptureActivity.DEFAULT_MY_BUSINESS_CARD)?pref:MY_BUSINESS_CARD_SELECTED);
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_MY_BUSINESS_CARD, imagePath);
            editor.commit();

            //Toast.makeText(PreferencesActivity.this,imagePath,Toast.LENGTH_SHORT).show();
            preferenceMyBusinessCard.setSummary(MY_BUSINESS_CARD_SELECTED);
        }
    }

    /**
     * Called when Activity is about to lose focus. Unregisters the
     * OnSharedPreferenceChangeListener.
     */
    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}