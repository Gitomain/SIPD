/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptoTools;

//import Tools.Constants;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import tools.Constants;
import tools.Tools;



/**
 * Generates Public-Private Keys for each user.
 * 
 * @author Athanasia Katsouraki
 * 
 */
public class KeyGenMain {
	public static void main(String args[]) throws NoSuchAlgorithmException, FileNotFoundException, IOException 
	{
		List<String> userIDList = new ArrayList<String>();

		if (!Tools.isFileExists(args[0]))
			System.out.println("A file which contains the users' IDs is needed!");
		else {
			userIDList = getUserIDs (args[0]);

			for (String zeroLine : userIDList) {
				System.out.println("KeyPair for user: userID: " + zeroLine);
				/*Generate the public and the private key for each user*/
				produceKeyPairs(zeroLine);
			}
		}
	}
	/**
	 * Produces Public - Private key.
	 * @param userID
	 * @throws NoSuchAlgorithmException 
	 */
	public static void produceKeyPairs(String userID) throws NoSuchAlgorithmException{

		KeyGenerator keys = new KeyGenerator();

		try 
		{
			Tools.createDir( Constants.KEYS_PATH );

			//key generator - public-private keys
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

			keyGen.initialize( Constants.RSA_KEY_LENGTH );
			KeyPair generatedKeyPair = keyGen.genKeyPair();

			System.out.println("Generating Key Pair is in process...");
			keys.getPublicKey(generatedKeyPair);
			keys.SavePublicKey(Tools.getHome() + Constants.KEYS_PATH+ Constants.PUB_KEY_PREFIX+userID+Constants.KEY_EXT , generatedKeyPair);
			keys.getPrivateKey(generatedKeyPair);
			keys.SavePrivateKey(Tools.getHome() + Constants.KEYS_PATH+ Constants.PR_KEY_PREFIX+userID+Constants.KEY_EXT , generatedKeyPair);

		} catch (Exception e) 
		{
			e.printStackTrace();
			return;
		}

	}

	/**
	 * Open a file and read the UserIDs and store them in a list. 
	 * @param userIDs
	 * @return 
	 *          a list with userIDs 
	 */
	public static List<String> getUserIDs (String userIDs) throws FileNotFoundException, IOException
	{
		FileReader fileReader = new FileReader(userIDs);
		BufferedReader reader = new BufferedReader(fileReader);

		List<String> lineList = new ArrayList<String>();
		String thisLine = reader.readLine();

		while (thisLine != null) {
			lineList.add(thisLine);
			thisLine = reader.readLine();
		}

		reader.close();
		fileReader.close();
		return lineList;
	}
}

