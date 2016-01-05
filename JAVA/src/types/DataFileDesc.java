package types;

/**
 *
 * @author tranvan
 */
public class DataFileDesc extends DataType
{
	public int fileGIDLength;
	public String fileGID;
	public int typeLength;
	public String type;
	public int descLength;
	public String desc;

	public DataFileDesc( int fileGIDLength, String fileGID, int typeLength, String type, int descLength, String desc )
	{
		this.fileGIDLength = fileGIDLength;
		this.fileGID = fileGID;
		this.typeLength = typeLength;
		this.type = type;
		this.descLength = descLength;
		this.desc = desc;
	}

}
