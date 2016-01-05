package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.BufferUnderflowException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.bouncycastle.util.encoders.Base64;

import cryptoTools.KeyManager;
import deviceInfo.DeviceInfo;


/**
 * Tools provides static utility methods. 
 * @author tranvan
 */

public class Tools 
{

	/**
	 * Checks if a file exists 
	 * @param fullFilename the file's path
	 * @return true if the file exists, false if not
	 */
	public static boolean isFileExists ( String fullFilename )
	{
		return new File(fullFilename).exists();
	}

	/**
	 * Create a directory in case it does not exist.
	 * @param path the file's path
	 * @return true if the directory has been created of it already exists, false if an error occured
	 */
	public static boolean createDir ( String path )
	{
		boolean res = true;
		File f = new File( path );
		// if the directory does not exist, create it
		if ( !f.exists() ) 
		{
			res = f.mkdir(); 
			if ( res )
				System.out.println("Directory " + path + " created" ); 
			else
				System.err.println("ERROR : cannot create directory " + path ); 
		}

		return res;
	}


	public static String getHome()
	{

		String home = System.getProperty("user.dir");
		//System.out.println(home);
		//home = home.substring(0, home.indexOf(Constants.HOME_JAVA));
		home = home + "/";
		
		System.out.println(home);
		return home;
	}
	
	public static boolean containsChar(String s, char search) 
	{
		if (s.length() == 0)
			return false;
		else
			return s.charAt(0) == search || containsChar(s.substring(1), search);
	}

	public static int timesOfCharInStr(String str, char toCheck)
	{
		int count=0;
		for (int i = 0; i < str.length(); i++) 
		{
			if (str.charAt(i) == toCheck) 
				count++;  
		}
		return count;
	}

