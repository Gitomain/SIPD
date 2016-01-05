package types;

/**
 *
 * @author tranvan
 */
public class DataString extends DataType
{
    public int length;
    public String str;
    
    public DataString( int length, String str )
    {
    	this.length = length;
    	this.str = str;
    }
}
