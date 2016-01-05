package messages;


import java.io.FileWriter;
import java.sql.Date;
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
import tools.Constants;

public class CreateUser {
	CreateUser(String Password, String MyUserId, String Nom, String Prenom) throws IOException {
		String home = new String("");
		home = Tools.getHome();
		FileWriter fWriter = new FileWriter(home+Constants.USER_INFOS_PATH,true);
		fWriter.write(Password);
		fWriter.write(MyUserId);
		fWriter.write(Nom);
		fWriter.write(Prenom);
		fWriter.close();
	}
}
