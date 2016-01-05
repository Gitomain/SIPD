package messages;

import java.security.PrivateKey;
import java.sql.Statement;

import tools.Constants;
import tools.Tools;
import cryptoTools.AsymmetricDecryption;
import database.TCellDB;
import database.TCellDBManager;


/**
 * Process the Messages.
 * 
 * @author Athanasia Katsouraki
 */
public class ProcessMessage 
{

	public ProcessMessage (){}


	/**
	 * Decrypts a message and store the fields in the database
	 * @param encBytes encrypted bytes
	 * @param iv initialization vector
	 * @param userGID userGID of the TC
	 * @return status of the execution
	 * @throws Exception
	 */
	public static int process(byte[] encBytes, byte[] iv, String userGID) throws Exception
	{
		String fileID;
		PrivateKey loadedPrivateKey = Tools.getPrivateKey( Tools.getHome() + Constants.KEYS_PATH, userGID );
		if ( loadedPrivateKey == null )
			return Constants.KO;

		int retValue = 0;


		AsymmetricDecryption ac = new AsymmetricDecryption();    
		byte [] decMessage = ac.decryptBlockByBlock(encBytes, loadedPrivateKey);

		String decryptedMsg = new String(decMessage); 

		String[] msgFields = Tools.getMessageFields( decryptedMsg );
		if( msgFields == null )
			return Constants.KO;

		String type = msgFields[0];

		if (type.equals("STORE"))
		{
			fileID = msgFields[1];
			String sKey = msgFields[2];

			Statement stmt=TCellDBManager.getStatement();
			TCellDB tCellDB = new TCellDB();
			String fileGID = userGID +"|"+fileID;
			fileID = Tools.getHome() + Constants.TCELL_FILES + Tools.getFileName(fileGID);
			/* Insert into FileDesc values (userGID, fileID, sKey, type, "my file"); */
			retValue = tCellDB.InsertFileDesc ( fileGID, fileID, sKey, new String(iv), type, "my file",stmt );
		}

		else if (type.equals("SHARE"))
		{
			String fileGID = msgFields[1];
			fileID = msgFields[2];
			String sKey = msgFields[3];

			userGID = Tools.extractUserID(fileGID);
			//Select from users
			Statement stmt = TCellDBManager.getStatement();
			TCellDB tCellDB = new TCellDB();
			/* Insert into FileDesc values (FileGID, fileID, sKey, type, "shared file"); */
			retValue = tCellDB.InsertFileDesc ( fileGID, fileID, sKey, new String(iv),type, "shared file",stmt );

		}

		return retValue;

	}
}



