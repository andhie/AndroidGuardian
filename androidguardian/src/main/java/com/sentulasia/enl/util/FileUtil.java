package com.sentulasia.enl.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.sentulasia.enl.model.GuardianPortal;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by andhie on 2/1/14.
 */
public class FileUtil {

    public static final String LIVE_PORTAL_FILE = "live.txt";

    public static final String DEAD_PORTAL_FILE = "dead.txt";


    public static void savePortalList(Context context, List<GuardianPortal> list, String filename) {
        Gson gson = Util.getGson();
        writeToFile(context, filename, gson.toJson(list));
    }

    public static List<GuardianPortal> getPortalList(Context context, String filename) {
        String data = readFromFile(context, filename);

        Gson gson = Util.getGson();
        ArrayList<GuardianPortal> list = gson
                .fromJson(data, new TypeToken<ArrayList<GuardianPortal>>() {
                }.getType());

        if (list == null) {
            return Collections.<GuardianPortal>emptyList();
        } else {
            return list;
        }
    }

    private static String readFromFile(Context context, String filename) {

        try {
            InputStream inStream = context.openFileInput(filename);

            if (inStream == null) {
                return "[]";
            }

            InputStreamReader inReader = new InputStreamReader(inStream);
            BufferedReader br = new BufferedReader(inReader);

            String receiveString;
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = br.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            inStream.close();
            inReader.close();
            br.close();

            return stringBuilder.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("util", "file not found ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("util", "return empty array");
        return "[]";
    }

    private static void writeToFile(Context context, String filename, String json) {

        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
