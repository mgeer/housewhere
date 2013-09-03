package com.sjl;
import com.sjl.estatesinsqllite.EstatesInDB;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2){
            System.out.println("usage: java -jar housewhere.gen.db.jar [db_location] [estates_in_file]");
            return;
        }
        String dbLocation = args[0];
        String estatesInFile = args[1];
        EstatesInDB estatesInDB = new EstatesInDB(dbLocation);
        boolean result = estatesInDB.create(estatesInFile);
        if (result){
            System.out.println("create db success!");
        }
        else {
            System.out.println("create db failed!");
        }
    }
}
