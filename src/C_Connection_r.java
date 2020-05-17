/**
 * @author 2425693
 */
import java.net.*;
import java.io.*;


// Reacts to a node request.
// Receives and records the node request in the buffer.
//
public class C_Connection_r extends Thread{

	// class variables
	private C_buffer 	   buffer;

	private Socket 	   s;
	private InputStream    in;
	private BufferedReader bin;


	public C_Connection_r(Socket s, C_buffer b){
		this.s = s;
		this.buffer = b;

	}


	public synchronized void run() {	

		final int NODE = 0;
		final int PORT = 1;

		String[] request = new String[2];

		System.out.println("C:connection IN    dealing with request from socket "+ s);
		try {	

			// >>> read the request, i.e. node ip and port from the socket s
			// >>> save it in a request object and save the object in the buffer (see C_buffer's methods).

			in = s.getInputStream();
			bin = new BufferedReader(new InputStreamReader(in));

			request[NODE] = bin.readLine();
			request[PORT] = bin.readLine();

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				System.out.println("C:connection      Sleep failed: " + e);
			}
			
			// if the request == null (i.e. it was a backlogged request from a node which
			// is no longer available) ....
			
			if ((request[NODE] == null) || (request[PORT] == null)) {

				// inform the user that the request was null
				System.out.println("C:connection      null request received. Request discarded");
			} else {
				//... otherwise a valid request was received and it is saved to the buffer
				System.out.println("C:connection      Adding request to buffer...");
				buffer.saveRequest(request);
			}
			in.close();
			s.close();
			System.out.println("C:connection OUT    received and recorded request from "+ request[NODE]+":"+request[PORT]+ "  (socket closed)"); 


		} 
		catch (java.io.IOException e){
			System.out.println("Socket unavailable");
			//System.exit(1);
			return;
		} 	
		System.out.println("C:Connection   Print objects in buffer:");
		buffer.show();

	} // end of run() method

} // end of class
