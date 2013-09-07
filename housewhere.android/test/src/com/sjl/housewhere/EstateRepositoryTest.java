package com.sjl.housewhere;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;
import com.sjl.housewhere.database.AssetsDatabaseManager;
import com.sjl.housewhere.model.Estate;
import com.sjl.housewhere.model.EstateRepository;
import junit.framework.Assert;

import java.util.List;


public class EstateRepositoryTest extends AndroidTestCase {
    private String testDatabaseFile = "empty_estates.db";

    @Override
    protected void setUp() throws Exception {
        SQLiteDatabase database = clearEstatesTable();
        insertFixture(database);
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
    }

    private void insertFixture(SQLiteDatabase database) {
        String estatesForFixture[][] = {
                {"天通苑东一区","20820","480000","116.443016","40.073729"},
                {"天通苑北一区","22465","480000","116.42386","40.081124"},
                {"天通西苑三区","19820","490000","116.415902","40.082007"},
                {"荣丰2008","48801","114000","116.341507","39.899633"},
                {"天通苑中苑","22043","480000","115.893421","28.665621"},
                {"经纬度为0的小区","22043","480000", "0","0"},
        };
        for(String[] estate : estatesForFixture){
            String sql = "INSERT INTO estates (name, price, area, longitude, latitude) " +
                    String.format("VALUES('%s',%s,%s,%s,%s)", estate[0], estate[1], estate[2], estate[3], estate[4]);
            database.execSQL(sql);
        }
    }

    private SQLiteDatabase clearEstatesTable() {
        AssetsDatabaseManager.initManager(this.getContext());
        AssetsDatabaseManager manager = AssetsDatabaseManager.getManager();
        SQLiteDatabase database = manager.getDatabase(testDatabaseFile);
        database.execSQL("DELETE FROM estates", new String[0]);
        return database;
    }


    //  -----------------------------------------------------------------
    //  |            |           | 40.082007  |            |            |
    //  -----------------------------------------------------------------
    //  |            | 40.081124 |            |            |            |
    //  -----------------------------------------------------------------
    //  | 40.073729  |           |            |            |            |
    //  -----------------------------------------------------------------
    //  | 116.443016 | 116.42386 | 116.415902 | 116.341507 | 115.893421 |   //经度
    //  -----------------------------------------------------------------
    //  |            |           |            | 39.899633  |            |
    //  -----------------------------------------------------------------
    //  |            |           |            |            | 28.665621  |
    //  -----------------------------------------------------------------

    public void testGetEstatesWithinLongitudeScope() throws Exception {
        EstateRepository estateRepository = new EstateRepository(this.getContext(), testDatabaseFile);
        List<Estate> estates = estateRepository.getEstatesByLongLatScope(116.443017, 116.443015, 40.073729, 40.073729);
        Assert.assertEquals(1, estates.size());
        Assert.assertEquals("天通苑东一区", estates.get(0).getName());
    }

    public void testGetEstatesWithinLatitudeScope() throws Exception {
        EstateRepository estateRepository = new EstateRepository(this.getContext(), testDatabaseFile);
        List<Estate> estates = estateRepository.getEstatesByLongLatScope(116.42386, 116.42386, 40.081125, 40.081123);
        Assert.assertEquals(1, estates.size());
        Assert.assertEquals("天通苑北一区", estates.get(0).getName());
    }
    public void testGetEstatesWithinLongLatScope() throws Exception {
        EstateRepository estateRepository = new EstateRepository(this.getContext(), testDatabaseFile);
        List<Estate> estates = estateRepository.getEstatesByLongLatScope(116.415902, 116.341507, 40.082007, 39.899633);
        Assert.assertEquals(2, estates.size());
        Assert.assertEquals("天通西苑三区", estates.get(0).getName());
        Assert.assertEquals("荣丰2008", estates.get(1).getName());
    }

    public void testGetAllEstates(){
        EstateRepository estateRepository = new EstateRepository(this.getContext(), testDatabaseFile);
        List<Estate> estates = estateRepository.getAllEstates();
        assertEquals(5, estates.size());
    }

    public void testEstatesArea(){
        EstateRepository estateRepository = new EstateRepository(this.getContext());
        List<Estate> estates = estateRepository.getEstatesWhoesAreaMoreThan1500000();
        for(Estate estate : estates){
            Log.i("debug area", estate.getName());
        }
    }
}
