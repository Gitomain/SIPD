	package dataTransfer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import types.DataFile;
import types.DataType;

/**
 * Send and receive a DataFile through an IOStreams object.
 * 
 * @author Athanasia Katsouraki
 * 
 */
public class FileTransfer extends DataTransfer
{   
	IOStreams stream;

	/**Create a FileTransfer object
	 * @param stream the IOStreams object 
	 */
	public FileTransfer( IOStreams stream )
	{
		this.stream = stream;
	}

	@Override
	public void send(DataType data ) 
	{
		DataFile file = (DataFile)data;

		try 
		{
			/*Send the file name, the file and its size to the server */
			sendInt( file.filePathLength, stream );
			sendBytes( file.filePath.getBytes(), stream );
			sendInt( file.fileSize, stream );
			sendBytes ( file.file, stream );

		} catch (IOException ex) {
			Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public DataType receive() 
	{
		DataType file = null;
		try 
		{
			/* Get the length of the file name */
			int filePathLength = receiveInt( stream );

			/* Get the file path */
			byte[] filePathBytes = new byte[ filePathLength ];
			receiveBytes(filePathBytes, stream);
			String filePath = new String(filePathBytes);
			/* Get the file size and create a byte array */
			int fileSize = receiveInt( stream );
			byte [] byteArray  = new byte[ fileSize ];

			/* Read the file */
			receiveBytes( byteArray, stream );

			file = new DataFile( filePathLength, filePath, fileSize, byteArray );


		} catch (IOException ex) {
			Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
		}

		return file;
	}
}