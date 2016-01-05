/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package messages;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import tools.Constants;
import tools.Network;
import tools.Tools;
import types.DataFileDesc;
import types.DataListFilesDesc;
import dataTransfer.FilesDescTransfer;
import dataTransfer.IOStreams;
import deviceInfo.DeviceInfo;

/**
 * GetFileDesc provides the method to ask for the list of all the files' descriptions stored on a TC. 
 * A file description consists of a fileGID, type and description.
 * 
 * @author tranvan
 */
public class GetFileDesc 
{

	/**
	 * Get the files' descriptions
	 * @return the list of the files' description, as a String
	 */
	public static ArrayList<String> getFileDesc()
	{
		ArrayList<String> filesList = new ArrayList<String>();
		
		/* Get the infos from device.ini file */
		DeviceInfo deviceInfos = Tools.readDeviceInfo();
		if ( deviceInfos == null )
			return null;
		String TCellIP = deviceInfos.getTCellIP();

		if ( Network.isHostAvailable(TCellIP, Constants.TCELL_PORT))
		{
			try 
			{
				Socket socket = new Socket( TCellIP, Constants.TCELL_PORT );
				/* Creation of the stream */
				IOStreams stream = new IOStreams( socket );

				/* Send the command */
				Network.sendCommand( Constants.CMD_GET_FILES_DESC, stream);

				int status = Network.receiveStatus( stream );
				if ( status == Constants.OK)
				{
					FilesDescTransfer fdt = new FilesDescTransfer( stream );
					DataListFilesDesc listFiles = (DataListFilesDesc)fdt.receive();

					if(listFiles.nFiles > 0)
					{
						System.out.println("Here is the description of the " + listFiles.nFiles + " files stored in TCell : ");
						for( int i=0;i<listFiles.nFiles;i++)
						{
							DataFileDesc fileDesc = (DataFileDesc) listFiles.list.get(i);
							System.out.println("FileGID : " + fileDesc.fileGID + " Type : " + fileDesc.type +" Description : " + fileDesc.desc);
							filesList.add( fileDesc.fileGID );
						}
					}
					else
						System.err.println("No file found");
				}

				else if ( status == Constants.KO)
					System.err.println("An error occured");


			} catch (IOException ex) {
				Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		else
			System.err.println("host unreachable");
		
		return filesList;
	}



}
