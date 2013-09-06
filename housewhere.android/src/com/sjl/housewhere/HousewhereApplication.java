package com.sjl.housewhere;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HousewhereApplication extends Application {

    BMapManager mBMapManager = null;

    @Override
    public void onCreate() {
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.

        initEngineManager(this);
    }

    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }


        if (!mBMapManager.init(getKey(), new MyGeneralListener(context))) {
            Toast.makeText(context, "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }

    }

    private String getKey() {
        AssetManager assets = getAssets();
        try{
            InputStream inputStream = assets.open("license");
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            String license = bufferedReader.readLine();
            inputStream.close();
            streamReader.close();
            bufferedReader.close();
            return license;
        }
        catch (IOException e) {
            Log.e("License", "get license failed!");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return "";
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    class MyGeneralListener implements MKGeneralListener {

        private Context context;

        public MyGeneralListener(Context context) {

            this.context = context;
        }

        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(context, "您的网络出错啦！", Toast.LENGTH_LONG).show();
            } else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(context, "输入正确的检索条件！", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                Toast.makeText(context, "请在 DemoApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
            }
        }
    }
}
