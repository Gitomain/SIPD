package dataTransfer;

import java.io.IOException;

import types.DataIntegers;
import types.DataType;

/**
 * Send and receive a DataIntegers object through an IOStreams object. This corresponds to the sending of several integers
 * @author tranvan
 */
public class AttributesTransfer extends DataTransfer
{
	IOStreams stream;

	/**Create an AttributesTransfer object
	 * @param stream the IOStreams object 
	 */
	public AttributesTransfer( IOStreams stream )
	{
		this.stream = stream;
	}	

	@Override
	public void send(DataType data) 
	{
		try 
		{
			DataIntegers attributes = (DataIntegers)data;

			int nAttr = attributes.nAttr;
			sendInt ( nAttr, stream );

			for(int i=0;i<nAttr;i++)
				sendInt ( attributes.attrs[i], stream );


		} catch (IOException ex) {
			System.err.println("ERROR : send DataIntegers failed");
			return;
		}
	}


	@Override
	public DataType receive() 
	{
		DataType data = null;
		try 
		{
			int nAttr = receiveInt( stream );

			int[] attrs = new int[nAttr];
			for(int i=0;i<nAttr;i++)
			{
				attrs[i] = receiveInt( stream );
			}
			data = new DataIntegers( nAttr, attrs );

		} catch (IOException ex) {
		}

		return data;
	}


}
