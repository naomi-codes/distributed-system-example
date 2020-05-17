/**
 *
 *  @author 2425693
 *  
 *  Node in the DME
 */
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.io.*;
import java.util.*;

public class Node{

	private Random ra;					// Random used to generate random sleep time
	private Socket	s;					// Socket used to send the request to the coordinator

	private PrintWriter pout = null;	// PrintWiter to write the request to the socket 	

	private ServerSocket n_ss;			// Server socket to listen to incoming connections from the coordinator
	private Socket	n_token;			// Socket where the token is accepted

	private static String	c_host = "127.0.0.1";		// Coordinator host
	private int 	c_request_port = 7000;		// Coordinator request port (connection)
	private int 	c_return_port = 7001;		// Coordinator return port (mutex)


	private static String	n_host;		// Node host
	private String 	n_host_name;				// Node host name
	private int     n_port;						// Node port



	/**
	 * 
	 * Constructor for a Node in the DME, takes the host name, 
	 * the port to listen to and an amount to sleep
	 * 
	 * Sends a request to the coordinator and waits for a token to be granted.
	 * Once the token has been granted, it is return to the coordinator.
	 * 
	 * @param nam
	 * @param por
	 * @param sec
	 */
	public Node (String nam, int por, int sec){

		// set the security manager
		System.setSecurityManager(new SecurityManager());

		// initialise host and port with passed parameters
		ra = new Random();
		n_host_name = nam;
		n_port = por;

		// Print statement to show node is active
		System.out.println("Node " +n_host_name+ ":" +n_port+ " of DME is active ....");


		/** 
		 * 
		 * NODE sends n_host and n_port  through a socket s to the coordinator
		 * c_host:c_req_port
		 * and immediately opens a server socket through which will receive 
		 * a TOKEN (actually just a synchronization).
		 * 
		 */


		//initialise the server socket for the node to listen for the TOKEN return
		try {
			n_ss = new ServerSocket(n_port);
			
			// node will wait for a connection from the return port for 15 seconds
			// NOT OPTIMAL FOR MORE THAN 3 OR 4 NODES
			n_ss.setSoTimeout(5000);
		} catch (IOException e) {
			System.out.println("Node " +n_host_name+ ":" +n_port+ " - IOException:" + e);
		} 



		while(true){


			// try connecting to the coordinator (via its receiver port)
			try {
				s = new Socket (c_host, c_request_port);

				// Successful connection
				System.out.println("Node " +n_host_name+ ":" +n_port+ " of DME socket is connected to coordinator ....");
			} catch (IOException e) {

				// Unsuccessful connection
				System.out.println("Node " +n_host_name+ ":" +n_port+ " no such host ("+ c_host +"): " + e);
				System.exit(1);
			} 


			// >>>  sleep a random number of seconds linked to the initialisation sec value
			System.out.println("Node " +n_host_name+ ":" +n_port+ " attempting to sleep...");
			try {
				//try to sleep
				Thread.sleep(ra.nextInt(sec));
				System.out.println("Node " +n_host_name+ ":" +n_port+ " successfully slept!");
			} catch (InterruptedException e) {

				// Failed to sleep
				System.out.println("Node " +n_host_name+ ":" +n_port+ " failed to sleep: " + e);
			}


			// >>>
			// **** Send to the coordinator a token request.
			// send your ip address and port number
			System.out.println("Node " +n_host_name+ ":" +n_port+ " attempting to send token request...");
			try {

				// initiliase the PrintWriter to the socket (connected to the coordinator) outputstream
				// clears the stream automatically
				pout = new PrintWriter(s.getOutputStream(), true);

				//print the this node's host and port to the output stream
				pout.println(n_host);
				pout.println(n_port);

				//close the PrintWriter
				pout.close();
				System.out.println("Node " +n_host_name+ ":" +n_port+ " request sent successfully!");
			} catch (IOException e) {

				// Failed to create the PrintWriter
				System.out.println("Node " +n_host_name+ ":" +n_port+ " failed to create PrintWriter: " + e);
			} 

			// try to close the socket responsible for sending requests
			try {
				s.close();
			} catch (IOException e) {
				System.out.println("Node " +n_host_name+ ":" +n_port+ " Unable to close request socket: " + e);
			}

			// >>>
			// **** Wait for the token
			// this is just a synchronization
			// Print suitable messages

			// waits for a connection, if none is incoming, releases the resources
			// to allow another node to receive the connection
			Thread.yield();

			//Print statement to say node is waiting for token
			System.out.println("Node " +n_host_name+ ":" +n_port+ " waiting for token...");
			try {

				n_token = n_ss.accept();
				System.out.println("Node " +n_host_name+ ":" +n_port+"Token granted");
			} catch (IOException e) {
				System.out.println("Node " +n_host_name+ ":" +n_port+ " Timed out, server not available: " + e);
				System.exit(1);
			}


			try {
				n_token.close();
			} catch (IOException e) {
				System.out.println("Node " +n_host_name+ ":" +n_port+ " Unable to close token receiving socket: " + e);
			}

			
			Object mutexLock = new Object();
			
			synchronized(mutexLock) {
				
				//>>>
				// Sleep half a second, say
				// This is the critical section
				System.out.println("Node " +n_host_name+ ":" +n_port+ " Entering CS...");
				try {
					Thread.sleep(500);
					System.out.println("Node " +n_host_name+ ":" +n_port+ " CS successful!");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("Node " +n_host_name+ ":" +n_port+ " CS failed: " + e);
				}
				
			}

			


			// >>>
			// **** Return the token
			// this is just establishing a sync connection to the coordinator's ip and return port.
			// Print suitable messages - also considering communication failures 

			System.out.println("Node " +n_host_name+ ":" +n_port+ " Attempting to return the token...");
			try {

				// attempt to return the token
				s = new Socket(c_host, c_return_port);
				System.out.println("Node " +n_host_name+ ":" +n_port+ " token return successfully!");
			} catch (IOException e) {

				// failed to return the token
				System.out.println("Node " +n_host_name+ ":" +n_port+ " IOException: " + e);
			}

			// try to close the socket responsible for returning the token
			try {
				s.close();
			} catch (IOException e) {
				System.out.println("Node " +n_host_name+ ":" +n_port+ " unable to close socket responsible for returning the token " + e);
			}

		}
	}



	public static void main (String args[]){


		String n_host_name = ""; 			//variable to hold the node's host parameter
		int n_port;							// varibale to hold the node's port parameter


		// port and millisec (average waiting time) are specific of a node
		if ((args.length < 3) || (args.length > 3)){
			System.out.print("Usage: Node [coordinator host] [port number] [millisecs]");
			System.exit(1);
		}

		// get the IP address and the port number of the node
		try{ 
			
			c_host = args[0];
			
			
			InetAddress n_inet_address =  InetAddress.getLocalHost() ;
			n_host_name = n_inet_address.getHostName();
			n_host = n_inet_address.getHostAddress();
			System.out.println ("node hostname is " +n_host_name+":"+n_inet_address);
		}
		catch (java.net.UnknownHostException e){
			System.out.println(e);
			System.exit(1);
		} 

		// convert the string value read for the port to an int
		n_port = Integer.parseInt(args[1]);
		//n_port = Integer.parseInt(args[0]);				
		System.out.println ("node port is "+n_port);

		
		// initialise the node
		Node n = new Node(n_host_name, n_port, Integer.parseInt(args[2]));

	}


}

