package com.enclaveit.trm;

import com.enclaveit.trm.controller.MainController;

import android.app.Application;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initController();
    }

    private void initController() {
        MainController.getInstance();
    }

}
