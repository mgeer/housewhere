package com.sjl.estatesinsqllite;

import java.io.*;
import java.sql.*;

public class EstatesInDB {
    private String dbLocation;

    public EstatesInDB(String dbLocation) {
        this.dbLocation = dbLocation;
    }

    public boolean create(String estatesInFile) {
        try {
            Class.forName("org.sqlite.JDBC");
            String connectionString = String.format("jdbc:sqlite:%s", dbLocation);
            Connection connection = DriverManager.getConnection(connectionString);
            Statement statement = connection.createStatement();
            createTableEstates(statement);
            insertEstates(estatesInFile, statement);
            statement.close();
            connection.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            return false;
        }
        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    private void insertEstates(String estatesInFile, Statement statement) {
        try {
            FileInputStream inputStream = new FileInputStream(estatesInFile);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            while( (line = reader.readLine()) != null){
                String[] items = line.split(",");
                if ( items.length != 5 ){
                    continue;
                }
                String sql = "INSERT INTO estates (name, price, area, longitude, latitude) " + "" +
                        String.format("VALUES('%s',%s,%s,%s,%s)",
                                items[0], items[1], items[2], items[3], items[4]);
                log("executing:" + sql);
                statement.executeUpdate(sql);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void createTableEstates(Statement statement) {
        String dropSql = "DROP TABLE IF EXISTS estates";

        String sql = "CREATE TABLE IF NOT EXISTS estates" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT     NOT NULL," +
                " name           TEXT    NOT NULL, " +
                " price          REAL    NOT NULL, " +
                " area           INT     NOT NULL, " +
                " longitude      REAL    NOT NULL, " +
                " latitude       REAL    NOT NULL)";
        try {
            log("executing: " + dropSql);
            statement.executeUpdate(dropSql);

            log("executing:" + sql);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void log(String log) {
        System.out.println(log);
    }
}
