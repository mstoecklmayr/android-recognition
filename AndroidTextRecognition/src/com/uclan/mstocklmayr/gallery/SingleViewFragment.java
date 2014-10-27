package com.uclan.mstocklmayr.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.uclan.mstocklmayr.R;

import java.io.File;

public class SingleViewFragment extends Fragment {

    private static final String ARG_IMAGE_RESOURCE = "imagePath";

    public static SingleViewFragment buildWithResource(int res){
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RESOURCE, res);

        SingleViewFragment fragment = new SingleViewFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gallery_single_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String resource = getArguments().getString(ARG_IMAGE_RESOURCE);

        //get image from storage
        File file = new File(resource);
        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());

        ImageView imageView = (ImageView) getView().findViewById(R.id.imageview);
        imageView.setImageBitmap(bmp);

        TextView tv = (TextView) getView().findViewById(R.id.caption);
        tv.setText(resource);
    }
}
