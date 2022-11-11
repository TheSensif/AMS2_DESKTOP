package database;

import java.io.File;
import java.sql.Connection;

public class Database {

    public static void startDatabase() {
        String filePath = System.getProperty("user.dir") + "/src/database/database.db";
        // Conectar

        File fDatabase = new File(filePath);
        if (!fDatabase.exists()) {
            Connection conn = UtilsSQLite.connect(filePath);

            UtilsSQLite.queryUpdate(conn,"CREATE TABLE IF NOT EXISTS users (" +
                    "nom varchar(15) PRIMARY KEY, password varchar(15));");

            UtilsSQLite.queryUpdate(conn, "INSERT INTO users (nom, password) VALUES (\"admin\",\"1234\");");

            UtilsSQLite.disconnect(conn);
        }

    }
}
