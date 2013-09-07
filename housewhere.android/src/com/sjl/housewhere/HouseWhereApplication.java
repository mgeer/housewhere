package com.sjl.housewhere;


import android.app.Application;
import com.baidu.mapapi.BMapManager;

public class HouseWhereApplication extends Application {

    public BMapManager getMapManager() {
        return mapManager;
    }

    private BMapManager mapManager;

    @Override
    public void onCreate() {
        mapManager = new BMapManager(this);
        mapManager.init("A7fceec52518d6b3689b1536d5d0b5b4", null);
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
