package deviceInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * DeviceInfo contains the TCell IP and the UserGID of a device 
 * 
 * @author Athanasia Katsouraki
 * 
 */

public class DeviceInfo
{

	private String TCellIP;
	private String UserID;


	/**Create a DeviceInfo object
	 * @param TCellIP
	 * @param UserID
	 */
	public DeviceInfo(String TCellIP, String UserID)
	{
		setTCellIP(TCellIP);
		setUserID(UserID);
	}
	/**
	 * @return the TCellIP
	 */
	public String getTCellIP() {
		return TCellIP;
	}
	/**
	 * @param TCellIP the TCellIP to set
	 */
	public void setTCellIP(String TCellIP) {
		this.TCellIP = TCellIP;
	}

	/**
	 * @return the UserID
	 */
	public String getUserID() {
		return UserID;
	}
	/**
	 * @param UserID the UserID to set
	 */
	public void setUserID(String UserID) {
		this.UserID = UserID;
	}

	public String toString(){
		return this.TCellIP + " " + this.UserID;
	}

	/**
	 * Reads a file and return the different fields. The fields has to be separated by a ':'
	 * @param filePath the file's path
	 * @return a String array, containing the fields of the file
	 */
	public static String[] getFieldsFromFile( String filePath )
	{
		String[] fields = null;

		try 
		{
			File f = new File( filePath );
			Scanner sc = new Scanner(f);

			while(sc.hasNextLine())
			{
				String line = sc.nextLine();
				fields = line.split(":");

				/* remove all the spaces */
				for(String str : fields)
					str = str.replaceAll("\\s+","");
			}

			sc.close();

		} catch (FileNotFoundException ex) {
			System.err.println(filePath + " not found");
			return null;
		}
		return fields;
	}

}