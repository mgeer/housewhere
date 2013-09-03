package com.sjl.housewhere;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AssetsDatabaseManager.initManager(getApplication());
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase database = mg.getDatabase("estates.db");
        Cursor cursor = database.rawQuery("SELECT * FROM estates LIMIT 5", new String[0]);
        Log.i("db", "trying to access rows in db!");
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            Log.i("db", name);
        }
        database.close();
        setContentView(R.layout.main);

    }
}
