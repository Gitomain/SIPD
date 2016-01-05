/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.sql.Statement;

import tools.Constants;
import tools.Tools;
import cryptoTools.KeyManager;

/**
 * Initialization of the DataBase.
 * 
 * @author Athanasia Katsouraki
 */
public class DatabaseMain {
	/**
	 * Initialization of db.
	 * @param args
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, FileNotFoundException  
	{

		try
		{
			//Attempt to connect to the ODBC database. 
			Statement stmt=TCellDBManager.getStatement();
			//Drop all tables that I've created.
			TCellDB tCellDB = new TCellDB();
			tCellDB.DropTables(stmt);
			//Create the appropriate tables.
			tCellDB.CreateTables( stmt);


			String contactsFilePath = Tools.getHome() + Constants.CONTACTS_INFOS_PATH;
			if ( !Tools.isFileExists( contactsFilePath ))
			{
				System.err.println("ERROR : " + Tools.getHome() + Constants.CONTACTS_INFOS_PATH + " does not exists");
				return;
			}

			// Open the file 
			//Insert Values into tables.
			//Retrieve Contacts' Info to Insert in the USER table
			//ContactsFilePath: -- meta/contacts/ContactsTCellDB.txt
			String KeyPath = Tools.getHome() + Constants.KEYS_PATH;
			FileInputStream fstream = new FileInputStream( contactsFilePath );
			// Get the contacts info of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;


			//Read File Line By Line
			while ((strLine = br.readLine()) != null) 	
			{
				if (strLine != "")
				{
					KeyManager keygen = new KeyManager (); 
					String[] tokens = strLine.split(":");
					String TCellIP = tokens[0];
					String UserID = tokens[1];
					String PublicKeyPath = KeyPath+Constants.PUB_KEY_PREFIX+UserID+Constants.KEY_EXT;
					//Load the corresponding public key
					PublicKey pubKey = keygen.LoadPublicKey(PublicKeyPath, Constants.RSA_ALG);
					//public key to String 
					String strPubkey = keygen.PublicKeyToString(pubKey);
					//Insert User in the TCellDB
					tCellDB.InsertUser ( UserID,TCellIP,strPubkey, stmt);
				}
			}
			br.close();
			in.close();

		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return;
		}

	}


}
