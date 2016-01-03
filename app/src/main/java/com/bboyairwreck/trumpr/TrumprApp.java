package com.bboyairwreck.trumpr;

import android.app.Application;

/**
 * Created by eric on 1/2/16.
 */
public class TrumprApp extends Application {
    public static final String TAG = TrumprApp.class.getSimpleName();
    public static TrumprApp instance;

    public static TrumprApp getInstance() {
        if (instance == null) {
            instance = new TrumprApp();
        }
        return instance;
    }

    public TrumprApp() {

    }
}
