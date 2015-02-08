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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;


public class ImagesPagerAdapter extends FragmentStatePagerAdapter {

    public Context ctx;
    ArrayList<String> imagePathList = new ArrayList<String>();

    public ImagesPagerAdapter(FragmentManager fm,Context ctx) {
        super(fm);
        this.ctx=ctx;
    }

    @Override
    public Fragment getItem(int i) {
        Bundle args = new Bundle();
        args.putString("imagePath", imagePathList.get(i));

        GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public int getItemPosition(Object object){
        return ImagesPagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return imagePathList.size();
    }

    public void add(String i){
        imagePathList.add(i);
    }

    public void clear(){
        imagePathList.clear();
    }
}