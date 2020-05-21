/**
 * @author 2425693
 */
import java.util.*;

public class C_buffer {

	// variable to hold the buffer data
	private Vector<Object> data;


	/**
	 * Buffer Constructor takes no arguments but initialises 
	 * the data storing variable
	 */
	public C_buffer (){
		data = new Vector<Object>(); 
	}


	/**
	 * Returns the length of the data variable
	 * @return
	 */
	public int size(){
		return data.size();
	}


	/**
	 * Takes a request to be saved as a parameter
	 * request[0] = requesting node's name
	 * request[1] = requesting node's port
	 * @param request
	 */
	public synchronized void saveRequest (String[] request){
		data.add(request[0]);
		data.add(request[1]);    
	}


	/**
	 * Prints the contents of the request buffer to the system printer
	 */
	public void show(){

		for (int i=0; i<data.size();i++)
			System.out.print(" "+data.get(i)+" ");
		System.out.println(" ");
	}


	/**
	 * Saves an object (request param) to the data variable
	 * @param o
	 */
	public void add(Object o){
		data.add(o);
	}

	/**
	 * Removes and returns the first object from the data variable
	 * @return
	 */
	synchronized public Object  get(){

		Object o = null; 

		if (data.size() > 0){
			o = data.get(0);
			data.remove(0);
		}
		return o;
	}



}


