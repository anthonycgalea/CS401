// The connection Thread is spawned from the ServerSocketHandler class for every new Client connections. 
//Responsibilities for this thread are to hnadle client specific actions like requesting file, registering to server, and client wants to quit.
import java.io.*;
import java.net.*;
import java.util.*;

class Connection extends Thread
{
    Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    int peerPort;
    int peer_listen_port;
    int peerID;
    InetAddress peerIP;
    char FILE_VECTOR[];
    ArrayList<Connection> connectionList;    
    

    public Connection(Socket socket, ArrayList<Connection> connectionList) throws IOException
    {
        this.connectionList=connectionList;
        this.socket=socket;
        this.outputStream=new ObjectOutputStream(socket.getOutputStream());
        this.inputStream=new ObjectInputStream(socket.getInputStream());
        this.peerIP=socket.getInetAddress();
        this.peerPort=socket.getPort();
        
    }

    @Override
    public void run() {
        //wait for register packet.
        // once received, listen for packets with client requests.
        Packet p;
        while (true){
            try { 
                p = (Packet) inputStream.readObject();
                eventHandler(p);
            }
            catch (Exception e) {
            	break;
            }

        }

    }

   

   

    public void eventHandler(Packet p)
    {
        int event_type = p.event_type;
        switch (event_type)
        {
            case 0: {//client register
            	this.FILE_VECTOR = p.FILE_VECTOR;
            	this.peerID = p.peerID;
            	this.peer_listen_port = p.peer_listen_port;
            	System.out.println("User with id #" + this.peerID + " on port " + this.peer_listen_port + " has connected.");
            	break;
            }
            case 1: {// client is requesting a file
            	boolean inNetwork = false;
            	Packet returnInfo = new Packet();
            	returnInfo.event_type = 2;
            	returnInfo.req_file_index = p.req_file_index;
            	System.out.println("Client is requesting packet # " + p.req_file_index);
            	for (int i = 0; i < this.connectionList.size(); i++) {
            		if (this.connectionList.get(i).clientContainsFile(p.req_file_index) != -1 && this.connectionList.get(i).isAlive()) {
            			inNetwork = true;
            			returnInfo.peerID = this.connectionList.get(i).peerID;
            			System.out.println("Client " + returnInfo.peerID + " has the requested file.");
            			try {
                  		   this.outputStream.flush();
                  		   this.outputStream.reset();
                  		   this.outputStream.writeObject(returnInfo);
                  		   this.outputStream.flush();
                  		   this.outputStream.reset();
              		   } catch (IOException e) {
              			   e.printStackTrace();
              			   System.out.println("Error.");
              		   }
            		}
            	}
            	if (!inNetwork) {
         		   returnInfo.peerID = -1;
         		   try {
             		   this.outputStream.flush();
             		   this.outputStream.reset();
             		   this.outputStream.writeObject(returnInfo);
             		   this.outputStream.flush();
             		   this.outputStream.reset();
         		   } catch (IOException e) {
         			   e.printStackTrace();
         			   System.out.println("Error.");
         		   }
            	}
            	break;
            }

            case 5: {// client wants to quit
            	System.out.println("* " + this.peerID + "* at " + this.peerIP+" wishes to quit,");
				try {
					this.socket.close();
				} catch (IOException e) {
					// DONE Auto-generated catch block
					e.printStackTrace();
				}
	            break;
            }
        };
    }
    
    //other methods go here
    public int clientContainsFile(int index) {
    	if (this.FILE_VECTOR[index] == 1) {
    		return this.peerID;
    	} else {
    		return -1;
    	}
    	
    }

	public void closeConnection() {
		// TODO Auto-generated method stub
		if (this.socket.isConnected()) {
			System.out.println("Closing connection with user #" + this.peerID);
			Packet closeConnect = new Packet();
			closeConnect.event_type = 6;
			try {
      		   this.outputStream.flush();
      		   this.outputStream.reset();
      		   this.outputStream.writeObject(closeConnect);
      		   this.outputStream.flush();
      		   this.outputStream.reset();
  		   } catch (IOException e) {
  			   e.printStackTrace();
  			   System.out.println("Error.");
  		   }
		}
		
	}
    
}
