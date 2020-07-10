package com.webfarms.treatmyfish.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.webfarms.treatmyfish.utils.GlobalData;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class WebImage implements SmartImage {
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 10000;

    private static WebImageCache webImageCache;

    private String url;
    private int height, width;
    private ScalingUtilities.ScalingLogic type;

    public WebImage(String url, int height, int width,
                    ScalingUtilities.ScalingLogic type) {
        this.url = url;
        this.height = height;
        this.width = width;
        this.type = type;
    }

    public Bitmap getBitmap(Context context) {
        // Don't leak context
        if (webImageCache == null) {
            webImageCache = new WebImageCache(context);
        }

        // Try getting bitmap from cache first
        Bitmap bitmap = null;
        if (url != null) {
            bitmap = webImageCache.get(url);
            if (bitmap == null) {
                double size = (GlobalData.checkFileSizeFromURL(url) / 1024.0);
                if (size > 500) {
                    return null;
                }
                bitmap = getBitmapFromUrl(url);
                if (bitmap != null) {
                    webImageCache.put(url, bitmap);
                }
            }
        }
        if (bitmap != null) {
            bitmap = ScalingUtilities.createScaledBitmap(bitmap, width, height,
                    type);
        }

        return bitmap;

    }

    public static Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;

        try {

            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            bitmap = BitmapFactory
                    .decodeStream((InputStream) conn.getContent());


            // if (tmpBitmap != null) {
            // bitmap = ScalingUtilities.createScaledBitmap(tmpBitmap,
            // GlobalData.IMG_WIDTH_L, GlobalData.IMG_HEIGHT_L,
            // ScalingLogic.FIT);
            // tmpBitmap.recycle();
            // tmpBitmap = null;
            // }

            // if (bitmap != null) {
            // bitmap = Bitmap.createScaledBitmap(bitmap,
            // GlobalData.IMG_WIDTH, GlobalData.IMG_HEIGHT, true);
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static void removeFromCache(String url) {
        if (webImageCache != null) {
            webImageCache.remove(url);
        }
    }
}
