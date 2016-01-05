/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package daemon;


import java.util.ArrayList;

import database.FileDesc;
import database.TCellDB;
import types.DataFileDesc;

/**
 * ProcessFile provides the methods to get the files' list from the TC's database.
 * @author tranvan
 */
public class ProcessFile 
{

	/**
	 * Get all the file's description from the database 
	 * @return an ArrayList containing the DataFileDesc objects
	 */
	public static ArrayList<DataFileDesc> getListFilesDesc( )
	{
		TCellDB db = new TCellDB();
		ArrayList<FileDesc> list = db.getFileDesc();
		ArrayList<DataFileDesc> listFileDesc = null;

		if( list == null )
			return null;

		else
		{
			listFileDesc = new ArrayList<DataFileDesc>();

			for(int i=0;i<list.size();i++)
			{
				DataFileDesc fileDesc = new DataFileDesc( list.get(i).fileGID.length() ,list.get(i).fileGID, list.get(i).type.length(),
						list.get(i).type, list.get(i).descr.length(),list.get(i).descr );
				listFileDesc.add( fileDesc );
			}
		}

		return listFileDesc;
	}
}
