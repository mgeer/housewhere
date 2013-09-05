package com.sjl.housewhere.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.sjl.housewhere.database.AssetsDatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class EstateRepository {
    private final AssetsDatabaseManager assetsDatabaseManager;

    public EstateRepository(Context application) {
        AssetsDatabaseManager.initManager(application);
        assetsDatabaseManager = AssetsDatabaseManager.getManager();
    }

    public List<Estate> getFiveEstates() {
        SQLiteDatabase database = getDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM estates LIMIT 5", new String[0]);
        Log.i("db", "trying to access rows in db!");
        ArrayList<Estate> estates = new ArrayList<Estate>();
        while (cursor.moveToNext()) {
            Estate estate = createEstate(cursor);
            estates.add(estate);
            Log.i("db", estate.toString());
        }
        return estates;
    }

    private Estate createEstate(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex("name"));
        double price = cursor.getDouble(cursor.getColumnIndex("price"));
        int area = cursor.getInt(cursor.getColumnIndex("area"));
        double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
        double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
        return new Estate(name, price, area, longitude, latitude);
    }

    private SQLiteDatabase getDatabase() {
        return assetsDatabaseManager.getDatabase("estates.db");
    }
}
