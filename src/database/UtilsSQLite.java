package database;
import com.password4j.Password;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

public class UtilsSQLite {
    public UtilsSQLite() throws NoSuchAlgorithmException {
    }

    //private static final String UNICODE_FORMAT = "UTF-8";




    public static Connection connect (String filePath) {
        Connection conn = null;

        try {
            String url = "jdbc:sqlite:" + filePath;
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("BBDD driver: " + meta.getDriverName());
            }
            System.out.println("BBDD SQLite connectada");
        } catch (SQLException e) { e.printStackTrace(); }

        return conn;
    }

    public static void disconnect (Connection conn ) {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("DDBB SQLite desconnectada");
            }
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    public static int numeroAleatorioEnRango(int minimo, int maximo) {
        // nextInt regresa en rango pero con límite superior exclusivo, por eso sumamos 1
        return ThreadLocalRandom.current().nextInt(minimo, maximo + 1);
    }

    public static String cadenaAleatoria() {
        // El banco de caracteres
        String banco = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        // La cadena en donde iremos agregando un carácter aleatorio
        String cadena = "";
        for (int x = 0; x < 6; x++) {
            int indiceAleatorio = numeroAleatorioEnRango(0, banco.length() - 1);
            char caracterAleatorio = banco.charAt(indiceAleatorio);
            cadena += caracterAleatorio;
        }
        return cadena;
    }

    //static String pwdSalt = "ac4re21";
    //static String pwdPepper = "pepperCool";

    public static String encrypt(Connection conn,String input) {
        String pwdSalt = cadenaAleatoria();
        String pwdPepper = cadenaAleatoria();

        UtilsSQLite.queryUpdate(conn, "INSERT INTO salts (salt) VALUES (\""+pwdSalt+"\");");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO pepers (peper) VALUES (\""+pwdPepper+"\");");

        String has = Password.hash(input).addSalt(pwdSalt).addPepper(pwdPepper).withArgon2().getResult();
        return has;
    }

    public static boolean decrypt(Connection conn,int num, String input , String chiferText) throws SQLException {
        String pwdSalt = UtilsSQLite.querySelect(conn, "SELECT * FROM salts where id = "+ num +";").getString(2);
        String pwdPepper = UtilsSQLite.querySelect(conn, "SELECT * FROM pepers where id = "+ num +";").getString(2);

        boolean veri = Password.check(input, chiferText).addSalt(pwdSalt).addPepper(pwdPepper).withArgon2();
        return veri;
    }

    public static ArrayList<String> listTables (Connection conn) {
        ArrayList<String> list = new ArrayList<>();
        try {
            ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
            while (rs.next()) {
                list.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
        return list;
    }

    public static int queryUpdate (Connection conn, String sql) {
        int result = 0;
        try {
            Statement stmt = conn.createStatement();
            result = stmt.executeUpdate(sql);
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    public static ResultSet querySelect (Connection conn, String sql) {
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) { e.printStackTrace();System.out.println("ERROR SQLUTILS"); }
        return rs;
    }
}
