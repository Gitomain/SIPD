package messages;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import tools.Constants;
import tools.Network;
import tools.Tools;
import types.DataFile;
import types.DataString;
import dataTransfer.FileTransfer;
import dataTransfer.IOStreams;
import dataTransfer.StringTransfer;
import deviceInfo.DeviceInfo;

/**
 * ReadFile provides the method to ask for a decrypted file to a TC. 
 * @author tranvan
 */
public class ReadFile 
{

	/**
	 * readfile Reads the file described by a fileGID 
	 * @param FileGID the global ID of the file 
	 */
	public static void readFile(Boolean IsTcell, String FileGID) 
	{
		/* Get the infos from device.ini file */
		DeviceInfo deviceInfos = Tools.readDeviceInfo();
		if ( deviceInfos == null )
			return;
		String TCellIP = deviceInfos.getTCellIP();

		if (  Network.isHostAvailable(TCellIP, Constants.TCELL_PORT ))
		{
			try 
			{
				Socket socket = new Socket( TCellIP, Constants.TCELL_PORT );
				/* Creation of the stream */
				IOStreams stream = new IOStreams( socket );

				/* Send the command */
				Network.sendCommand( Constants.CMD_READ_FILE, stream);

				DataString ds = new DataString( FileGID.length(), FileGID );
				StringTransfer ts = new StringTransfer( stream );
				ts.send( ds );

				int status = Network.receiveStatus( stream );
				if ( status == Constants.OK )
				{
					FileTransfer tf = new FileTransfer( stream );
					DataFile df = (DataFile)tf.receive();
					if (IsTcell)
						df.filePath = Tools.getHome() + Constants.TCELL_FILES + Tools.getFileName(df.filePath);
					else
						df.filePath = Tools.getHome() + Constants.APP_FILES + Tools.getFileName(df.filePath);
					Tools.writeFileFromPath( df.filePath, df.file );
					System.out.println("File available at " + df.filePath);
				}
				else
					System.err.println("This file cannot be read");


				stream.close();

			} catch (IOException ex) {
				Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		else
			System.err.println("TCell is not available");

	}
}