	public Vector <String> getFileDesc(String path)
	{
		Vector<String> myList = new Vector<String>();

		List<String> oList= new  ArrayList<String>();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			if (listOfFiles[i].isFile()) 
				oList.add(listOfFiles[i].getName().toString());
		} 
		for (String str: oList) 
			myList.add(str); 
		return myList;
	}

	/** Extract the UserGID from a fileGID
	 * @param fileGID the fileGID
	 * @return the UserGID as a String
	 */
	public static String extractUserID(String fileGID)
	{
		String UserGID = null;
		//Retrieve UserGID  (FileGID: UserGID|FileID)
		UserGID = fileGID.split("\\|")[0];
		return UserGID;
	}

	/**
	 * Extract the file name from the fileID
	 * @param fileName the fileName
	 * @return the file name
	 */
	public static String getFileName(String fileName)
	{
		String fileID = null;
		int i = timesOfCharInStr(fileName, '/');
		if (containsChar(fileName, '/'))
			fileID = fileName.split("\\/")[i];
		else
			fileID = fileName;

		return fileID;
	}

	public static String getFilePath(String fullFileName)
	{
		String home = System.getProperty("user.dir");
		home = home.substring(0, home.indexOf(Constants.HOME_JAVA));
		//System.out.println(home);

		return fullFileName.substring(0, fullFileName.indexOf(getFileName(fullFileName)));
				
	}

	public static byte[] copyBytesStore(byte[] arr, int length)
	{
		byte[] newArr = null;
		if (arr.length == length)
			newArr = arr;
		else
		{
			newArr = new byte[length];
			for (int i = 0; i < length; i++)
			{
				newArr[i] = (byte) arr[i];
			}
		}
		return newArr;
	}

	/**
	 * Read the file DeviceInfo.ini 
	 * @return a DeviceInfo object, which contains the fields of the file
	 */
	public static DeviceInfo readDeviceInfo()
	{
		/* Parse the device.info */
		String[] infos = DeviceInfo.getFieldsFromFile(Tools.getHome() + Constants.DEVICE_INFO );

		if ( infos == null ) 
		{
			System.err.println("ERROR : cannot read " + Tools.getHome() + Constants.DEVICE_INFO);
			return null;
		}

		else if ( infos.length != 2 )
		{
			System.err.println(Tools.getHome() + Constants.DEVICE_INFO + " is not correct");
			return null;
		}

		/* Create the static fields */
		return new DeviceInfo( infos[0], infos[1] );
	}


	/**
	 * Encode a byte array in base64
	 * @param arrayToEncode the byte array to encode
	 * @return the encoded byte array
	 */
	public static byte[] encodeBytes( byte[] arrayToEncode )
	{
		return Base64.encode( arrayToEncode );        
	}

	/**
	 * Decode a byte array in base64
	 * @param arrayToDecode the byte array to decode
	 * @return the decoded byte array
	 */
	public static byte[] decodeBytes( byte[] arrayToDecode )
	{
		return Base64.decode( arrayToDecode );
	}

	/**
	 * Decode a String in base64 into a byte array
	 * @param strToDecode the String to decode
	 * @return the decoded byte array
	 */
	public static byte[] decodeString( String strToDecode )
	{
		return Base64.decode( strToDecode );
	}


	/**
	 * Extract the fields from a message
	 * @param msg the message
	 * @return a String array containing the fields
	 */
	public static String[] getMessageFields( String msg )
	{
		System.out.println ("msg = "+msg);
		String[] fields =  msg.split(":");
		if( fields.length < 1 )
		{
			System.err.println("ERROR : incorrect message");
			return null;
		}

		/* This is necessary (for now) to eliminate the padding bytes added by encryption */
		if( fields[0].equals( "STORE"))
		{
			if( fields.length != 3 )
			{
				System.err.println("ERROR : incorrect store message");
				return null;
			}
			fields[2] = fields[2].substring(0, Constants.AES_KEY_ENCODED_BYTES_LENGTH );
		}


		else if( fields[0].equals( "SHARE"))
		{
			if( fields.length != 4 )
			{
				System.err.println("ERROR : incorrect share message");
				return null;
			}
			System.out.println ("1= "+fields[1] + " 2 = "+fields[2] + " 3 =" +fields[3]);
			fields[3] = fields[3].substring(0, Constants.AES_KEY_ENCODED_BYTES_LENGTH );
		}
		else
		{
			System.err.println("ERROR : message type unknown");
			return null;
		}

		return fields;
	}

	/**
	 * Utility method to read the bytes of a file
	 * @param path the file's path
	 * @return a byte array filled by the content of the file
	 */
	public static byte[] readFileFromPath ( String path ) 
	{
		byte[] file = null;
		try 
		{
			FileInputStream inStream = new FileInputStream( path );
			int dataSize = inStream.available();
			file = new byte[ dataSize ];
			inStream.read( file );
			inStream.close();

		} catch (IOException ex) {
			System.err.println("ERROR : "+ path + " can not be read");
			return null;
		}

		return file;
	}

	/**
	 * Utility method to write some bytes inside a file
	 * @param path the file's path
	 * @param file the bytes to write
	 * @return true if the file has been written, false otherwise
	 */
	public static boolean writeFileFromPath( String path, byte[] file )
	{
		try
		{
			FileOutputStream outStream = new FileOutputStream( path ); 
			outStream.write( file );
			outStream.close();

		} catch (IOException ex) {
			System.err.println("ERROR : "+ path + " can not be written");
			return false;
		}

		return true;
	}

	/**
	 * Utility method to write an integer inside a file
	 * @param path the file's path
	 * @param i the integer to write
	 * @return true if the file has been written, false otherwise
	 */
	public static boolean writeFileFromPath( String path, int i)
	{
		try
		{

			Writer wr = new FileWriter( path );
			wr.write(String.valueOf(i));
			wr.close();

		} catch (IOException ex) {
			System.err.println("ERROR : "+ path + " can not be written");
			return false;
		}

		return true;
	}


	/**
	 * Utility method to delete a file from a path
	 * @param path the file's path
	 * @return true if the file has been deleted, false otherwise
	 */
	public static boolean deleteFileFromPath( String path )
	{
		try
		{
			File file = new File( path );

			if(file.delete()){
				return true;
			}else
				System.err.println("Delete operation has failed");

		}catch(Exception e){
			System.err.println("Delete operation has failed");
			return false;
		}

		return true;
	}



	/**
	 * Get the public key from the path and the UserGID
	 * @param keyPath the path of the key
	 * @param UserGID the UserGID, contained in the public key name
	 * @return a PublicKey object
	 */
	public static PublicKey getPublicKey( String keyPath, String UserGID )
	{
		PublicKey pubKey = null;

		try 
		{
			/* Check if the public key exists */
			if( !Tools.isFileExists( keyPath + Constants.PUB_KEY_PREFIX + UserGID + ".key"))
			{
				System.err.println("ERROR : public key is missing :" + keyPath + Constants.PUB_KEY_PREFIX + UserGID + ".key");
				return null;
			}
			/* Get the public key from the file*/
			KeyManager keys = new KeyManager();
			pubKey = keys.LoadPublicKey( keyPath + Constants.PUB_KEY_PREFIX + UserGID + ".key", "RSA");

		} catch ( NoSuchAlgorithmException ex) {
			System.err.println("Public key error");
		} catch (InvalidKeySpecException e) {
			System.err.println("Public key error");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Public key error");
		}
		return pubKey;
	}


	/**
	 * Get the private key from the path and the UserGID
	 * @param keyPath the path of the key
	 * @param UserGID the UserGID, contained in the private key name
	 * @return a PrivateKey object
	 */
	public static PrivateKey getPrivateKey( String keyPath, String UserGID )
	{
		PrivateKey priKey = null;

		try 
		{
			/* Check if the public key exists */
			if( !Tools.isFileExists( keyPath + Constants.PR_KEY_PREFIX + UserGID + ".key"))
			{
				System.err.println("ERROR : private key is missing");
				return null;
			}
			/* Get the public key from the file*/
			KeyManager keys = new KeyManager();
			priKey = keys.LoadPrivateKey( keyPath + Constants.PR_KEY_PREFIX + UserGID + ".key", "RSA");

		} catch ( NoSuchAlgorithmException ex) {
			System.err.println("Private key error");
		} catch (InvalidKeySpecException e) {
			System.err.println("Private key error");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Private key error");
		}

		return priKey;
	}

	/**
	 * Convert an encoded String into a PublicKey object
	 * @param stringPubKey the encoded String
	 * @return a PublicKey object
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey stringToPublicKey(String stringPubKey ) throws IOException, NoSuchAlgorithmException,InvalidKeySpecException 
	{
		// Read Public Key.
		byte[] decodedPublicKey = new byte[(int) stringPubKey.length()];
		decodedPublicKey = stringPubKey.getBytes();
		decodedPublicKey = Tools.decodeBytes(decodedPublicKey );
		// Generate PublicKey.
		KeyFactory keyFactory = KeyFactory.getInstance( "RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

		return publicKey;
	}

	
	public static PrivateKey stringToPrivateKey( String stringPrivKey ) throws IOException, NoSuchAlgorithmException,InvalidKeySpecException 
	{
		// Read Public Key.
		byte[] decodedPrivateKey = new byte[(int) stringPrivKey.length()];
		decodedPrivateKey = stringPrivKey.getBytes();
		decodedPrivateKey = Tools.decodeBytes(decodedPrivateKey );
		// Generate PublicKey.
		KeyFactory keyFactory = KeyFactory.getInstance( "RSA");
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

		return privateKey;
	}


	/**
	 * Utility method to convert a byte array into an integer
	 * @param bytes the byte array
	 * @return the int value
	 */
	public static int bytesToInt(byte[] bytes)  
	{
		int ret = 0;
		try
		{
			String str = new String(bytes);
			str = str.replaceAll("\\s+","");
			ret = Integer.parseInt(str);
		}
		catch(BufferUnderflowException e)
		{
			return Constants.KO;
		}
		return ret;
	}






}
