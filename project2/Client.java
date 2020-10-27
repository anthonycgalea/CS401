// The client class will implemnet the functions listed in the project description. 
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.*; 

/*
 * TODO:
 * Initialization. The client will be invoked with a command line which contains two arguments. The first argument may be 
 * the server’s IP address in dotted decimal notation (127.0.0.1) or the server’s domain name (localhost). The second 
 * argument is the name of a config file which the client reads to find its ID, the server’s listening port, its own listening 
 * port, and its initial file vector; the client should print out such information. The config files are different for different 
 * clients and will be supplied by me. For example, the client program can be run by using the following command. java Client 
 * localhost clientconfig1.txt (Note: this is slightly different from the way I start the client in the demo video, but stick to this notation)
 * 
 * 
 * TODO: 
 * Connect to the server. The client tries to connect to the server by first creating a socket and supplying the server IP and port number.
 * If the connection is successful, the client prints out a success message; otherwise it quits. In this assignment, all connections are TCP.
 * 
 * TODO:
 * Report to the server. Once connection has been established, the client then sends the server a message with information of its id, its 
 * listening port, and the file vector. The client does this by creating a Packet object (attributes specified in the supplied Packet class)
 *  and send the packet to the server.
 * 
 * TODO: 
 * Wait for commands. The client enters an infinite loop. The client will get user inputs and may also get messages from the server. It accepts two commands, "f" and "q":
 * 
 * If the user types "f", the client will ask which file the user wants and the user will input the file index. If the user inputs, say, 10, the client first checks if it 
 * has file 10. If yes, it prints out a message like "I already have file 10." Otherwise, it sends a message to the server requesting the ID of a client who has file 10. 
 * The server will reply with the ID of a client who has file 10 (-1 if no client has the file). After receiving the server’s message, the client prints out the id of the 
 * client who has the file, then waits for the next command.
 * 
 * If the user types "q", the client quits: a) it first sends the server a message saying that it wishes to quit, b) waits until server closes the connection, c) terminate the program.
 * 
 * TODO: 
 * Auxiliary functions. The client also reads commands from the server. For this project there is only one command: quit. If the server sends the client this command, 
 * the client will close the connection by calling the close() function and exit.
 * 
 * 
 * 
 * 
 */
public class Client {

     int serverPort = 5000;
     InetAddress ip=null; 
     Socket s; 
     ObjectOutputStream outputStream ;
     ObjectInputStream inputStream ;
     int peerID;
     int peer_listen_port;
     char FILE_VECTOR[];
     

    public static void main(String args[])
    {
        // parse client config and server ip.
        // create client object and connect to server. If successfull, print success message , otherwise quit.
    	Client c = new Client();
    	try {
        c.s = new Socket(args[0], 5000);
    	} catch (IOException e) {
    		c.s = null;
    	}
        // Once connected, send registration info, event_type=0
       // start a thread to handle server responses. This class is not provided. You can create a new class called ClientPacketHandler to process these requests.
       
        //done! now loop for user input
            while (true){
                
               // wait for user commands.
        }
       
    }

 
    // implement other methods as necessary

}
