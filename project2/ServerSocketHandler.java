// This thread simply listens for connections on port 5000 and starts a new Connection Thread for each incoming connection
import java.io.*;
import java.net.*;
import java.util.*;

class ServerSocketHandler extends Thread
{

    Server s;
    ArrayList<Connection> connectionList;

    public ServerSocketHandler(Server s, ArrayList<Connection> connectionList){
        this.s=s;
        this.connectionList=connectionList;
    }

    public void run(){
        Socket clientSocket;
        while (true){
           // wait for incoming connections. Start a new Connection Thread for each incoming connection.
        	try {
    			
    			// Next let's start a thread that will handle incoming connections

    			clientSocket = s.listener.accept();
    			Connection c = new Connection(clientSocket, s.connectionList);
    			c.inputStream = new ObjectInputStream(c.inputStream);
    			c.outputStream = new ObjectOutputStream(c.outputStream);
    			s.connectionList.add(c);
    			new Thread(c).start();
    		} catch (IOException e) {
    			// DONE Auto-generated catch block
    			e.printStackTrace();
    		}
        }
    }
    
    //other methods may be necessary. Include them when appropriate.

}