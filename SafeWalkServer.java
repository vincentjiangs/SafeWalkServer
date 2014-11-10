import java.util.*;
import java.io.*;
import java.net.*;
public class SafeWalkServer {
private int port;
private ServerSocket serverSocket = null;
private ArrayList<Walker> a = new ArrayList(0);
private String[] locations = {"CL50", "EE", "LWSN", "PMU", "PUSH"};
	/*
	* Construct the server, set up the socket.
	*
	* @throws SocketException if the socket or port cannot be obtained properly. * @throws IOException if hte port cannot be reused.
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
				respondToLine(line, client);
			}
				catch (IOException e) {
			}
		}
	}
	void respondToLine(String line, Socket client) {
        try {
		    if (line.equals(":SHUTDOWN")) {
			    serverSocket.close();
			    System.exit(0);
		    }
		    else if (line.equals(":RESET")) {
		    	a = new ArrayList(0);
		    }
		    else if (line.equals(":LIST_PENDING_REQUESTS")) {
		    }
		    else {
		    	a.add(new Walker(line, client));
		    	pair();
		    }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	}
	public void pair() {
		boolean volunteer = a.get(a.size() - 1).isVolunteer();
		for (int i = 0; i < a.size() - 1; i++) {
			if (a.get(i).getFrom().equals(a.get(a.size() - 1).getFrom())) {
				if (a.get(i).isVolunteer() != volunteer) {
					response("RESPONSE: " + a.get(i).printWalker(), a.get(a.size() - 1).getSocket());
					response("RESPONSE: " + a.get(a.size() - 1).printWalker(), a.get(i).getSocket());
					a.remove(i);
					a.remove(a.size() - 1);
					return;
				}
			}
		}
	}
	public void response(String line, Socket client) {
        try {
	    	PrintWriter pw = new PrintWriter(client.getOutputStream());
	    	pw.printf("%s", line);
	    	pw.flush();
	    	pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
    public static void main(String[] args) {
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
	private int priority = 0;
	private Socket client = null;
	public Walker(String line, Socket client) {
		this.client = client;
		while (line.indexOf(",") != -1) {
			int counter = 0;
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
					return;
		}
		line = line.substring(line.indexOf(",") + 1);
		counter++;
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
		return String.format("%s,%s,%s,%d\n", this.name, this.from, this.to, this.priority);
	}
}
