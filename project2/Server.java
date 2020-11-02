// The server class will implement the functions listed in the project description. 

import java.io.*;
import java.net.*;
import java.util.*;


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

		Server s = new Server(); //create server object
		try {
			s.listener = new ServerSocket(5000); //create listener on server
			new Thread(new ServerSocketHandler(s, s.connectionList)).start(); //create thread to listen to connections
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		// Note in programs shown in class, at this point we listen for incoming
		// connections in the main method. However for this project since the server has
		// to handle incoming connections and also handle user input simultaneously, we
		// start a separate thread to listen for incoming connections in the Server.
		// This is the ServerSocketHandler thread, which will in turn spawn new
		// Connection Threads, for each client connection.

		// Done! Now main() will just loop for user input!.
		boolean notQuit = true;
		Scanner sc = new Scanner(System.in); //take input
		char command;
		while (notQuit) {

			// wait on user inputs
			String input = sc.nextLine();
    		if (input.length() >= 1) { //so blank entries don't break it
    			command = input.charAt(0);
    		} else {
    			System.out.println("Invalid command. Please try again. Valid commands:\nf:\trequest file\nq:\tquit");
    			sc.nextLine();
    			command = 'a';
    		}
			// wait for user commands.
			switch (command) {
				case 'q': { //quit
					for (int i = 0; i < s.connectionList.size(); i++) {
						s.connectionList.get(i).closeConnection();
					}
					System.exit(0); //kill program
					notQuit = false;
					break;
				}
				case 'p': { //print active connections
					System.out.println("Clients connected:");
					for (int i = 0; i < s.connectionList.size(); i++) {
						if (s.connectionList.get(i).socket.isConnected()) {
							String str = "";
							for (int j = 0; j < s.connectionList.get(i).FILE_VECTOR.length; j++) {
								str+=s.connectionList.get(i).FILE_VECTOR[j];
							}
							System.out.println("User id:\t" + s.connectionList.get(i).peerID + "\tFile Vector:\t" + str);
						}
					}
					break;
				}
			}
		}
		// will quit on user input
		sc.close();

	}

	// add other methods as necessary. For example, you will probably need a method
	// to print the incoming connection info.
}
