/**
 * CS 180 - Project 5 - SafeWalkServerPhase1
 * 
 * Implements and expands a server for the Purdue Safe Walk Program. 
 * 
 * @author Vincent Jiang <jiangv@purdue.edu>
 * @author Sameer Manchanda <smanchan@purdue.edu>
 * 
 * @lab Vincent Jiang: Section 811
 * @lab Sameer Manchanda: Section 817
 *
 * @date November 11, 2014
 */

import java.util.*;
import java.io.*;
import java.net.*;
public class SafeWalkServer implements Runnable {
    private int port;
    private ServerSocket serverSocket = null;
    private ArrayList<Walker> a = new ArrayList<Walker>();
    private String[] locations = {"CL50", "EE", "LWSN", "PMU", "PUSH"};
    private boolean keepRun = true;
    /*
     * Construct the server, set up the socket.
     *
     * @throws SocketException if the socket or port cannot be obtained properly.    * @throws IOException if hte port cannot be reused.
     */
    public SafeWalkServer(int port) throws SocketException, IOException {
        if (port >= 1025 && port <= 65535) {
            this.port = port;
            this.serverSocket = new ServerSocket(port);
        }
        else {
            System.out.printf("Port %d is invalid\n", port);
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
        while (keepRun) {
            try {
                Socket client = serverSocket.accept();
                client.setReuseAddress(true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String line = in.readLine();
                respondToLine(line, client);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void respondToLine(String line, Socket client) {
        try {
            if (line.equals(":SHUTDOWN")) {
                for (int i = 0; i < a.size(); i++) {
                    response("ERROR: connection reset\n", a.get(i).getSocket());
                    // System.out.printf("%d\n", a.size());
                    // System.out.printf("%d\n", i);
                }
                response("RESPONSE: success\n", client);
                for (int j = 0; j < a.size(); j++) {
                    a.get(j).getSocket().close();
                }
                client.close();
                serverSocket.setReuseAddress(true);
                serverSocket.close();
                keepRun = false;
            }
            else if (line.equals(":RESET")) {
                for (int i = 0; i < a.size(); i++) {
                    response("ERROR: connection reset", a.get(i).getSocket());
                }
                response("RESPONSE: success", client);
                for (int j = 0; j < a.size(); j++) {
                    a.get(j).getSocket().close();
                }
                client.close();
                a.clear();
            }
            else if (line.equals(":LIST_PENDING_REQUESTS")) {
                String s = "";
                for (int i = 0; i < a.size() - 1; i++) {
                    s += "[" + a.get(i).printWalker() + "], ";
                }
                s += "[" + a.get(a.size() - 1).printWalker() + "]"; 
                s = "[" + s + "]";
                response(s, client);
                client.close();
            }
            else {
                if (line.charAt(0) == ':') {
                    response("ERROR: invalid request\n", client);
                    client.setReuseAddress(true);
                    client.close();
                    return;
                }
                else {
                    boolean validLocation = false;
                    Walker b = new Walker (line, client);
                    if (b.getName().equals("")) {
                        response("ERROR: invalid request\n", client);
                        client.setReuseAddress(true);
                        client.close();
                        return;
                    }
                    for (int i = 0; i < locations.length; i++) {
                        if (locations[i].equals(b.getFrom())) { 
                            validLocation = true;
                        }
                    }
                    if (validLocation == false) {
                        response("ERROR: invalid request\n", client);
                        client.setReuseAddress(true);
                        client.close();
                        return;
                    }
                    validLocation = false;
                    for (int i = 0; i < locations.length; i++) {
                        if (locations[i].equals(b.getTo())) {
                            validLocation = true;
                        }
                    }
                    if (b.getTo().equals("*")) {
                        validLocation = true;
                    }
                    if (validLocation == false) {
                        response("ERROR: invalid request\n", client);
                        client.setReuseAddress(true);
                        client.close();
                        return;
                    }
                    if (b.getPriority() == -1) {
                        response("ERROR: invalid request\n", client);
                        client.setReuseAddress(true);
                        client.close();
                        return;
                    }
                    if (b.getFrom().equals(b.getTo())) {
                        response("ERROR: invalid request\n", client);
                        client.setReuseAddress(true);
                        client.close();
                        return;
                    }
                    if (true) {
                        a.add(b);
                        // System.out.printf("size: %d\n", a.size());
                        pair();
                        // System.out.printf("size2: %d\n", a.size());
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void pair() {
        try {
            boolean volunteer = a.get(a.size() - 1).isVolunteer();
            for (int i = 0; i < a.size() - 1; i++) {
                if (a.get(i).getFrom().equals(a.get(a.size() - 1).getFrom())) {
                    if (a.get(i).isVolunteer() != volunteer) {
                        response("RESPONSE: [" + a.get(i).printWalker() + "]\n", a.get(a.size() - 1).getSocket());
                        response("RESPONSE: [" + a.get(a.size() - 1).printWalker() + "]\n", a.get(i).getSocket());
                        a.get(i).getSocket().setReuseAddress(true);
                        a.get(a.size() - 1).getSocket().setReuseAddress(true);
                        a.get(i).getSocket().close();
                        a.get(a.size() - 1).getSocket().close();
                        a.remove(i);
                        a.remove(a.size() - 1);
                        return;
                    }
                    if (a.get(i).getTo().equals(a.get(a.size() - 1).getTo())) {
                        if (a.get(i).getTo().equals("*") || a.get(a.size() - 1).getTo().equals("*")) {
                        }
                        else {
                        response("RESPONSE: [" + a.get(i).printWalker() + "]\n", a.get(a.size() - 1).getSocket());
                        response("RESPONSE: [" + a.get(a.size() - 1).printWalker() + "]\n", a.get(i).getSocket());
                        a.get(i).getSocket().setReuseAddress(true);
                        a.get(a.size() - 1).getSocket().setReuseAddress(true);
                        a.get(i).getSocket().close();
                        a.get(a.size() - 1).getSocket().close();
                        a.remove(i);
                        a.remove(a.size() - 1);
                        return;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
        }
    }
    public void response(String line, Socket client) {
        try {
            PrintWriter pw = new PrintWriter(client.getOutputStream());
            pw.printf("%s", line);
            pw.flush();
            client.setReuseAddress(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception {
        try {
            SafeWalkServer s = new SafeWalkServer();
            s.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class Walker {
    private String name = "";
    private String from = "";
    private String to = "";
    private int priority = -1;
    private Socket client = null;
    public Walker(String line, Socket client) {
        this.client = client;
        int counter = 0;
        while (line.indexOf(",") != -1) {
            switch(counter) {
                case 0:
                    this.name = line.substring(0, line.indexOf(","));
                    break;
                case 1:
                    this.from = line.substring(0, line.indexOf(","));
                    break;
                case 2:
                    this.to = line.substring(0, line.indexOf(","));
                    break;
                default:
                    break;
            }
            line = line.substring(line.indexOf(",") + 1, line.length());
            counter++;
        }
        try {
        priority = Integer.parseInt(line);
        }
        catch (Exception e) {
        }
    }
    public String getName() {
        return this.name;
    }
    public String getFrom() {
        return this.from;
    }
    public String getTo() {
        return this.to;
    }
    public int getPriority() {
        return this.priority;
    }
    public Socket getSocket() {
        return this.client;
    }
    public boolean isVolunteer() {
        if (getTo().equals("*"))
            return true;
        else
            return false;
    }
    public String printWalker() {
        return this.name + ", " + this.from + ", " + this.to + ", " + this.priority;
    }
}
