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

            JSONArray appData = getJSONFile(ctx);
            appData.put(text);

            writeJSONFile(ctx, appData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //adds an entry to the JSON file with the corresponding fileName
    public static void addRecordForFile(Context ctx, String filePath, String name, String value){
        JSONArray appData = getJSONFile(ctx);
        try {
            for(int i = 0; i<appData.length();i++){
                JSONObject obj = appData.getJSONObject(i);
                if(obj.getString(FILENAME).equalsIgnoreCase(filePath)){
                    obj.put(name, value);
                    break;
                }
            }
            writeJSONFile(ctx,appData);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray getJSONFile(Context ctx){
        JSONArray appData = null;
        try {
            StringBuffer b = new StringBuffer();

            FileInputStream fis = ctx.openFileInput(JSONFileName);
            BufferedInputStream bis = new BufferedInputStream(fis);
            while (bis.available() != 0) {
                char c = (char) bis.read();
                b.append(c);
            }
            bis.close();
            fis.close();


            if(b.length() > 0)
                appData = new JSONArray(b.toString());
            else{
                appData = new JSONArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return appData;
    }

    private static void writeJSONFile(Context ctx, JSONArray array){
        try {
            //create output stream to internal storage, MODE_PRIVATE : only this application can read it
            FileOutputStream fos = ctx.openFileOutput(JSONFileName, ctx.MODE_PRIVATE);
            if (array != null)
                fos.write(array.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
