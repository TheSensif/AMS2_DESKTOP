import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import database.UtilsSQLite;

// Compilar amb: 
// javac -cp "lib/*:." Servidor.java
// java -cp "lib/*:." Servidor

// Tutorials: http://tootallnate.github.io/Java-WebSocket/

public class Server extends WebSocketServer {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private ByteBuffer configurationData;
    private static boolean running = true;

    public static void main(String[] args)  throws InterruptedException, IOException {
        int port = 8888; 

        // Deshabilitar SSLv3 per clients Android
        java.lang.System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        Server socket = new Server(port);
        socket.start();
        System.out.println("Server working on port: " + socket.getPort());

        while (running) {
            String line = in.readLine();
            if (line.equals("exit")) {
                running = false;
            }
        }    

        System.out.println("Shutdown Server");
        socket.stop(1000);
    }

    public Server(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public Server(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // SEND INFO!!!!
        if(conn.getRemoteSocketAddress().getAddress().getHostAddress().equalsIgnoreCase("127.0.0.1")){
            System.out.println("YOU'RE THE DESKTOP!!!!");
            conn.send("Welcome Desktop");
        }
        else{
            conn.send("Connected");
        }
        // conn.send("Benvingut a WsServer"); 

        String host = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        System.out.println(host + " CONNECTED");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        System.out.println(conn + " DISCONNECTED");
    }
    @Override
    public void onMessage(WebSocket conn, String message){
        if(conn.getRemoteSocketAddress().getAddress().getHostAddress().equalsIgnoreCase("127.0.0.1")){
            System.out.println("YOU'RE THE DESKTOP!!!!"+message);
            System.out.println(message.equalsIgnoreCase("exit"));
            if(message.equalsIgnoreCase("exit")){
                
                System.out.println("si eres");
                
                
            }
        }
        else if(message=="requestConfiguration"){
            conn.send(this.configurationData);
        }else{
            String[] param=message.split("&");
            if(param.length>1){
                String basePath = System.getProperty("user.dir") + File.separator;
                String filePath = basePath + "database/database.db";
                Connection sqlite = UtilsSQLite.connect(filePath);
                ResultSet end = UtilsSQLite.querySelect(sqlite, "SELECT * FROM users where nom='"+param[0]+"' and password='"+param[1]+"';");
                
               
                try {
                    //end.getString(1).length()>0
                    int quantity = 0;
                    while (end.next()) {
                        quantity+=1;
                    }

                    if(quantity>0){
                        conn.send("OK");
                        System.out.println("OK");
                    }
                    else{
                        conn.send("ERROR");
                        System.out.println("ERROR");
                    }
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    conn.send("ERROR");
                }
                try {
                    sqlite.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            

        }
        System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress()+" sended a message");
    };
    
    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {

        if(conn.getRemoteSocketAddress().getAddress().getHostAddress().equalsIgnoreCase("127.0.0.1")){
            System.out.println("YOU'RE THE DESKTOP!!!!");
            this.configurationData=message;

        }
        else{
            System.out.println("Sending configuration...");
            // conn.send(this.configurationData);
        }

        
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        // Server starts
        System.out.println("'exit' for shutdown the server");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    public String getConnectionId (WebSocket connection) {
        String name = connection.toString();
        return name.replaceAll("org.java_websocket.WebSocketImpl@", "").substring(0, 3);
    }
}
