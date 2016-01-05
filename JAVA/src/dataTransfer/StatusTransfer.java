package dataTransfer;

import java.io.IOException;

import types.DataStatus;
import types.DataType;

/**
 * Send and receive a DataStatus through an IOStreams object.
 * @author tranvan
 */
public class StatusTransfer extends DataTransfer
{
	IOStreams stream;

	/**Create a StatusTransfer object
	 * @param stream the IOStreams object 
	 */
	public StatusTransfer( IOStreams stream )
	{
		this.stream = stream;
	}

	@Override
	public void send(DataType data) 
	{
		DataStatus s = (DataStatus)data;

		try 
		{
			sendInt( s.status, stream );

		} catch (IOException ex) {
			System.err.println("Status error");
		}

	}

	@Override
	public DataType receive() 
	{
		DataStatus s = null;
		try 
		{	
			int status = receiveInt( stream );
			s = new DataStatus( status );

		} catch (IOException ex) {
			System.err.println("Status error");
		}

		return s;
	}

}
