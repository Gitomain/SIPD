package dataTransfer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import types.DataFileDesc;
import types.DataListFilesDesc;
import types.DataType;

/**
 * Send and receive a DataFileDesc through an IOStreams object.
 * @author tranvan
 */
public class FilesDescTransfer extends DataTransfer
{
	IOStreams stream;

	/**Create an FilesDescTransfer object
	 * @param stream the IOStreams object 
	 */
	public FilesDescTransfer( IOStreams stream )
	{
		this.stream = stream;
	}

	@Override
	public void send(DataType data) 
	{
		try 
		{
			DataListFilesDesc listFilesDesc = (DataListFilesDesc)data;
			sendInt( listFilesDesc.nFiles, stream );

			for (int i=0; i<listFilesDesc.nFiles; i++)
			{
				sendInt( listFilesDesc.list.get(i).fileGIDLength, stream );
				sendString( listFilesDesc.list.get(i).fileGID, stream );
				sendInt( listFilesDesc.list.get(i).typeLength, stream );
				sendString( listFilesDesc.list.get(i).type, stream );
				sendInt( listFilesDesc.list.get(i).descLength, stream );
				sendString( listFilesDesc.list.get(i).desc, stream );
			}


		} catch (IOException ex) {
			System.err.println("ERROR : send DataListFilesDesc failed");
			return;
		}

	}

	@Override
	public DataType receive() 
	{
		DataType data = null;
		List<DataFileDesc> listFiles = new ArrayList<DataFileDesc>();

		try 
		{  
			int nFiles = receiveInt( stream );

			for (int i=0; i<nFiles; i++)
			{
				int fileGIDLength = receiveInt( stream );
				String fileGID = receiveString( fileGIDLength, stream );
				int typeLength = receiveInt( stream );
				String type = receiveString( typeLength, stream );
				int descLength = receiveInt( stream );
				String desc = receiveString( descLength, stream );

				DataFileDesc fileDesc = new DataFileDesc(fileGIDLength, fileGID, typeLength, type, descLength, desc);
				listFiles.add( fileDesc );
			}

			data = new DataListFilesDesc( nFiles, listFiles);

		} catch (IOException ex) {
			System.err.println("ERROR : receive DataListFilesDesc failed");
			return null;
		}

		return data;
	}
}
