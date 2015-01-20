package com.uclan.mstocklmayr.gallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.FloatMath;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.uclan.mstocklmayr.CaptureActivity;
import com.uclan.mstocklmayr.PreferencesActivity;
import com.uclan.mstocklmayr.R;
import com.uclan.mstocklmayr.utils.JSONHandler;
import com.uclan.mstocklmayr.utils.Util;

import java.io.File;

public class GalleryFragment extends Fragment {

    private static final String ARG_IMAGE_RESOURCE = "imagePath";

    private boolean isNotesVisible = false;

    //zoom test variables
    private static final String TAG = GalleryFragment.class.getSimpleName()+"-Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        String filePath = getArguments().getString(ARG_IMAGE_RESOURCE);

        Location location = JSONHandler.getLocation(this.getActivity(), filePath.substring(filePath.lastIndexOf("/") + 1));
        if (location == null) {
            MenuItem item = menu.findItem(R.id.action_location);
            Drawable icon = getResources().getDrawable(R.drawable.ic_action_place);
            item.setIcon(Util.convertDrawableToGrayScale(icon));
            item.setEnabled(false);
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String cardPath = settings.getString(PreferencesActivity.KEY_MY_BUSINESS_CARD, CaptureActivity.DEFAULT_MY_BUSINESS_CARD);

        if (cardPath.equals(CaptureActivity.DEFAULT_MY_BUSINESS_CARD)) {
            MenuItem item = menu.findItem(R.id.action_send_my_card);
            Drawable icon = getResources().getDrawable(R.drawable.ic_action_email);
            item.setIcon(Util.convertDrawableToGrayScale(icon));
            item.setEnabled(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    public static GalleryFragment buildWithResource(int res) {
        Bundle args = new Bundle();

        args.putInt(ARG_IMAGE_RESOURCE, res);

        GalleryFragment fragment = new GalleryFragment();
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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvNotes = (TextView) getView().findViewById(R.id.notes);
                if(!isNotesVisible){
                    tvNotes.setVisibility(View.VISIBLE);
                    String filePath = getArguments().getString(ARG_IMAGE_RESOURCE);
                    filePath = filePath.substring(filePath.lastIndexOf("/") + 1);
                    String notes = JSONHandler.getProperty(getActivity(), filePath, JSONHandler.NOTES);
                    tvNotes.setText(notes == null ? getString(R.string.initialNotesMessage) : notes);
                    isNotesVisible=true;
                }else {
                    tvNotes.setVisibility(View.GONE);
                    isNotesVisible = false;
                }
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String filePath = getArguments().getString(ARG_IMAGE_RESOURCE);
                final String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                String notes = JSONHandler.getProperty(getActivity(), filePath, JSONHandler.NOTES);

                final EditText input = new EditText(getActivity());
                input.setText(notes);
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.notesPopupTitle))
                        .setMessage(getString(R.string.notesPopUpQuestion))
                        .setView(input)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Editable value = input.getText();

                                JSONHandler.addRecordForFile(getActivity(),fileName,JSONHandler.NOTES,value.toString());
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.d(TAG, "cancel onClickListener"); // write to LogCat
                    }
                }).show();
                return true;
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                view.setScaleType(ImageView.ScaleType.MATRIX);
                float scale;

                dumpEvent(event);
                // Handle touch events here...

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN: // first finger down only
                        matrix.set(view.getImageMatrix());
                        savedMatrix.set(matrix);
                        Log.d(TAG, "mode=DRAG"); // write to LogCat
                        mode = DRAG;
                        break;

                    case MotionEvent.ACTION_UP: // first finger lifted

                    case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                        mode = NONE;
                        Log.d(TAG, "mode=NONE");
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                        oldDist = spacing(event);
                        Log.d(TAG, "oldDist=" + oldDist);
                        if (oldDist > 5f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                            Log.d(TAG, "mode=ZOOM");
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:

                        if (mode == DRAG) {
                            // matrix.set(savedMatrix);
                            //matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                        } else if (mode == ZOOM) {
                            // pinch zooming
                            float newDist = spacing(event);
                            Log.d(TAG, "newDist=" + newDist);
                            if (newDist > 5f) {
                                matrix.set(savedMatrix);
                                scale = newDist / oldDist; // setting the scaling of the
                                // matrix...if scale > 1 means
                                // zoom in...if scale < 1 means
                                // zoom out
                                matrix.postScale(scale, scale, mid.x, mid.y);
                            }
                        }
                        break;
                }

                view.setImageMatrix(matrix); // display the transformation on screen

                return false; // indicate event was handled
            }
        });

        TextView tvNotes = (TextView) getView().findViewById(R.id.notes);
        tvNotes.setVisibility(View.GONE);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Show an event in the LogCat view, for debugging
     */
    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }
}
