package messages;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import cryptoTools.Signature;
import cryptoTools.AsymmetricEncryption;
import tools.Constants;
import tools.Tools;
import deviceInfo.DeviceInfo;
import messages.AllInfo;

/*
 *  Demande à la TCell d'un utilisateur A de produire un Identifiant certifié,
 *que A pourra ensuite transmettre à quiconque désireux d'échanger des fichiers 
 *avec lui en étant certain qu'il s'agit bien de A. Cet identifiant certifié 
 *contient l'identifiant de A, sa clé publique et l'adresse de sa TCell, le tout 
 *signé par la TCell de A(le tout haché et chiffré avec la clé privée de la TCell, 
 *en faisant l'hypothèse que toutes les TCells partagent une même paire Kpub, 
 *Kpriv installée à la fabrication et prouvant qu'elles sont de 'vraies' TCell.).
 */

/*
 * 
 *Add a Tcell API: create_CertifiedID
 *–  Retrieve from the Tcell a set of non-technical data (name, photo, etc)
 *–  Retrieve from the Tcell technical data (Public Key, Tcell@)
 *–  Build a CertifiedID that can be sent to others (that want to contact me)
 *–  CertifiedID = AsymEncrypt(Hash(all info)) with the TCell private key + TCell certificate
 *Add a Tcell API: AddContact (CertifiedID)
*/


/*
 * 
4. Scénarios d'usage
Ci-dessous quelques séquences typiques d'actions mettant en œuvre l'architecture.
Les scénarios sont décrits tels qu'ils devraient fonctionner une fois l'architecture totalement réalisée en fin de projet (les
parties à réaliser dans le projet étant en gras) :
Envoi d'un fichier F à l'utilisateur U par l'application APP 7
1. Au préalable (une seule fois), U invoque BuildCertifiedId sur sa TCell pour récupérer son identifiant certifié prouvant qu'il est bien U et donnant ses informations de connexion. Ceci
 (a) construit un message contenant (Id de U, adresse de la TCell de U, Kpub de U), 
 (b) signe ce message en le hachant et en chiffrant le haché avec la clé privée commune à toutes les TCells.
2. U envoie ce CertifiedId à APP par un moyen quelconque (ex: par mail ou en s'enregistrant comme client de APP).
3. APP vérifie que le CertifiedId de U a bien été généré par une TCell et qu'il correspond bien à U en vérifiant la signature du certificat grâce à la clé publique des TCell.
4. APP exécute un StoreFile du fichier F vers la TCell de U. Ceci a pour effet de 
 (a) générer une clé secrète Ks, 
 (b) chiffrer le fichier F avec Ks, 
 (c) chiffrer Ks avec Kpub de U, 
 (d) envoyer le tout à la TCell de U.
5. La TCell de U reçoit F, le stocke sur le PC hôte, enregistre ce fichier et la clé Ks dans la table FileDesc.
*/

public class BuildCertifiedId {
	
	public BuildCertifiedId(){}
	
	//l'identifiant de A, sa clé publique et l'adresse de sa TCell
	public static CertifiedId buildCertifiedId(){
		DeviceInfo deviceInfo = Tools.readDeviceInfo();
		String UserID = deviceInfo.getUserID(); 
		String TCellIP = deviceInfo.getTCellIP();
		String home = new String("");
		home = Tools.getHome();
		PublicKey Pubkey = Tools.getPublicKey(home+Constants.KEYS_PATH, UserID);
		
		AllInfo allInfo= new AllInfo(Pubkey, UserID, TCellIP);
		PrivateKey Prkey = Tools.getPrivateKey(home+Constants.KEYS_PATH, UserID);
		PrivateKey TcellPriKey = Tools.getPrivateKey(home+Constants.KEYS_PATH, "00");
		
		Signature sig = new Signature();
		byte[] bytes = ByteBuffer.allocate(4).putInt(allInfo.hashCode()).array();
		byte[] encryptBySelf = sig.encryptBlockByBlock(bytes, Prkey); 
		byte[] encryptByTcell = sig.encryptBlockByBlock(bytes, TcellPriKey);
		
		CertifiedId ci = new CertifiedId(allInfo, encryptBySelf, encryptByTcell);	
		
		return ci;

	}
}
