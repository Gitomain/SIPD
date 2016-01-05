package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import tools.Constants;
import tools.Tools;

/**
 * TCell DataBase Manager.
 * 
 * @author Athanasia Katsouraki
 * 
 */
public class TCellDBManager {

	//Singleton Class
	private static TCellDBManager instance = null;
	private static Statement stmt =null;
	protected TCellDBManager() {
		// Exists only to defeat instantiation.
	}
	public static TCellDBManager getInstance() {
		if(instance == null) {
			instance = new TCellDBManager();
		}
		return instance;
	}
	public static Statement getStatement(){
		if(stmt == null) {
			//stmt = TCellDBManager.getInstance().CreateDB(Tools.getHome() + Constants.TCELLDB_FILE, "jdbc:sqlite:", "org.sqlite.JDBC");
			stmt = TCellDBManager.getInstance().CreateDB("Test1", "jdbc:sqlite:", "org.sqlite.JDBC");
		}
		return stmt; 
	}
	static Connection con = null; 

	public Statement CreateDB(String Namedb, String DriverManagerName, String ClassConn){
		//Attempt to connect to the ODBC database. 
		String db=Namedb;// ODBC database name 
		System.out.println("Attempting to open database " + db + "..."); 
		try 
		{ 
			Class.forName(ClassConn); 
			//MyDataBase
			con = DriverManager.getConnection(DriverManagerName +db); 

			stmt = con.createStatement(); 
			System.out.println("Opened database '"+Namedb +"' successfully!!");
		} 
		catch (Exception ex) 
		{ 
			// if not successful, quit 
			System.out.println("Cannot open database -- make sure ODBC is configured properly."); 
			System.exit(1); 
		}
		return stmt;
	}

	public void CloseDB(){
		//close database
		try 
		{ 
			con.close(); 
		} 
		catch (Exception ex){
			ex.getStackTrace();
		}
	}

}
