package database;

/**
 * TCellDB FileDesc records.
 * 
 * @author Athanasia Katsouraki
 * 
 */

public class FileDesc {
	public String fileGID;
	public String fileID;
	public String sKey;
	public String iv;
	public String type;
	public String descr;


	public FileDesc(String fileGID, String fileID, String sKey, String iv, String type, String descr)
	{
		this.fileGID=fileGID;
		this.fileID=fileID;
		this.sKey=sKey;
		this.iv = iv;
		this.type=type;
		this.descr=descr;            
	}

	public FileDesc(String fileID,String sKey, String iv)
	{
		this.fileID=fileID;
		this.sKey=sKey;      
		this.iv = iv;
	}





}


