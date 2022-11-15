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

            UtilsSQLite.queryUpdate(conn,"CREATE TABLE IF NOT EXISTS users (" +
                    "name varchar(15) PRIMARY KEY, password varchar(15));");

            UtilsSQLite.queryUpdate(conn, "INSERT INTO users (name, password) VALUES (\"admin\",\"" + UtilsSQLite.encrypt("1234") + "\");");

            System.out.println(UtilsSQLite.querySelect(conn, "SELECT * FROM users;").getString(2));
            System.out.println(UtilsSQLite.decrypt(UtilsSQLite.querySelect(conn, "SELECT password FROM users;").getString(1)));
            UtilsSQLite.disconnect(conn);
        }

    }
}
