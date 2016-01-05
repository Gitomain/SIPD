package tools;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import dataTransfer.AttributesTransfer;
import dataTransfer.IOStreams;
import types.DataIntegers;

/**
 *Network provides utility static network oriented methods
 * @author tranvan
 */
public class Network 
{
	
	/**
	 * Send a status through an IOStreams object.
	 * @param status the status to send
	 * @param stream the IOStreams object
	 */
	public static void sendStatus( int status, IOStreams stream )
	{
		sendInt( status, stream );
	}

	/**
	 * Send a command through an IOStreams object.
	 * @param cmd the command to send
	 * @param stream the IOStreams object
	 */
	public static void sendCommand( int cmd, IOStreams stream )
	{
		sendInt( cmd, stream );
	}

	/**
	 * Receive a status through an IOStreams object.
	 * @param stream the IOStreams object
	 * @return the received status
	 */
	public static int receiveStatus( IOStreams stream )
	{
		return receiveInt( stream );
	}

	/**
	 * Receive a command through an IOStreams object.
	 * @param stream the IOStreams object
	 * @return the received command
	 */
	public static int receiveCommand( IOStreams stream )
	{
		return receiveInt( stream );
	}

	/**
	 * Send an integer through an IOStreams object.
	 * @param i the integer to send
	 * @param stream the IOStreams object
	 */
	private static void sendInt( int i, IOStreams stream )
	{
		if ( stream == null )
		{
			System.err.println("Null stream");
			return;
		}

		AttributesTransfer at = new AttributesTransfer( stream );
		DataIntegers di = new DataIntegers( 1, i );
		at.send( di );
	}

	/**
	 * Receive an integer through an IOStreams object.
	 * @param stream the IOStreams object
	 * @return the received integer
	 */
	private static int receiveInt( IOStreams stream )
	{
		if ( stream == null )
		{
			System.err.println("Null stream");
			return Constants.KO;
		}

		AttributesTransfer at = new AttributesTransfer( stream );
		DataIntegers di = (DataIntegers)at.receive();
		if (di != null)
			return di.attrs[0];
		else
			return Constants.KO;
	}

	/**
	 * Print a message based on the received status
	 * @param status the received status
	 */
	public static void interpretStatus( int status )
	{
		switch ( status )
		{
			case Constants.OK: case Constants.SQL_OK:
				System.out.println("File correctly received by the TCell");
				break;
			case Constants.SQL_NOT_UNIQUE:
				System.err.println("WARNING : the file is already in the database");
			case Constants.SQL_KO: case Constants.KO:    
				System.err.println("ERROR : the database server has failed");
			default:
				System.err.println("Error");
		}
	}

	/**
	 * Check if an host is available on a particular given port
	 * @param IP the host IP
	 * @param port the port
	 * @return true is the host is available, false otherwise
	 */
	public static boolean isHostAvailable( String IP, int port )
	{
		try{
			Socket s = new Socket(IP, port);
			s.close();
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	/**
	 * Checks if someone (IP address) is either online or offline.
	 * @param IP
	 * 
	 * @return true  if  is online 
	 *         false if  is offline 
	 */
	public static boolean connectionStatus (String IP) 
	{  
		boolean networkStatus = false;
		int timeOut = 3000;  
		try {  
			networkStatus = InetAddress.getByName(IP).isReachable(timeOut);  
		} catch (IOException ex) {  
			ex.getStackTrace();
		}  
		if (networkStatus) {  
			networkStatus = true;
			//System.out.println("Online");  
		} else {  
			networkStatus = false;
			System.out.println(IP + " is offline");  
		}  
		return networkStatus;
	}  

	/**
	 * Detects and returns the IP of my PC.
	 * 
	 * @return IP
	 */
	public String getMyIPAddress () 
	{
		String IP = "";
		try {
			InetAddress thisIp = InetAddress.getLocalHost();
			IP = thisIp.getHostAddress();
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		return IP;
	}

}
