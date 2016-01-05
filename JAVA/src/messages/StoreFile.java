package messages;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import tools.Constants;
import tools.Network;
import tools.SymKeyTools;
import tools.Tools;
import types.DataFile;
import types.DataMessage;
import cryptoTools.AsymmetricEncryption;
import cryptoTools.SymmetricEncryption;
import dataTransfer.FileTransfer;
import dataTransfer.IOStreams;
import dataTransfer.MessageTransfer;
import deviceInfo.DeviceInfo;

/**
 * StoreFile is the class used to store a file in a TC.
 * It provides the methods to send an encrypted file to a TC followed by an encrypted message containing the description of the file.
 * The file is encrypted by AES and the message is encrypted by RSA, with the TC's public key
 * 
 * @author Athanasia Katsouraki 
 * 
 */
public class StoreFile 
{

	/**
	 * Establishes the connection with the TC and send to file with the store message.
	 * @param fullFileName the path of the file to send
	 */
	public static void storeFile(String fullFileName )
	{
		try 
		{
			/* The socket used to send the file and the messages */
			Socket socket;

			/* Extract the fileName from the path */
			String fileName = Tools.getFileName(fullFileName);
			
			/* Get the infos from device.ini file */
			DeviceInfo deviceInfos = Tools.readDeviceInfo();
			if ( deviceInfos == null )
				return;

			String TCellIP = deviceInfos.getTCellIP();
			String userGID = deviceInfos.getUserID();

			/* Creation of the temporary directory */
			if ( !Tools.createDir(Tools.getHome() + Constants.TMP_FILES ))
				return;

			/* Check the connection status in order to see where the file will be sent */
			boolean checkConnectionTCell = Network.isHostAvailable(TCellIP, Constants.TCELL_PORT );

			/* TCell disconnected */
			if ( !checkConnectionTCell)
			{
				System.err.println("TCell is not connected! The file can not be sent!");
				return;
			}
			else
			{
				System.out.println("File is being sent to TCell...");
				socket = new Socket( TCellIP, Constants.TCELL_PORT );
			}


			/* Creation of the stream */
			IOStreams stream = new IOStreams( socket );

			/* Send the command */
			Network.sendCommand( Constants.CMD_STORE_FILE, stream);

			/* Encryption of the file */
			SecretKey sKey = SymKeyTools.GenSymKey();
			byte[] iv = new byte[ Constants.IV_LENGTH ];
			byte[] encrFile = encryptFile ( fullFileName, sKey, iv );
			if( encrFile == null )
				return;
			
			byte[] iv_encoded = Tools.encodeBytes( iv );
			String encrPath = Tools.getHome() + Constants.TMP_FILES + Constants.SYM_ENCR_FILE_NAME + fileName;

			/* Send the file through the output stream */
			FileTransfer ft = new FileTransfer( stream );
			int pathLength = encrPath.length();
			int encFileLength = encrFile.length;
			DataFile file = new DataFile( pathLength, encrPath, encFileLength, encrFile );
			ft.send(file);

			/* Creation of the message and encryption */
			byte[] inpBytes;
			String stringKey = SymKeyTools.GenSymKeyToString(sKey);
			inpBytes = createMessage( Tools.getFilePath(fullFileName), fileName, stringKey );
			if( inpBytes == null )
				return;
			
			PublicKey pubKey = Tools.getPublicKey(Tools.getHome() + Constants.KEYS_PATH, userGID );
			if ( pubKey == null )
				return;

			byte[] encrMsg = encryptMessage( inpBytes, pubKey );
			if( encrMsg == null )
				return;

			/* Send the message through the output stream */
			MessageTransfer mt = new MessageTransfer( stream );
			DataMessage msg = new DataMessage(encrMsg.length, encrMsg, iv_encoded );
			mt.send( msg );

			/* Get the SQL status from the server */
			int status = Network.receiveStatus( stream );
			Network.interpretStatus( status );

			stream.close();
			socket.close();
			/* Delete the temporarily encrypted file */
			Tools.deleteFileFromPath(encrPath);

		} catch (Exception ex) {
			System.err.println("ERROR : store file has failed");
		}

	}
	/**
	 * Creates a message of type Store
	 * @param fileName the filename 
	 * @param K the secret key used to encrypt the file
	 * @return the message
	 */
	private static byte[] createMessage(String filePath, String fileName, String K)
	{
		byte[] inpBytes = null;
		try 
		{
			System.out.println("Creating Message for " + filePath + "---" + fileName);
			//Create a STORE message 
			CreateMessage createStoreMsg = new CreateMessage();
			String EncFullFileName = filePath + Constants.SYM_ENCR_FILE_NAME + fileName;

			String storeMsg = createStoreMsg.createStoreMessage(EncFullFileName, K);
			inpBytes = storeMsg.getBytes();
			
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
	 * Encrypts the message.
	 * @param msg the message to encrypt
	 * @param pubKey the TC's public key
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


	/**
	 * Encrypts the file.
	 * @param fullFileName the path of the file to encrypt
	 * @param sKey the SecretKey 
	 * @param iv the initialization vector
	 * @return the encrypted file
	 */
	public static byte[] encryptFile(String fullFileName, SecretKey sKey, byte[] iv)
	{
		byte [] encbytes = null;
		

		try
		{
			String filename = Tools.getFileName( fullFileName );
			FileInputStream is; is = new FileInputStream(fullFileName); 

			String fullEncrName = Tools.getHome() + Constants.TMP_FILES + Constants.SYM_ENCR_FILE_NAME+filename;
			FileOutputStream eos = new FileOutputStream( fullEncrName );
			//Encrypt the file
			int dataSize = is.available();
			byte[] inbytes = new byte[ dataSize ];
			is.read(inbytes);
			//Calling the Symmetric Encryption for encrypting the file
			SymmetricEncryption.encryptFile( inbytes,sKey, eos, iv );
			eos.flush();
			
			//Read the Outputstream
			encbytes = Tools.readFileFromPath( fullEncrName );
			if( encbytes == null )
			{
				System.err.println("ERROR : The file cannot be encrypted");
				is.close();
				eos.close();
				return null;
			}
			
			is.close();
			eos.close();

			System.out.println("Encryption is done..."+fullEncrName+" is now ready to be sent");


		} catch (FileNotFoundException ex) {
			Logger.getLogger(StoreFile.class.getName()).log(Level.SEVERE, null, ex);

		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(StoreFile.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(StoreFile.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(StoreFile.class.getName()).log(Level.SEVERE, null, ex);
		}

		return encbytes;
	}
}