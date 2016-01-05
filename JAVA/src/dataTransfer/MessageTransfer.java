package dataTransfer;

import java.io.IOException;

import tools.Constants;
import types.DataMessage;
import types.DataType;

/**
 * Send and receive a DataMessage through an IOStreams object.
 * 
 * @author Athanasia Katsouraki
 */
public class MessageTransfer extends DataTransfer
{
	IOStreams stream;

	/**Create a MessageTransfer object
	 * @param stream the IOStreams object 
	 */
	public MessageTransfer(IOStreams stream )
	{
		this.stream = stream;
	}

	
	@Override
	public void send(DataType data) 
	{
		DataMessage msg = (DataMessage)data;

		try 
		{
			/* Send the encrypted message and its length */
			sendInt( msg.messageLength, stream );
			sendBytes( msg.message, stream );
			sendBytes( msg.iv, stream );

		} catch (IOException ex) {
			System.err.println("send message error");
		}
	}


	@Override
	public DataType receive() 
	{
		DataType msg = null;
		try 
		{

			/* Get the length of the message and create a byte array */
			int length = receiveInt( stream );

			byte[] messageByteArray = new byte[ length ];

			/* Read the message */
			receiveBytes( messageByteArray, stream );

			/* Get the iv */
			byte[] iv = new byte[ Constants.IV_ENCODED_BYTES_LENGTH ];
			receiveBytes ( iv, stream );

			msg = new DataMessage( length, messageByteArray, iv );

		} catch (IOException ex) {
			System.err.println("receive message error");
		}

		return msg;

	}


}
