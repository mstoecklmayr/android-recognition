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
package com.uclan.mstocklmayr.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class EditContentsAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

    private Bitmap lastBitmap;
    private GoogleApiClient mGoogleApiClient;
    private Context ctx;

    public EditContentsAsyncTask(Context context, Bitmap lastBitmap, GoogleApiClient mGoogleApiClient) {
        super(context);
        this.ctx = context;
        this.lastBitmap = lastBitmap;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    @Override
    protected Boolean doInBackgroundConnected(DriveFile... args) {
        DriveFile file = args[0];
        try {
            DriveApi.DriveContentsResult driveContentsResult = file.open(
                    mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return false;
            }
            Bitmap bitmap = lastBitmap;
            ByteArrayOutputStream blob = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, blob);
            byte[] bitmapdata = blob.toByteArray();

            DriveContents driveContents = driveContentsResult.getDriveContents();
            OutputStream outputStream = driveContents.getOutputStream();
            outputStream.write(bitmapdata);
            com.google.android.gms.common.api.Status status =
                    driveContents.commit(mGoogleApiClient, null).await();
            return status.getStatus().isSuccess();
        } catch (IOException e) {
            Log.e(DriveHandler.TAG, "IOException while appending to the output stream", e);
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (!result) {
            Toast.makeText(ctx, "Error while editing contents", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(ctx, "Successfully edited contents", Toast.LENGTH_LONG).show();
    }
}
