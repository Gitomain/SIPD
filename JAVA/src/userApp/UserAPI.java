package userApp;

import java.util.ArrayList;

import messages.GetFileDesc;
import messages.ReadFile;
import messages.StoreFile;

public class UserAPI 
{
	public static void storeFile(String fileID )
	{
		StoreFile.storeFile(fileID );
	}
	
	public static void readFile( String fileGID )
	{
		ReadFile.readFile(false, fileGID );
	}
	
	public static ArrayList<String> getFileDesc()
	{
		return GetFileDesc.getFileDesc();
	}
}
