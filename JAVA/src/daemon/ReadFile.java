/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daemon;

import cryptoTools.SymmetricDecryption;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import database.FileDesc;
import database.TCellDB;
import tools.Constants;
import tools.SymKeyTools;
import tools.Tools;
import types.DataFile;


/**
 * Based on a fileGID, retrieves the description of the file from the database, and decrypt the file.
 * 
 * @author Athanasia Katsouraki
 */
public class ReadFile 
{

	/**
	 * Reads an encrypted file based on the FileGID.
	 * @param FileGID the fileGID
	 * @return a DataFile object
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	public static DataFile read ( String FileGID ) throws FileNotFoundException, IOException
	{
		DataFile df = null;
		List <FileDesc> listFidSkey = new ArrayList<FileDesc>();
		TCellDB tCellDB = new TCellDB();
		//Select SECRETKEY, FILEID from FILE  WHERE FILEGID = 'fullFileName';
		listFidSkey = tCellDB.getFileIDsKey( FileGID );
		if (!(listFidSkey.isEmpty()))
		{ 
			/* Decryption of the file */
			String stringKey = listFidSkey.get(0).sKey;
			String fileID = listFidSkey.get(0).fileID;
			String iv = listFidSkey.get(0).iv;

			SecretKey sKey = SymKeyTools.StringToGenSymKey(stringKey);

			byte[] iv_decoded = Tools.decodeBytes( iv.getBytes() );
			byte[] file = decryptFile( fileID, sKey, iv_decoded );
			if( file == null )
				return null;
			
			String filename= Tools.getFileName( fileID );
			String[] parts = filename.split(Constants.SYM_ENCR_FILE_NAME);
			filename = parts[1]; 
			String fullDecName = Tools.getHome() + Constants.TCELL_FILES + Constants.SYM_DECR_FILE_NAME + filename;
			
			df = new DataFile ( fullDecName.length(), fullDecName, file.length, file );

		}
		else
			System.err.println("No file found");

		return df;


	}
	/**
	 * Decrypts the file.
	 * @param fullEncFileName the path of the encrypted file
	 * @param sKey the secret key
	 * @param iv the initialization vector
	 * @return the decrypted file
	 */
	public static byte[] decryptFile(String fullEncFileName, SecretKey sKey, byte[] iv)
	{
		byte[] decBytes = null;
		FileInputStream is;
		try 
		{
			//Decrypt the message

			String filename= Tools.getFileName(fullEncFileName);
			is = new FileInputStream(fullEncFileName);
			int dataSize= is.available();
			decBytes = new byte[dataSize];
			is.read(decBytes);

			String[] parts = filename.split(Constants.SYM_ENCR_FILE_NAME);
			filename = parts[1]; 
			String fullDecName = Tools.getHome() + Constants.TCELL_FILES + Constants.SYM_DECR_FILE_NAME + filename;
			FileOutputStream dos = new FileOutputStream(fullDecName );
			SymmetricDecryption.decryptFile(decBytes,sKey, dos, iv);

			decBytes = Tools.readFileFromPath ( fullDecName );
			if(decBytes == null)
				return null;
			
			Tools.deleteFileFromPath( fullDecName );
			
			dos.flush();
			is.close();
			dos.close();             


		} catch (Exception ex) {
			System.err.println("Can not decrypt the file " + fullEncFileName);
			return null;
		}

		return decBytes;
	}

}
