/**
 * @author 2425693
 */

import java.io.IOException;
import java.net.*;


public class C_receiver extends Thread{

	private C_buffer 	 buffer; 
	private int 		port;

	private ServerSocket 	s_socket; 
	private Socket		socketFromNode;

	private C_Connection_r	connection_r;


	public C_receiver (C_buffer b, int p){
		buffer = b;
		port = p;
	}


	public synchronized void run () {

		// >>> create the socket the server will listen to
		try {
			s_socket = new ServerSocket(port);
			s_socket.setSoTimeout(15000);
			
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println ("C:receiver    Failed to create server socket") ;
		}



		while (true) {

			System.out.println ("C:receiver    Listening for incoming connections from Nodes...") ;

			// >>> get a new connection
			try {
				socketFromNode = s_socket.accept();
			} catch (IOException e) {
				System.out.println ("C:receiver    SocketTimeoutException no requests received. No nodes available" + e) ;
				System.exit(15000);
			}

			System.out.println ("C:receiver    Coordinator has received a request ...") ;

			// >>> create a separate thread to service the request, a C_Connection_r thread.
			connection_r = new C_Connection_r(socketFromNode, buffer);
			connection_r.start();
			

		}
	}//end run

}


