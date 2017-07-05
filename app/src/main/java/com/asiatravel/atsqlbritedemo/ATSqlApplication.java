package com.asiatravel.atsqlbritedemo;

import android.app.Application;

import com.asiatravel.atsqlbritedemo.db.ATDbManager;

/**
 * Created by jsion on 16/5/18.
 */
public class ATSqlApplication extends Application {
    private static ATSqlApplication atSqlApplication;
    private ATDbManager atDbManager;

    @Override
    public void onCreate() {
        super.onCreate();
        atSqlApplication = this;
        atDbManager = new ATDbManager(this);
    }

    @Override
    public void onTerminate() {
        atSqlApplication = null;
        super.onTerminate();
    }

    public static ATSqlApplication getApplication() {
        return atSqlApplication;
    }

    public ATDbManager getAtDbManager() {
        return atDbManager;
    }

}
