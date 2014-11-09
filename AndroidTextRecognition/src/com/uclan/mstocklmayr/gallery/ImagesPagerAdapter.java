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