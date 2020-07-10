package com.webfarms.treatmyfish.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.webfarms.treatmyfish.custom.FontSetting;
import com.webfarms.treatmyfish.utils.LruBitmapCache;


public class AppController extends Application {

    public static final String TAG = AppController.class
            .getSimpleName();

    private static Context mAppContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public static boolean isAppOpen = false;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;

        FontSetting.setDefaultFont(this, "DEFAULT", "fonts/Roboto-Regular.ttf");
        FontSetting.setDefaultFont(this, "MONOSPACE", "fonts/Roboto-Medium.ttf");
        FontSetting.setDefaultFont(this, "SERIF", "fonts/ANK72605.TTF");
        FontSetting.setDefaultFont(this, "SANS_SERIF", "fonts/segoeui.ttf");
        mInstance = this;
        isAppOpen = true;



        initImageLoader(getApplicationContext());

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onActivityStarted(Activity activity) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity,
                                                    Bundle outState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onActivityResumed(Activity activity) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onActivityPaused(Activity activity) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                // TODO Auto-generated method stub
                isAppOpen = false;
            }

            @Override
            public void onActivityCreated(Activity activity,
                                          Bundle savedInstanceState) {
                // TODO Auto-generated method stub

            }
        });
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }



    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public RequestQueue getRequestQueueContext(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mAppContext = null;
        isAppOpen = false;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public RequestQueue getRequestQueue1() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(getApplicationContext().getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueueContext(Request<T> req, String tag, Context context) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueueContext(context).add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    private void initImageLoader(Context applicationContext) {
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(applicationContext);
        builder.threadPriority(Thread.NORM_PRIORITY);
        builder.denyCacheImageMultipleSizesInMemory();
        builder.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        builder.diskCacheSize(50 * 1024 * 1024);
        builder.tasksProcessingOrder(QueueProcessingType.LIFO);
        builder.writeDebugLogs();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(builder.build());
    }
}
