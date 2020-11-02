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
    			clientSocket = s.listener.accept(); //wait for new connection
    			Connection c = new Connection(clientSocket, s.connectionList); //create connection object
    			s.connectionList.add(c); //add to ConnectionList
    			new Thread(c).start(); //start thread with connection
    		} catch (IOException e) {
    			// DONE Auto-generated catch block
    			e.printStackTrace();
    		}
        }
    }

}