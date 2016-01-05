/*
                                                * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import tools.Constants;
import tools.Network;
import tools.Tools;
import types.DataFile;
import types.DataMessage;
import cryptoTools.AsymmetricEncryption;
import dataTransfer.FileTransfer;
import dataTransfer.IOStreams;
import dataTransfer.MessageTransfer;
import database.FileDesc;
import database.Recipient;
import database.TCellDB;
import database.User;
import deviceInfo.DeviceInfo;

/**
 * ShareFile is the class used to share a file with others TCs.
 * It provides the methods to retrieve a file description inside the database, create the share message, and send it with the file to the TCs.
 * 
 * @author Athanasia Katsouraki 
 * 
 */
public class ShareFile 
{

	/**
	 * Establishes the connection with the TCs and send the file with the share message.
	 * @param fullFileName the file's path to share.
	 * @param recipientGID a list of userGID to whom we share the file. Those users need to be in the User table.
	 */
	public static void shareFile( String fullFileName, List<Recipient> recipientGID ) throws UnknownHostException, IOException
	{
		try 
		{
			/* The socket used to send the file and the messages */
			Socket socket = null;

			/* Get the infos from device.ini file */
			DeviceInfo deviceInfos = Tools.readDeviceInfo();
			if ( deviceInfos == null )
				return;

			String UserGID = deviceInfos.getUserID();

			TCellDB tCellDB = new TCellDB();     
			String FileGID = UserGID + "|" + fullFileName;

			/* Select FILEID, SECRETKEY,IV from FILE WHERE FILEGID = 'FileGID'; */
			ArrayList<FileDesc> fileDescList = tCellDB.getFileIDsKey( FileGID );
			if( fileDescList.size() < 1)
			{
				System.err.println("ERROR : " + FileGID + " has not been found in the database");
				return;
			}
			String fileID = fileDescList.get(0).fileID;
			String sKey = fileDescList.get(0).sKey;
			String iv = fileDescList.get(0).iv;

			/* for each recipient, we send the share file */
			for(int i=0;i<recipientGID.size();i++)
			{                 
				/* Select TCELLIP,PublicKey from USER WHERE USERGID = 'recipientGID'; */
				ArrayList<User> userList = tCellDB.getIPsFromUser(recipientGID.get(i).userGID);
				if( userList.size() < 1 )
				{
					System.err.println("ERROR : " + recipientGID.get(i).userGID + " has not been found in the database");
					return;
				}

				String recipientTCellIP = userList.get(0).TCellIP;
				String recipientPubKey = userList.get(0).PubKey;

				/* Check the connection status in order to see where the file will be sent */
				boolean checkConnectionRecipientTCell = Network.isHostAvailable( recipientTCellIP, Constants.TCELL_PORT );


				/* Recipient TCell disconnected */
				if ( !checkConnectionRecipientTCell)
				{
					System.err.println("recipient TCell is not connected! The file can not be sent!");
					return;
				}
				/* TCell is connected */
				else
				{
					System.out.println("File is being sent to TCell...");
					socket = new Socket( recipientTCellIP, Constants.TCELL_PORT );
				}

				/* Creation of the stream */
				IOStreams userStream = new IOStreams( socket ); 

				/* Send the share file command */
				Network.sendCommand( Constants.CMD_SHARE_FILE, userStream);

				/* Encrypted file to Send */
				byte [] fileBytes = Tools.readFileFromPath(fileID );
				if( fileBytes == null)
				{
					System.err.println("The file cannot be read (sharefile)");
					return;
				}

				int fileIDLength = fileID.length();
				int fileLength = fileBytes.length;

				/* Send the file through the output stream */
				FileTransfer ft = new FileTransfer( userStream );
				DataFile file = new DataFile( fileIDLength, fileID, fileLength, fileBytes );
				ft.send(file);

				socket = new Socket( recipientTCellIP, Constants.TCELL_PORT );
				userStream = new IOStreams( socket ); 

				Network.sendCommand( Constants.CMD_SHARE_MSG, userStream);

				/* Creation of the message and encryption */
				byte[] inpBytes;
				inpBytes = createMessage(FileGID, fileID, sKey );
				if( inpBytes == null )
					return;

				byte[] encrMsg = encryptMessage( inpBytes, Tools.stringToPublicKey( recipientPubKey ) );
				if( encrMsg == null )
					return;

				/* Send the message through the output stream */
				MessageTransfer mt = new MessageTransfer( userStream );
				DataMessage msg = new DataMessage( encrMsg.length, encrMsg,iv.getBytes() );
				mt.send( msg );

				/* Get the SQL status from the server */
				int status = Network.receiveStatus( userStream );
				Network.interpretStatus( status );

				userStream.close();
				socket.close();

			} //end for

		} catch (Exception ex) {
			Logger.getLogger(ShareFile.class.getName()).log(Level.SEVERE, null, ex);
		}

	}




	/**
	 * Creation of the share message.
	 * @param fileGID the global ID of the file that must be shared
	 * @param fileName the filename
	 * @param K the secret key used to encrypt the file
	 * @return the store message
	 */
	private static  byte[] createMessage(String fileGID , String fileName, String K)
	{
		byte[] inpBytes = null;
		try 
		{
			CreateMessage createShareMsg = new CreateMessage();
			String fileID = Tools.getFileName(fileName);
			String TCellpath = Tools.getHome() + Constants.TCELL_FILES + fileID;

			/* Creation of the message */
			String shareMsg = createShareMsg.createShareMessage(fileGID, TCellpath, K);
			inpBytes = shareMsg.getBytes();
			if( inpBytes == null)
			{
				System.err.println("ERROR : creation of the message failed");
				return null;
			}

		} catch (IOException ex) {
			System.err.println("ERROR : creation of the message failed");
			return null;
		}

		return inpBytes;
	}
	/**
	 * Encryption of the message.
	 * @param msg the message to encrypt
	 * @param pubKey the public key of the recipient user
	 * @return the encrypted message
	 */
	private static byte[] encryptMessage(byte[] msg, PublicKey pubKey )
	{
		AsymmetricEncryption ac = new AsymmetricEncryption();
		byte[] ret = ac.encryptBlockByBlock(msg, pubKey);
		if( ret == null )
			return null;
		return ret;
	}	
}
