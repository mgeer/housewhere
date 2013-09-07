package com.sjl.housewhere;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainUI extends Activity {

    Button mBtnCityView = null;
    Button mBtnBridgeUs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ui);

        mBtnCityView = (Button)findViewById(R.id.cityview);
        mBtnBridgeUs = (Button)findViewById(R.id.bridge);

        OnClickListener clickListener = new OnClickListener(){
            public void onClick(View v) {
                ShowChildView(v);
            }
        };

        mBtnCityView.setOnClickListener(clickListener);
        mBtnBridgeUs.setOnClickListener(clickListener);
    }

    void ShowChildView(View v) {
        if (mBtnCityView.equals(v)) {
            Intent intent = new Intent();
            intent.putExtra("textintent", "��ӭ���뵽�ڶ���activity");
            intent.setClass(MainUI.this, EstatesInCityActivity.class);
            this.startActivity(intent);
        } else if (mBtnBridgeUs.equals(v)) {
            Intent intent = new Intent();
            intent.putExtra("textintent", "��ӭ���뵽�ڶ���activity");
            intent.setClass(MainUI.this, MainActivity.class);
            this.startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
