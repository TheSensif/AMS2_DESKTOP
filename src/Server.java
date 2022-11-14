import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

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