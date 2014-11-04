package com.uclan.mstocklmayr;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;


/**
 * Created by mike on 11/4/14.
 */
public class JSONHandler {
    static String JSONFileName = "appData";
    static String FILENAME = "filename";
    static String NOTES = "notes";
    static String LATITUDE = "latitude";
    static String LONGITUDE = "longitude";

    /*
        Creates new JSON file in the internal storage or adds an entry to it if it exists
     */
    public static void addImage(Context ctx, String fileName) {
        File file = ctx.getFileStreamPath(JSONFileName);

        if (!file.exists()) {
            //create new file

            try {
                //create output stream to internal storage, MODE_PRIVATE : only this application can read it
                FileOutputStream fos = ctx.openFileOutput(JSONFileName, ctx.MODE_PRIVATE);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JSONArray newAppData = new JSONArray();
        JSONObject object = new JSONObject();

        try {
            object.put(FILENAME, fileName);
            object.put(NOTES, "");
            object.put(LATITUDE, "");
            object.put(LONGITUDE, "");
            newAppData.put(object);


            String text = object.toString();
            StringBuffer b = new StringBuffer();

            FileInputStream fis = ctx.openFileInput(JSONFileName);
            BufferedInputStream bis = new BufferedInputStream(fis);
            while (bis.available() != 0) {
                char c = (char) bis.read();
                b.append(c);
            }
            bis.close();
            fis.close();

            JSONArray appData;
            if(b.length() > 0)
                appData = new JSONArray(b.toString());
            else{
                appData = new JSONArray();
            }
            appData.put(text);

            //create output stream to internal storage, MODE_PRIVATE : only this application can read it
            FileOutputStream fos = ctx.openFileOutput(JSONFileName, ctx.MODE_PRIVATE);
            if (appData != null)
                fos.write(appData.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
