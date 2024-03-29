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
import android.os.AsyncTask;

import java.io.File;

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
        if(files != null){
            for (File file : files) {
                publishProgress(file.getAbsolutePath());
                if (isCancelled()) break;
            }
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