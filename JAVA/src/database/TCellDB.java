/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import tools.Constants;

/**
 * TCell Database.
 * 
 * @author Athanasia Katsouraki
 * 
 */
public class TCellDB
{ 

	/**
	 * Drop the tables of DB.
	 * @param stmt the SQL statement
	 */
	public void DropTables ( Statement stmt)
	{
		String sqlUser="DROP TABLE USER";
		String sqlFile="DROP TABLE FILE";

		System.out.println("Executing " + sqlUser +", "+sqlFile);
		try 
		{ 
			stmt.executeUpdate(sqlUser);
			stmt.executeUpdate(sqlFile);
		} 
		catch (Exception ex) 
		{ 
			// error executing SQL statement 
			System.out.println("Error: " + ex); 
		}

	}
	/**
	 * Creates the tables in DB.
	 * @param stmt the SQL statement
	 */
	public void CreateTables(Statement stmt)
	{
		//1. USER
		String sqlUser = "CREATE TABLE USER " +
				"(USERGID INT PRIMARY KEY NOT NULL, " +
				" TCELLIP TEXT   NOT NULL, " + 
				" PUBLICKEY TEXT) " ;

		System.out.println("Executing " +sqlUser);
		//2. FILEDESC
		String sqlFile = "CREATE TABLE FILE " +
				"(FILEGID TEXT PRIMARY KEY    NOT NULL, " +
				" FILEID TEXT   NOT NULL, " + 
				" SECRETKEY TEXT   NOT NULL, " +
				" IV TEXT NOT NULL, " + 
				" TYPE TEXT NOT NULL, " +
				" DESCRIPT TEXT NOT NULL)";
		System.out.println("Executing " +sqlFile);
		//3. MYINFO
		String sqlMyInfo = "CREATE TABLE MYINFO " +
				"(MYUSERGID INT PRIMARY KEY NOT NULL, " +
				" MYTCELLIP TEXT NOT NULL, " + 
				" PUBLICKEY TEXT, " +
				" PRIVATEKEY TEXT)";
		System.out.println("Executing " +sqlMyInfo);
		try
		{ 
			stmt.executeUpdate(sqlUser); 
			stmt.executeUpdate(sqlFile);
			stmt.executeUpdate(sqlMyInfo);
		} 
		catch (Exception ex) 
		{ 
			// error executing SQL statement 
			System.out.println("Error: " + ex); 
		}
	}

	/*------------------------------------ INSERT VALUES IN THE TABLES ------------------------------------*/

	/**
	 * Inserts a file's description
	 * @param fileGID the global id of the file
	 * @param fileID the file id of the file
	 * @param sKey the secret key used to encrypt the file
	 * @param iv the initialization vector
	 * @param type the type of the file (store or share)
	 * @param descr the description of the file
	 * @param stmt the SQL statement to process the insert
	 * @return the SQL status
	 */
	public int InsertFileDesc ( String fileGID, String fileID, String sKey, String iv, String type,String descr, Statement stmt )
	{
		try
		{
			if( isFileGIDexists( fileGID, stmt))
			{
				int ret = DeleteFileDesc( fileGID, stmt );
				if ( ret == Constants.SQL_KO )
					return ret;
			}
			
			FileDesc record = new FileDesc(fileGID,fileID,sKey, iv, type,descr);//process record , etc
			//SQL
			//Values into FileDescFILE
			String sqlFile ="INSERT INTO FILE (FILEGID, FILEID, SECRETKEY, IV, TYPE, DESCRIPT) VALUES "
					+"('"+ record.fileGID+"','"+record.fileID+"','"+record.sKey+"', '"+record.iv+"','"+record.type+"','"+record.descr+"')";
			System.out.println("Executing " + sqlFile);

			stmt.executeUpdate(sqlFile); 

		}         
		catch (SQLException e){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			if ( e.getMessage().equals("column FILEGID is not unique"))
			{
				return DeleteFileDesc( fileGID, stmt );
			}
			else
				return Constants.SQL_KO; 
		}
		return Constants.SQL_OK;
	}

	/**
	 * Inserts a user in the User table
	 * @param UserGID the user id
	 * @param TCellIP the TC's IP of the user
	 * @param PubKey the public key of the user
	 * @param stmt the SQL statement to process the insert
	 */
	public void InsertUser ( String UserGID,String TCellIP, String PubKey, Statement stmt )
	{
		try{

			User record = new User(UserGID,TCellIP);
			User pubKey = new User (PubKey);
			//SQL
			//Values into User
			String sqlUser ="INSERT INTO USER (USERGID, TCELLIP, PUBLICKEY) VALUES "
					+"('"+ record.userGID+"','"+record.TCellIP+"','"+pubKey.PubKey+"')";
			System.out.println("Executing " + sqlUser);
			stmt.executeUpdate(sqlUser); 

		}
		catch (SQLException e){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}

	}
	
