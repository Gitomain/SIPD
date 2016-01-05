package database;

public class MyInfo {
		/**
		 * "CREATE TABLE MYINFO " +
				"(MYUSERGID INT PRIMARY KEY NOT NULL, " +
				" MYTCELLIP TEXT NOT NULL, " + 
				" PUBLICKEY TEXT, " +
				" PRIVATEKEY TEXT)";
		 */
		public String UserGID;
		public String TCellIP;
		public String PubKey;
		public String PrivKey;
		
		public MyInfo(String userGID, String tCellIP, String pubKey, String privKey) {
			UserGID = userGID;
			TCellIP = tCellIP;
			PubKey = pubKey;
			PrivKey = privKey;
		}

		public MyInfo(String userGID, String tCellIP, String pubKey) {
			UserGID = userGID;
			TCellIP = tCellIP;
			PubKey = pubKey;
		}

		public MyInfo(String privKey) {
			PrivKey = privKey;
		}

	
}
