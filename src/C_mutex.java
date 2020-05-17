/**
 * @author 2425693
 */
import java.net.*;

public class C_mutex extends Thread{

	private C_buffer buffer;
	private Socket   s;
	private int      port;

	// ip address and port number of the node requesting the token.
	// They will be fetched from the buffer    
	private String n_host;
	private int    n_port;



	public C_mutex (C_buffer b, int p){

		this.buffer = b;
		this.port = p;
	}



	private void go (){

		try{ 
			//  >>>  Listening from the server socket on port 7001
			// from where the TOKEN will be later on returned.
			// This place the server creation outside he while loop.
			ServerSocket ss_back = new ServerSocket(7001);
			ss_back.setSoTimeout(5000);

			while (true){

				Thread.sleep(4000);
				// >>> Print some info on the current buffer content for debugging purposes.
				// >>> please look at the available methods in C_buffer

				System.out.println("C:mutex   Buffer size is "+ buffer.size());
				System.out.println("C:mutex   Print objects in buffer:");

				// use buffer method to print data in buffer 
				buffer.show();

				// if the buffer is not empty
				if ( buffer.size() > 0) {

					
					System.out.println("C:mutex   Retrieving request from buffer...:");
					
					// >>>   Getting the first (FIFO) node that is waiting for a TOKEN from the buffer
					//       Type conversions may be needed.
					n_host = buffer.get().toString();		// get the host and cast to a string
					n_port = Integer.parseInt((String)buffer.get()); // get the port and cast to an int

					System.out.println("C:mutex   Retrieved request from buffer: " + n_host + ":" + n_port);


					// >>>  **** Granting the token
					//
					try{
						
						//try connecting to the node associated with the request
						System.out.println("C:mutex   Granting the token to: " + n_host + ":" + n_port);
						s = new Socket(n_host, n_port);

					}
					catch (java.io.IOException e) {
						
						// unable to connect to the node
						System.out.println(e);
						
						// print statement
						System.out.println("C:mutex failed to connect to: " + n_host + ":" + n_port 
								+ ". Node unavailable - unable to grant token. Request discarded.");
						
						// move on to next request
						System.out.println("C:mutex moving on to next request after failing to grant token to unavailable node");
						continue;
					}


					
					System.out.println("C:mutex waiting to get get token back from: " + n_host + ":" + n_port);
					Thread.yield();
					
						//  >>>  **** Getting the token back
						try{
							
							//THIS IS BLOCKING !
							
							//ss_back.setSoTimeout(timeout);
							ss_back.accept();
							
							System.out.println("C:mutex received token back from: " + n_host + ":" + n_port);
						}
						catch (java.io.IOException e) {
							System.out.println(e);
							System.out.println("C:mutex unable to retrieve token from: " + n_host + ":" + n_port);
							System.out.println("C:mutex ...generating new token...: " + n_host + ":" + n_port);
							continue;
						}
						

				}// endif	
			}// endwhile


		}catch (Exception e) {System.out.print(e);}

	}


	public synchronized void run (){

		go();
	}


}
