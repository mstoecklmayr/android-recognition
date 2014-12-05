/*
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class to send OCR requests to the OCR engine in a separate thread, send a success/failure message,
 * and dismiss the indeterminate progress dialog box. Used for non-continuous mode OCR only.
 */
final class OcrRecognizeAsyncTask extends AsyncTask<Void, Void, Boolean> {

    //  private static final boolean PERFORM_FISHER_THRESHOLDING = false;
    //  private static final boolean PERFORM_OTSU_THRESHOLDING = false;
    //  private static final boolean PERFORM_SOBEL_THRESHOLDING = false;

    private CaptureActivity activity;
    private TessBaseAPI baseApi;
    private byte[] data;
    private int width;
    private int height;
    private OcrResult ocrResult;
    private long timeRequired;
    private final String filePath;

    OcrRecognizeAsyncTask(CaptureActivity activity, TessBaseAPI baseApi, byte[] data, int width, int height, String filePath) {
        this.activity = activity;
        this.baseApi = baseApi;
        this.data = data;
        this.width = width;
        this.height = height;
        this.filePath = filePath;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        long start = System.currentTimeMillis();
        Bitmap bitmap = null;
        if(this.filePath == null){
            bitmap = activity.getCameraManager().buildLuminanceSource(data, width, height).renderCroppedGreyscaleBitmap();
            //TODO remove as soon as possible
            saveImage(bitmap);
        }else{
            File file = new File(this.filePath);
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        String textResult;

        //      if (PERFORM_FISHER_THRESHOLDING) {
        //        Pix thresholdedImage = Thresholder.fisherAdaptiveThreshold(ReadFile.readBitmap(bitmap), 48, 48, 0.1F, 2.5F);
        //        Log.e("OcrRecognizeAsyncTask", "thresholding completed. converting to bmp. size:" + bitmap.getWidth() + "x" + bitmap.getHeight());
        //        bitmap = WriteFile.writeBitmap(thresholdedImage);
        //      }
        //      if (PERFORM_OTSU_THRESHOLDING) {
        //        Pix thresholdedImage = Binarize.otsuAdaptiveThreshold(ReadFile.readBitmap(bitmap), 48, 48, 9, 9, 0.1F);
        //        Log.e("OcrRecognizeAsyncTask", "thresholding completed. converting to bmp. size:" + bitmap.getWidth() + "x" + bitmap.getHeight());
        //        bitmap = WriteFile.writeBitmap(thresholdedImage);
        //      }
        //      if (PERFORM_SOBEL_THRESHOLDING) {
        //        Pix thresholdedImage = Thresholder.sobelEdgeThreshold(ReadFile.readBitmap(bitmap), 64);
        //        Log.e("OcrRecognizeAsyncTask", "thresholding completed. converting to bmp. size:" + bitmap.getWidth() + "x" + bitmap.getHeight());
        //        bitmap = WriteFile.writeBitmap(thresholdedImage);
        //      }

        try {
            baseApi.setImage(ReadFile.readBitmap(bitmap));
            textResult = baseApi.getUTF8Text();
            timeRequired = System.currentTimeMillis() - start;

            // Check for failure to recognize text
            if (textResult == null || textResult.equals("")) {
                return false;
            }

            //TODO remove result borders
            ocrResult = new OcrResult();
            ocrResult.setWordConfidences(baseApi.wordConfidences());
            ocrResult.setMeanConfidence(baseApi.meanConfidence());
            ocrResult.setRegionBoundingBoxes(baseApi.getRegions().getBoxRects());
            ocrResult.setTextlineBoundingBoxes(baseApi.getTextlines().getBoxRects());
            ocrResult.setWordBoundingBoxes(baseApi.getWords().getBoxRects());
            ocrResult.setStripBoundingBoxes(baseApi.getStrips().getBoxRects());
            //ocrResult.setCharacterBoundingBoxes(baseApi.getCharacters().getBoxRects());
        } catch (RuntimeException e) {
            Log.e("OcrRecognizeAsyncTask", "Caught RuntimeException in request to Tesseract. Setting state to CONTINUOUS_STOPPED.");
            e.printStackTrace();
            try {
                baseApi.clear();
                activity.stopHandler();
            } catch (NullPointerException e1) {
                // Continue
            }
            return false;
        }
        timeRequired = System.currentTimeMillis() - start;
        ocrResult.setBitmap(bitmap);
        ocrResult.setText(textResult);
        ocrResult.setRecognitionTimeRequired(timeRequired);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        Handler handler = activity.getHandler();
        if (handler != null) {
            // Send results for single-shot mode recognition.
            if (result) {
                Message message = Message.obtain(handler, R.id.ocr_decode_succeeded, ocrResult);
                message.sendToTarget();
            } else {
                Message message = Message.obtain(handler, R.id.ocr_decode_failed, ocrResult);
                message.sendToTarget();
            }
            activity.getProgressDialog().dismiss();
        }
        if (baseApi != null) {
            baseApi.clear();
        }
    }

    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Recognitions");
        myDir.mkdirs();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = sdf.format(new Date());
        String fname = "OCR" + date + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
