package com.uclan.mstocklmayr.gallery;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

/**
 * Created by mike on 10/20/14.
 */
public class AsyncTaskLoadFiles extends AsyncTask<Void, String, Void> {
    File targetDirector;
    ImagesPagerAdapter myTaskAdapter;
    String path = null;
    Context ctx;

    public AsyncTaskLoadFiles(Context ctx, ImagesPagerAdapter adapter, String path) {
        myTaskAdapter = adapter;
        this.path = path;
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        String targetPath = path;
        targetDirector = new File(targetPath);
        myTaskAdapter.clear();

        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {

        File[] files = targetDirector.listFiles();
        for (File file : files) {
            publishProgress(file.getAbsolutePath());
            if (isCancelled()) break;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        myTaskAdapter.add(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        myTaskAdapter.notifyDataSetChanged();
        super.onPostExecute(result);
    }

}