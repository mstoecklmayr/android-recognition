package com.uclan.mstocklmayr.utils;

import android.content.Context;
import android.location.Location;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;


/**
 * Created by mike on 11/4/14.
 */
public class JSONHandler {
    static final String JSONFileName = "appData";
    static final String FILENAME = "filename";
    static final String NOTES = "notes";
    static final String LATITUDE = "latitude";
    static final String LONGITUDE = "longitude";


    /*
        Creates new JSON file in the internal storage or adds an entry to it if it exists without GPS coordinates
     */

    public static void addImage(Context ctx, String fileName){
        addImage(ctx,fileName, null);
    }

    /*
        Creates new JSON file in the internal storage or adds an entry to it if it exists
     */
    public static void addImage(Context ctx, String fileName, Location location) {
        File file = ctx.getFileStreamPath(JSONFileName);

//        if (!file.exists()) {
//            //create new file
//            try {
//                //create output stream to internal storage, MODE_PRIVATE : only this application can read it
//                FileOutputStream fos = ctx.openFileOutput(JSONFileName, ctx.MODE_PRIVATE);
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        JSONArray newAppData = new JSONArray();
        JSONObject object = new JSONObject();

        try {
            object.put(FILENAME, fileName);
            if(location != null){
                object.put(LONGITUDE, Double.toString(location.getLongitude()));
                object.put(LATITUDE, Double.toString(location.getLatitude()));
            }
            newAppData.put(object);

            if(!file.exists()){
                writeJSONFile(ctx, newAppData);
            }else{
                JSONArray appData = getJSONFile(ctx);
                appData.put(object);

                writeJSONFile(ctx, appData);
            }
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

    //get one property from JSON file
    public static String getProperty(Context ctx, String fileName, String property){
        JSONArray appData = getJSONFile(ctx);
        try {
            for(int i = 0; i<appData.length();i++){
                JSONObject obj = appData.getJSONObject(i);
                if(obj.getString(FILENAME).equalsIgnoreCase(fileName)){
                    if (property.equals(NOTES)) {
                        return obj.getString(NOTES);
                    } else if (property.equals(LATITUDE)) {
                        return obj.getString(LATITUDE);
                    } else if (property.equals(LONGITUDE)) {
                        return obj.getString(LONGITUDE);
                    }
                    break;
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //get one property from JSON file
    public static Location getLocation(Context ctx, String fileName){
        JSONArray appData = getJSONFile(ctx);
        try {
            for(int i = 0; i<appData.length();i++){
                JSONObject obj = appData.getJSONObject(i);
                if(obj.getString(FILENAME).equalsIgnoreCase(fileName)){
                    //provider name is useless
                    Location location = new Location("Image location");
                    location.setLongitude(new Double(obj.getString(LONGITUDE)));
                    location.setLatitude(new Double(obj.getString((LATITUDE))));
                    return location;
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void removeJSONObject(Context ctx, String fileName) {
        try {
            JSONArray appData = getJSONFile(ctx);
            ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
            for (int i = 0; i < appData.length(); i++) {
                if (!appData.getJSONObject(i).getString(FILENAME).equalsIgnoreCase(fileName)) {
                    jsonList.add(appData.getJSONObject(i));
                }
            }
            JSONArray newAppData = new JSONArray();

            for (JSONObject obj : jsonList) {
                newAppData.put(obj);
            }
            writeJSONFile(ctx,newAppData);
        } catch (JSONException e) {
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
                appData = null;
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
            String text = array.toString();
            if (text != null)
                fos.write(text.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
