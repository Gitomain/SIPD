package messages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.PublicKey;

import tools.Constants;
import tools.Tools;
import cryptoTools.Signature;
import deviceInfo.DeviceInfo;
import messages.CertifiedId;
import messages.AllInfo;
import cryptoTools.CheckSignature;



public class AddContact {
	
	public AddContact(){}
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
		try {
			FileWriter fWriter = new FileWriter(home+Constants.CONTACTS_INFOS_PATH,true);
			fWriter.write(allInfo.getTCellIP_()+":"+allInfo.getUserID_()+"\n");
			fWriter.close();
		} catch (IOException e1) {
			// TODO: handle exception
			System.out.println("IOException: "+ e1.getMessage());
		}
		
		return;
	}

}
