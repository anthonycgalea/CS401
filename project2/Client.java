// The client class will implement the functions listed in the project description. 
import java.io.*;
import java.net.*;
import java.util.*; 

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
    	Client c = new Client();
    	String ip = args[0];
    	try {
    		File f = new File(args[1]);
    		Scanner fileRead = new Scanner(f);
    		while(fileRead.hasNext()) {
    			String s = fileRead.nextLine();
    			if (s.split(" ")[0].equals("CLIENTID")) {
    				c.peerID = Integer.parseInt(s.split(" ")[1]);
    			} else if (s.split(" ")[0].equals("SERVERPORT")) {
    				c.serverPort = Integer.parseInt(s.split(" ")[1]);
    			} else if (s.split(" ")[0].equals("MYPORT")) {
    				c.peer_listen_port = Integer.parseInt(s.split(" ")[1]);
    			} else if (s.split(" ")[0].equals("FILE_VECTOR")) {
    				c.FILE_VECTOR = s.split(" ")[1].toCharArray();
    			}
    		}
    		fileRead.close();
    	} catch (IOException e) {
    		System.out.println("Invalid file. Please try again. Proper notation is java Client [IP Address] [config file]");
    		return;
    	}
        // create client object and connect to server. If successful, print success message , otherwise quit.
    	try {
    		c.s = new Socket(ip, c.serverPort);
    		c.outputStream = new ObjectOutputStream(c.s.getOutputStream());
    		c.inputStream = new ObjectInputStream(c.s.getInputStream());
    		System.out.println("Connected successfully!");
    		
    	} catch (IOException e) {
    		c.s = null;
    		System.out.println("Invalid Socket Attempt. Please try again. Proper notation is java Client [IP Address] [config file]");
    		return;
    	}
        // Once connected, send registration info, event_type=0
    	Packet p = new Packet();
    	p.event_type = 0;
    	p.FILE_VECTOR = c.FILE_VECTOR;
    	p.peer_listen_port = c.peer_listen_port;
    	p.sender = c.peerID;
    	try {
    		c.outputStream.flush();
    		c.outputStream.reset();
    		c.outputStream.writeObject(p);
    		c.outputStream.flush();
    		c.outputStream.reset();
    	} catch (IOException e) {
    		System.out.println("Error sending packet.");
    		return;
    	}
    	
    	//DONE: start a thread to handle server responses. This class is not provided. You can create a new class called ClientPacketHandler to process these requests.
    	ClientPacketHandler cph = new ClientPacketHandler(c);
    	new Thread(cph).start();
        //done! now loop for user input
        Scanner sc = new Scanner(System.in);
        boolean noQuit = true;
        char command;
    	while (noQuit && cph.killProgram == false){
    		String input = sc.nextLine();
    		if (input.length() >= 1) {
    			command = input.charAt(0);
    		} else {
    			System.out.println("Invalid command. Please try again. Valid commands:\nf:\trequest file\nq:\tquit");
    			sc.nextLine();
    			command = 'a';
    		}
               // wait for user commands.
               switch (command)
               {
                   
                   case 'f': {// user is requesting a file 
                	   int index = -1;
                	   do {
                		   System.out.print("\nPlease enter file index requested:\t");
                		   index = Integer.parseInt(sc.nextLine());
                	   } while (index <0 && index >= c.FILE_VECTOR.length);
                	   if (c.containsFile(index) == c.peerID) {
                		   System.out.println("I already have file " + index);
                	   } else {
                		   System.out.println("Requesting file " + index);
                		   Packet requestPacket = new Packet();
                		   requestPacket.event_type = 1;
                		   requestPacket.sender = c.peerID;
                		   requestPacket.req_file_index = index;
                		   try {
	                		   c.outputStream.flush();
	                		   c.outputStream.reset();
	                		   c.outputStream.writeObject(requestPacket);
	                		   c.outputStream.flush();
	                		   c.outputStream.reset();
                		   } catch (IOException e) {
                			   e.printStackTrace();
                			   System.out.println("Error.");
                		   }
                	   }
                	   break;
                   }

                   case 'q': {// user wants to quit
                	   noQuit=false;
                	   System.out.println("Quitting");
            		   Packet quitPacket = new Packet();
            		   quitPacket.event_type = 5;
            		   quitPacket.sender = c.peerID;
            		   try {
                		   c.outputStream.flush();
                		   c.outputStream.reset();
                		   c.outputStream.writeObject(quitPacket);
                		   c.outputStream.flush();
                		   c.outputStream.reset();
                		   //wait for server to close connection
                		   boolean running = true;
                		   while(running) {
                			   if (!c.s.isConnected()) {
                				   running = false;
                			   }
                		   }
            		   } catch (IOException e) {
            			   e.printStackTrace();
            			   System.out.println("Error.");
            		   }
                	   break;
                   	}
                   
                   default:
                	   System.out.println("Invalid command. Please try again. Valid commands:\nf:\trequest file\nq:\tquit");
                  
               };
            	
        }
    	sc.close();
       
    }
 
    // implement other methods as necessary

    //to see if we already have the file
    public int containsFile(int index) {
    	if (this.FILE_VECTOR[index] == '1') {
    		return this.peerID;
    	} else {
    		return -1;
    	}
    	
    }
}

//handles commands from server
class ClientPacketHandler implements Runnable {
	Client client;
	boolean killProgram;
	ClientPacketHandler(Client client) {
		this.client = client;
		this.killProgram = false;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Packet p;
        while (client.s.isConnected()){ //while connection is active, read packets from server
            try { 
                p = (Packet) this.client.inputStream.readObject();
                eventHandler(p);
            }
            catch (Exception e) {
            	break;
            }

        }
	}

	private void eventHandler(Packet p) {
		// TODO Auto-generated method stub
		int event_type = p.event_type;
        switch (event_type) {
	        case 2: { //reply from server for requested file
	        	
	        	if (p.peerID == -1) {
	        		System.out.println("No user on the network has the requested file.");
	        	} else {
	        		System.out.println("User #" + p.peerID + " has packet " + p.req_file_index);
	        	}
	        	break;
	        }
        	case 6: { //server requests to close connection
	        	try {
	        		System.out.println("Server is closing connection.");
					client.s.close();
					this.killProgram = true;
					System.exit(0); //kill java program
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	break;
        	}
        }
	}
	
}
