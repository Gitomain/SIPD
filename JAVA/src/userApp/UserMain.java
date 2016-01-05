package userApp;

import java.io.IOException;
import java.util.ArrayList;

import tools.Tools;


public class UserMain 
{
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException 
	{
		String Home = Tools.getHome();
		String Dest = "10";
		// TEST STOREFILE
		UserAPI.storeFile(Home + "APP/Image_10.png");

		// TEST GETFILEDESC
		ArrayList<String> file_list = UserAPI.getFileDesc();
		if(file_list != null) {
			System.out.println("file list : ");
			for(String data :file_list) {
				System.out.println(data);
			}
		}
		// TEST READFILE
		UserAPI.readFile(Dest+"|"+ Home + "APP/Encrypted_Image_10.png");
	}
}
