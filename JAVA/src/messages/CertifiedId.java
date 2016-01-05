package messages;

import java.security.PrivateKey;
import java.util.Arrays;


public class CertifiedId {

	private AllInfo allInfo;
	private byte[] encryptBySelf;
	private byte[] encryptByTcell;
	
	public CertifiedId(AllInfo allInfo, byte[] encryptBySelf, byte[] encryptByTcell) {
		this.allInfo = allInfo;
		this.setEncryptBySelf(encryptBySelf);
		this.setEncryptByTcell(encryptByTcell);
	}

	public AllInfo getAllInfo() {
		return allInfo;
	}

	public void setAllInfo(AllInfo allInfo) {
		this.allInfo = allInfo;
	}

	public byte[] getEncryptBySelf() {
		return encryptBySelf;
	}

	public void setEncryptBySelf(byte[] encryptBySelf) {
		//this.encryptBySelf = encryptBySelf;
		this.encryptBySelf = Arrays.copyOf(encryptBySelf, encryptBySelf.length);
	}

	public byte[] getEncryptByTcell() {
		return encryptByTcell;
	}

	public void setEncryptByTcell(byte[] encryptByTcell) {
		//this.encryptByTcell = encryptByTcell;
		this.encryptByTcell = Arrays.copyOf(encryptByTcell, encryptByTcell.length);
	}

}
