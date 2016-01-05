package types;

import java.util.List;

/**
 *
 * @author tranvan
 */
public class DataListFilesDesc extends DataType
{
    public int nFiles;
    public List<DataFileDesc> list;
    
    public DataListFilesDesc( int nFiles, List<DataFileDesc> list)
    {
    	this.nFiles = nFiles;
    	this.list = list;
    }
    
}
