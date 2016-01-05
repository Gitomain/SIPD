package messages;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		CertifiedId cId = BuildCertifiedIdDB.buildCertifiedId();
		AddContactDB.addContact(cId);
		
		AllInfo allInfo = null; 
		allInfo = cId.getAllInfo();
		allInfo.setTCellIP_("127.0.100.2");
		CertifiedId cId2 = new CertifiedId(allInfo, cId.getEncryptBySelf(), cId.getEncryptByTcell());
		AddContactDB.addContact(cId2);
			
			//AllInfo allInfo = new AllInfo(Pubkey, UserID, TCellIP)
			//CertifiedId certifiedId = new CertifiedId();
			// envoyer le certified (simulé)
			
		
	}

}
