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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.*;

public class DriveHandler {

    public GoogleApiClient mGoogleApiClient;
    private Context ctx;
    private String fileName;
    private Bitmap lastBitmap;
    private DriveId mFolderDriveId;

    public static final String TAG = DriveHandler.class.getSimpleName();

    public DriveHandler(GoogleApiClient mGoogleApiClient, Context ctx, Bitmap lastBitmap, String fileName){
        this.mGoogleApiClient = mGoogleApiClient;
        this.ctx = ctx;
        this.lastBitmap = lastBitmap;
        this.fileName = fileName;
    }

    public void start(){
        Drive.DriveApi.getRootFolder(mGoogleApiClient).listChildren(mGoogleApiClient).setResultCallback(metadataCallback);
    }


    final ResultCallback<DriveFolder.DriveFileResult> fileCallback =
            new ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Toast.makeText(ctx, "Error while trying to create the file", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Log.d(TAG, "Created a file: " + result.getDriveFile().getDriveId());
                    new EditContentsAsyncTask(ctx, lastBitmap, mGoogleApiClient).execute(result.getDriveFile());
                }
            };

    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Toast.makeText(ctx, "Error while trying to create new file contents", Toast.LENGTH_LONG).show();
                        return;
                    }
                    boolean isConnected = mGoogleApiClient.isConnected();
                    if(isConnected){
                        DriveFolder folder = Drive.DriveApi.getFolder(mGoogleApiClient, mFolderDriveId);
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(fileName)
                                .setMimeType("image/bmp")
                                .setStarred(true).build();
                        folder.createFile(mGoogleApiClient, changeSet, result.getDriveContents())
                                .setResultCallback(fileCallback);
                    }else{
                        Toast.makeText(ctx, "drive not connected", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            };

    final ResultCallback<DriveFolder.DriveFolderResult> folderCallback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            //Toast.makeText(ctx, "drive result: "+result.getStatus(), Toast.LENGTH_LONG).show();
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(driveContentsCallback);
        }
    };
    final ResultCallback<DriveApi.MetadataBufferResult> metadataCallback =
            new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Toast.makeText(ctx, "Problem while retrieving results: "+result.getStatus(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    MetadataBuffer mdb = result.getMetadataBuffer();
                    boolean exists=false;
                    if (mdb != null) {
                        for (Metadata md : mdb) {
                            if (md == null) continue;
                            if(md.getTitle().equalsIgnoreCase("Recognition")){
                                mFolderDriveId = md.getDriveId();      // here is the "Drive ID"
                                exists = true;
                                break;
                            }
                        }
                    }
                    if(exists)
                        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                                .setResultCallback(driveContentsCallback);
                    else{
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle("Recognition").build();
                        Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(
                                mGoogleApiClient, changeSet).setResultCallback(folderCallback);
                    }
                }
            };

}

