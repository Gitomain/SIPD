package messages;

import java.security.PrivateKey;
import java.security.PublicKey;

import tools.Constants;
import tools.Tools;
import deviceInfo.DeviceInfo;


/*
 * DeviceInfo deviceInfo = Tools.readDeviceInfo();
		String UserID = deviceInfo.getUserID(); 
		String TCellIP = deviceInfo.getTCellIP();
		String home = new String("");
		home = Tools.getHome();
		PublicKey Pubkey = Tools.getPublicKey(home+Constants.KEYS_PATH, UserID);
		PrivateKey Prkey = Tools.getPrivateKey(home+Constants.KEYS_PATH, UserID);
		//String FileGID;
		int AllInfo = UserID.hashCode()+TCellIP.hashCode()+Pubkey.hashCode();*/

public class AllInfo {
	
	public PublicKey Pubkey_;
	public String UserID_;
	public String TCellIP_;
	
	
	AllInfo(PublicKey Pubkey, String UserID, String TCellIP){
		Pubkey_ = Pubkey;
		UserID_ = UserID;
		TCellIP_ = TCellIP;
	}

	public PublicKey getPubkey_() {
		return Pubkey_;
	}

	public void setPubkey_(PublicKey pubkey_) {
		Pubkey_ = pubkey_;
	}

	public String getUserID_() {
		return UserID_;
	}

	public void setUserID_(String userID_) {
		UserID_ = userID_;
	}

	public String getTCellIP_() {
		return TCellIP_;
	}

	public void setTCellIP_(String tCellIP_) {
		TCellIP_ = tCellIP_;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Pubkey_ == null) ? 0 : Pubkey_.hashCode());
		result = prime * result
				+ ((TCellIP_ == null) ? 0 : TCellIP_.hashCode());
		result = prime * result + ((UserID_ == null) ? 0 : UserID_.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AllInfo other = (AllInfo) obj;
		if (Pubkey_ == null) {
			if (other.Pubkey_ != null)
				return false;
		} else if (!Pubkey_.equals(other.Pubkey_))
			return false;
		if (TCellIP_ == null) {
			if (other.TCellIP_ != null)
				return false;
		} else if (!TCellIP_.equals(other.TCellIP_))
			return false;
		if (UserID_ == null) {
			if (other.UserID_ != null)
				return false;
		} else if (!UserID_.equals(other.UserID_))
			return false;
		return true;
	}
	
	
}
