package types;

/**
 *
 * @author tranvan
 */
public class DataFile extends DataType
{
	public int filePathLength;
	public String filePath;
	public int fileSize;
	public byte[] file; 

	public DataFile(int filePathLength, String filePath, int fileSize, byte[] file)
	{
		this.filePathLength = filePathLength;
		this.filePath = filePath;
		this.fileSize = fileSize;
		this.file = file;
	}
}
