import java.util.ArrayList;
import java.io.*;
import java.net.*;

public class SafeWalkServer {
    private int port;
    private ServerSocket serverSocket = null;
    static ArrayList<String> a = new ArrayList(0);
    static String[] locations = {"CL50", "EE", "LWSN", "PMU", "PUSH"};

    /*
     * Construct the server, set up the socket.
     *
     * @throws SocketException if the socket or port cannot be obtained properly.      * @throws IOException if hte port cannot be reused.
     */
    public SafeWalkServer(int port) throws SocketException, IOException {
        if (port >= 1025 && port <= 65535) {
            this.port = port;
            this.serverSocket = new ServerSocket(port);
        }
        else {
            System.out.printf("Port %d is invalid\n", port);
            System.exit(0);
        }
    }
    /**
     * Construct the server and let the system allocate it a port.
     * 
     * @throws SocketException if the socket or port cannot be obtained properly.
     * @throws IOException if the port cannot be reused.
     */
    public SafeWalkServer() throws SocketException, IOException {
        this.port = 8888;
        this.serverSocket = new ServerSocket(port);
    }
    /**
     * Return the port number on which the server is listening.
     */
    public int getLocalPort() {
        return this.port;
    }
    /**
     * Start a loop to accept incoming connections.
     */
    public void run() {
        while (true) {
            try {
                Socket client = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String line = in.readLine();
                respondToLine(line);
            }
            catch (IOException e) {
                
            }
        }
    }
    static void respondToLine(String line) {
        if (line.equals(":SHUTDOWN")) {
            serverSocket.close();
            System.exit(0);
        }
        else if (line.equals(":
    }
}
