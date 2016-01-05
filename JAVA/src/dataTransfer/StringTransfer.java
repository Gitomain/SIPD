/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dataTransfer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import types.DataString;
import types.DataType;

/**
 * Send and receive a DataSring through an IOStreams object.
 * @author tranvan
 */
public class StringTransfer extends DataTransfer
{
	IOStreams stream;

	/**Create a StringTransfer object
	 * @param stream the IOStreams object 
	 */
	public StringTransfer( IOStreams stream )
	{
		this.stream = stream;
	}

	@Override
	public void send(DataType data) 
	{
		DataString dStr = (DataString)data;

		try 
		{
			sendInt( dStr.length, stream );
			sendString( dStr.str, stream );

		} catch (IOException ex) {
			Logger.getLogger(StatusTransfer.class.getName()).log(Level.SEVERE, null, ex);
		}

	}


	@Override
	public DataType receive() 
	{
		DataString dStr = null;
		try 
		{	
			int length = receiveInt( stream );
			String str = receiveString( length, stream );
			dStr = new DataString( length, str );

		} catch (IOException ex) {
			Logger.getLogger(StatusTransfer.class.getName()).log(Level.SEVERE, null, ex);
		}

		return dStr;
	}

}
