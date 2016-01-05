package messages;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Statement;
import java.util.ArrayList;

import cryptoTools.KeyGenerator;
import cryptoTools.Signature;
import cryptoTools.AsymmetricEncryption;
import tools.Constants;
import tools.Tools;
import database.MyInfo;
import database.MyTCellDB;
import database.TCellDBManager;
import deviceInfo.DeviceInfo;
import messages.AllInfo;

public class BuildCertifiedIdDB {
	
	public BuildCertifiedIdDB(){}
	
	//l'identifiant de A, sa clé publique et l'adresse de sa TCell
	public static CertifiedId buildCertifiedId(){
		ArrayList<MyInfo> myInfos = null;
		DeviceInfo deviceInfo = Tools.readDeviceInfo();
		String UserID = deviceInfo.getUserID(); 
		String TCellIP = deviceInfo.getTCellIP();
		
		String home = Tools.getHome();
	
		PublicKey pubKey = null;
		PrivateKey privKey = null;
		
		PublicKey publicKey = Tools.getPublicKey(home+Constants.KEYS_PATH, UserID);
		PrivateKey privateKey = Tools.getPrivateKey(home+Constants.KEYS_PATH, UserID);
		
		KeyGenerator kg = null;
		String StringPublickey = null;
	    String StringPrivkey = null;
		try {
			kg = new KeyGenerator();
			
			StringPublickey = kg.PublicKeyToString(publicKey);
			StringPrivkey = kg.PrivateKeyToString(privateKey);
		} catch (NoSuchAlgorithmException | IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
		
			SqliteDemo sd = new SqliteDemo();
			MyTCellDB mtdb = new MyTCellDB();
			Statement stmt = sd.dbManager.getStatement();
			
			//CreateDB(String Namedb, String DriverManagerName, String ClassConn)
			mtdb.CreateTables(stmt);
			mtdb.InsertMyInfo(UserID, TCellIP, StringPublickey, StringPrivkey, stmt);
			myInfos = mtdb.getMyInfo();
			
			pubKey = Tools.stringToPublicKey(myInfos.get(0).PubKey);
			privKey = Tools.stringToPrivateKey(myInfos.get(1).PrivKey);
		
		} catch (NoSuchAlgorithmException
				| InvalidKeySpecException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		AllInfo allInfo = new AllInfo(pubKey, myInfos.get(0).UserGID, myInfos.get(0).TCellIP);
			
		
		PrivateKey TcellPriKey = Tools.getPrivateKey(home+Constants.KEYS_PATH, "00");
		
		Signature sig = new Signature();
		byte[] bytes = ByteBuffer.allocate(4).putInt(allInfo.hashCode()).array();
		byte[] encryptBySelf = sig.encryptBlockByBlock(bytes, privKey); 
		byte[] encryptByTcell = sig.encryptBlockByBlock(bytes, TcellPriKey);
		
		CertifiedId ci = new CertifiedId(allInfo, encryptBySelf, encryptByTcell);	
		
		return ci;

	}
}
