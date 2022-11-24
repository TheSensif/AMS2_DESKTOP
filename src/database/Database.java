package database;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

public class Database {

    public static void startDatabase() throws SQLException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String filePath = System.getProperty("user.dir") + "/src/database/database.db";
        // Conectar

        File fDatabase = new File(filePath);
        if (!fDatabase.exists()) {
            Connection conn = UtilsSQLite.connect(filePath);

            // Create tables for users

            UtilsSQLite.queryUpdate(conn,"CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT,name varchar(15) NOT NULL UNIQUE, password varchar(500));");

            UtilsSQLite.queryUpdate(conn,"CREATE TABLE IF NOT EXISTS salts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, salt varchar(500));");

            UtilsSQLite.queryUpdate(conn,"CREATE TABLE IF NOT EXISTS pepers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, peper varchar(500));");

            // Create tables for SNAPTSHOTS


            // date: strftime('%Y-%m-%d %H:%M')

            UtilsSQLite.queryUpdate(conn,"CREATE TABLE IF NOT EXISTS snaptshots (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT,json JSON NOT NULL,date TIMESTAMP NOT NULL,name varchar(15) NOT NULL UNIQUE);");

            // Create user

            UtilsSQLite.queryUpdate(conn, "INSERT INTO users (name, password) VALUES (\"admin\",\"" + UtilsSQLite.encrypt(conn,"1234") + "\");");

            //System.out.println(UtilsSQLite.decrypt(conn,UtilsSQLite.querySelect(conn, "SELECT id FROM users;").getInt(1),"1234",UtilsSQLite.querySelect(conn, "SELECT password FROM users;").getString(1)));
            UtilsSQLite.disconnect(conn);
        }

    }
}
