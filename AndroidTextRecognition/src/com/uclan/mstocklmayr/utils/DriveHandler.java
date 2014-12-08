package com.uclan.mstocklmayr.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by mike on 12/8/14.
 */
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
                    Toast.makeText(ctx, "Created a file: " + result.getDriveFile().getDriveId(), Toast.LENGTH_LONG).show();

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
                    DriveFolder folder = Drive.DriveApi.getFolder(mGoogleApiClient, mFolderDriveId);
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(fileName)
                            .setMimeType("image/bmp")
                            .setStarred(true).build();
                    folder.createFile(mGoogleApiClient, changeSet, result.getDriveContents())
                            .setResultCallback(fileCallback);
                }
            };

    final ResultCallback<DriveFolder.DriveFolderResult> folderCallback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            Toast.makeText(ctx, "drive result: "+result.getStatus(), Toast.LENGTH_LONG).show();
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

class EditContentsAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

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