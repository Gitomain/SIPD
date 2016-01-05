package dataTransfer;

import java.io.IOException;

import types.DataType;

/**
 * DataTransfer is an abstract class which provides the basis methods to send and receive data. 
 * The send and receive methods are implemented in the inherited classes.
 * All the methods of this class are protected.
 * @author tranvan
 * 
 * 
 */

public abstract class DataTransfer 
{
	/**
	 * abstract send method
	 * @param data the DataType object to be sent
	 */
	protected abstract void send( DataType data);
	/**
	 * abstract receive method
	 * @return the DataType object to be received
	 */
	protected abstract DataType receive();
	/**
	 * Send an integer through an IOStreams object 
	 * 
	 * @param i integer to send
	 * @param stream the IOStreams object
	 * @throws IOException
	 */
	protected void sendInt(int i, IOStreams stream ) throws IOException
	{
		stream.getOutputStream().writeInt(i);
	}

	/**
	 * Send a byte array through an IOStreams object 
	 * @param b byte array to send
	 * @param stream the IOStreams object
	 * @throws IOException
	 */
	protected void sendBytes(byte[] b, IOStreams stream ) throws IOException
	{
		stream.getOutputStream().write(b);
	}
	
	/**
	 * Send a String through an IOStreams object 
	 * @param str String to send
	 * @param stream the IOStreams object
	 * @throws IOException
	 */
	protected void sendString(String str, IOStreams stream ) throws IOException
	{
		stream.getOutputStream().writeBytes(str);
	}
	
	/**
	 * Receive an integer through an IOStreams object
	 * @param stream the IOStreams object
	 * @return the received integer
	 * @throws IOException
	 */
	protected int receiveInt( IOStreams stream ) throws IOException
	{
		return stream.getInputStream().readInt();
	}

	/**
	 * Receive a byte array through an IOStreams object
	 * @param b the byte array to be filled
	 * @param stream the IOStreams object
	 * @throws IOException
	 */
	protected void receiveBytes( byte[] b, IOStreams stream  ) throws IOException
	{
		stream.getInputStream().readFully( b );
	}

	/**
	 * Receive a String through an IOStreams object
	 * @param length the length of the expected String
	 * @param stream the IOStreams object
	 * @return the received String
	 * @throws IOException
	 */
	protected String receiveString ( int length, IOStreams stream  ) throws IOException
	{
		byte[] b = new byte[ length ];
		stream.getInputStream().readFully( b );
		return new String ( b );
	}
}
