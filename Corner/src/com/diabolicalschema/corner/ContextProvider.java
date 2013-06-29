package com.diabolicalschema.corner;

import android.app.Application;
import android.content.Context;

/** ContextProvider
 * 
 * This is just a handy place to grab the application context so I can grab it when I want
 * from any other part of the program without having to explicitly pass it everywhere.
 * 'Cause that's lame.
 * 
 * Also, it works with static classes and utility classes that don't inherit from Activity.
 * 
 * Anywhere you need context to store settings or whatever, use:
 * ContextProvider.getContext();
 * 
 * @author android
 *
 */
public class ContextProvider extends Application {

	/**
     * Keeps a reference of the application context
     */
    private static Context sContext;
 
    @Override
    public void onCreate() {
        super.onCreate();
 
        sContext = getApplicationContext();
 
    }
 
    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }

}
