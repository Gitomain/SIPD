package adminApp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import database.Recipient;
import messages.GetFileDesc;
import messages.ReadFile;
import messages.ShareFile;
import messages.StoreFile;

public class TcellAPI 
{
	public static void storeFile(String fileID )
	{
		StoreFile.storeFile(fileID );
	}
	
	public static void readFile( String fileGID )
	{
		ReadFile.readFile(true, fileGID );
	}
	
	public static ArrayList<String> getFileDesc()
	{
		return GetFileDesc.getFileDesc();
	}
	public static void shareFile(String fileID, List<Recipient> recipiendID) throws UnknownHostException, IOException
	{
		ShareFile.shareFile( fileID, recipiendID );
	}
	
}
