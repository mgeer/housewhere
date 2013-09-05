package com.sjl.estatesinsqllite;

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.sql.*;

public class EstatesInDBTest {
    String testDBLocation = "test\\estates.test.db";
    String estatesInFixture[][] = {
            {"天通苑东一区","20820","480000","116.443016","40.073729"},
            {"天通西苑三区","19820","490000","116.415902","40.082007"},
            {"荣丰2008","48801","114000","116.341507","39.899633"},
            {"天通苑中苑","22043","480000","115.893421","28.665621"},
            {"天通苑北一区","22465","480000","116.42386","40.081124"}};

    @Test
    public void testCreateDBEstates() throws Exception {
        EstatesInDB estatesInDB = new EstatesInDB(testDBLocation);
        Assert.assertEquals(true, estatesInDB.create("test\\fixture\\empty_estates.txt"));
        assertEstatesInDB(new String[0][0]);
    }

    @Test
    public void testImportEstatesInGivenFile() throws SQLException, ClassNotFoundException {
        new File(testDBLocation).delete();
        EstatesInDB estatesInDB = new EstatesInDB(testDBLocation);
        boolean ret = estatesInDB.create("test\\fixture\\estates.txt");
        Assert.assertEquals(true, ret);
        assertEstatesInDB(estatesInFixture);
    }

    @Test
    public void testDropThenInsertIfDBExists() throws SQLException, ClassNotFoundException {
        EstatesInDB estatesInDB = new EstatesInDB(testDBLocation);
        estatesInDB.create("test\\fixture\\estates.txt");
        boolean ret = estatesInDB.create("test\\fixture\\estates.txt");
        Assert.assertEquals(true, ret);
        assertEstatesInDB(estatesInFixture);
    }

    private void assertEstatesInDB(String[][] estates) throws ClassNotFoundException, SQLException {
        Connection connection;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", testDBLocation));
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM estates;");
        int resultLength = 0;
        while (rs.next()){
//            Assert.assertEquals(estates[resultLength][0], rs.getString("name"));
            Assert.assertEquals(String.format("%s.0", estates[resultLength][1]), rs.getString("price"));

            resultLength ++;
        }
        Assert.assertEquals(estates.length, resultLength);
        statement.close();
        connection.close();
    }

}
