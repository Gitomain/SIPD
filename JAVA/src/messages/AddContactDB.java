package messages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Statement;

import tools.Constants;
import tools.Tools;
import cryptoTools.KeyGenerator;
import cryptoTools.Signature;
import database.MyTCellDB;
import deviceInfo.DeviceInfo;
import messages.CertifiedId;
import messages.AllInfo;
import cryptoTools.CheckSignature;



public class AddContactDB {
	
	public AddContactDB(){}
	/*1 Vérifier le certifiedId
	 *2 L'ajouter dans le tableau de User*/
	
	public static void addContact(CertifiedId certifiedId){
		// Vérifier le certifiedId
		AllInfo allInfo = certifiedId.getAllInfo();
		int hashCodeAllInfo = allInfo.hashCode();
		byte[] encryptBySelf = certifiedId.getEncryptBySelf();
		byte[] encryptByTcell = certifiedId.getEncryptByTcell();
		
		String home = new String("");
		home = Tools.getHome();
		
		int hashCodeSelf = 0;
		int hashCodeTcell = 0;
		try {
			CheckSignature cs = new CheckSignature();
			hashCodeSelf = ByteBuffer.wrap(cs.decryptBlockByBlock(encryptBySelf, allInfo.getPubkey_())).getInt();
			hashCodeTcell = ByteBuffer.wrap(cs.decryptBlockByBlock(encryptByTcell, Tools.getPublicKey(home+Constants.KEYS_PATH, "00"))).getInt();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Decryption Exception: "+ e.getMessage());
			return;
		}
		
		if(hashCodeSelf != hashCodeAllInfo || hashCodeAllInfo != hashCodeTcell){
			System.out.println("HashCodes not equal");
			return ;
		}
		System.out.println("hashCodeAllInfo: "+hashCodeAllInfo);
		System.out.println("hashCodeSelf: "+hashCodeSelf);
		System.out.println("hashCodeTcell: "+hashCodeTcell);
		System.out.println(allInfo.getTCellIP_());
		System.out.println(allInfo.getUserID_());
		System.out.println(allInfo.getPubkey_());
		
		//ajouter le contact
		SqliteDemo sd = new SqliteDemo();
		Statement stmt = sd.dbManager.getStatement();
		MyTCellDB mtdb = new MyTCellDB();
		String pubKey = null;
		try {
			KeyGenerator kg = new KeyGenerator();
			pubKey = kg.PublicKeyToString(allInfo.getPubkey_());
		} catch (NoSuchAlgorithmException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		mtdb.InsertUser(allInfo.getUserID_(), allInfo.getTCellIP_(), pubKey, stmt);
		
		return;
	}

}
