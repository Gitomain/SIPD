package adminApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tools.Tools;
import userApp.UserAPI;
import database.Recipient;


public class AdminMain 
{
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException 
	{
		String Home = Tools.getHome();
		String Dest = "10";
		List<Recipient> myrecip = Arrays.asList(new Recipient (Dest));
		
		// TEST GETFILEDESC
		ArrayList<String> file_list = TcellAPI.getFileDesc();
		if(file_list != null) {
			System.out.println("file list : ");
			for(String data :file_list) {
				System.out.println(data);
			}
		}
		// TEST READFILE
		TcellAPI.readFile(Dest+"|"+ Home + "APP/Encrypted_Image_10.png");

		// TEST SHAREFILE
		TcellAPI.shareFile(Home + "APP/Encrypted_Image_10.png", myrecip);

	}
}