	/*------------------------------------ DELETE VALUES FROM THE TABLES ------------------------------------*/
	
	/**
	 * Deletes a file in the db
	 * @param fileGID the fileGID of the file
	 * @param stmt the SQL statement
	 * @return the SQL status
	 */
	public int DeleteFileDesc( String fileGID, Statement stmt )
	{
		String query = "delete from FILE where FILEGID = '" + fileGID + "';";
		int ret = Constants.OK;
		try 
		{
			System.out.println( query );
			stmt.executeUpdate( query );
			
			
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return Constants.SQL_KO;
		}
		
		return ret;
	}
	
	
	/*------------------------------------ SELECT VALUES FROM THE TABLES ------------------------------------*/
	
	/**
	 * Checks if the file exists in the db
	 * @param fileGID the fileGID of the file
	 * @param stmt the SQL statement
	 * @return true if the fileGID exists
	 */
	boolean isFileGIDexists( String fileGID, Statement stmt )
	{
		int cpt = 0;
		try 
		{
			String query = "SELECT FILEGID FROM FILE WHERE FILEGID = '" + fileGID + "';";
			ResultSet rs = stmt.executeQuery( query );
			
			while( rs.next() )
				cpt++;
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if( cpt > 0)
			return true;
		else
			return false;
		
	}
	
	
	
	/**
	 * Select USERGID, TCELLIP, PUBLICKEY from USER WHERE USERGID = 'userGID';
	 * @param userGID the user ID
	 * @return List<IP>
	 *    List of IPs <USERGID, TCELLIP, PUBLICKEY>
	 */
	public ArrayList<User> getIPsFromUser( String userGID )
	{

		String UserGID = null; 
		String TCellIP = null;
		String PubKey = null;

		ArrayList<User> list = new ArrayList<User>();
		//Retrieve TCellIP  and PublicKey from USER table in TCellDB

		try{
			Statement stmt = TCellDBManager.getStatement();
			ResultSet rs = stmt.executeQuery("SELECT USERGID, TCELLIP, PUBLICKEY FROM USER WHERE USERGID='"+userGID +"';");
			while (rs.next() ) {
				UserGID =rs.getString("USERGID");
				TCellIP = rs.getString("TCELLIP");
				PubKey = rs.getString("PUBLICKEY");
				list.add(new User(UserGID,TCellIP,PubKey));
			}
			rs.close();
			stmt.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}

		return list;                      
	}
	/**
	 * Select FILEGID , TYPE, DESCRIPT FROM FILE;
	 * @return  List<FileDescData>
	 *          List of the files' descriptions in the db
	 */
	public ArrayList<FileDesc> getFileDesc()
	{  

		String fileGID =null;
		String type = null;
		String Description =null;

		//List that I'll give back: <fileGID,type,Description)
		ArrayList<FileDesc> list = new ArrayList<FileDesc>();

		//Retrieve TCellIP from USER table in TCellDB
		try{
			Statement stmt = TCellDBManager.getStatement();
			ResultSet rs = stmt.executeQuery("SELECT FILEGID, TYPE, DESCRIPT FROM FILE;");
			while (rs.next()==true ) {
				fileGID = rs.getString("FILEGID");
				type =rs.getString("TYPE");
				Description=rs.getString("DESCRIPT");
				list.add(new FileDesc(fileGID, "", "", "", type, Description));
			}
			rs.close();
			stmt.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}

		return list;                      
	}
	/**
	 * Select FILEID, K FROM FILE WHERE FILEGID ='fileGID';
	 * @return  List<FileDescData>
	 *          List of the file's description with the given fileGID
	 */
	public  ArrayList<FileDesc> getFileIDsKey(String fileGID)
	{  
		//List that I'll give back: <fileGID,type,Description)
		ArrayList<FileDesc> list = new ArrayList<FileDesc>();

		//Retrieve TCellIP from USER table in TCellDB
		try{
			Statement stmt=TCellDBManager.getStatement();
			ResultSet rs = stmt.executeQuery("SELECT FILEID, SECRETKEY, IV FROM FILE WHERE FILEGID='"+fileGID +"';");
			while (rs.next()==true ) {
				String fileID = rs.getString("FILEID");
				String sKey =rs.getString("SECRETKEY");
				String iv = rs.getString("IV");
				list.add(new FileDesc(fileID,sKey,iv));
			}
			rs.close();
			stmt.close();
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}

		return list;                      
	}
}

