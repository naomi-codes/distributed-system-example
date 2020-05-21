/**
 *  @author 2425693
 *  
 *  Implementation of the Coordinator Remote object
 */

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Scanner;


public class CoordinatorImpl {


	private static C_buffer buffer;
	private static C_mutex mutex;
	private static C_receiver receiver;

	protected CoordinatorImpl() throws RemoteException {
		
	}

	public static void main (String args[]) throws RemoteException{

		// default port for the coordinator, also port which receives connections
		int port = 7000;	

		// set the security manager
		System.setSecurityManager(new SecurityManager());

		try {    
			InetAddress c_addr = InetAddress.getLocalHost();
			String c_name = c_addr.getHostName();
			System.out.println("*****COORDINATOR RUNNING****");
			System.out.println("PRESS 'q' TO EXIT");
			System.out.println ("Coordinator address is "+c_addr);
			System.out.println ("Coordinator host name is "+c_name+"\n\n");  

			//CoordinatorImpl server = new CoordinatorImpl ();
			//Naming.rebind("rmi://"+c_name+"/Coordinator", server);
		}
		catch (Exception e) {
			System.err.println(e);
			System.err.println("Error in coordinator");
		}


		// allows defining port at launch time
		if (args.length == 1) port = Integer.parseInt(args[0]);


		// Create and run a C_receiver and a C_mutex object sharing a C_buffer object
		buffer = new C_buffer();

		// thread to deal with logged requests
		mutex = new C_mutex(buffer, port);
		
		// thread to deal with new incoming requests
		receiver = new C_receiver(buffer, port);

		
		// start the threads
		mutex.start();
		receiver.start();

		
		// use a scanner to deal with user input to stop the program
		Scanner keyboard = new Scanner(System.in);
		boolean exit = false;
		while (!exit) {
			String input = keyboard.nextLine();
			if(input != null) {
				
				//allow user to type 'q' to quit the server
				if ("q".equals(input)) {
					System.out.println("Exit program");
					exit = true;
				} 
			}
		}

		// exit == true
		System.exit(0);
		keyboard.close();   
	}

}
