package com.alvarosantisteban.moderacion15m.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;

/**
 * Set of static methods used along the program.
 *
 * @author Alvaro Santisteban Dieguez 30/12/14 - alvarosantisteban@gmail.com
 */
public class Utils {

    private static final String TAG = "Util";

    ///////////////////////////////////////////////////////////////////
    // SIZE RELATED
    ///////////////////////////////////////////////////////////////////

    public static int pxFromDp(Context context, float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    /**
     * Returns the height of the window, in pixels.
     *
     * @param context
     * @return the height of the window in pixels
     */
    public static int getWindowHeight(Context context) {
        /*
        int measuredHeight = 0;
        Point size = new Point();
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            w.getDefaultDisplay().getSize(size);
            measuredHeight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            measuredHeight = d.getHeight();
        }
        return measuredHeight;
        */

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int realWidth;
        int realHeight;

        if (Build.VERSION.SDK_INT >= 17) {
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;
            realHeight = realMetrics.heightPixels;

        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                realWidth = display.getWidth();
                realHeight = display.getHeight();
                Log.e("Display Info", "Couldn't use reflection to get the real display metrics.");
            }

        } else {
            //This should be close, as lower API devices should not have window navigation bars
            realWidth = display.getWidth();
            realHeight = display.getHeight();
        }
        return realHeight;
    }

    /**
     * Returns the height of the action bar in pixels
     * @param context
     * @return the height of the action bar in pixels
     */
    public static int getActionBarHeight(Context context){
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = context.getResources().getDimensionPixelSize(tv.resourceId);
        return actionBarHeight;
    }

    /**
     * Returns the height of the status bar in pixels
     *
     * This method might return a non-zero value on some devices that do not have a status bar:
     * http://stackoverflow.com/questions/3407256/height-of-status-bar-in-android
     *
     * @param context
     * @return the height of the status bar in pixels
     */
    public static int getStatusBarHeight(Context context){
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getNavigationBarHeight(Context context){
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * Checks if the integer is an odd or an even number
     *
     * @param i the integer to be checked
     * @return true if the integer is odd, false if it is even
     */
    public static boolean isOdd(int i){
        if((i & 1) == 0){
            return false;
        }
        return true;
    }

    public static void writeToFile(String stringToBeWritten, String fileName) {
        try {
            File f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + fileName);
            FileOutputStream fos = new FileOutputStream(f);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(stringToBeWritten);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
