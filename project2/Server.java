// The server class will implement the functions listed in the project description. 

import java.io.*;
import java.net.*;
import java.util.*;

/*DONE: Initialization. The server first creates a socket, binds it to port 5000 (5000 is the so-called "well-known" 
//server listening port for our project), and calls the accept() function to wait for connection requests from this socket
*/

/*TODO: Accept requests. If there is a connection request, the server calls accept() to accept the request. The integer returned by accept() 
// is the socket id through which server will communicate with this client. The server then reads the message the client sends which contains 
 the client’s ID, listening port, and file vector.
*/

/*TODO: Answer client queries. If a client sends the server a query for a file, server first prints out the client’s ID, IP address (the 
client’s IP address can be found when calling the accept() function and can be later stored, it can be found as an attribute of the 
clientSocket object), and the file index the client is asking for. The server then prints out the IDs of all clients who have the file 
(who have reported to the server by this time) and sends this list to the requesting client.
*/


/*TODO:Respond to user command. The server receives only 2 types of command from the user, "q" or "p"
If the user types in "q", the server sends to all clients a "quit" message and waits until all clients have closed their connections with the server, after which it will exit.
If the user types in "p", the server will print a list of client id's and the file vectors that are currently active.
*/

/*TODO:
 * Respond to client quit message. If a client sends a "quit" message to the server, the server prints out a message like "(client ID) 
 * at (IP address) wishes to quit,” then closes the connection with this client.
 */


public class Server {

	int serverPort;
	int MAX_CONNECTED_CLIENTS;
	ServerSocket listener;
	int numClients;
	ArrayList<Connection> connectionList;

	public Server() {
		serverPort = 5000;
		MAX_CONNECTED_CLIENTS = 20;
		listener = null;
		numClients = 0;
		connectionList = new ArrayList<Connection>();
	}

	public static void main(String args[]) {

		// First, let's start our server and bind it to a port(5000).

		Server s = new Server();
		try {
			s.listener = new ServerSocket(5000);
			// Next let's start a thread that will handle incoming connections

			Socket client = s.listener.accept();
			new Thread(new Connection(client, s.connectionList)).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Note in programs shown in class, at this point we listen for incoming
		// connections in the main method. However for this project since the server has
		// to handle incoming connections and also handle user input simultaneously, we
		// start a separate thread to listen for incoming connections in the Server.
		// This is the ServerSocketHandler thread, which will in turn spawn new
		// Connection Threads, for each client connection.

		// Done! Now main() will just loop for user input!.
		while (true) {

			// wait on user inputs

		}
		// will quit on user input

	}

	// add other methods as necessaryu. For example, you will prbably need a method
	// to print the incoming connection info.
}
