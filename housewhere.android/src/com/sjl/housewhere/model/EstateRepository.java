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
    private String databaseFile = "estates.db";



    public EstateRepository(Context application, String databaseFile) {
        this(application);
        this.databaseFile = databaseFile;
    }

    public EstateRepository(Context application) {
        AssetsDatabaseManager.initManager(application);
        assetsDatabaseManager = AssetsDatabaseManager.getManager();
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
        return assetsDatabaseManager.getDatabase(databaseFile);
    }

    public List<Estate> getEstatesByLongLatScope(double maxLong, double minLong, double maxLat, double minLat) {
        ArrayList<Estate> estates = new ArrayList<Estate>();
        SQLiteDatabase database = getDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM estates where longitude <= ? AND longitude >= ? AND latitude <= ? AND latitude >= ?",
                new String[]{
                        Double.toString(maxLong), Double.toString(minLong),
                        Double.toString(maxLat), Double.toString(minLat),
                });
        while (cursor.moveToNext()){
            Estate estate = createEstate(cursor);
            estates.add(estate);
        }
        return estates;
    }
}
